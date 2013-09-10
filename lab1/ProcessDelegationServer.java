import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

class ProcessDelegationServer extends UnicastRemoteObject implements MigratableProcessManagerInterface
{
    private static final String serverName = "processDelegationServer";
    
    public ProcessDelegationServer() throws RemoteException
    {
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<MigratableProcess> lookupCurrentProcesses() throws RemoteException
    {
        System.out.println("Client Connected");
        return null;
    }
    
    

}
