import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class RegistryServer
{
    private static int defaultPort = 15044;
    private static AbstractMap<String, RemoteObjectReference> lookup;
    
    private ServerSocket serverSock;

    public RegistryServer(int port)
    {
        lookup = new ConcurrentHashMap<String, RemoteObjectReference>();
        
        try{
            serverSock = new ServerSocket(port);
        } catch (Exception e)
        {
            System.out.println("Server failed to setup");
            e.printStackTrace();
        }
        
        while (true)
        {
            try{

                //Setup file streams
                Socket clientSock = serverSock.accept();
                //Everything after here could be parallelized

                InputStream in = clientSock.getInputStream();
                ObjectInputStream objIn = new ObjectInputStream(in);

                OutputStream out = clientSock.getOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(out);

                Object readObject = null;
                try {
                    readObject = objIn.readObject();
                } catch (ClassNotFoundException e)
                {
                    System.out.println("Server must have remote class");
                    e.printStackTrace();
                }
                
                Class<?> objClass = readObject.getClass();

                
                //This must be a lookup if it is a string,
                //so return the ROR
                if (objClass == String.class)
                {
                    String objString = (String)readObject;
                    RemoteObjectReference ror = lookup.get(objString);
                    
                    //If this is null, null will be written
                    objOut.writeObject(ror);
                }
                //This must be a 'put' since a ror is given
                else if (objClass == RemoteObjectReference.class)
                {
                    RemoteObjectReference ror = (RemoteObjectReference)readObject;
                    String key = ror.name;
                    lookup.put(key, ror);
                }
                
                clientSock.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args)
    {
        int port = defaultPort;
        if (args.length > 0)
        {
            Integer givenPort = Integer.getInteger(args[0]);
            if (givenPort == null)
            {
                System.out.println("Invalid port number: " + args[0] + 
                "; using defailt port.");
            }
            else
            {
                port = givenPort;
            }
        }
        System.out.println("Opening Registry on port " + port);
        new RegistryServer(port);

    }

    //Here for potentially serializing the lookup/put process
    public class LookupThread extends Thread
    {
        public void run()
        {
            
        }
    }

}
