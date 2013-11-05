import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class NodeServer extends UnicastRemoteObject implements FileServerInterface
{
    private static Registry rmiRegistry;

    //Info from Config file
    private static final String configFileName = Config.configFileName;
    private int registryPort;
    private String masterServerRegistryKey;
    private int nodePort;

    //instance variables
    private MasterFileServerInterface masterServer;


    public NodeServer() throws RemoteException
    {
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
        } catch (IOException e) {
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
            registryPort = Integer.parseInt(prop.getProperty("registryPort"));
            masterServerRegistryKey = prop.getProperty("masterServerRegistryKey");
            nodePort = Integer.parseInt(prop.getProperty("nodePort"));

        } catch (NumberFormatException e) {
            System.out.println("Incorrectly formatted number " + e.getMessage());
        }
        return;
    }


    // This allows the server to be reached by any nodes or users
    public void start() throws RemoteException
    {
        try{
            rmiRegistry = LocateRegistry.getRegistry(registryPort);
            masterServer = (MasterFileServerInterface)
                rmiRegistry.lookup(masterServerRegistryKey);
            //UnicastRemoteObject.exportObject(this, nodePort);
            System.out.println("Node started");
            masterServer.register(this);
            System.out.println("Registered with master");
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
    }
}
