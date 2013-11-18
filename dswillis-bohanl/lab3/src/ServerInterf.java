import java.io.*;
import java.rmi.*;

public interface ServerInterf extends Remote {
    public OutputStream getOutputStream(File f) throws IOException;
    public InputStream getInputStream(File f) throws IOException;
    public String sayHello() throws RemoteException;
}
