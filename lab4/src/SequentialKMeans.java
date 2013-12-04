import java.util.*;
import java.io.*;

public class SequentialKMeans
{
    //This is a data struct to hold the centroid and data list
    private class Cluster{
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

    private class DataUnit{
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
    private int k;
    private List<DataUnit> dataList;
    private List<Cluster> clusters;

    public SequentialKMeans(List<DataInterface> inputData, int k) 
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
        System.out.println("Sequential K means object created");
    }

    public List<List<DataInterface>> calculateGroups()
    {
        System.out.println("Picking initial centroids");
        pickInitialCentroids();

        for (int i = 0; i < mu; i++)
        {
            //Assign all data to centroids
            assignData();
            
            //Recalculate Centroids
            recalculateCentroids();
        }
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
        if (args.length != 3) {
            System.out.println("Run with java SequentialKMeans [points or dna] [K] [inputfile]");
            return;
        }
        //Start clock before performing any computation
        long startTime = System.currentTimeMillis();
        K = Integer.parseInt(args[1]); 

        //Select which data type to use
        List<DataInterface> theData = null;
        if (args[0].equals("points"))
            theData = getPairData(args);
        else if (args[0].equals("dna"))
            theData = getDNAData(args);

        SequentialKMeans worker = new SequentialKMeans(theData, K);
        System.out.println("Starting calculation with K:" + K + " mu:" + mu);
        List<List<DataInterface>> groups = worker.calculateGroups();
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time was " + (endTime-startTime) + "ms");
    }

    private static List<DataInterface> getPairData(String[] args)
    {
        List<DataInterface> randomList = new ArrayList<DataInterface>();
        try{
            File f = new File(args[2]);
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

    private static List<DataInterface> getDNAData(String[] args)
    {
        List<DataInterface> randomList = new ArrayList<DataInterface>();
        try{
            File f = new File(args[2]);
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

