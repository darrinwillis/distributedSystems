import java.io.*;
import java.net.Socket;

/*
The abstract class that all stubs for remote objects inherit from.
Sends a message to the server and receive a response. 
*/

public abstract class RemoteStub implements Remote440 {
    public RemoteObjectReference ror;
    
    protected Object executeMessage(RMIMessage msg) {
    	Socket sock = null;
    	//make sure that the reference is attached to the message
    	if (msg.remoteObject == null) 
	    msg.remoteObject = ror;
    	
    	try {
	    sock = new Socket(ror.adr, ror.port);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    	
    	msg.send();
    	
    	return msg.getReturn();
    }
}
