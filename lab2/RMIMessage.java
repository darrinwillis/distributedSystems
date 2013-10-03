import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class RMIMessage implements Serializable{
    
    public Method method; 
    public RemoteObjectReference remoteObject;
    public Object args;

    public RMIMessage(RemoteObjectReference object, Method method, Object args)
    {
        this.method = method;
        this.remoteObject = object;
        this.args = args;
        this.send();
    }

    private void send()
    {
        try{
            Socket comSock = new Socket(remoteObject.adr, remoteObject.port);
            OutputStream outStream = comSock.getOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(outStream);
            objStream.writeObject(this);
            objStream.close();
            comSock.close();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
