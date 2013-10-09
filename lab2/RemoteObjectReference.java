import java.util.*;
import java.net.InetAddress;
import java.io.*;
import java.lang.*;

public class RemoteObjectReference implements Serializable {
    public InetAddress adr;
    //The given key to look up ths object
    public String key;
    public int port;
    //The class name of the object that this references
    public String name;

    private static final long serialVersionUID = 1702994469;

    public RemoteObjectReference(InetAddress inet, int port, String key, String name) {
        this.adr = inet;
        this.key = key;
        this.name = name;
	    this.port = port;
    }

    public Remote440 localize() {
	String stubName = name + "_stub";
	Object stub = null;
		
	try {
	    System.out.println("Looking for class \"" + stubName + "\"");
        Class c = Class.forName(stubName);
	    stub = c.newInstance();

        if (!(stub instanceof RemoteStub))
        {
            System.out.println("Stub class is " + stub.getClass() + " not remotestub");
            System.out.println("Interfaces are " + Arrays.toString(stub.getClass().getInterfaces()));
            System.out.println("superclass is " + stub.getClass().getSuperclass());
            return null;
        }

	} catch (Exception e) {
	    e.printStackTrace();
	}
		
	return ((Remote440)stub);
    }
    
}
