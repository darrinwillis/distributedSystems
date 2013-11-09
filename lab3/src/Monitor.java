import java.util.*;
import java.io.*;
// This is a class to handle any administrative duties
// and system-level user interaction
public class Monitor {
    

    public static void main(String[] args)
    {
        if (checkConfig())
        {
            try{
                MasterServer server = new MasterServer();
                server.start();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
            System.out.println("Invalid config file\nDelete or Fix");

        return;
	
    }

    private static boolean checkConfig()
    {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(Config.configFileName));
        } catch (FileNotFoundException e) {
            Config.generateConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (! Config.checkConfigFile())
        {
            return false;
        }
        return true;
    }
}
