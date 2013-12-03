import java.util.*;

public class SequentialKMeans
{
    //This is a data struct to hold the centroid and data list
    private class Cluster{
        DataInterface centroid;
        List<DataUnit> data;

        public Cluster(DataInterface d)
        {
            this.centroid = d;
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
    private static int mu = 10;

    //Instace Variables
    private int k;
    private List<DataUnit> dataList;
    private List<Cluster> clusters;

    public SequentialKMeans(List<DataInterface> inputData, int k) 
        throws IllegalArgumentException
    {
        if (inputData.size() < k)
            throw new IllegalArgumentException();
        // Convert the input data into DataUnits
        List<DataUnit> units = new ArrayList<DataUnit>();
        Iterator<DataInterface> iter = inputData.iterator();
        while (iter.hasNext()) {
            units.add(new DataUnit(iter.next()));
        }
        this.dataList = units;
        this.k = k; 
    }

    public List<List<DataInterface>> calculateGroups()
    {
        //Pick initial centroids
        pickInitialCentroids();

        for (int i = 0; i < mu; i++)
        {
            System.out.println("Executing K means, iteration " + i);
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
            } while (!picked.contains(index));
            picked.add(index);
            //This is now guaranteed to be a unique DataUnit
            DataUnit newCentroid = dataList.get(index);
            Cluster newCluster = new Cluster(newCentroid.data);
            clusters.add(newCluster);
        }
    }

    private void assignData()
    {
        Iterator<DataUnit> diter = dataList.iterator();
        // Assign each DataInterface in the data list
        while (diter.hasNext())
        {
            DataUnit d = diter.next();
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
        }
        return finalList;
    }

    public static void main(String[] args)
    {
        List<DataInterface> theData = getData();
        SequentialKMeans worker = new SequentialKMeans(theData, K);
        List<List<DataInterface>> groups = worker.calculateGroups();
        System.out.println("The resultant groups were:\n" + groups);
    }

    private static List<DataInterface> getData()
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
        return list;
    }
}

