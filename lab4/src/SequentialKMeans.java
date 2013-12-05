import java.util.*;
import java.io.*;

public class SequentialKMeans
{
    //This is a data struct to hold the centroid and data list
    private class Cluster{
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
    }


    private static int K = 3;
    //maximum number of iterations to ensure completion
    private static int maxMu = 100;

    //Instace Variables
    private int k;
    private int distanceThreshold;
    private List<DataInterface> dataList;
    private List<Cluster> clusters;

    // Note, distance is a threshold for determining when to stop
    public SequentialKMeans(List<DataInterface> inputData, int k, int distance) 
        throws IllegalArgumentException
    {
        if (inputData.size() < k)
            throw new IllegalArgumentException();

        this.dataList = inputData;
        this.k = k;
        this.clusters = new ArrayList<Cluster>();
        this.distanceThreshold = distance;
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
            //This is now guaranteed to be a unique DataInterface
            DataInterface newCentroid = dataList.get(index);
            Cluster newCluster = new Cluster(newCentroid);
            clusters.add(newCluster);
        }
    }

    private void assignData()
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

    private void recalculateCentroids()
    {
        Iterator<Cluster> citer = clusters.iterator();
        // Assign each cluster a new DataInterface as its centroid
        while (citer.hasNext())
        {
            Cluster eachCluster = citer.next();
            eachCluster.lastCentroid = eachCluster.centroid;
            if (dataList.size() != 0)
            {
                DataInterface average = dataList.get(0).average(eachCluster.data);
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
       return (maxDistance <= distanceThreshold);
    }

    private List<List<DataInterface>> formList()
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

