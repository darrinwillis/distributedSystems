import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class NodeServer extends UnicastRemoteObject implements NodeFileServerInterface
{
    private static Registry rmiRegistry;

    //Info from Config file
    private static final String configFileName = Config.configFileName;
    private String registryHost;
    private int registryPort;
    private String masterServerRegistryKey;
    private int nodePort;
    
    //instance variables
    private String name;
    private MasterFileServerInterface masterServer;
    private boolean isRunning;
    private SlaveNode slave;
    private int numCores;
    private int mapSlots; // map + reduce + 1 = cores
    private int reduceSlots; 
    private TaskTracker taskTracker; // admin thread
    private List<TaskThread> taskThreads;

    public NodeServer() throws RemoteException
    {
        try{
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
            numCores = Runtime.getRuntime().availableProcessors();
            float mapRatio = Config.getMappertoReducer();
            mapSlots = (int)(numCores * mapRatio);
            reduceSlots = numCores - mapSlots-1;
            taskThreads = new LinkedList<TaskThread>();
            taskTracker = new TaskTracker();
            taskTracker.start();
            isRunning = true;
            slave = new SlaveNode();

            parseFile(configFileName);
            name = InetAddress.getLocalHost().getHostName();
        } catch (Exception e){
            e.printStackTrace(System.out);
        }
    }
    public boolean isFull() throws RemoteException {
        return (taskThreads.size() >= mapSlots + reduceSlots); //TODO: Differentiate tasks
    }

    public void scheduleTask(Task task) throws RemoteException{
        TaskThread t = new TaskThread(task);
        t.start();
        taskThreads.add(t);
    }
    public class TaskTracker extends Thread {
        public void run() {
            TaskThread t;
            while(isRunning) {
                for(int i = 0; i < taskThreads.size(); i++){
                    try{
                        t = taskThreads.get(i);
                        t.join(10);
                        if(!t.isAlive()) {
                            taskThreads.remove(t);
                            if (t.task instanceof MapTask) {
                                masterServer.finishedMap((MapTask)t.task,name);
                            } else { //reducetask
                                masterServer.finishedReduce((ReduceTask)t.task,name);
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace(System.out);
                        continue;
                    }
                }           
            }
        }
    }
    public void print(String s){
        System.out.println(s);
    }
    public class TaskThread extends Thread {
        public Task task;
        
        public TaskThread(Task t) {
            task = t;
        }
        public void run() {
            if(task instanceof MapTask) {
    
                slave.doMap((MapTask)task);
            }else {
        
                ReduceTask r = (ReduceTask) task;
                List<Node> nodeList = r.getNodeList(); 
                List<String> inputFiles = new LinkedList<String>(); 
                String fileName = Config.getLocalDirectory() + r.getJob().getJid() + "reduce" + r.getNodeId();
                //System.out.println("Node " + name +  " Doing " + fileName);
                int counter = 0;
                try {
                    for(Node node : nodeList) {
                        try {
                            File f;
                            if(node.name.equals(name)){
                                //System.out.println(fileName + " is local");
                                f = new File(fileName);
                                f.renameTo(new File(fileName + "_" + counter));
                          
                            } else {
                                //System.out.println("Dowloading " + fileName + " from " + node.name);
                                FileIO.download(node.server,new File(fileName),new File(fileName + "_" + counter)); 
                            
                            }
                            inputFiles.add(fileName + "_" + counter); 
                            counter++;
                        } catch(FileNotFoundException e) {}
                    }
                }catch (Exception e) {
                    e.printStackTrace(System.out);
                }
                r.setInputFiles(inputFiles);
                slave.doReduce(r);
            }
        }
    }

    public Status getStatus() throws RemoteException
    {
        Status currentStatus = new Status();
        currentStatus.tasks = this.slave.tasks;
        currentStatus.mapSlots = this.mapSlots;
        currentStatus.reduceSlots = this.reduceSlots;
        return currentStatus;
    }
            
    
    // This parses constants in the format
    // key=value from fileConfig.txt
    private void parseFile(String filename)
    {
        Properties prop = Config.getProp();;

        //Check and assure file is formatted correctly
        if (! Config.checkConfigFile())
            {
                System.out.println("Invalid file config");
                return;
            }

        try{
            //Load in all config properties
            registryHost = prop.getProperty("REGISTRY_HOST");
            registryPort = Integer.parseInt(prop.getProperty("REGISTRY_PORT"));
            masterServerRegistryKey = prop.getProperty("MASTER_SERVER_REGISTRY_KEY");
            nodePort = Integer.parseInt(prop.getProperty("NODE_PORT"));

        } catch (NumberFormatException e) {
            System.out.println("Incorrectly formatted number " + e.getMessage());
        }
        return;
    }
        
    // This allows the server to be reached by any nodes or users
    public void start() throws RemoteException
    {
        boolean foundMaster = false;
        int attemptedConnections = 0;
        int max = Config.getMaxAttempts();
        while ((foundMaster == false) && (attemptedConnections < max)) {
            try{
                rmiRegistry = LocateRegistry.getRegistry(registryHost, registryPort);

                masterServer = (MasterFileServerInterface)
                    rmiRegistry.lookup(masterServerRegistryKey);

                //UnicastRemoteObject.exportObject(this, nodePort);
                masterServer.register(this, this.name, this.numCores);
                foundMaster = true;
            } catch (RemoteException|NotBoundException e)
                {
                    System.out.println("Encountered an issue setting up... try " 
                                       + attemptedConnections);
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException i)
                        {
                            e.printStackTrace();
                        }
                    attemptedConnections++;
                } 
        }
        if (!foundMaster)
            stop();
    }


    // This allows a user to stop the server
    public void stop() throws RemoteException
    {
        //This should probably do other things before ending
        //the registry
        try{
            isRunning = false;
            unexportObject(this, true);
            System.out.println("Node stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            Thread.sleep(5);
        } catch (InterruptedException e) {
        }
        System.exit(0);
      
    }
   
    private void addNewFile(String filename) throws RemoteException
    {
        if (this.masterServer != null)
            {
                try {
                    System.out.println("Adding file " + filename + " to master...");
                    masterServer.addNewFile(filename, this);
                    System.out.println("File added to master");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        //Distribute the file among nodes
        System.out.println("adding file " + filename);

        return;
    }

    //Delete all local files
    public void cleanLocalDirectory() throws RemoteException
    {
        File localDir = new File(Config.getLocalDirectory());
        for (File f:localDir.listFiles()) f.delete();
    }

    public OutputStream getOutputStream(File f) throws IOException {
        f.getParentFile().mkdirs();
        return new RMIOutputStream(new RMIOutputStreamImpl(new 
                                                           FileOutputStream(f)));
    }
    public InputStream getInputStream(File f) throws IOException {
        return new RMIInputStream(new RMIInputStreamImpl(new 
                                                         FileInputStream(f)));
    }
    
    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting Server");
        if (Config.checkConfigFile()) {
            NodeServer server = new NodeServer();
            server.start();
        }
        else
            System.out.println("invalid config file");
    }
}
