import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class TestClient
{
    private static String testString = "This is a test string";
    private static String testString2 = "DISTRIBUTED SYSTEMS";
    private static int increment = 5;

    public static void main(String[] args)
    {
        PrintingObjectInterface obj1 = (PrintingObjectInterface)Communicate.lookup("printing1");
	PrintingObjectInterface obj2 = (PrintingObjectInterface)Communicate.lookup("printing2");
	int testsfailed = 0;
	int ret1 = 0; 
	int ret2 = 0;
	System.out.println("Calling Method");
        try{
	    int start = obj1.getCounter();
	    for(int i = start + 1; i <= start + 100; i++) {
		ret1 = obj1.printThis(testString2, increment);
		ret2 = obj2.printThis(testString, increment);
		if(ret1 != i) { testsfailed++; }
		if(ret2 != i) { testsfailed++; }
	    }
	    obj1.printThisException(testString, 2*increment);;
        } catch (Remote440Exception e)
	    {
		if(e.getCause().getClass() != ArithmeticException.class){
		    testsfailed++;
		    System.out.println("Exception not correctly thrown");
		    e.printStackTrace();
		} else {
		    System.out.println("Exception correctly thrown");
		}
	    }
	if(testsfailed == 0) 
	    System.out.println("All Tests Passsed");
	else
	    System.out.println("Failed " + testsfailed + " tests");
    }
}
