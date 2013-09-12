import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

class ProcessManagerClient implements ProcessManagerClientInterface
{
//    private static final String DefaultMasterServerURL = "rmi://unix12.andrew.cmu.edu/usr18/dswillis/private/15440/distributedSystems/lab1";

    private static final String DefaultMasterServerURL = "rmi://unix12.andrew.cmu.edu/processDelegationServer";
    public ProcessManagerClient()
    {
    }

    public static void main (String []args)
    {
        String MasterServerURL = DefaultMasterServerURL;
        if (args.length != 0)
            MasterServerURL = args[0];
        
        ProcessManagerClient client = new ProcessManagerClient();

        try
        {
            System.setSecurityManager (new RMISecurityManager());
            MasterServerInterface processMaster = (MasterServerInterface) Naming.lookup(DefaultMasterServerURL);

            ProcessManager processManager = new ProcessManager();
            processMaster.register(client);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<MigratableProcess> getProcesses()
    {
        return null;
    }

    public void setProcesses(List<MigratableProcess> processes)
    {
    }
}
