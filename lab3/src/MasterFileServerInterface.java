/* This is the interface for communication with the master
 * node of the distributed file server */

import java.rmi.*;
import java.io.*;
import java.util.*;

public interface MasterFileServerInterface extends FileServerInterface
{
    // Adds
    void addNewFile(String filename, FileServerInterface host) throws RemoteException;
    void register(NodeFileServerInterface node, String address, int cores) throws RemoteException;
    void stop() throws RemoteException;

    void newJob(Job j) throws RemoteException;
    void finishedMap(Task t, String name) throws RemoteException;
    void finishedReduce(Task t, String name) throws RemoteException;
    String monitorAll() throws RemoteException;
    String monitorFiles() throws RemoteException;
    String monitorNodes() throws RemoteException;
}

