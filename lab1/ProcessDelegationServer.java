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
        clients = new LinkedList<ProcessManagerClientInterface>();
        //Any constructor methods   
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
    /*    MigratableProcess newProcess = processClass.newInstance();
        String fileName = newProcess.getCanonicalName();
        File newProcessFile = new File(fileName);
        newProcessFile.createNewFile();

        newProcess.out = FileOutputStream(newProcessFile);
    
        this.processes.add(newProcess);
   */ }

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

    private Integer nextPid() {
        for(int i = 0; i <= Integer.MAX_VALUE; i++) {
            if(processIDs.contains(Integer.toString(i)) == false)
                return (new Integer(i));
        }
        return -1;
    }

}
