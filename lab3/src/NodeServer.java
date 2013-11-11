import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class NodeServer extends UnicastRemoteObject implements FileServerInterface
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


    public NodeServer() throws RemoteException
    {
        parseFile(configFileName);
        try{
            name = InetAddress.getLocalHost().getHostName();
        } catch (Exception e)
        {
            e.printStackTrace();
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
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
   
    public void addNewFile(String filename) throws RemoteException
    {
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
        server.start();
        Thread.sleep(5*1000);
        //server.stop();
    }
}
