
import java.net.InetAddress;
import java.io.*;
import java.lang.*;

public class RemoteObjectReference implements Serializable {
    public InetAddress adr;
    public int key;
    public int port;
    public String name;

    private static final long serialVersionUID = 1702994469;

    public RemoteObjectReference(InetAddress inet, int port, int k, String name) {
        adr = inet;
        key = k;
        this.name = name;
	    this.port = port;
    }

    public Remote440 localize() {
	String stubName = name + "_stub";
	Object stub = null;
		
	try {
	    Class<?> c = Class.forName(stubName);
	    stub = c.newInstance();

        if (stub.getClass() != RemoteStub.class)
        {
            System.out.println("Stub class not found");
            return null;
        }

	} catch (Exception e) {
	    e.printStackTrace();
	}
		
	return ((Remote440)stub);
    }
    
}
