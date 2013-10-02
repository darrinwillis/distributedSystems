import java.net.InetAddress;
import java.io.*;
import java.lang.*;

public class RemoteObjectReference implements Serializable {
    public InetAddress adr;
    public int key;
    public String interface;

    public RemoteObjectReference(InetAddress i, int k, String n) {
        adr = i;
        key = k;
        interface = n;
    }

    public 440Remote getStub() {
		String stubName = interface + "stub";
		440Remote stub;
		
		try {
			Class c = Class.forName(stubName);
			stub = (440Remote) c.newInstance();
			stub.setRef(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stub;
    }
    
}