import java.io.*;
import java.util.*;

public class Config {
    public static final String configFileName = "fileConfig.txt";
    public static Properties sharedProp;

    static{
        sharedProp = getProp();
    }


    public static Properties getProp()
    {
        Properties prop = new Properties();
        try{
            prop.load(new FileInputStream(configFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (checkConfigFile(prop))
            return prop;
        else
            return null;

    }

    public static boolean checkConfigFile() {
        Properties prop = getProp();
	if (prop != null)
	    return checkConfigFile(prop);
	else
	    return false;
    }

    public static boolean checkConfigFile(Properties prop)
    {
        return (prop.containsKey("REGISTRY_PORT") &&
            prop.containsKey("REGISTRY_HOST") &&
            prop.containsKey("MASTER_HOST") &&
            prop.containsKey("MASTER_SERVER_REGISTRY_KEY") &&
            prop.containsKey("BUF_SIZE") &&
            prop.containsKey("NODE_PORT") &&
            prop.containsKey("PIPE_PORT") &&
            prop.containsKey("CONNECTION_ATTEMPTS") &&
            prop.containsKey("FILE_PARTITION_SIZE") &&
            prop.containsKey("REPLICATION_FACTOR") &&
            prop.containsKey("NODE_SCRIPT_FORMAT") &&
            prop.containsKey("MASTER_SCRIPT_FORMAT") &&
            prop.containsKey("LOCAL_DIRECTORY") &&
            prop.containsKey("NODE0"));
    
    }

    public static Properties generateConfigFile() {
	Properties prop = new Properties();

        try{
            //Set default values for properties
            prop.setProperty("REGISTRY_HOST", "unix4.andrew.cmu.edu");
            prop.setProperty("MASTER_HOST", "unix4.andrew.cmu.edu");
            prop.setProperty("REGISTRY_PORT", "1099");
            prop.setProperty("MASTER_SERVER_REGISTRY_KEY", "masterServer");
            prop.setProperty("BUF_SIZE", "65536"); 
            prop.setProperty("NODE_PORT", "1098");
            prop.setProperty("PIPE_PORT", "1097");
            prop.setProperty("NODE0", "unix2.andrew.cmu.edu");
            prop.setProperty("NODE_SCRIPT_FORMAT", "ssh %s -f 'cd private/15440/distributedSystems/lab3/src/;\njava NodeServer &;exit 1;echo \"Exited\"'");
            prop.setProperty("MASTER_SCRIPT_FORMAT", "ssh %s -f 'cd private/15440/distributedSystems/lab3/src/;\njava Monitor startMaster&;exit 1;echo \"Exited\"'");
            prop.setProperty("MONITOR_SCRIPT_FORMAT", "ssh %s -f 'cd private/15440/distributedSystems/lab3/src/;\njava Monitor startMonitor&;exit 1;echo \"Exited\"'");
            prop.setProperty("FILE_PARTITION_SIZE", "1000");
            prop.setProperty("REPLICATION_FACTOR", "3");
            prop.setProperty("LOCAL_DIRECTORY", "/tmp/distributedFiles/");
            prop.setProperty("CONNECTION_ATTEMPTS", "20");

            //Save properties to config file
            prop.store(new FileOutputStream(configFileName), null);

            System.out.println("New config file created: " + configFileName);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return prop;
    }

    public static int getMaxAttempts() throws NumberFormatException {
        return Integer.parseInt(sharedProp.getProperty("CONNECTION_ATTEMPTS"));
    }

    public static int getBlockSize() throws NumberFormatException {
        return Integer.parseInt(sharedProp.getProperty("FILE_PARTITION_SIZE"));
    }

    public static int getReplicationFactor() throws NumberFormatException {
        return Integer.parseInt(sharedProp.getProperty("REPLICATION_FACTOR"));
    }

    public static String getLocalDirectory() {
        return sharedProp.getProperty("LOCAL_DIRECTORY");
    }

    public static String getMasterAddress() {
        return sharedProp.getProperty("MASTER_HOST");
    }

    public static String getNodePort() {
        return sharedProp.getProperty("NODE_PORT");
    }

    public static int getPipePort() {
        return Integer.parseInt(sharedProp.getProperty("PIPE_PORT"));
    }

    public static ArrayList<String> getNodeAddresses(){
        ArrayList<String> addresses = new ArrayList<String>();
	    String nodeKey = "NODE";
        int i = 0;
        //Load in all node addresses
        do{
            String nodeName = nodeKey + i;
            addresses.add(sharedProp.getProperty(nodeName));
            i++;
        } while(sharedProp.containsKey(nodeKey + i));
        return addresses;
    }

    public static String getNodeSSHFormatString(){
        String formatString = sharedProp.getProperty("NODE_SCRIPT_FORMAT");
        return formatString;
    }

    public static String getMasterSSHFormatString(){
        String formatString = sharedProp.getProperty("MASTER_SCRIPT_FORMAT");
        return formatString;
    }
    
    public static String getMonitorFormatString(){
        String formatString = sharedProp.getProperty("MONITOR_SCRIPT_FORMAT");
        return formatString;
    }
}
