import java.rmi.*;
import java.io.*;
import java.util.HashMap;

public class ProcessManager
{
    private HashMap<String, MigratableProcess> processes;
    private static final String MasterServerURL=
        "some url?";

    public ProcessManager()
    {
        processes = new HashMap<String, MigratableProcess>();
    }

    // Requires a unique name for the process; otherwise replaces old process
    public void runProcess(MigratableProcess process, String name)
    {
        MigratableProcess oldProcess = processes.put(name, process);
        if (oldProcess != null)
        {
            System.out.println("Old Process replaced");
            //Handle old thread
        }
        Thread thread = new Thread(process, name);
        thread.start();
    }

    //Stops a process by name, and writes it to out
    public void stopProcess(String name, ObjectOutputStream out)
    {
        MigratableProcess process = this.processes.remove(name);
        if (process != null)
        {
            try {
                process.suspend();
                //Handle stopping thread
                System.out.println("Suspending and writing process " + name);
                out.writeObject(process);
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
