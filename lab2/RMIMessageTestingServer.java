import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class RMIMessageTestingServer
{
    private static int port = 15444;
    private static PrintingObject printObj;

    public static void main(String[] args)
    {
        try{
            ServerSocket serverSock = new ServerSocket(port);
            
            printObj = new PrintingObject();
            Socket clientSock = serverSock.accept();

            InputStream in = clientSock.getInputStream();
            OutputStream out = clientSock.getOutputStream();

            
            ObjectInputStream objIn= new ObjectInputStream(in);
            
            Object readObject = objIn.readObject();
            Class<?> objClass = readObject.getClass();
            if (objClass.equals(Class.forName("RMIMessage")))
            {
                System.out.println("Message received");
                RMIMessage message = (RMIMessage)readObject;
                message.getMethod().invoke(printObj, message.args);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
