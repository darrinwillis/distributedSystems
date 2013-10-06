import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class ProxyDispatcher {
    public HashMap<String,Object> objList;
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
	    o = in.readObject();
	    if(o.getClass().getName().equals("RMIMessage")) {
		msg = (RMIMessage)o;
		m = msg.getMethod();
		callee = objList.get(msg.remoteObject.name);
		returnValue = m.invoke(callee, msg.args);
		out.writeObject(returnValue);
		out.flush();
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
	
    }		
}	