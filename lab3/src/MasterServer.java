import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class MasterServer extends UnicastRemoteObject implements MasterFileServerInterface
{
    private static Registry rmiRegistry;

    //Info from Config file
    private static final String configFileName = "fileConfig.txt";
    private static int registryPort;
    private static String serverRegistryKey;


    public MasterServer() throws RemoteException
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
            prop = generateConfigFile(); 
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try{
            //Load in all config properties
            this.registryPort = Integer.parseInt(prop.getProperty("registryPort"));
            this.serverRegistryKey = prop.getProperty("serverRegistryKey");
        
        } catch (NumberFormatException e) {
            System.out.println("Incorrectly formatted number " + e.getMessage());
        }
        return;
    }

    private Properties generateConfigFile()
    {
        Properties prop = new Properties();

        try{
            //Set default values for properties
            prop.setProperty("registryPort", "1099");
            prop.setProperty("serverRegistryKey", "masterServer");

            //Save properties to config file
            prop.store(new FileOutputStream(configFileName), null);

            System.out.println("New config file created: " + configFileName);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return prop;
    }

    // This allows the server to be reached by any nodes or users
    public void start() throws RemoteException
    {
        try{
            rmiRegistry = LocateRegistry.createRegistry(registryPort);
            rmiRegistry.bind(serverRegistryKey, this);
            System.out.println("Server started");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Registry port is: " + registryPort);
        System.out.println("serverRegistryKey is: " + serverRegistryKey);
    }

    // This allows a user to stop the server
    public void stop() throws RemoteException
    {
        //This should probably do other things before ending
        //the registry
        try{
            rmiRegistry.unbind(serverRegistryKey);
            unexportObject(this, true);
            unexportObject(rmiRegistry, true);
            System.out.println("Server stopped");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
   
    public void addNewFile(String filename) throws RemoteException
    {
        //Distribute the file among nodes
        return;
    }
    
    public static void main(String[] args) throws Exception
    {
        MasterServer server = new MasterServer();
        server.start();
    }
}