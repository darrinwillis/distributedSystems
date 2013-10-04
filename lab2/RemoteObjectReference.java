
import java.net.InetAddress;
import java.io.*;
import java.lang.*;

public class RemoteObjectReference implements Serializable {
    public InetAddress adr;
    public int key;
    public int port;
    public String name;

    public RemoteObjectReference(InetAddress i, int p, int k, String n) {
        adr = i;
        key = k;
        name = n;
	port = p;
    }

    public Object localise() {
	String stubName = name + "_stub";
	Object stub = null;
		
	try {
	    Class c = Class.forName(stubName);
	    stub = c.newInstance();

	} catch (Exception e) {
	    e.printStackTrace();
	}
		
	return stub;
    }
    
}
