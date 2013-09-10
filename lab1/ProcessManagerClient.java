import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

class ProcessManagerClient
{
//    private static final String DefaultMasterServerURL = "rmi://unix12.andrew.cmu.edu/usr18/dswillis/private/15440/distributedSystems/lab1";

    private static final String DefaultMasterServerURL = "rmi://unix12.andrew.cmu.edu/processDelegationServer";
    public static void main (String []args)
    {
        String MasterServerURL = DefaultMasterServerURL;
        if (args.length != 0)
            MasterServerURL = args[0];

        try
        {
            System.setSecurityManager (new RMISecurityManager());
            MigratableProcessManagerInterface processMaster = (MigratableProcessManagerInterface) Naming.lookup(DefaultMasterServerURL);

            ProcessManager processManager = new ProcessManager();
            while (true)
            {
                List<MigratableProcess> processes =
                        processMaster.lookupCurrentProcesses();
                processManager.setProcesses(processes);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
