import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class TestClient
{
    private static String testString = "This is a test string";
    private static String testString2 = "DISTRIBUTED SYSTEMS";

    public static void main(String[] args)
    {
        PrintingObjectInterface obj = (PrintingObjectInterface)Communicate.lookup("printing");
	int testsfailed = 0;
	String ret = ""; 
	System.out.println("Calling Method");
        try{
	    for(int i = 0; i <= 100; i++) {
		ret = obj.printThis(testString2, 5);
	    }
	    obj.printThisException(testString, 10);
        } catch (Remote440Exception e)
        {
	    if(e.getCause().getClass() != ArithmeticException.class){
		testsfailed++;
		System.out.println("Exception not correctly thrown");
	    } else {
		System.out.println("Exception correctly thrown");
	    }
        }
	System.out.println(ret); 
	if(testsfailed == 0) 
	    System.out.println("All Tests Passsed");
    }
}