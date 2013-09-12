import java.rmi.*;
import java.io.*;
import java.util.*;

public class ProcessManager
{
    private HashMap<Integer, TimedProcess> processMap;
    private HashMap<Thread, Integer> threadMap;
    
    private LinkedList<Thread> threads;
    	
    private static final String MasterServerURL=
        "some url?";

    public ProcessManager()
    {
        processMap = new HashMap<Integer, TimedProcess>();
        threadMap = new HashMap<Thread, Integer>();
        
        threads = new LinkedList<Thread>();
    }
    
    private Integer nextPid() {
    	for(int i = 0; i <= Integer.MAX_VALUE; i++) {
    		if(processMap.containsValue(i) == false)
    			return (new Integer(i));
    	}
    	return -1;
    }

    public void setProcesses(List<MigratableProcess> processes)
    {
    	Thread t;
    	while(true) {
    		try {
    			Thread.sleep(10);
    		} catch (Exception e) {}
    		
    		for(int i = 0; i < threads.size(); i++) {
    			try {
    				t = threads.get(i);
    				t.join(10);
    			} catch(Exception e) {
    				continue;
    			}
    			if(!t.isAlive()) {
    				processMap.remove(threadMap.get(t)); 
    				threads.remove(t);
    				threadMap.remove(t);
    			}
    		} 
    	}
    }
    
    public MigratableProcess unsuspendProcess(Integer pid) {
    	try{	
	    	TransactionalFileInputStream in = new TransactionalFileInputStream(pid.toString());
	    	ObjectInputStream ois = new ObjectInputStream(in);
	    	
	    	MigratableProcess p = (MigratableProcess)ois.readObject();
	    	
	    	ois.close();
	    	in.close();
	    	return p;
    	} catch (Exception e)	{
        	System.out.println("Exception: " + e.getMessage());
        	return null;
        }
    }
    
    public void suspendProcess(Integer pid) {
    	TimedProcess p = processMap.remove(pid);
    	p.suspend();
    	try{   	
	    	TransactionalFileOutputStream out = new TransactionalFileOutputStream(pid.toString(), false);
	    	ObjectOutputStream oos = new ObjectOutputStream(out); 
	    		
	    	oos.writeObject(p);
	    	
	    	oos.close();
	    	out.close();
    	} catch (Exception e)	{
        	System.out.println("Exception: " + e.getMessage());
        }
    }

    // Requires a unique name for the process; otherwise replaces old process
    public void runProcess(MigratableProcess process)
    {
        TimedProcess timedProcess = new TimedProcess(process);
        Integer pid = nextPid();
        processMap.put(pid, timedProcess);
        //This must handle the timer TODO
        
        Thread thread = new Thread(process);
        threads.add(thread);
        threadMap.put(thread,pid);
        	
        thread.start();
    }

    //Stops a process by name, and writes it to out
    public void stopProcess(Integer pid, ObjectOutputStream out)
    {
        
    }
}
