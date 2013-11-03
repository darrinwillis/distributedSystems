/* This is the interface for communication with the master
 * node of the distributed file server */

import java.rmi.*;

public interface MasterFileServerInterface extends Remote
{
    // Adds
    void addNewFile(String filename) throws RemoteException;

}
