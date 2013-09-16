import java.rmi.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ProcessManager
{
    private ConcurrentHashMap<String, MigratableProcess> processMap; //map process name to process
    private ConcurrentHashMap<Thread, String> threadMap; //map process thread to process name
    
    private LinkedList<Thread> threads; //active threads
    	
    private Boolean checkingThreads; 

    public ProcessManager()
    {
        processMap = new ConcurrentHashMap<String, MigratableProcess>();
        threadMap = new ConcurrentHashMap<Thread, String>();
        
        threads = new LinkedList<Thread>();
        
        checkingThreads = false;
    }
	
	// take a list of processes and syncs with current processes
    public void setProcesses(List<String> pids)
    {
        List<String> original = new ArrayList<String>();
		List<String> selected = new ArrayList<String>(pids);
		original.addAll(processMap.keySet());
		
		// run all new processes given by pids
        ArrayList<String> add = new ArrayList<String>(selected);
		add.removeAll(original);
		for(String s : add) {
	    	runProcess(s);
		}   
		
		// suspend processes not included in pids
		ArrayList<String> remove = new ArrayList<String>(original);
		remove.removeAll(selected);
		for(String s : remove) {
		    suspendProcess(s);
		}
        
        // run a thread checker in the background that checks for dead threads
        if (!checkingThreads) {
        	checkingThreads = true;
        	ThreadChecker thread = new ThreadChecker();
	        thread.setDaemon(true);
	        thread.start();
        }
    }
    
    // return running processes  
    public List<String> getProcesses() {
    	List<String> tmp = new ArrayList<String>();
    	tmp.addAll(processMap.keySet()); 	
    	return tmp;
    }
    
    // Constantly checks for threads that have died
    public class ThreadChecker extends Thread {	
		public void run() {
	    	Thread t;
            Boolean b = true;
	    	while(b) {
	            try {
	                Thread.sleep(10);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	                
	            for(int i = 0; i < threads.size(); i++) {
	                try {
	                    t = threads.get(i);
	                    t.join(10);
	                } catch(Exception e) {
	                    e.printStackTrace();
	                    continue;
	                }
	                if(!t.isAlive()) {
	                	// remove all occurences of dead thread
	                    String filename = threadMap.get(t);
			            System.out.println("Killed " + filename);
	                    ProcessIO.delete(filename); 
	                    processMap.remove(filename); 
	                    threads.remove(t);
	                    threadMap.remove(t);
	                }
	            } 
	    	}
	    }
	}
    
    // suspend process and write it to output stream for migrating
    public void suspendProcess(String pid) {
	System.out.println("suspendProcess " + pid);
        MigratableProcess p = processMap.remove(pid);
        if (p != null)
        {
            p.suspend();
            ProcessIO.writeProcess(p, pid.toString());
        }
    }
	
	// open suspendded process from input stream and start a thread for it
    public void runProcess(String pid)
    {
        System.out.println("runProcess: " + pid);
        MigratableProcess process = ProcessIO.readProcess(pid);
        if (process != null)
        {
            processMap.put(pid, process);
            
            Thread thread = new Thread(process);
            threads.add(thread);
            threadMap.put(thread,pid);
                
            thread.start();
        }
    }
}
