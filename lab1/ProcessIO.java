import java.rmi.*;
import java.io.*;
import java.util.*;

class ProcessIO{
    public static void writeProcess(MigratableProcess process, String filename){
	try{   	
	    TransactionalFileOutputStream out = new TransactionalFileOutputStream(filename, false);
 	    ObjectOutputStream oos = new ObjectOutputStream(out); 
	    		 
	    oos.writeObject(process);
	    	
	    oos.close();
	    out.close();
    	} catch (Exception e)	{
	    System.out.println("Exception: " + e.getMessage());
        }
    }

    public static MigratableProcess readProcess(String filename){
	try{	
	    TransactionalFileInputStream in = new TransactionalFileInputStream(filename);
	    ObjectInputStream ois = new ObjectInputStream(in);
	    	
	    MigratableProcess p = (MigratableProcess)ois.readObject();
	    	
	    ois.close();
	    in.close();
	    return p;
    	} catch (Exception e)	{
	    System.out.println("Exception: " + e.getMessage());
	    return null;
        }
    }
}
