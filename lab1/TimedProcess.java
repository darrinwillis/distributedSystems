import java.util.*;

public class TimedProcess
{
    public Timer timer;
    public MigratableProcess migratableProcess;

    public TimedProcess(MigratableProcess migratableProcess)
    {
        timer = new Timer();
    }

    public void run()
    {

    }

    public void suspend()
    {
    }
}
