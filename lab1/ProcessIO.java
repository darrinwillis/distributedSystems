import java.rmi.*;
import java.io.*;
import java.util.*;

class ProcessIO{
    public static void writeProcess(MigratableProcess process, String filename){
	    try{   	
	        FileOutputStream out = new FileOutputStream(filename, false);
 	        ObjectOutputStream oos = new ObjectOutputStream(out); 
	    		 
	        oos.writeObject(process);
	    	
	        oos.close();
	        out.close();
    	} catch (Exception e)	{
	        System.out.println("Error writing process");
            e.printStackTrace();
        }
    }

    public static MigratableProcess readProcess(String filename){
	    try{	
	        FileInputStream in = new FileInputStream(filename);
	        ObjectInputStream ois = new ObjectInputStream(in);
	    	
	        MigratableProcess p = (MigratableProcess)ois.readObject();
	    	
	        ois.close();
	        in.close();
	        return p;
    	} catch (Exception e)	{
	        System.out.println("Error reading process");
	        e.printStackTrace();
            return null;
        }
    }
}
