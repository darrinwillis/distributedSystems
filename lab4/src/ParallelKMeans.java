import java.util.*;
import java.io.*;
import mpi.*;

public class ParallelKMeans
{
    //This is a data struct to hold the centroid and data list
    private static class Cluster implements Serializable {
        DataInterface centroid;
        DataInterface lastCentroid;
        List<DataInterface> data;

        public Cluster(DataInterface d)
        {
            this.centroid = d;
            this.lastCentroid = null;
            this.data = new ArrayList<DataInterface>();
        }

        public Cluster(DataInterface d, List<DataInterface> givenData)
        {
            this.centroid = d;
            this.lastCentroid = null;
            this.data = givenData;
        }
        public String toString() {
            return (data.toString());
        }
    }

    //TODO: Take this as a commandline argument
    private static int K = 3;
    //TODO: Dynamically determine mu
    private static int mu = 100;

    //Instace Variables
    private static List<DataInterface> dataList;
    private static List<Cluster> clusters;
    private static int myrank;
    private static int distanceThreshold;
    private static int p; //num processors
    private static int partSize; //size of data sent to each processor

    public static void main(String[] args) 
    {
        try{
            MPI.Init(args);
            if (args.length != 4) {
                System.out.println("Run with java SequentialKMeans [points or dna] [K] [distanceThreshold] [inputfile]");
                return;
            }
            K = Integer.parseInt(args[1]);

            long startTime = System.currentTimeMillis();
            
            myrank = MPI.COMM_WORLD.Rank();
            p = MPI.COMM_WORLD.Size();
            if(myrank == 0) 
                master(args);
            else 
                slave();

            MPI.Finalize();
            long endTime = System.currentTimeMillis();
            System.out.println("Execution time was " + (endTime-startTime) + "ms");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void master(String[] args) throws MPIException{
        distanceThreshold = Integer.parseInt(args[2]);

        String filename = args[3];
        //Select which data type to use
        List<DataInterface> theData = null;
        if (args[0].equals("points"))
            theData = getPairData(filename);
        else if (args[0].equals("dna"))
            theData = getDNAData(filename);
        
        initialize(theData);

        partSize = dataList.size()/(p-1);

        System.out.println("Num processors " + p);
        System.out.println("Part Size " + partSize);

        System.out.println("Picking initial centroids");
        pickInitialCentroids();
        formList();

        int i;
        for(i = 0; i < mu; i++){
            System.out.println("Scheduling workers");
            int remainder = dataList.size()-(p-1)*partSize;
            DataInterface[] dataArray = dataList.toArray(new DataInterface[0]);
            Cluster[] clusterArray = clusters.toArray(new Cluster[0]);
            int[] psize = {partSize + 1};
            int index = 0;

            for(int rank = 1; rank < p; rank++) {//partition and send work to slaves
                if(remainder == 0){ 
                    psize[0]--;
                    remainder--;
                } else 
                    remainder--;
                MPI.COMM_WORLD.Send(psize,0,1,MPI.INT,rank,99);
                MPI.COMM_WORLD.Send(clusterArray,0,K,MPI.OBJECT,rank,0); //clusters
            
                MPI.COMM_WORLD.Send(dataArray,index,psize[0],MPI.OBJECT,rank,1); //datapoints
                index += psize[0];
            }

            Cluster[][] results = new Cluster[p-1][K];
            int counter = 0;
            while(counter < p-1) { //receiving output
                Status s = MPI.COMM_WORLD.Recv(results[counter],0,K,MPI.OBJECT,MPI.ANY_SOURCE,2);
                //System.out.println("Recieved " + Arrays.toString(results[counter]) + " from Slave");
                counter++;
            }
       
            System.out.println("All messages recieved");
            mergeData(results);
            recalculateCentroids();

            if(checkConvergence()) 
                break;
        }

        formList();
    }
    
    public static void slave() throws MPIException {
        while(true) {
            int[] psize = new int[1];
        
            Status s = MPI.COMM_WORLD.Recv(psize,0,1,MPI.INT,0,99); //recieve partition size info
            partSize = psize[0];

            Cluster[] cluster = new Cluster[K];
            DataInterface[] data = new DataInterface[partSize];
            Status s1 = MPI.COMM_WORLD.Recv(cluster,0,K,MPI.OBJECT,0,0); //recieve cluster list
            Status s2 = MPI.COMM_WORLD.Recv(data,0,partSize,MPI.OBJECT,0,1); //recieve data list 
            calculateGroups(cluster,data);
            cluster = clusters.toArray(new Cluster[0]);
            System.out.println("Slave " + myrank + " data " + Arrays.toString(data));
            System.out.println("Slave " + myrank + " cluster " + Arrays.toString(cluster));
            MPI.COMM_WORLD.Send(cluster,0,K,MPI.OBJECT,0,2);
        }     
    }

    private static boolean checkConvergence()
    {
       Iterator<Cluster> citer = clusters.iterator();
       
       //Calculate the most that any centroid has moved
       int maxDistance = 0;
       while (citer.hasNext())
       {
            Cluster c = citer.next();
            int thisDistance = c.centroid.distance(c.lastCentroid);
            maxDistance = Math.max(thisDistance, maxDistance);
       }
       System.out.println("max distance is " + maxDistance);
       return (maxDistance <= distanceThreshold);
    }

    public static void initialize(List<DataInterface> inputData) 
    {
        System.out.println("Making new Parallel K means object");
        // Convert the input data into DataInterfaces
        List<DataInterface> units = new ArrayList<DataInterface>();
        Iterator<DataInterface> iter = inputData.iterator();
        System.out.println("Adding data units");
        while (iter.hasNext()) {
            units.add(iter.next());
        }
        dataList = units;
        clusters = new ArrayList<Cluster>();
        System.out.println("Parallel K means object created");
        System.out.println("Data size " + dataList.size());
    }

    public static void calculateGroups(Cluster[] c, DataInterface[] d)
    {
        dataList = new ArrayList<DataInterface>(Arrays.asList(d));
        clusters = new ArrayList<Cluster>(Arrays.asList(c));

        //Assign all data to centroids
        assignData();
    }

    // This random selects k unique points
    private static void pickInitialCentroids()
    {
        List<Integer> picked = new ArrayList<Integer>();
        int numElements = dataList.size();
        
        //Generate k new clusters
        for (int i = 0; i < K; i++)
        {
            //Keep picking an index until a new one is found
            int index = -1;
            do {
                // index is [0, data.length)
                index = (int)(Math.random() * numElements);
            } while (picked.contains(index));
            picked.add(index);
            //This is now guaranteed to be a unique DataInterface
            DataInterface newCentroid = dataList.get(index);
            Cluster newCluster = new Cluster(newCentroid);
            clusters.add(newCluster);
        }
    }

    private static void assignData()
    {
        Iterator<Cluster> resetIter = clusters.iterator();
        while (resetIter.hasNext())
        {
            resetIter.next().data = new ArrayList<DataInterface>();
        }

        Iterator<DataInterface> diter = dataList.iterator();
        // Assign each DataInterface in the data list
        while (diter.hasNext())
        {
            DataInterface d = diter.next();
            if(d == null) 
                break;
            //System.out.println("TESTING DATA " + d.data);
            Iterator<Cluster> citer = clusters.iterator();
            Cluster closest = null;
            int minDistance = Integer.MAX_VALUE;
            // Check through each cluster to determine optimal
            while (citer.hasNext())
            {
                Cluster c = citer.next();
                int thisDistance = d.distance(c.centroid);
                if (thisDistance < minDistance) 
                {
                    closest = c;
                    minDistance = thisDistance;
                }
            }
            //System.out.println("closest cluster center is " + closest.centroid);
            // closest is now the optimal cluster; add d to it
            closest.data.add(d);
        }
        return;
    }

    private static void recalculateCentroids()
    {
        Iterator<Cluster> citer = clusters.iterator();
        // Assign each cluster a new DataInterface as its centroid
        while (citer.hasNext())
        {
            Cluster eachCluster = citer.next();
            eachCluster.lastCentroid = eachCluster.centroid;
            Iterator<DataInterface> diter = eachCluster.data.iterator();
            List<DataInterface> dataList = new ArrayList<DataInterface>();
            while (diter.hasNext())
                dataList.add(diter.next());
            if (dataList.size() != 0)
            {
                DataInterface average = dataList.get(0).average(dataList);
                System.out.println("Centroid changed to " + average + " from " + eachCluster.centroid);
                eachCluster.centroid = average;
            }
        }
    }

    private static List<List<DataInterface>> formList()
    {
        List<List<DataInterface>> finalList = new ArrayList<List<DataInterface>>();
        Iterator<Cluster> iter = clusters.iterator();
        while (iter.hasNext())
        {
            Cluster c = iter.next();
            List<DataInterface> newList = new ArrayList<DataInterface>();
            Iterator<DataInterface> diter = c.data.iterator();
            while (diter.hasNext())
            {
                newList.add(diter.next());
            }
            finalList.add(newList);
            System.out.println("Cluster has centroid " + c.centroid);
        }
        return finalList;
    }
    private static List<DataInterface> getPairData(String filename)
    {
        List<DataInterface> randomList = new ArrayList<DataInterface>();
        try{
            File f = new File(filename);
            Scanner s = new Scanner(f);
            while (s.hasNextInt()) {
                Pair p = new Pair(s.nextInt(), s.nextInt());
                randomList.add(p);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return randomList;
    }

    private static List<DataInterface> getDNAData(String filename)
    {
        List<DataInterface> randomList = new ArrayList<DataInterface>();
        try{
            File f = new File(filename);
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                DNA dna = new DNA(s.nextLine());
                randomList.add(dna);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return randomList;
    }
 
    public static void mergeData(Cluster[][] cs) {
        dataList.clear();
        for(int rank = 0; rank < cs.length; rank++) {
            System.out.println(Arrays.toString(cs[rank]));
            for(int i = 0; i < K; i++) {             
                clusters.get(i).data.addAll(cs[rank][i].data);
                dataList.addAll(cs[rank][i].data);
            }
        }
    }
}