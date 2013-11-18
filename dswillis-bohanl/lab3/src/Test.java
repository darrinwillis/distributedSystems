import java.util.*;
import java.io.*;
import java.net.*;
// This is a class to handle any administrative duties
// and system-level user interaction
public class Test {
    public static void main(String[] args)
    {
        InetAddress testAddress = null;
        try{
            testAddress = InetAddress.getLocalHost();
        } catch (Exception e)
        {
           e.printStackTrace(); 
        }
        System.out.println("The local host name is " + testAddress.getHostName());
        System.out.println("The local host address is " + testAddress);

    }
}
