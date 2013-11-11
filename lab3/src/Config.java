import java.io.*;
import java.util.*;

public class Config {
    public static final String configFileName = "fileConfig.txt";

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
        return (prop.containsKey("REGISTRY_PORT") &&
            prop.containsKey("REGISTRY_HOST") &&
            prop.containsKey("MASTER_HOST") &&
            prop.containsKey("MASTER_SERVER_REGISTRY_KEY") &&
            prop.containsKey("BUF_SIZE") &&
            prop.containsKey("NODE_PORT") &&
            prop.containsKey("NODE_SCRIPT_FORMAT") &&
            prop.containsKey("MASTER_SCRIPT_FORMAT") &&
            prop.containsKey("NODE0"));
    }

    public static boolean checkConfigFile(Properties prop)
    {
        return (prop.containsKey("REGISTRY_PORT") &&
            prop.containsKey("REGISTRY_HOST") &&
            prop.containsKey("MASTER_HOST") &&
            prop.containsKey("MASTER_SERVER_REGISTRY_KEY") &&
            prop.containsKey("BUF_SIZE") &&
            prop.containsKey("NODE_PORT") &&
            prop.containsKey("NODE_SCRIPT_FORMAT") &&
            prop.containsKey("MASTER_SCRIPT_FORMAT") &&
            prop.containsKey("NODE0"));
    
    }

    public static Properties generateConfigFile() {
	Properties prop = new Properties();

        try{
            //Set default values for properties
            prop.setProperty("REGISTRY_HOST", "unix12.andrew.cmu.edu");
            prop.setProperty("MASTER_HOST", "unix12.andrew.cmu.edu");
            prop.setProperty("REGISTRY_PORT", "1099");
            prop.setProperty("MASTER_SERVER_REGISTRY_KEY", "masterServer");
            prop.setProperty("BUF_SIZE", "65536"); 
            prop.setProperty("NODE_PORT", "1098");
            prop.setProperty("NODE0", "unix2.andrew.cmu.edu");
            prop.setProperty("NODE_SCRIPT_FORMAT", "ssh %s -f 'cd private/15440/distributedSystems/lab3/src/;\njava NodeServer &;exit 1;echo \"Exited\"'");
            prop.setProperty("MASTER_SCRIPT_FORMAT", "ssh %s -f 'cd private/15440/distributedSystems/lab3/src/;\njava Monitor startMaster&;exit 1;echo \"Exited\"'");
            prop.setProperty("MONITOR_SCRIPT_FORMAT", "ssh %s -f 'cd private/15440/distributedSystems/lab3/src/;\njava Monitor startMonitor&;exit 1;echo \"Exited\"'");

            //Save properties to config file
            prop.store(new FileOutputStream(configFileName), null);

            System.out.println("New config file created: " + configFileName);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return prop;
    }

    public static String getMasterAddress() {
        Properties prop = getProp();
        return prop.getProperty("MASTER_HOST");
    }

    public static ArrayList<String> getNodeAddresses(){
        Properties prop = getProp();
        ArrayList<String> addresses = new ArrayList<String>();
	    String nodeKey = "NODE";
        int i = 0;
        //Load in all node addresses
        do{
            String nodeName = nodeKey + i;
            addresses.add(prop.getProperty(nodeName));
            i++;
        } while(prop.containsKey(nodeKey + i));
        return addresses;
    }

    public static String getNodeSSHFormatString(){
        Properties prop = getProp();
        String formatString = prop.getProperty("NODE_SCRIPT_FORMAT");
        return formatString;
    }

    public static String getMasterSSHFormatString(){
        Properties prop = getProp();
        String formatString = prop.getProperty("MASTER_SCRIPT_FORMAT");
        return formatString;
    }
    
    public static String getMonitorFormatString(){
        Properties prop = getProp();
        String formatString = prop.getProperty("MONITOR_SCRIPT_FORMAT");
        return formatString;
    }
}
