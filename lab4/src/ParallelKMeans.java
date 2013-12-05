import java.util.*;
import java.io.*;
import mpi.*;

// Implements a parallelized version of K means clustering using
// OpenMPI in java
public class ParallelKMeans
{
    private static final int PARTSIZE   = 0;
    private static final int CLUSTER    = 1;
    private static final int DATA       = 2;
    private static final int DONECHECK  = 3;

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

    private static int K = 3;
    // This is the maximum number of k means interations
    private static int mu = 100;

    //Instace Variables
    private static List<DataInterface> dataList;
    private static List<Cluster> clusters;
    // Instance rank of the local openmpi process (0 is master)
    private static int myrank;
    private static int distanceThreshold;
    private static int p; //num processors
    private static int partSize; //size of data sent to each processor

    public static void main(String[] args) 
    {
        try{
            // Needed for any MPI calls
            MPI.Init(args);

            // Parse cmd line input
            if (args.length != 4) {
                System.out.println("Run with java SequentialKMeans [points or dna] [K] [distanceThreshold] [inputfile]");
                return;
            }
            K = Integer.parseInt(args[1]);

            
            // unique process number (0 is master)
            myrank = MPI.COMM_WORLD.Rank();
            // p is the number of processors
            p = MPI.COMM_WORLD.Size();

            // If master, master code
            if(myrank == 0) 
                master(args);
            else 
                slave();

            // Necessary to close MPI connections
            MPI.Finalize();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void master(String[] args) throws MPIException{
        // Parse cmd line args
        distanceThreshold = Integer.parseInt(args[2]);

        String filename = args[3];
        //Select which data type to use
        List<DataInterface> theData = null;
        if (args[0].equals("points"))
            theData = getPairData(filename);
        else if (args[0].equals("dna"))
            theData = getDNAData(filename);


        // Start time (before any real computation)
        long startTime = System.currentTimeMillis();
        
        // Add data to local
        initialize(theData);

        int numWorkers = p-1;
        partSize = dataList.size()/(numWorkers);
        int remainder = dataList.size()-numWorkers*partSize;

        System.out.println("Num processors " + p);
        System.out.println("Part Size " + partSize);

        System.out.println("Picking initial centroids");
        pickInitialCentroids();
        // Here formList is just printing initial centroids
        formList();
        
        // MPI only takes arrays, so psize must be sent as a singleton array
        // in order to account for remainders, psize is 1 more than necessary
        // and will be decremented later when all remainder is accounted for
        int[] psize = {partSize + 1};
        // current location in total data array
        int index = 0;

        DataInterface[] dataArray = dataList.toArray(new DataInterface[0]);
        // slaves start at rank 1, so start there
        // send work to slaves
        for(int rank = 1; rank <= numWorkers; rank++) {
            if(remainder == 0){ 
                // All remainders have been allocated, thus psize is reduced
                psize[0]--;
                remainder--;
            } else 
                remainder--;
            // first the arraysize is sent, so slaves know the coming size
            MPI.COMM_WORLD.Send(psize,0,1,MPI.INT,rank,PARTSIZE);
            // Each slave should only know a portion of data          
            MPI.COMM_WORLD.Send(dataArray,index,psize[0],MPI.OBJECT,rank,DATA);
            // just sent psize datapoints, inc index
            index += psize[0];
        }
            
        int i;
        for(i = 0; i < mu; i++){
            System.out.println("Scheduling workers");
            
            // MPI necessitates arrays, instead of lists
            Cluster[] clusterArray = new Cluster[K];

            //clear existing cluster data, all we need are the centroids
            for(int c = 0; c < clusterArray.length; c++) {
                clusterArray[c] = new Cluster(clusters.get(c).centroid);
            }
            
            // slaves start at rank 1, so start there
            // send work to slaves
            for(int rank = 1; rank <= numWorkers; rank++) {
                // All slaves should know all clusters
                MPI.COMM_WORLD.Send(clusterArray,0,K,MPI.OBJECT,rank,CLUSTER);   
            }
            // Slaves now calculate; wait for their response of datapoint
            // to cluster allocation

            Cluster[][] results = new Cluster[numWorkers][K];
            // count number of slaves which have responded
            int counter = 0;
            while(counter < numWorkers) {
                //Recv(variable, offset, num_received, type, source, tag)
                Status s = MPI.COMM_WORLD.Recv(results[counter],0,K,MPI.OBJECT,MPI.ANY_SOURCE,CLUSTER);
                //System.out.println("Recieved " + Arrays.toString(results[counter]) + " from Slave");
                counter++;
            }
       
            System.out.println("All datapoint reallocation messages recieved");
            mergeData(results);
            // Local copy of all clusters is now complete
            // TODO: Parallelize this
            recalculateCentroids();

            boolean done = checkConvergence();
            boolean[] doneArray = {done};
            for(int rank = 1; rank <= numWorkers; rank++) {
                MPI.COMM_WORLD.Send(doneArray,0,1,MPI.BOOLEAN,rank,DONECHECK);
            }
            if (done)
                break;
        }
        System.out.println("Finished after " + i + " conversions");

        // Form output into a usable form
        formList();

        // Done with real computation; stop time
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time was " + (endTime-startTime) + "ms");
    }
    
    // This is what each non-master processor runs
    public static void slave() throws MPIException {
        // Singleton array
        boolean[] doneArray = {false};
        // This will be the size of the received dataportion
        int[] psize = new int[1];
        
        // Receive size of data portion
        Status s = MPI.COMM_WORLD.Recv(psize,0,1,MPI.INT,0,PARTSIZE);
        partSize = psize[0];
        
        // This slave's portion of the overall data
        DataInterface[] data = new DataInterface[partSize];
        Status s2 = MPI.COMM_WORLD.Recv(data,0,partSize,MPI.OBJECT,0,DATA);
        dataList = new ArrayList<DataInterface>(Arrays.asList(data));

        while(!doneArray[0]) {
            // Array of all clusters
            Cluster[] clusterArray = new Cluster[K];
            // Receive info
            Status s1 = MPI.COMM_WORLD.Recv(clusterArray,0,K,MPI.OBJECT,0,CLUSTER);
            // Assign each slave's portion of the data to nearest cluster
            assignData(clusterArray);
            // Package data to be sent
            //System.out.println("Slave " + myrank + " data " + Arrays.toString(data));
            //System.out.println("Slave " + myrank + " cluster " + Arrays.toString(clusterArray));
            MPI.COMM_WORLD.Send(clusterArray,0,K,MPI.OBJECT,0,CLUSTER);
            Status s3 = MPI.COMM_WORLD.Recv(doneArray,0,1,MPI.BOOLEAN,0,DONECHECK);
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

    // Add data
    public static void initialize(List<DataInterface> inputData) 
    {
        System.out.println("Making new Parallel K means object");
        // Assign total input data to master instance
        dataList = inputData;
        clusters = new ArrayList<Cluster>();
        System.out.println("Parallel K means object created");
        System.out.println("Data size " + dataList.size());
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

    // Assign each slave's portion of the data
    private static void assignData(Cluster[] clusterArray)
    {
        // TODO: use simple Arrays.asList;
        clusters = new ArrayList<Cluster>(Arrays.asList(clusterArray));
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
            List<DataInterface> data = eachCluster.data;
            if (data.size() != 0)
            {
                DataInterface average = dataList.get(0).average(data);
                //System.out.println("Centroid changed from " + eachCluster.centroid + " to " + average);
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
            finalList.add(c.data);
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
 
    //This takes all slave's versions of clusters, datapoints should be
    //simply added from each cluster 
    public static void mergeData(Cluster[][] cs) {
        // Reset all clusters
        Iterator<Cluster> resetIter = clusters.iterator();
        while (resetIter.hasNext())
        {
            resetIter.next().data.clear();
        }

        // For each slave
        for(int j = 0; j < cs.length; j++) {
            // For each slave's version of cluster K 
            for(int i = 0; i < K; i++) {             
                clusters.get(i).data.addAll(cs[j][i].data);
            }
        }
    }
}
