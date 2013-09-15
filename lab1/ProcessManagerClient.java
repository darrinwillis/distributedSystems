/*
 * This is a client which acts as a medium between the server
 * and the process manager, allowing each to be blind to the other
 * run with ./startClient rmi://your.host.url.com
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

class ProcessManagerClient implements ProcessManagerClientInterface
{
    // Input your own URL as a command line arg for a different host
    private static final String DefaultMasterServerURL = "rmi://unix12.andrew.cmu.edu/processDelegationServer";
    private ProcessManager processManager;

    public ProcessManagerClient(String masterServerURL)
    {
        processManager = new ProcessManager();
        try
        {
            System.setSecurityManager (new RMISecurityManager());
            //This is what actually connects to the server
            MasterServerInterface processMaster = (MasterServerInterface) Naming.lookup(masterServerURL);

            // Allows the server to access this client
            System.out.println("Exporting Object");
            UnicastRemoteObject.exportObject(this,15440);

            // Gives the server a handle on this client
            System.out.println("Registering with Server");
            processMaster.register(this);
            System.out.println("Registered with Server");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main (String []args)
    {
        String MasterServerURL = DefaultMasterServerURL;
        if (args.length != 0)
            MasterServerURL = args[0];
        ProcessManagerClient client = new ProcessManagerClient(MasterServerURL);
    }

    //Forwards a process request onto the manager
    public List<String> getProcesses()
    {
        return processManager.getProcesses();
    }

    //Forwards a process request onto the manager
    public void setProcesses(List<String> processes)
    {
        if (processes != null && processes.size() > 0)
        {
            System.out.println("Attempting to set list of " + processes.size() + " processes");
            //This line allows the runtime class be String[]
            processManager.setProcesses(processes);
        }
        else
        {
            System.out.println("No Processes to do");
            
        }
    }
}
