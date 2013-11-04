import java.io.*;
import java.util.*;

public class Config {
    private static final String configFileName = "fileConfig.txt";

    public static Properties generateConfigFile() {
	Properties prop = new Properties();

        try{
            //Set default values for properties
            prop.setProperty("registryPort", "1099");
            prop.setProperty("serverRegistryKey", "masterServer");
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