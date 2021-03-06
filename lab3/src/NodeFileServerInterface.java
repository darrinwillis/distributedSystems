import java.rmi.*;
import java.io.*;

public interface NodeFileServerInterface extends FileServerInterface {
    boolean isFull() throws RemoteException;
    void scheduleTask(Task task) throws RemoteException;
    void cleanLocalDirectory() throws RemoteException;
    Status getStatus() throws RemoteException;
}
