import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

class ProcessManagerClient implements ProcessManagerClientInterface
{
//    private static final String DefaultMasterServerURL = "rmi://unix12.andrew.cmu.edu/usr18/dswillis/private/15440/distributedSystems/lab1";

    private static final String DefaultMasterServerURL = "rmi://unix12.andrew.cmu.edu/processDelegationServer";
    private ProcessManager processManager;

    public ProcessManagerClient(String masterServerURL)
    {
        processManager = new ProcessManager();
            
        try
        {
            System.setSecurityManager (new RMISecurityManager());
            MasterServerInterface processMaster = (MasterServerInterface) Naming.lookup(masterServerURL);

            processManager = new ProcessManager();
            System.out.println("Exporting Object");
            UnicastRemoteObject.exportObject(this,15440);
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

    public List<String> getProcesses()
    {
        return processManager.getProcesses();
    }

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
