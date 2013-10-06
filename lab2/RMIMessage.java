
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class RMIMessage implements Serializable{
    
    //Methods are not serializable, so the same info is given in strings
    private Class<?> theClass;
    private String methodName;
    private Class<?>[] argumentClasses;
    private Object returnObject;

    public RemoteObjectReference remoteObject;
    public Object[] args;

    //Instantiates and sends message request, holding the return value unti
    //queried by getReturn()
    public RMIMessage(RemoteObjectReference object, Method method, Object[] args)
    {
        this.theClass = method.getDeclaringClass();
        this.methodName = method.getName();
        this.argumentClasses = method.getParameterTypes();
        this.remoteObject = object;
        this.args = args;
        this.send();
    }

    //Builds a Method from private objects, in effect making Methods serializable
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

    public Object getReturn()
    {
        return returnObject;
    }

    private void send()
    {
        try{
            //Write out this message to the host specified by the remote ref
            Socket comSock = new Socket(remoteObject.adr, remoteObject.port);
            OutputStream outStream = comSock.getOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(outStream);
            objOut.writeObject(this);
            objOut.flush();

            //If there is a return value, read it from input
            if (this.getMethod().getReturnType() != Void.TYPE)
            {
                InputStream inStream = comSock.getInputStream();
                ObjectInputStream objIn = new ObjectInputStream(inStream);
                //Attempt to read the object
                //TODO: Get the .class over http if not found
                Object inputObj = null;
                try{
                    inputObj = objIn.readObject();
                } catch (ClassNotFoundException e)
                {
                    System.out.println("Class is not on client");
                }
                
                this.returnObject = inputObj;
            }
            
            comSock.close();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
