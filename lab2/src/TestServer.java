import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class TestServer
{
    private static String testString = "This is a test string";

    public static void main(String[] args)
    {
        String registryURL = "unix12.andrew.cmu.edu";
        int port = 15444;
        if (args.length == 2)
        {
            registryURL = args[0];
            port = Integer.parseInt(args[1]);
        }
        PrintingObject po1 = new PrintingObject();
        Communicate.rebind(registryURL, port, "printing1", po1);
        PrintingObject po2 = new PrintingObject();
        Communicate.rebind(registryURL, port, "printing2", po2);
    }
}
