import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

final class Node
{
    String name;
    NodeFileServerInterface server;
    boolean isConnected;
    InetAddress address;
    int cores;
    ArrayList<FilePartition> files;
}

public class MasterServer extends UnicastRemoteObject implements MasterFileServerInterface
{
    private static Registry rmiRegistry;

    //Info from Config file
    private static final String configFileName = Config.configFileName;
    private static int registryPort;
    private static String masterServerRegistryKey;
    private static final String serverName = "MasterServer";

    //Instance variables
    // DFS information
    private volatile Hashtable<String, Node> nodes;
    private volatile Hashtable<Integer, Node> nodeMap;
    private List<DistributedFile> fileList;

    // MapReduce information
    private int currentJid;
    private int currentTid;
    private int currentNodeId;
    private Queue<Job> jobs;
    private Queue<Object[]> tasks; 
    private Queue<Node> nodeQueue;
    private ConcurrentMap<Integer,Integer> jobMapsDone; //jid, maps done
    private ConcurrentMap<Integer,Integer> jobReducesDone;
    private Map<Integer,List<FileServerInterface>> jobNodeList; 
    private Scheduler scheduler;
    private boolean isRunning;

    public MasterServer() throws RemoteException
    {
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        this.nodes = new Hashtable<String, Node>();
        this.nodeMap = new Hashtable<Integer, Node>();
        this.jobs = new LinkedList<Job>();
        this.tasks = new LinkedList<Object[]>();
        this.jobMapsDone = new ConcurrentHashMap<Integer,Integer>();
        this.jobReducesDone = new ConcurrentHashMap<Integer,Integer>();
        this.jobNodeList = new HashMap<Integer,List<FileServerInterface>>();
        this.fileList = new LinkedList<DistributedFile>();

        nodeQueue = new LinkedList<Node>(nodes.values());
        this.isRunning = true;
        scheduler = new Scheduler();
        scheduler.start();

        parseFile(configFileName);
        this.currentJid = 0;
        this.currentNodeId = 0;
    }
    
    public List<Object[]> locateFile(String fileName) {
        List<Object[]> nodeFiles = new LinkedList<Object[]>();
        for(Node node : nodeQueue) {
            for(FilePartition f : node.files) {
                if (f.getFileName().equals(fileName))
                    nodeFiles.add(new Object[]{node,f});
            }
        }
        return nodeFiles;
    }

