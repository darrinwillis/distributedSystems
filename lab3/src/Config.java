import java.io.*;
import java.util.*;

public class Config {
    public static final String configFileName = "fileConfig.txt";

    public static boolean checkConfigFile() {
        Properties prop = new Properties();
        try{
            prop.load(new FileInputStream(configFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return (prop.containsKey("REGISTRY_PORT") &&
            prop.containsKey("REGISTRY_HOST") &&
            prop.containsKey("MASTER_SERVER_REGISTRY_KEY") &&
            prop.containsKey("BUF_SIZE") &&
            prop.containsKey("NODE_PORT") &&
            prop.containsKey("NODE0"));
    }

    public static Properties generateConfigFile() {
	Properties prop = new Properties();

        try{
            //Set default values for properties
            prop.setProperty("REGISTRY_HOST", "unix12.andrew.cmu.edu");
            prop.setProperty("REGISTRY_PORT", "1099");
            prop.setProperty("MASTER_SERVER_REGISTRY_KEY", "masterServer");
            prop.setProperty("BUF_SIZE", "65536"); 
            prop.setProperty("NODE_PORT", "1098");
            prop.setProperty("NODE0", "unix2.andrew.cmu.edu");

            //Save properties to config file
            prop.store(new FileOutputStream(configFileName), null);

            System.out.println("New config file created: " + configFileName);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return prop;
    }
}
