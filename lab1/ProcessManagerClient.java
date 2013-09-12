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

            ProcessManager processManager = new ProcessManager();
            System.out.println("Exporting Object");
            UnicastRemoteObject.exportObject(this);
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
        return null;
    }

    public void setProcesses(List<String> processes)
    {
        if (processes != null)
        {
            System.out.println("Attempting to set list of " + processes.size() + " processes");
            //processManager.setProcesses((String[]) processes.toArray());
        }
        else
        {
            System.out.println("No Processes to do");
            
        }
    }
}
