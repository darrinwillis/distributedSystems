import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class ProxyDispatcher {
    public HashMap<String,Object> objList; //maps object names to actual objects
    public ObjectInputStream in; 
    public ObjectOutputStream out;
    

    public ProxyDispatcher(int p, InetAddress a) {
	try {
	    Socket soc = new Socket(a, p);

	    InputStream inStream = soc.getInputStream();
	    in = new ObjectInputStream(inStream);
	
	    OutputStream outStream = soc.getOutputStream();
	    out = new ObjectOutputStream(outStream);
	} catch(Exception e) {
	    e.printStackTrace();
	}

	objList = new HashMap<String,Object>();
    }

    public void addObj(String name,Object o) {
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
		m = msg.getMethod(); //get method from message 
		callee = objList.get(msg.remoteObject.name); //find object associated with reference
		returnValue = m.invoke(callee, msg.args); //invoke local method call
		out.writeObject(returnValue);//send return value back to stub
		out.flush();
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
	
    }		
}	