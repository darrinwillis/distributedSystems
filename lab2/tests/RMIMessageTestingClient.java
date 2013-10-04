import java.lang.reflect.*;
import java.io.*;
import java.net.*;

class RMIMessageTestingClient
{
    private static String hostname = "unix12.andrew.cmu.edu";
    private static String methodName = "printThis";
    private static int port = 15444;

    public static void main(String[] args)
    {
	try{
	    String s = "The sent message";
	    Socket sock = new Socket(hostname, port);

	    InputStream in = sock.getInputStream();

	    Class theClass = Class.forName("PrintingObject");
	    RemoteObjectReference ref = new RemoteObjectReference(sock.getInetAddress(), sock.getPort(), 0, theClass.toString());
        
	    Method method = theClass.getMethod(methodName, String.class);

	    new RMIMessage(ref, method, s);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
