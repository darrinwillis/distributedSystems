import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;

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
                server.addProcess(processClass);
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
    
    private void addProcess(Class<? extends MigratableProcess> processClass)
    {
        MigratableProcess newProcess = null;
        try {
            newProcess = processClass.newInstance();
        } catch (Exception e)
        {
            System.out.println("Failed to create class isntance");
            e.printStackTrace();
        }

        try {
            String fileName = nextPid();
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
