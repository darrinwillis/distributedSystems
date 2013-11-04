/* This is the interface for communication with the master
 * node of the distributed file server */

import java.rmi.*;
import java.io.*;

public interface MasterFileServerInterface extends Remote, FileServerInterface
{
    // Adds
    void addNewFile(String filename, FileServerInterface host) throws RemoteException;
    void stop() throws RemoteException;
}

