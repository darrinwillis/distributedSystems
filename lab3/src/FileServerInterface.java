import java.rmi.*;
import java.io.*;

public interface FileServerInterface
{
    public OutputStream getOutputStream(File f) throws IOException;
    public InputStream getInputStream(File f) throws IOException;
}
