import java.util.*;
import java.io.*;
// This is a class to handle any administrative duties
// and system-level user interaction
class MonitorThread extends Thread{
    public MasterServer server;
    public void run() {
        while (true)
        {
            System.out.println(server.monitorAll());
            try{
                Thread.sleep(10*1000);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
public class Monitor {
    

    public static void main(String[] args)
    {
        if (checkConfig())
        {
            try{
                MasterServer server = new MasterServer();
                server.start();
                MonitorThread thread = new MonitorThread();
                thread.server = server;
                thread.run();
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
