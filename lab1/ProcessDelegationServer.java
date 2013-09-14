import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

class ProcessDelegationServer extends UnicastRemoteObject implements MasterServerInterface
{
    private static final String serverName = "processDelegationServer";
    private List<ProcessManagerClientInterface> clients;
    private List<String> processIDs;

    public ProcessDelegationServer() throws RemoteException
    {
        //Any constructor methods   
        clients = new LinkedList<ProcessManagerClientInterface>();
        processIDs = new LinkedList<String>(); 
    }

    public static void main (String []args)
    {
        try
        {
            //Server Stuff

            ProcessDelegationServer server = new ProcessDelegationServer();

            Naming.rebind (serverName, server);

            System.out.println("Process Delegation Server Ready");
        
            //for (int i = 0; i < 100; i++)
            //{
                Class<? extends MigratableProcess> processClass = GrepProcess.class;
                String[] strings = {" ", "README.md", "out.txt"};
                Object[] arguments = {strings};
                server.addProcess(processClass, arguments);
            //}

            while (true)
            {
                //DO SOME LOAD BALANCING
                server.balanceProcesses();
                Thread.sleep(1000);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void register(ProcessManagerClientInterface newClient) throws RemoteException
    {
        System.out.println("Client Connected");

        clients.add(newClient);
    }
    
    private void addProcess(Class<? extends MigratableProcess> processClass, Object[] args)
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
            String fileName = "processes/" + nextPid();
            File newProcessFile = new File(fileName);
            newProcessFile.createNewFile();

            ProcessIO.writeProcess(newProcess, fileName);
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
                processIDs.remove(i);
        }
    }

    private void balanceProcesses()
    { 
        int numClients = clients.size();
        int numProcesses = 0;
        /*
        for (int i = 0; i < numClients; i++)
            {
                numProcesses += clients.get(i).getProcesses().size();
            }
        int avgProcesses = numProcesses / numClients;
        */
        if (clients.size() > 0)
        {
            ProcessManagerClientInterface firstClient = clients.get(0);;
        

            try{
                firstClient.setProcesses(processIDs);
            } 
            catch(ConnectException|UnmarshalException e)
            {
                System.out.println("Client disconnected");
                clients.remove(firstClient);
            } 
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private String nextPid() {
        for(int i = 0; i <= Integer.MAX_VALUE; i++) {
            if(processIDs.contains(Integer.toString(i)) == false)
                return (Integer.toString(i));
        }
        return Integer.toString(-1);
    }

}
