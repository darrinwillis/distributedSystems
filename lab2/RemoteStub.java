import java.io.*;
import java.net.Socket;

/*
The abstract class that all stubs for remote objects inherit from.
Sends a message to the server and receive a response. 
*/

public abstract class RemoteStub implements Remote440 {
    public RemoteObjectReference ror;
    
    protected Object methodCall(Method m, Object[] args) {
    	RMIMessage msg = new RMIMessage(ror,m,args);
    	
    	return msg.getReturn();
    }
}
