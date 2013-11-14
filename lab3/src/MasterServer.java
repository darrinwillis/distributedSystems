import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.net.*;

final class Node
{
    String name;
    FileServerInterface server;
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
    private volatile Hashtable<String, Node> nodes;
    private int currentJid;

    public MasterServer() throws RemoteException
    {
        this.nodes = new Hashtable<String, Node>();
        parseFile(configFileName);
	    this.currentJid = 0;
    }
    
    public void newJob(Job j) {
	    int jid = currentJid++;
	
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
    }

    // This allows the server to be reached by any nodes or users
    public void start() throws RemoteException
    {
        // Get the old registry, or create a new one
        try{
            rmiRegistry = LocateRegistry.getRegistry(registryPort);
            rmiRegistry.list();
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
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
  
    private void stopNodes() throws RemoteException
    {
        Enumeration<Node> enumerate = this.nodes.elements();
        do {
            System.out.println("Stopping a node");
            Node each = enumerate.nextElement();
            FileServerInterface eachServer = each.server;
            if (eachServer != null) {
                System.out.println("Sending a stop message");
                eachServer.stop();
                System.out.println("Sent a stop message");
            }
        } while (enumerate.hasMoreElements());
        System.out.println("Done stopping nodes");
        
    }

    public void register(FileServerInterface server, String address)
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
        }
    }
   
    //Adds a new file to the distributed file system
    //if host is either null or 'this' it must be local to thismaster
    public void addNewFile(String filename, FileServerInterface host) throws RemoteException
    {
        //Distribute the file among nodes
        System.out.println("adding file " + filename + " from host " + host);
        //Download the file from the remote host
        File newFile = new File(filename);
        if ((host == null) || (host == this))
            System.out.println("File is already local");
        else {
            System.out.println("File " + filename + " is at remote host; downloading");
            try{

                FileIO.download(host, newFile, newFile);
            } catch(IOException e) {
                System.out.println("Failed to download");
                e.printStackTrace();
            }
        }
        System.out.println("downloaded " + filename + " from remote host");
        // Partition the files and send it to nodes
        partitionFile(newFile);

        return;
    }

    private void partitionFile(File originalFile)
    {
        DistributedFile dfile = null;
        try{
            dfile = new DistributedFile(originalFile);
            //Add nodes to all of the parts of dfile
            dfile = allocateFile(dfile);
            //Send relevant partitions to all nodes
            commit(dfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    private DistributedFile allocateFile(DistributedFile dfile)
    {
        ListIterator<FilePartition[]> iter = dfile.getBlocks().listIterator();
        Enumeration<Node> nodeEnum = this.nodes.elements();
        // TODO: THIS SHOULD BE DYNAMIC
        Node singleNode = nodeEnum.nextElement();
        while (iter.hasNext()) {
            FilePartition[] block = iter.next();
            // TODO: THIS SHOULD BE DYNAMIC
            block[0].location = singleNode;
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
                Node destination = eachPartition.location;
                destination.files.add(eachPartition);
                FileServerInterface server = destination.server;
                File partitionFile = new File(eachPartition.getFileName());
                FileIO.upload(server, partitionFile, partitionFile);
            }
        }
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
                s = s + "\n\t\t" + fp.getFileName() + " part " + fp.getIndex() 
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
        server.start();
    }
}

