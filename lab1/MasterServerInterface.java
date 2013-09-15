/* This allows clients to interface with the Delegation Server
 */

import java.rmi.*;

public interface MasterServerInterface extends Remote
{
    // Register sets up client to take MigratableProcesses
    void register(ProcessManagerClientInterface client) throws RemoteException;
    // addProcess allows any client to add more processes to the server
    void addProcess(Class<? extends MigratableProcess> processClass, Object[] args) throws RemoteException;
}
