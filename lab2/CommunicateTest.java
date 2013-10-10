import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class CommunicateTest
{

    private static String testString = "This is a test string";

    public static void main(String[] args)
    {
        PrintingObject po = new PrintingObject();
        Communicate.rebind("printing", po);
        System.out.println("obj bound on registry");
        PrintingObjectInterface obj = (PrintingObjectInterface)Communicate.lookup("printing");
  
	System.out.println("Calling Method");
        try{
            obj.printThisException(testString, 10);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
