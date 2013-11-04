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
        return (prop.containsKey("registryPort") &&
            prop.containsKey("masterServerRegistryKey") &&
            prop.containsKey("BUF_SIZE"));
    }

    public static Properties generateConfigFile() {
	Properties prop = new Properties();

        try{
            //Set default values for properties
            prop.setProperty("registryPort", "1099");
            prop.setProperty("masterServerRegistryKey", "masterServer");
            prop.setProperty("BUF_SIZE", "65536"); 

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
