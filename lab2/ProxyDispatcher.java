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

    public ProxyDispatcher(int p) {
	try{
	    objList = new HashMap<String,Object>();
	    port = p;
	    adr = InetAddress.getLocalHost();  
	} catch(Exception e) {
	    e.printStackTrace();
	}
	ProxyThread pthread = new ProxyThread(this);
	pthread.start();

    }
    
    public class ProxyThread extends Thread {
	ProxyDispatcher pd;
	public ProxyThread(ProxyDispatcher p) {
	    pd = p;
	}
	public void run() {
	    try {
		ServerSocket serverSock = new ServerSocket(pd.port,pd.BACKLOG,pd.adr);
		while(true) {
		    Socket soc = serverSock.accept();
		    System.out.println("client connected");

		    InputStream inStream = soc.getInputStream();
		    pd.in = new ObjectInputStream(inStream);
	
		    OutputStream outStream = soc.getOutputStream();
		    pd.out = new ObjectOutputStream(outStream);
	    
		    pd.executeMessage();
		}
	    }
	    catch(Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public void addObj(String name,Object o) {
	System.out.println("Added Object " + name);
	objList.put(name,o);
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
		m = msg.getMethod();
		callee = objList.get(msg.remoteObject.key);
		System.out.println(callee);
		// Handles any exceptions thrown by this object and fowards
		// them to the client
		try{
		    returnValue = m.invoke(callee, msg.args);
		} catch (Exception e) {
		    Throwable cause = e;
		    if (e.getClass() == InvocationTargetException.class)
			cause = e.getCause();

		    //Returns a wrapped Throwable to clientside
		    returnValue = new Remote440Exception(cause);
		}
        
		out.writeObject(returnValue);
   
		out.flush();
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
	
    }
}	
