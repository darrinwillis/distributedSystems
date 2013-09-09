import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

class ProcessManagerClient
{
    private static final String MasterServerURL = "Some url";

    public static void main (String []args)
    {
        try
        {
            System.setSecurityManager (new RMISecurityManager());
            MigratableProcessManagerInterface processMaster = (MigratableProcessManagerInterface) Naming.lookup(MasterServerURL);
            List<MigratableProcess> processStarts = processMaster.lookupStarts();
            List<MigratableProcess> processEnds = processMaster.lookupEnds();

            ProcessManager processManager = new ProcessManager();
            start(processStarts, processManager);
            end(processEnds, processManager);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void start(List<MigratableProcess> processStarts, ProcessManager processManager)
    {
        for (int i = 0; i < processStarts.size(); i++)
        {
            MigratableProcess process = processStarts.get(i);
            processManager.runProcess(process, "NAME");
        }
    }

    //Writes out all processes to a given file NOTE:NEEDS FILE
    private static void end(List<MigratableProcess> processEnds, ProcessManager processManager)
    {
        for (int i = 0; i < processEnds.size(); i++)
        {
            MigratableProcess process = processEnds.get(i);
            processManager.stopProcess("NAME", null);
        }
    }

}
