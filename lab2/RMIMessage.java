
public class RMIMessage implements Serializable{
    
    public Method method; 
    public RemoteObjectReference remoteObject;

    public RMIMessage(RemoteObjectReference object, Method method)
    {
        this.method = method;
        this.remoteObject = object;
        this.invoke();
    }

    private void invoke()
    {
        Socket comSock = Socket(remoteObject.adr, remoteObject.port);
        OutputStream outStream = comSock.getOutputStream();
        ObjectOutputStream objStream = ObjectOutputStream(outStream);
        objStream.writeObject(this);
        objStream.close();
        comSock.close();
    }
}
