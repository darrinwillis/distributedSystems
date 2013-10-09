import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class ProxyDispatcher {
    public HashMap<String,Object> objList; //maps object names to actual objects
    public ObjectInputStream in; 
    public ObjectOutputStream out;
    public int port;
    public InetAddress adr; 
    
    public static int BACKLOG = 10;

    public ProxyDispatcher(int p, InetAddress a) {
	objList = new HashMap<String,Object>();
	port = p;
	adr = a;
    }

    public void addObj(String name,Object o) {
	objList.put(name,o);
    }

    public void start(){
	try {
	    ServerSocket serverSock = new ServerSocket(port,BACKLOG,adr);
	    Socket soc = serverSock.accept();
	    System.out.println("client connected");

	    InputStream inStream = soc.getInputStream();
	    in = new ObjectInputStream(inStream);
	
	    OutputStream outStream = soc.getOutputStream();
	    out = new ObjectOutputStream(outStream);
	    
	    executeMessage();
        } catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    
    public void executeMessage(){
	Object o = null;
	RMIMessage msg;
	Method m;
	Object callee; 
	Object returnValue = null;
	try {
	    o = in.readObject(); //get message sent by stub 
	    if(o.getClass().getName().equals("RMIMessage")) {
       
		msg = (RMIMessage)o; 
		m = msg.getMethod(); //get method from message 
		callee = objList.get(msg.remoteObject.name); //find object associated with reference
		returnValue = m.invoke(callee, msg.args); //invoke local method call
		out.writeObject(returnValue);//send return value back to stub
	      
		msg = (RMIMessage)o;
		m = msg.getMethod();
		callee = objList.get(msg.remoteObject.name);
		
		// Handles any exceptions thrown by this object and fowards
		// them to the client
		try{
		    returnValue = m.invoke(callee, msg.args);
		} catch (Exception e) {
		    Throwable cause = e;
		    if (e.getClass() == InvocationTargetException.class)
			cause = e.getCause();

		    //Returns a wrapped Throwable to clientside
		    returnValue = new RMIException(cause);
		}
        
		out.writeObject(returnValue);
   
		out.flush();
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
	
    }		
}	
