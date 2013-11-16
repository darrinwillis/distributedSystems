/* This is the interface for communication with the master
 * node of the distributed file server */

import java.rmi.*;
import java.io.*;
import java.util.*;

public interface MasterFileServerInterface extends FileServerInterface
{
    // Adds
    void addNewFile(String filename, FileServerInterface host) throws RemoteException;
    void register(NodeFileServerInterface node, String address) throws RemoteException;
    void stop() throws RemoteException;

    void newJob(Job j) throws RemoteException;
    void finishedMap(Task t, String name) throws RemoteException;
    void finishedReduce(Task t) throws RemoteException;
    String monitorAll() throws RemoteException;
}

