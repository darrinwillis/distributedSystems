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
            
            ObjectInputStream objIn= new ObjectInputStream(in);
           
            Object readObject = objIn.readObject();
            Class<?> objClass = readObject.getClass();
            if (objClass.equals(Class.forName("RMIMessage")))
            {
                System.out.println("Message received");
                RMIMessage message = (RMIMessage)readObject;
                Object returnObj = null;
                try{
                    returnObj = message.getMethod().invoke(printObj, message.args);
                } catch (Exception e)
                {
                    Throwable cause = e;
                    if (e.getClass() == InvocationTargetException.class)
                    {
                        cause = e.getCause();
                    }
                    
                    returnObj = new RMIException(cause);
                }
                
                if (returnObj != null)
                {
                    OutputStream out = clientSock.getOutputStream();
                    ObjectOutputStream objOut = new ObjectOutputStream(out);

                    objOut.writeObject(returnObj);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
