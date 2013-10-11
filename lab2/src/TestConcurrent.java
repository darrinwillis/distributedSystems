import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class TestConcurrent extends Thread
{
    private static String testString = "This is a test string";
    private static String testString2 = "DISTRIBUTED SYSTEMS";
    private static int increment = 5;
    private static PrintingObjectInterface obj;
    
    public void run() {
	try{
	    obj.printThis(testString2, increment);
	    obj.printThisException(testString, 2*increment);
        } catch (Remote440Exception e) {
	    if(e.getCause().getClass() != ArithmeticException.class){
		System.out.println("Exception not correctly thrown");
		e.printStackTrace();
	    } else {
		System.out.println("Exception correctly thrown");
	    }
	}
    }
	
    public static void main(String[] args)
    {
        obj = (PrintingObjectInterface)Communicate.lookup("printing1");
	try {
	    int start = obj.getCounter();
	    for(int i = 0; i < 50; i++)
		(new TestConcurrent()).start();
	    Thread.sleep(8000);
	    if(obj.getCounter() == start + 50) 
		System.out.println("All tests passed");
	    else
		System.out.println("Failure");
	} catch(Exception e) {}
        
    }
}
