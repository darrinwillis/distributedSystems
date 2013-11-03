import java.rmi.*;
import java.io.*;

public interface RMIOutputStreamInterf extends Remote {
    
    public void write(int b) throws IOException, RemoteException;
    public void write(byte[] b, int off, int len) throws IOException, RemoteException;
    public void close() throws IOException, RemoteException;
    public int getPipeKey();
    public void transfer(RMIPipe pipe) throws IOException;
}