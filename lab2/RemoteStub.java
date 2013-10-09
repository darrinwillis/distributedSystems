import java.io.*;
import java.net.Socket;
import java.lang.*;
import java.lang.reflect.*;


/*
The abstract class that all stubs for remote objects inherit from.
Sends a message to the server and receive a response. 
*/

public abstract class RemoteStub implements Remote440{
    public RemoteObjectReference ror;

    protected RemoteStub(RemoteObjectReference r) {
	ror = r;
    }
    
    protected Object methodCall(Method m, Object[] args) throws Throwable{
	RMIMessage msg = new RMIMessage(ror,m,args);
    
	return msg.getReturn();
    }
}