    public void newJob(Job j) {
        try {
            System.out.println("Recieved Job " + j);
            int jid = currentJid++;
            j.setJid(jid);
            jobs.add(j);
            jobMapsDone.put(jid,0); 
            jobReducesDone.put(jid,0);
            jobNodeList.put(jid,new LinkedList<FileServerInterface>());
            List<String> inputs = j.getInput();
            for(int i = 0; i < inputs.size(); i++) { //TODO: variable mapTasks
                String name = inputs.get(i);
                List<Object[]> nodeList = locateFile(name);
                for(Object[] node_file : nodeList ) {
                    FilePartition f = (FilePartition)node_file[1]; 
                    Node node = (Node)node_file[0];
                    MapTask m = new MapTask(i,jid,f,j,jid + "out" + i); 
                    tasks.add(new Object[]{node,m}); //TODO: change Scheduler
                }
            }
            System.out.println("Finished Adding Map Tasks");
            System.out.println(tasks);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void scheduleReducers(Job j) {
        ReduceTask r;
        int tid = 0;
        
        for(int i = 0; i < j.getTotalReduces(); i++) {
            r = new ReduceTask(tid++,j.getJid(),null,j,j.getJid() + "part" + tid); 
            r.setNodeList(jobNodeList.get(j)); 
            r.setNodeId(i);
            tasks.add(new Object[]{null,r});
        }
    }

    public void scheduleFinalReduce(Job j) {
        FileInputStream in;
        RandomAccessFile out;
        String line;
        byte[] b;
        File f;
        try{
            out = new RandomAccessFile(j.getOutput(),"rws");
            for(int i = 0; i < j.getTotalReduces(); i++) {
                f = new File(j.getJid() + "part" + i);
                in = new FileInputStream(f);
                b = new byte[(int)f.length()];
                in.read(b);
                out.write(b);
                in.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
                
    public void finishedMap(Task t,String name) throws RemoteException{
        Job j = t.getJob();
        System.out.println(jobMapsDone);
        System.out.println(j);
        int maps = jobMapsDone.get(j.getJid()) + 1;
        jobMapsDone.put(j.getJid(),maps); 
        jobNodeList.get(j.getJid()).add(nodes.get(name).server); 
        if (maps >= j.getTotalMaps()) {
            scheduleReducers(j); //TODO: start reducing as maps finish
            //jobMapsDone.remove(j.getJid());
        }
    }
    public void finishedReduce(Task t) throws RemoteException{
        Job j = t.getJob();
        int reduces = jobReducesDone.get(j.getJid()) + 1;
        jobReducesDone.put(j.getJid(),reduces);
        if(reduces >= j.getTotalReduces()){
            scheduleFinalReduce(j);
            //jobReducesDone.remove(j.getJid());
        }
    }

    public class Scheduler extends Thread {

        public Scheduler() {
            System.out.println("Scheduler started");
        }

        public void run() {
            while(isRunning) {
                try{
                    while(nodeQueue.size() > 0 && tasks.size() > 0) {
                        System.out.println("Running");
                        Node n = nodeQueue.element();
                        Object[] objs = tasks.element();
                        Node _n = (Node)objs[0];
                        Task t = (Task)objs[1]; 
                        if(n.server.isFull() || n != _n) {
                            nodeQueue.add(nodeQueue.remove());
                        } else {
                            nodeQueue.remove();
                            System.out.println("Starting task on node " + n);
                            tasks.remove();
                            n.server.scheduleTask(t); 
                            System.out.println("Scheduled Task");
                            nodeQueue.add(n);
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Scheduler Stopped");
        }
    }   
        
            
    // This parses constants in the format
    // key=value from fileConfig.txt
    private void parseFile(String filename)
    {
        Properties prop = new Properties();
        try{
            prop.load(new FileInputStream(configFileName));
        } catch (FileNotFoundException e) {
            System.out.println("No config file found named: " + configFileName);
            prop = Config.generateConfigFile(); 
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //Check and assure file is formatted correctly
        if (! Config.checkConfigFile())
        {
            System.out.println("Invalid file config");
            return;
        }
        try{
            //Load in all config properties
            this.registryPort = Integer.parseInt(prop.getProperty("REGISTRY_PORT"));
            this.masterServerRegistryKey = prop.getProperty("MASTER_SERVER_REGISTRY_KEY");

            ArrayList<String> addresses = Config.getNodeAddresses();
            Iterator<String> iter = addresses.iterator();
            while (iter.hasNext())
                addNode(iter.next());

        } catch (NumberFormatException e) {
            System.out.println("Incorrectly formatted number " + e.getMessage());
        }
        return;
    }

    private void addNode(String address)
    {
        InetAddress newAddress = null;
        try{
            newAddress = InetAddress.getByName(address);
        } catch(Exception e) {
            e.printStackTrace();
        }
        Node newNode = new Node();
        newNode.name = address;
        newNode.address = newAddress;
        newNode.server = null;
        newNode.isConnected = false;
        newNode.files = new ArrayList<FilePartition>();
        this.nodes.put(address, newNode);
        this.nodeMap.put(currentNodeId++, newNode);
    }

    // This allows the server to be reached by any nodes or users
    public void start() throws RemoteException
    {
        // Get the old registry, or create a new one
        try{
            rmiRegistry = LocateRegistry.getRegistry(registryPort);
            System.out.println(Arrays.toString(rmiRegistry.list()));
            System.out.println("Registry server found");
        } catch (RemoteException e) {
            rmiRegistry = LocateRegistry.createRegistry(registryPort);
            System.out.println("Registry server created");
        }
    
        // Attempt to stop a previous instance of the server
        // TODO: This doesn't seem to work. 
        try{        
            MasterFileServerInterface oldServer = 
             (MasterFileServerInterface)rmiRegistry.lookup(masterServerRegistryKey);
            oldServer.stop();
            System.out.println("Stopped old server");
        } catch (NotBoundException|RemoteException e) {
            System.out.println("No old server found on old registry");
        }

        // Bind the new server
        try {
            rmiRegistry.bind(masterServerRegistryKey, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Registry port is: " + registryPort);
        System.out.println("masterServerRegistryKey is: " + masterServerRegistryKey);
        
    }

    // This allows a user to stop the server
    public void stop() throws RemoteException
    {
        System.out.println("Stopping master");
        //This should probably do other things before ending
        //the registry
        try{
            stopNodes();
            System.out.println("Unbinding");
            //rmiRegistry.unbind(masterServerRegistryKey);
            System.out.println("Unexporting self");
            unexportObject(this, true);
            System.out.println("Unexporting Registry");
            unexportObject(rmiRegistry, true);
            System.out.println("Server stopped");
            this.isRunning = false;

        } catch (Exception e)
        {
            System.out.println("Hit exception");
            e.printStackTrace();
        }
        System.exit(0);
    }
  
    private void stopNodes() throws RemoteException
    {
        Enumeration<Node> enumerate = this.nodes.elements();
        do {
            System.out.println("Stopping a node");
            Node each = enumerate.nextElement();
            NodeFileServerInterface eachServer = each.server;
            if (eachServer != null) {
                System.out.println(System.getProperty("sun.rmi.transport.tcp.responseTimeout"));
                System.out.println("Sending a stop message");
                try {
                    eachServer.stop();
                } catch (RemoteException e) {
                    System.out.println(each.name + " could not be reached to stop");
                }
                System.out.println("Sent a stop message");
            }
        } while (enumerate.hasMoreElements());
        System.out.println("Done stopping nodes");
        
    }

    public void register(NodeFileServerInterface server, String address)
    {
        System.out.println("Client connected");
        Node foundNode = this.nodes.get(address);
        if (foundNode == null){
            System.out.println("Unrecognized client; ignoring");
            return;
        }
        if (foundNode.isConnected)
        {
            boolean check = (server == foundNode.server);
            System.out.println(address + " checked in and the check is " + check);
        }
        else
        {
            System.out.println(address + " is now connected");
            foundNode.isConnected = true;
            foundNode.server = server;
            nodeQueue.add(foundNode);
        }
    }
   
    //Adds a new file to the distributed file system
    //if host is either null or 'this' it must be local to thismaster
    public void addNewFile(String filename, FileServerInterface host) throws RemoteException
    {
        //Distribute the file among nodes
        System.out.println("Adding file " + filename + " from host " + host);
        //Determine if file name is already taken
        for (DistributedFile eachFile : fileList)
        {
            if (filename.equals(eachFile.getFileName())) {
                System.out.println("File already exists with that name.");
                return;
            }
        }
        //Download the file from the remote host
        File newFile = new File(filename);
        if ((host == null) || (host == this))
            System.out.println("File is already local");
        else {
            System.out.println("File " + filename + " is at remote host; downloading");
            try{

                FileIO.download(host, newFile, newFile);
                System.out.println("Downloaded " + filename + " from remote host");
            } catch(IOException e) {
                System.out.println("Failed to download");
                e.printStackTrace();
            }
        }
        // Partition the files and send it to nodes
        this.partitionFile(newFile);
        System.out.println(filename + " has been added to the DFS");

        return;
    }

    private void partitionFile(File originalFile)
    {
        DistributedFile dfile = null;
        System.out.println("Making " + originalFile.getName() + 
            " a distributed file...");
        try{
            dfile = new DistributedFile(originalFile);
            //Add nodes to all of the parts of dfile
            dfile = allocateFile(dfile);
            //Send relevant partitions to all nodes
            commit(dfile);
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace(System.out);
        }
        return;
    }

    private DistributedFile allocateFile(DistributedFile dfile)
    {
        ListIterator<FilePartition[]> iter = dfile.getBlocks().listIterator();
        // Maps between nodes and size of replicas added to this
        // Node on this Distributed File
        Map<Node, Integer> tempSize = new HashMap<Node, Integer>();
        // TODO: THIS SHOULD BE DYNAMIC
        while (iter.hasNext()) {
            FilePartition[] block = iter.next();
            Set<Node> placedNodes = new HashSet<Node>();
            // Place each partition replica on a node
            for (int i = 0; i < block.length; i++)
            {
                FilePartition eachPartition = block[i];
                System.out.println("Performing on replica " + eachPartition.getFileName());
                // Place on node with fewest files
                Node optimalNode = null;
                int optSize = Integer.MAX_VALUE;
                // Looks through all nodes to find optimal location to place
                Enumeration<Node> nodeEnum = this.nodes.elements();
                while (nodeEnum.hasMoreElements())
                {
                    Node eachNode = nodeEnum.nextElement();
                    System.out.println("Considering " + eachNode.name);

                    // Determines viable nodes for this replica
                    if (eachNode.isConnected && (!placedNodes.contains(eachNode)))
                    {
                        System.out.println(eachNode.name + " is viable");
                        Integer tempNodeSize = tempSize.get(eachNode);
                        int thisSize = eachNode.files.size() + 
                            ((tempNodeSize == null) ? 0 : tempNodeSize);
                        if (thisSize < optSize) {
                            System.out.println(eachNode.name + " chosen for now");
                            //This is the new optimal node
                            optSize = thisSize;
                            optimalNode = eachNode;
                        }
                    }
                }
                // This can be null if the replication factor
                // is higher than the numberof online nodes
                if (optimalNode != null)
                    placedNodes.add(optimalNode);
                block[i].setLocation(optimalNode);
                // Update the TempSize for the chosen node
                Integer oldsize = tempSize.get(optimalNode);
                Integer newsize = ((oldsize == null) ? 0 : oldsize)
                    + block[i].getSize();
                tempSize.put(optimalNode, newsize);
                System.out.println("");
            }
        }
        return dfile;
    }

    private void commit(DistributedFile dfile) throws IOException
    {
        ListIterator<FilePartition[]> iter = dfile.getBlocks().listIterator();
        while (iter.hasNext()) {
            FilePartition[] eachBlock = iter.next();
            for (FilePartition eachPartition : eachBlock)
            {
                Node destination = eachPartition.getLocation();
                if (destination == null) {
                    System.out.println("File " + eachPartition.getFileName() + 
                        " has null location");
                    continue;
                }
                destination.files.add(eachPartition);
                FileServerInterface server = destination.server;
                File partitionFile = new File(eachPartition.getFileName());
                FileIO.upload(server, partitionFile, partitionFile);
            }
        }
        this.fileList.add(dfile);
        System.out.println("File Commited");
    }

    public String monitorFiles() throws RemoteException
    {
        String s = "###### Files ######\n";
        Iterator<DistributedFile> iter = this.fileList.listIterator();
        while (iter.hasNext()) {
            DistributedFile dfile = iter.next();
            s = s.concat("\n\t" + dfile.getFileName());
        }
        s = s.concat("\n\n###### EndFiles ######\n");
        return s;
    }

    //Prints out a status report of the whole system
    public String monitorAll() throws RemoteException
    {
        String s = "###### STATUS REPORT ######\n";
        Enumeration<Node> enumerate = this.nodes.elements();
        do {
            Node each = enumerate.nextElement();
            s = s.concat("\nReport for: " + each.name);
            s = s.concat(each.isConnected ? "\n\tConnected" : "\n\tNot Connected");
            s = s.concat("\n\tInetAddress: " + each.address);
            s = s.concat("\n\tFiles are:\n");
            ListIterator<FilePartition> iter = each.files.listIterator();
            while (iter.hasNext()) {
                FilePartition fp = iter.next();
                s = s + "\t\t" + fp.getFileName() + " part " + fp.getIndex() 
                    + " size: " + fp.getSize() + "\n";
            }
        } while (enumerate.hasMoreElements());
        s = s.concat("\n\n######  END  REPORT  ######");
        return s;
    }

    public OutputStream getOutputStream(File f) throws IOException {
        return new RMIOutputStream(new RMIOutputStreamImpl(new 
                                        FileOutputStream(f)));
    }
    public InputStream getInputStream(File f) throws IOException {
        return new RMIInputStream(new RMIInputStreamImpl(new 
                                        FileInputStream(f)));
    }
    
    public static void main(String[] args) throws Exception
    {
        MasterServer server = new MasterServer();
        // 10 sec rmi timeout
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        System.out.println(System.getProperty("sun.rmi.transport.tcp.responseTimeout"));
        server.start();
    }
}

