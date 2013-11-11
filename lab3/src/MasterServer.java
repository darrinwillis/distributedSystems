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

    public MasterServer() throws RemoteException
    {
        this.nodes = new Hashtable<String, Node>();
        parseFile(configFileName);
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
        //This should probably do other things before ending
        //the registry
        try{
            rmiRegistry.unbind(masterServerRegistryKey);
            unexportObject(this, true);
            unexportObject(rmiRegistry, true);
            System.out.println("Server stopped");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
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
        // Partition the files and send it to nodes
        partitionFile(newFile);

        return;
    }

    public String monitorAll() throws RemoteException
    {
        String s = "###### STATUS REPORT ######\n";
        Enumeration<Node> enumerate = this.nodes.elements();
        do {
            Node each = enumerate.nextElement();
            System.out.println("The keys are:\n" + this.nodes);
            s = s.concat("\nReport for: " + each.name);
            s = s.concat(each.isConnected ? "\n\tConnected" : "\n\tNot Connected");
            s = s.concat("\n\tInetAddress: " + each.address);
        } while (enumerate.hasMoreElements());
        s = s.concat("\n\n######  END  REPORT  ######");
        return s;
    }

    private void partitionFile(File originalFile)
    {
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
        MasterServer server = new MasterServer();
        server.start();
    }
}

