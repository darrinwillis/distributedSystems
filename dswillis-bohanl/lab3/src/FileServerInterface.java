import java.rmi.*;
import java.io.*;

public interface FileServerInterface extends Remote
{
    public OutputStream getOutputStream(File f) throws IOException;
    public InputStream getInputStream(File f) throws IOException;
    public void stop() throws RemoteException;
}
