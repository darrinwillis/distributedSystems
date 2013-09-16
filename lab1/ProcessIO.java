/* This is an abstraction of process writing to handle it for both
 * the client and the server.
 */

import java.rmi.*;
import java.io.*;
import java.util.*;

class ProcessIO{
    
    // Writes a process to file
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

    // Reads a process from a file
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
            return null;
        }
    }

    // Deletes a file
    public static void delete(String filename){
        File fileToDelete = new File(filename);
        if (fileToDelete.delete())
            System.out.println("Successfully deleted: " + filename);
        else
            System.out.println("Failed to delete: " + filename);   
    }
}
