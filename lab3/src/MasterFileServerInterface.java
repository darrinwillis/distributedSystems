/* This is the interface for communication with the master
 * node of the distributed file server */

import java.rmi.*;
import java.io.*;

public interface MasterFileServerInterface extends Remote
{
    // Adds
    void addNewFile(String filename) throws RemoteException;
    public OutputStream getOutputStream(File f) throws IOException;
    public InputStream getInputStream(File f) throws IOException;
}
