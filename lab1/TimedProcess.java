import java.util.*;

public class TimedProcess
{
    public Timer timer;
    public MigratableProcess migratableProcess;
    
    private class task extends TimerTask {
    	
    	task() {
    	}
    	public void run() {
    		
    	}
    }
	
    public TimedProcess(MigratableProcess migratableProcess)
    {
        timer = new Timer();
    }

    public void run()
    {
		migratableProcess.run();
    }

    public void suspend()
    {
    	migratableProcess.suspend();
    }
}
