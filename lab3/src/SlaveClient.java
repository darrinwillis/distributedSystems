import java.io.*;
import java.util.*;
import java.net.*;

public class SlaveClient {
    private static BufferedReader reader;
    private static SlaveNode slave;
    private static ServerThread server;
    private static ObjectOutputStream toMaster;
    private static ObjectInputStream fromMaster;

    private static boolean isRunning = true; 
    
    public static class ServerThread extends Thread {
	private ServerSocket soc;
	private SlaveNode slave;
	private boolean isRunning;
	
	public ServerThread(SlaveNode s) {
	    slave = s;
	    this.isRunning = true;
	    try {
		soc = new ServerSocket(15444);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	}
	    
	public void run() {
	    while(this.isRunning){
		Socket s = null;
		try {
		    s = soc.accept();
		    fromMaster = new ObjectInputStream(s.getInputStream());
		    System.out.println("Master Connected, Creating ComThread");
		    ComThread t = new ComThread(slave,s);
		    t.start();
		} catch(Exception e) {
		    e.printStackTrace();
		}
	    }
	}

	public void stopServer() {
	    this.isRunning = false;
	    try {
		soc.close();
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public static class ComThread extends Thread {
	private Socket soc; 
	private SlaveNode slave;

	public ComThread(SlaveNode sl, Socket s) {
	    soc = s;
	    slave = sl;
	    System.out.println("ComThread created");
	}
	
	public void run() {
	    while(true) {
		Message m = null;
		System.out.println("Waiting for message from Master");
		try {
		    m = (Message) fromMaster.readObject();
		    System.out.println("Got message " + m.type);
		} catch (Exception e){
		    e.printStackTrace();
		}

		System.out.println("Starting task");
		switch(m.type) {
		case MAP:
		    slave.doMap((MapTask)m.task);
		    System.out.println("Finished Task");
		    m.type = MessageType.ACK;
		    try{
			toMaster.writeObject(m);
			toMaster.flush();
		    } catch (Exception e){
			e.printStackTrace();
		    }
		    break;
		case REDUCE:
		    slave.doReduce((ReduceTask)m.task);
		    System.out.println("Finished Task");
		    m.type = MessageType.ACK;
		    try{
			toMaster.writeObject(m);
			toMaster.flush();
		    } catch (Exception e){
			e.printStackTrace();
		    }
		    break;
		case STOP:
		    stopSlave();
		    break;
		default:
		    break;
		}
	    }
	}
    }  
    
    public static class JobThread extends Thread {
	private Job job;
	
	public JobThread(Job j) {
	    job = j;
	}
	
	public void run() {
	    Socket soc;
	    Message m;
	    try {
		soc = new Socket("unix2.andrew.cmu.edu",15444);
		m = new Message(MessageType.NEW_JOB);
		m.job = job;

		toMaster = new ObjectOutputStream(soc.getOutputStream());
		
		toMaster.writeObject(m);
		toMaster.flush();
	    } catch (Exception e){
		e.printStackTrace();
	    }
	}
    }	    		
	  
	    
    public static void stopSlave(){
	isRunning = false;
	try{ 
	    reader.close();
	    server.stopServer();
	    System.exit(0);
	} catch (Exception e){
	    e.printStackTrace();
	}
    }
	
		    
    public static void main(String args[]) {
	slave = new SlaveNode();
	server = new ServerThread(slave);
	server.start();
	System.out.println("Slave Server Started");
	
	reader = new BufferedReader(new InputStreamReader(System.in));
	
	while(isRunning) {
	    try {
		String input = reader.readLine();
		String[] jobArgs = input.split(" ");

		if (jobArgs[0].equals("start")) {
		    if (jobArgs.length < 4) {
			System.out.println("Format: start (jobclass) (outputfile) (inputfiles ...)");
			continue;
		    }
		    //Starting a new job
		    String jobName = jobArgs[1];
		    Job j = (Job) Class.forName(jobName).newInstance();

		    j.setOutput(jobArgs[2]);
		    List<String> inputs = new ArrayList<String>();
                                        
		    for (int i = 3; i < jobArgs.length; i++) {
			inputs.add(jobArgs[i]);
		    }

		    j.setInput(inputs);
                                        
		    JobThread t = new JobThread(j);
		    t.start();
		    System.out.println(jobName + " started!");
		    continue;
		} else if (input.equals("list")) {
		    System.out.println(slave.tasks);
		}
		else if (input.equals("quit")) {
		    stopSlave();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}
	