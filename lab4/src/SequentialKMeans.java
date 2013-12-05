import java.util.*;
import java.io.*;

public class SequentialKMeans
{
    //This is a data struct to hold the centroid and data list
    private class Cluster{
        DataInterface centroid;
        DataInterface lastCentroid;
        List<DataUnit> data;

        public Cluster(DataInterface d)
        {
            this.centroid = d;
            this.lastCentroid = null;
            this.data = new ArrayList<DataUnit>();
        }

        public Cluster(DataInterface d, List<DataUnit> givenData)
        {
            this.centroid = d;
            this.lastCentroid = null;
            this.data = givenData;
        }
        public String toString() {
            return (data.toString());
        }
    }

    private class DataUnit{
        DataInterface data;
        Cluster cluster;
        public DataUnit(DataInterface d) {
            data = d;
        }
    }

    private static int K = 3;
    //maximum number of iterations to ensure completion
    private static int maxMu = 100;

    //Instace Variables
    private int k;
    private int distanceThreshold;
    private List<DataUnit> dataList;
    private List<Cluster> clusters;

    // Note, distance is a threshold for determining when to stop
    public SequentialKMeans(List<DataInterface> inputData, int k, int distance) 
        throws IllegalArgumentException
    {
        System.out.println("Making new Sequential K means object");
        if (inputData.size() < k)
            throw new IllegalArgumentException();

        // Convert the input data into DataUnits
        List<DataUnit> units = new ArrayList<DataUnit>();
        Iterator<DataInterface> iter = inputData.iterator();
        System.out.println("Adding data units");
        while (iter.hasNext()) {
            units.add(new DataUnit(iter.next()));
        }
        
        this.dataList = units;
        this.k = k;
        this.clusters = new ArrayList<Cluster>();
        this.distanceThreshold = distance;
        System.out.println("Sequential K means object created");
    }

    public List<List<DataInterface>> calculateGroups()
    {
        System.out.println("Picking initial centroids");
        pickInitialCentroids();

        int i;
        for (i = 0; i < maxMu; i++)
        {
            //Assign all data to centroids
            assignData();
            
            //Recalculate Centroids
            recalculateCentroids();

            if (checkConvergence())
                break;
        }
        System.out.println("Converged after " + (i+1) + " iterations");
        return formList();
    }

    // This random selects k unique points
    private void pickInitialCentroids()
    {
        List<Integer> picked = new ArrayList<Integer>();
        int numElements = dataList.size();
        
        //Generate k new clusters
        for (int i = 0; i < k; i++)
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

    private void assignData()
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

    private void recalculateCentroids()
    {
        Iterator<Cluster> citer = clusters.iterator();
        // Assign each cluster a new DataInterface as its centroid
        while (citer.hasNext())
        {
            Cluster eachCluster = citer.next();
            eachCluster.lastCentroid = eachCluster.centroid;
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

    private boolean checkConvergence()
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

    private List<List<DataInterface>> formList()
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

    public static void main(String[] args)
    {
        if (args.length != 4) {
            System.out.println("Run with java SequentialKMeans [points or dna] [K] [distanceThreshold] [inputfile]");
            return;
        }
        //Start clock before performing any computation
        long startTime = System.currentTimeMillis();
        K = Integer.parseInt(args[1]);
        int distanceT = Integer.parseInt(args[2]);

        String filename = args[3];
        //Select which data type to use
        List<DataInterface> theData = null;
        if (args[0].equals("points"))
            theData = getPairData(filename);
        else if (args[0].equals("dna"))
            theData = getDNAData(filename);


        SequentialKMeans worker = new SequentialKMeans(theData, K, distanceT);
        System.out.println("Starting calculation with K:" + K);
        List<List<DataInterface>> groups = worker.calculateGroups();
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time was " + (endTime-startTime) + "ms");
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
}

