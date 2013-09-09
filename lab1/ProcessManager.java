import java.rmi.*;
import java.io.*;
import java.util.HashMap;

public class ProcessManager
{
    private HashMap<String, TimedProcess> processes;
    private static final String MasterServerURL=
        "some url?";

    public ProcessManager()
    {
        processes = new HashMap<String, TimedProcess>();
    }

    // Requires a unique name for the process; otherwise replaces old process
    public void runProcess(MigratableProcess process, String name)
    {
        TimedProcess timedProcess = new TimedProcess(process);
        TimedProcess oldProcess = processes.put(name, timedProcess);
        if (oldProcess != null)
        {
            System.out.println("Old Process replaced");
            //Handle old thread
        }
        //This must handle the timer TODO
        Thread thread = new Thread(process, name);
        thread.start();
    }

    //Stops a process by name, and writes it to out
    public void stopProcess(String name, ObjectOutputStream out)
    {
        TimedProcess process = this.processes.remove(name);
        if (process != null)
        {
            try {
                process.migratableProcess.suspend();
                //Handle stopping thread
                System.out.println("Suspending and writing process " + name);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("No process named " + name + " found");
        }
    }
}
