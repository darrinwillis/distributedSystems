import java.rmi.*;
import java.io.*;
import java.util.*;

public class ProcessManager
{
    private HashMap<Integer, MigratableProcess> processMap;
    private HashMap<Thread, Integer> threadMap;
    
    private LinkedList<Thread> threads;
    private LinkedList<MigratableProcess> processes; 
    	
    private static final String MasterServerURL=
        "some url?";

    public ProcessManager()
    {
        processMap = new HashMap<Integer, MigratableProcess>();
        threadMap = new HashMap<Thread, Integer>();
        
        processes = new LinkedList<MigratableProcess>();
        threads = new LinkedList<Thread>();
    }
    
    private Integer nextPid() {
    	for(int i = 0; i <= Integer.MAX_VALUE; i++) {
	    if(processMap.containsValue(i) == false)
		return (new Integer(i));
    	}
    	return -1;
    }
    
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	for (Map.Entry<T, E> entry : map.entrySet()) {
	    if (value.equals(entry.getValue())) {
		return entry.getKey();
	    }
	}
	return null;
    }

    public void setProcesses(MigratableProcess[] ps)
    { 
	LinkedList<MigratableProcess> seen = new LinkedList<MigratableProcess>();
    	for(int i = 0; i < ps.length; i++) {
	    if(processes.contains(ps[i])) 
		seen.add(ps[i]);
	    else
		runProcess(ps[i]);
    	}
    	MigratableProcess[] processArray = (MigratableProcess[]) processes.toArray();
    	for(int i = 0; i < processArray.length; i++) {
	    if(!seen.contains(processArray[i]))
		suspendProcess(getKeyByValue(processMap,processArray[i]));
    	}
    }
    
    public void checkThreads(){
    	Thread t;
    	while(true) {
	    try {
		Thread.sleep(10);
	    } catch (Exception e) {
	    }
    		
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
    	MigratableProcess p = processMap.remove(pid);
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
	System.out.println("Running");
        Integer pid = nextPid();
        processMap.put(pid, process);
        //This must handle the timer TODO
        
        Thread thread = new Thread(process);
        threads.add(thread);
        threadMap.put(thread,pid);
        	
	System.out.println("Starting Thread");
        thread.start();
    }

    //Stops a process by name, and writes it to out
}
