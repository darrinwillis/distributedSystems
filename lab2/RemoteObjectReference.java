import java.net.InetAddress;
import java.io.*;
import java.lang.*;

public class RemoteObjectReference implements Serializable {
    public InetAddress adr;
    public int key;
    public String name;

    public RemoteObjectReference(InetAddress i, int k, String n) {
        adr = i;
        key = k;
        name = n;
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