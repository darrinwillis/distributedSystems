import java.rmi.*;

public interface MasterServerInterface extends Remote
{
    void register(ProcessManagerClientInterface client) throws RemoteException;
}
