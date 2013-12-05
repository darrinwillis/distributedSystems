import java.util.*;
import java.io.*;
import mpi.*;

public class ParallelKMeans
{
    //This is a data struct to hold the centroid and data list
    private static class Cluster{
        DataInterface centroid;
        List<DataUnit> data;

        public Cluster(DataInterface d)
        {
            this.centroid = d;
            this.data = new ArrayList<DataUnit>();
        }

        public Cluster(DataInterface d, List<DataUnit> givenData)
        {
            this.centroid = d;
            this.data = givenData;
        }
    }

    private static class DataUnit{
        DataInterface data;
        Cluster cluster;
        public DataUnit(DataInterface d) {
            data = d;
        }
    }


    //TODO: Take this as a commandline argument
    private static int K = 3;
    //TODO: Dynamically determine mu
    private static int mu = 20;

    //Instace Variables
    private static List<DataUnit> dataList;
    private static List<Cluster> clusters;
    private static int myrank;
    private static int p; //num processors
    private static int partSize; //size of data sent to each processor

    public static void main(String[] args) 
    {
        try{
            MPI.Init(args);
            long startTime = System.currentTimeMillis();;
            myrank = MPI.COMM_WORLD.Rank();
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
        List<DataInterface> theData = getData(args);
        initialize(theData);

        p = MPI.COMM_WORLD.Size();
        partSize = dataList.size()/p;

        System.out.println("Picking initial centroids");
        pickInitialCentroids();

        System.out.println("Scheduling workers");
        DataUnit[] dataArray = (DataUnit[])dataList.toArray();
        Cluster[] clusterArray = (Cluster[])clusters.toArray();
        
        for(int rank = 1; rank < p - 1; rank++) {//partition and send work to slaves
            MPI.COMM_WORLD.Send(clusterArray,0,K,MPI.OBJECT,rank,0); //clusters
            MPI.COMM_WORLD.Send(dataArray,(rank-1)*partSize,partSize,MPI.OBJECT,rank,1); //datapoints
        }
        MPI.COMM_WORLD.Send(dataArray,(p-1)*partSize,dataList.size()-(p-1)*partSize,MPI.OBJECT,p-1,1); //send the remainder


        DataUnit[][] results = new DataUnit[p][partSize];
        int counter = 0;
        while(counter < p) { //receiving output
            Status s = MPI.COMM_WORLD.Recv(results[counter],0,partSize,MPI.OBJECT,MPI.ANY_SOURCE,2);
            counter++;
        }
       
        mergeData(results);
        recalculateCentroids();
    }
    
    public static void slave() throws MPIException {
        Cluster[] cluster = new Cluster[K];
        DataUnit[] data = new DataUnit[partSize];

        Status s1 =  MPI.COMM_WORLD.Recv(cluster,0,K,MPI.OBJECT,0,0);
        Status s2 = MPI.COMM_WORLD.Recv(data,myrank*partSize,partSize,MPI.OBJECT,0,1);
        
        calculateGroups(cluster,data);

        MPI.COMM_WORLD.Send(dataList.toArray(),0,partSize,MPI.OBJECT,0,2);     
    }

    public static void initialize(List<DataInterface> inputData) 
    {
        System.out.println("Making new Parallel K means object");
        // Convert the input data into DataUnits
        List<DataUnit> units = new ArrayList<DataUnit>();
        Iterator<DataInterface> iter = inputData.iterator();
        System.out.println("Adding data units");
        while (iter.hasNext()) {
            units.add(new DataUnit(iter.next()));
        }
        dataList = units;
        clusters = new ArrayList<Cluster>();
        partSize = units.size()/p; 
        System.out.println("Parallel K means object created");
    }

    public static void calculateGroups(Cluster[] c, DataUnit[] d)
    {
        dataList = new ArrayList<DataUnit>(Arrays.asList(d));
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
            //This is now guaranteed to be a unique DataUnit
            DataUnit newCentroid = dataList.get(index);
            Cluster newCluster = new Cluster(newCentroid.data);
            clusters.add(newCluster);
        }
    }

    private static void assignData()
    {
        Iterator<Cluster> resetIter = clusters.iterator();
        while (resetIter.hasNext())
        {
            resetIter.next().data = new ArrayList<DataUnit>();
        }

        Iterator<DataUnit> diter = dataList.iterator();
        // Assign each DataInterface in the data list
        while (diter.hasNext())
        {
            DataUnit d = diter.next();
            //System.out.println("TESTING DATA " + d.data);
            Iterator<Cluster> citer = clusters.iterator();
            Cluster closest = null;
            int minDistance = Integer.MAX_VALUE;
            // Check through each cluster to determine optimal
            while (citer.hasNext())
            {
                Cluster c = citer.next();
                int thisDistance = d.data.distance(c.centroid);
                if (thisDistance < minDistance) 
                {
                    closest = c;
                    minDistance = thisDistance;
                }
            }
            //System.out.println("closest cluster center is " + closest.centroid);
            // closest is now the optimal cluster; add d to it
            d.cluster = closest;
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
            Iterator<DataUnit> diter = eachCluster.data.iterator();
            List<DataInterface> dataList = new ArrayList<DataInterface>();
            while (diter.hasNext())
                dataList.add(diter.next().data);
            if (dataList.size() != 0)
            {
                DataInterface average = dataList.get(0).average(dataList);
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
            Iterator<DataUnit> diter = c.data.iterator();
            while (diter.hasNext())
            {
                newList.add(diter.next().data);
            }
            finalList.add(newList);
            System.out.println("Cluster has centroid " + c.centroid);
        }
        return finalList;
    }
    
    private static List<DataInterface> getData(String[] args)
    {
        Pair a1 = new Pair(5,5);
        Pair b1 = new Pair(6,6);
        Pair a2 = new Pair(-5,5);
        Pair b2 = new Pair(-6,6);
        Pair a3 = new Pair(-5,-5);
        Pair b3 = new Pair(-6,-6);
        Pair a4 = new Pair(5,-5);
        Pair b4 = new Pair(6,-6);
        List<DataInterface> list = new ArrayList<DataInterface>();
        list.add(a1);
        list.add(a2);
        list.add(a3);
        list.add(a4);
        list.add(b1);
        list.add(b2);
        list.add(b3);
        list.add(b4);

        if (args.length == 0)
            return list;

        // File input
        List<DataInterface> randomList = new ArrayList<DataInterface>();
        try{
            File f = new File(args[0]);
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

    public static void mergeData(DataUnit[][] data) {
        dataList.clear();
        for(int rank = 1; rank < p; rank++) {
            for(int i = 0; i < partSize; i++) {
                dataList.add(data[rank][i]);
            }
        }   
    }
}