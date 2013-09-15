import java.rmi.*;

public interface MasterServerInterface extends Remote
{
    void register(ProcessManagerClientInterface client) throws RemoteException;
    void addProcess(Class<? extends MigratableProcess> processClass, Object[] args) throws RemoteException;
}
