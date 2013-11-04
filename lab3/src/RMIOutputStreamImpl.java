import java.rmi.*;
import java.rmi.server.*;
import java.io.*;


public class RMIOutputStreamImpl implements RMIOutputStreamInterf {
    private OutputStream out;
    private RMIPipe pipe;

    public RMIOutputStreamImpl(OutputStream out) throws IOException {
        this.out = out;
        this.pipe = new RMIPipe(out);
        UnicastRemoteObject.exportObject(this, 1099);
    }
    
    public void write(int b) throws IOException {
        out.write(b);
    }
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }
    public void close() throws IOException {
        out.close();
    }
    
    public int getPipeKey() throws IOException{
        return pipe.getKey();
    }
  
    public void transfer(RMIPipe pipe) throws IOException {
        // nothing more to do here
        // pipe has been serialized and that's all we want
    }
}