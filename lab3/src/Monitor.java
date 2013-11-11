import java.util.*;
import java.io.*;
import java.net.*;
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
            switch (args[0]){
                case "startMaster":
                {
                        try{
                            MasterServer server = new MasterServer();
                            server.start();
                            //MonitorThread thread = new MonitorThread();
                            //thread.server = server;
                            //thread.run();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                }
                case "startSystem":
                {
                    generateScripts();
                }
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

    private static void generateScripts()
    {
        ArrayList<String> addresses = Config.getNodeAddresses();
        Iterator<String> iter = addresses.iterator();
        String nodeFormatString = Config.getNodeSSHFormatString();
        String masterFormatString = Config.getMasterSSHFormatString();

        // Master script
        try{
            File scriptFile = new File("scripts/masterScript.sh");
            PrintWriter pw = new PrintWriter(scriptFile);
            pw.println("#!/bin/bash");
            pw.format(masterFormatString, Config.getMasterAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }

        int i = 0;
        // Node scripts
        while (iter.hasNext()){
            String eachAddress = iter.next();
            try{
                File scriptFile = new File("scripts/nodeScript" + i + ".sh");
                PrintWriter pw = new PrintWriter(scriptFile);
                pw.println("#!/bin/bash");
                pw.format(nodeFormatString, eachAddress);
                pw.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            i++;
        }
    }
    
}
