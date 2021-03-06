import java.io.*;
import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;

public class ServerImpl extends UnicastRemoteObject
    implements ServerInterf {
    Registry rmiRegistry;
    public ServerImpl() throws RemoteException {
	super();
    }
    public void start() throws Exception {
	rmiRegistry = LocateRegistry.createRegistry(1099);
	rmiRegistry.bind("server", this);
	System.out.println("Server started");
    }
    public void stop() throws Exception {
	rmiRegistry.unbind("server");
	unexportObject(this, true);
	unexportObject(rmiRegistry, true);
	System.out.println("Server stopped");
    }
        
    public String sayHello() {
	return "Hello world";
    }
    public OutputStream getOutputStream(File f) throws IOException {
	return new RMIOutputStream(new RMIOutputStreamImpl(new 
							   FileOutputStream(f)));
    }
    public InputStream getInputStream(File f) throws IOException {
	return new RMIInputStream(new RMIInputStreamImpl(new 
							 FileInputStream(f)));
    }
}