import java.rmi.*;
import java.io.*;

public interface RMIOutputStreamInterf extends Remote {
    
    public void write(int b) throws IOException;
    public void write(byte[] b, int off, int len) throws IOException;
    public void close() throws IOException;
    public int getPipeKey() throws IOException;
    public void transfer(RMIPipe pipe) throws IOException;
}