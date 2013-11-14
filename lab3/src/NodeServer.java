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
    private BufferedReader stdin;
    private int mapSlots; // map + reduce + 1 = cores
    private int reduceSlots; 
    private TaskTracker taskTracker; // admin thread
    private List<TaskThread> taskThreads;

    public NodeServer() throws RemoteException
    {
        mapSlots = 10;
        reduceSlots = 5;
        taskThreads = new LinkedList<TaskThread>();
        taskTracker = new TaskTracker();
        taskTracker.start();
        isRunning = true;

        parseFile(configFileName);
        try{
            name = InetAddress.getLocalHost().getHostName();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public boolean isFull() throws RemoteException {
        return (taskThreads.size() >= mapSlots + reduceSlots); //TODO: Differentiate tasks
    }

    public void scheduleTask(Task task) throws RemoteException{
        System.out.println("Recieved Task " + task);
        TaskThread t = new TaskThread(task);
        t.start();
        taskThreads.add(t);
    }
    public class TaskTracker extends Thread {
        public void run() {
            TaskThread t;
            HashMap<String, List<String>> partialKvs;
            while(isRunning) {
                for(int i = 0; i < taskThreads.size(); i++){
                    try{
                        t = taskThreads.get(i);
                        t.join(10);
                        if(!t.isAlive()) {
                            taskThreads.remove(t);
                            if (t.task instanceof MapTask) {
                                partialKvs = slave.sort(t.task.getOutputFile());
                                masterServer.finishedMap(t.task,partialKvs);
                            } else { //reducetask
                                masterServer.finishedReduce(t.task);
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }           
            }
        }
    }

    public class TaskThread extends Thread {
        public Task task;
        
        public TaskThread(Task t) {
            task = t;
        }
        public void run() {
            if(task instanceof MapTask) 
                slave.doMap((MapTask)task);
            else
                slave.doReduce((ReduceTask)task);
        }
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
        try{
            rmiRegistry = LocateRegistry.getRegistry(registryHost, registryPort);

            masterServer = (MasterFileServerInterface)
                rmiRegistry.lookup(masterServerRegistryKey);

            //UnicastRemoteObject.exportObject(this, nodePort);
            masterServer.register(this, this.name);

            slave = new SlaveNode();
            stdin = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("start list or quit");
            while(isRunning) {
                String input = stdin.readLine();
                String[] args = input.split(" ");
                
                if (args[0].equals("start")) {
                    if (args.length < 4) {
                        System.out.println("Format: start (jobclass) (outputfile) (inputfiles)");
                        continue;
                    }
                    //Starting a new job
                    String jobName = args[1];
                    Job j = (Job) Class.forName(jobName).newInstance();

                    j.setOutput(args[2]);
                    List<String> inputFiles = new ArrayList<String>();
                                        
                    for (int i = 3; i < args.length; i++) {
                        inputFiles.add(args[i]);
                    }

                    j.setInput(inputFiles);
                                        
                    masterServer.newJob(j);
                    System.out.println(jobName + " added");
                } else if (input.equals("list")) {
                    System.out.println(slave.tasks);
                }
                else if (input.equals("quit")) {
                    stop();
                } else if (args[0].equals("add")) {
                    System.out.println("add recognized");
                    if (args.length < 2) {
                        System.out.println("Format: add (filename)");
                        continue;
                    }
                    String filename = args[1];
                    File testFile = new File(filename);
                    if (!testFile.exists())
                    {
                        System.out.println("File " + filename + " cannot be found.");
                    } else {
                        addNewFile(filename);
                    }

                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }       
    }

    // This allows a user to stop the server
    public void stop() throws RemoteException
    {
        //This should probably do other things before ending
        //the registry
        try{
            System.out.println("all items are" + Arrays.toString(rmiRegistry.list()));
            unexportObject(this, true);
            System.out.println("Node stopped");
            isRunning = false; 
        } catch (Exception e)
        {
            e.printStackTrace();
        }

      
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
        NodeServer server = new NodeServer();
        System.out.println("Starting Server");
        server.start();
    }
}
