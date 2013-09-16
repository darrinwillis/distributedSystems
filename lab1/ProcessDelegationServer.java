import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

class ProcessDelegationServer extends UnicastRemoteObject implements MasterServerInterface
{
    private static final String serverName = "processDelegationServer";
    private volatile List<ProcessManagerClientInterface> clients;
    private volatile List<String> processIDs;
	private HashMap<ProcessManagerClientInterface,List<String>> files;
	
    public int nextPid;
    
	private boolean running;
	private static final int BALANCE_LEVEL = 2;
	
    public ProcessDelegationServer() throws RemoteException
    {
        //Any constructor methods   
        clients = new ArrayList<ProcessManagerClientInterface>();
        processIDs = new ArrayList<String>(); 
		nextPid = 0;
		running = false;
		files = new HashMap<ProcessManagerClientInterface,List<String>>(); 
    }
    
	// Assigns unassigned processes in processIDs to first client
	public void assignProcesses() {
		// Get current processes 
		List<String> currentPs = new ArrayList<String>(); 
		try{
			for(ProcessManagerClientInterface client : clients) {
				try {
					currentPs.addAll(client.getProcesses());
				} catch(ConnectException|UnmarshalException e)
				{
					System.out.println("Client disconnected");
					clients.remove(client);
				} 
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		} catch(ConcurrentModificationException e)
				{
					
				} 
		
		// Find unassigned ones and send them to first client 
		List<String> add = new ArrayList<String>(processIDs);
		add.removeAll(currentPs);
		if(processIDs.size() > 0 && clients.size() > 0) {
			try{
				clients.get(0).setProcesses(processIDs);
				System.out.println("First Client Set");
			} 
			catch(ConnectException|UnmarshalException e)
			{
				System.out.println("Client disconnected");
				clients.remove(0);
			} 
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}		
	}

	// balances load between clients 
	public void loadBalance() throws RemoteException {
		// victim is the client that takes on additional load
		int avg = 0; 
		int victim = 0;
		int current = 0;
		String f;
		ProcessManagerClientInterface victimC;
		List<String> ps;
		List<String> victimPs;
		int load;
		Random r = new Random(); 
		
		//calculate average and update hashmap
		for (ProcessManagerClientInterface client : clients) {
			try {
				files.put(client,client.getProcesses());
				avg = avg + client.getProcesses().size();
			} catch(ConnectException|UnmarshalException e)
			{
				System.out.println("Client disconnected");
				clients.remove(client);
			} 
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
		if (clients.size() != 0) 
			avg = avg / clients.size();
		else
			avg = 0;
		
		//balance the load of clients by getting a process from an overloaded client and sending it to a random other one
		for(ProcessManagerClientInterface client : clients) {
			try { 
				ps = files.get(client);
				load = ps.size();
				while(load - BALANCE_LEVEL > avg) {
					//designate victim
					do {
						victim = r.nextInt(clients.size());
					} while(victim == current) ;
					
					//move process
					victimC = clients.get(victim);
					victimPs = files.get(victimC);
					f = ps.remove(0); 
					victimPs.add(f);
					files.put(victimC,victimPs);
					load--;
				}
				files.put(client,ps);
				current++; 
			} 
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
		//set all changes
		for(ProcessManagerClientInterface client : clients) {
			try {
				ps = files.get(client);
				client.setProcesses(ps);
			} catch(ConnectException|UnmarshalException e)
			{
				System.out.println("Client disconnected");
				clients.remove(client);
			} 
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
	}
    public static void main (String []args)
    {
        try
        {
            //Server Stuff
            ProcessDelegationServer server = new ProcessDelegationServer();

            Naming.rebind (serverName, server);

            System.out.println("Process Delegation Server Ready");
        
            for(int i = 0; i < 10; i++) {
                Class<? extends MigratableProcess> processClass = GrepProcess.class;
                String outputFileName = "out/" + i + ".txt";
                String[] strings = {"1", "in.txt", outputFileName};
                Object[] arguments = {strings};
                server.addProcess(processClass, arguments);
	    	}
			while(true) {
				server.assignProcesses();
				server.loadBalance();	
				server.updateProcessList();
				Thread.sleep(1000);
			}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		System.out.println("Main End");
    }

    public void register(ProcessManagerClientInterface newClient) throws RemoteException
    {
        System.out.println("Client Connected");
		
        clients.add(newClient);
		System.out.println("Current processes " + processIDs.size()); 
		System.out.println("Current clients " + clients.size()); 
    }
    
    public void addProcess(Class<? extends MigratableProcess> processClass, Object[] args)
    {
        MigratableProcess newProcess = null;
        Class[] classes = new Class[args.length];
        for(int i = 0; i < args.length; i++)
        {
            classes[i] = args[i].getClass();
        }

        Constructor<?> constructor = null;
        try { 
            constructor = processClass.getConstructor(classes);
        } catch (NoSuchMethodException e)
        {
            Constructor<?>[] constructors = processClass.getConstructors();
            System.out.println("Possible constructors are: \n" + Arrays.toString( constructors ));
            System.out.println("Incorrect arguments for class");
            e.printStackTrace();
        }
        
        try {
            newProcess = (MigratableProcess) constructor.newInstance(args);
        } catch (Exception e)
        {
            System.out.println("Failed to create class instance");
            e.printStackTrace();
        }

        try {
            String fileName = "processes/" + nextPid;
	    	nextPid++;
            File newProcessFile = new File(fileName);
            newProcessFile.createNewFile();

            ProcessIO.writeProcess(newProcess, fileName);
	    	System.out.println("Process Written " +  fileName);
            processIDs.add(fileName); 
        } catch (Exception e)
        {
            System.out.println("Failed to make file for process");
            e.printStackTrace();
        }
    }

    private void updateProcessList()
    {
        for (int i = 0; i < processIDs.size(); i++)
        {
            String fileName = processIDs.get(i);
            File processFile = new File(fileName);
            //If the file is deleted, the process is completed
            if (!processFile.exists())
            {
                System.out.println("Removing " + fileName);
                processIDs.remove(i);
            }
        }
    }
}
