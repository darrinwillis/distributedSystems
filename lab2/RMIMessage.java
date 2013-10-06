
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class RMIMessage implements Serializable{
    
    //Methods are not serializable, so the same info is given in strings
    private Class<?> theClass;
    private String methodName;
    private Class<?>[] argumentClasses;

    public RemoteObjectReference remoteObject;
    public Object args;

    public RMIMessage(RemoteObjectReference object, Method method, Object args)
    {
        this.theClass = method.getDeclaringClass();
        this.methodName = method.getName();
        this.argumentClasses = method.getParameterTypes();
        this.remoteObject = object;
        this.args = args;
        this.send();
    }

    public Method getMethod()
    {
        try{
            Method meth = theClass.getMethod(methodName, argumentClasses);
            return meth;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void send()
    {
        try{
            Socket comSock = new Socket(remoteObject.adr, remoteObject.port);
            OutputStream outStream = comSock.getOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(outStream);
            objStream.writeObject(this);
            objStream.flush();
            objStream.close();
            comSock.close();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
