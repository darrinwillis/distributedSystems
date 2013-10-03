import java.net.*;

public class RMIMessageTestingServer
{
    private static int port = 15444;
    private static PrintingObject printObj;

    public static void main()
    {
        ServerSocket serverSock = new ServerSocket(port);
        printObj = new PrintingObject();
        clientSock = serverSock.accept();

        InputStream in = clientSock.getInputStream();
        OutputStream out = clientSock.getOutputStream();

        ObjectInputStream objIn = ObjectInputStream(in);

        Object readObject = objIn.readObject();
        Class<?> objClass = readObject.getClass();
        if (objClass.equals(RMIMessage.class))
        {
            System.out.println("Message received");
            RMIMessage message = (RMIMessage)readObject;
            message.method.invoke(printObj, message.args[0]);
        }
    }
}
