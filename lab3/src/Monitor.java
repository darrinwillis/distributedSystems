import java.util.*;
import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
// This is a class to handle any administrative duties
// and system-level user interaction

class MonitorThread extends Thread{
    public MasterFileServerInterface server;
    public void run() {
        while (true)
        {
            try{
                System.out.println(server.monitorAll());
                Thread.sleep(10*1000);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
public class Monitor {
    private static MasterFileServerInterface masterServer;
    private static MonitorThread monitorThread;
    private static volatile boolean shouldMonitor;

    public static void main(String[] args)
    {
        if (checkConfig())
        {
            switch (args[0]){
                case "startMaster":
                {
                    try{
                        MasterServer server = new MasterServer();
                        masterServer = server;
                        server.start();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case "startSystem":
                {
                    generateScripts();
                    break;
                }
                case "startMonitor":
                {
                    MonitorThread thread = new MonitorThread();
                    monitorThread = thread;
                    thread.server = masterServer;
                    thread.run();
                    break;
                }
                case "stopMonitor":
                {
                }
                case "info":
                {
                    System.out.println(monitor());
                }
            }
        } else
            System.out.println("Invalid config file\nDelete or Fix");
        return;
    }
    
    private static String monitor()
    {
        String registryHost = null;
        int registryPort = 0;
        String masterServerRegistryKey = null;
        MasterFileServerInterface master = null;
        Registry rmiRegistry = null;

        Properties prop = Config.getProp();
        try{
            registryHost = prop.getProperty("REGISTRY_HOST");
            registryPort = Integer.parseInt(prop.getProperty("REGISTRY_PORT"));
            masterServerRegistryKey = 
                prop.getProperty("MASTER_SERVER_REGISTRY_KEY");
            rmiRegistry = 
                LocateRegistry.getRegistry(registryHost, registryPort);
            master = (MasterFileServerInterface)
                rmiRegistry.lookup(masterServerRegistryKey);
            return master.monitorAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

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
        String monitorFormatString = Config.getMonitorFormatString();

        // Master script
        try{
            File scriptFile = new File("scripts/masterScript.sh");
            PrintWriter pw = new PrintWriter(scriptFile);
            pw.println("#!/bin/bash");
            System.out.println("format string is " + masterFormatString);
            pw.format(masterFormatString, Config.getMasterAddress());
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Monitor script
        try{
            File scriptFile = new File("scripts/masterScript.sh");
            PrintWriter pw = new PrintWriter(scriptFile);
            pw.println("#!/bin/bash");
            System.out.println("format string is " + masterFormatString);
            pw.format(masterFormatString, Config.getMasterAddress());
            pw.close();

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
