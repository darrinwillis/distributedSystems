/**
 * @(#)TransactionalFileInputStream.java
 *
 *
 * @author 
 * @version 1.00 2013/9/8
 */
import java.io.*;

public class TransactionalFileInputStream extends java.io.InputStream implements java.io.Serializable {

	String file;
	long index;
	
    public TransactionalFileInputStream(String path) {
    	file = path;
    	index = 0;
    }
    
    public TransactionalFileInputStream(String path, long i) {
    	file = path;
    	index = i;
    }
    
    public int read() {
    	int i = -1;
    	try{
    		FileInputStream in = new FileInputStream(file);
	    	in.skip(index);
	    	i = in.read();
	    	in.close(); 
    	} catch(Exception e) {
    		System.out.println("Exception: " + e.getMessage());
    	} 

    	if(i >= 0)
    		index = index + 1;
    		
    	return i; 
    }
    
   	public int read(byte[] b, int off, int len) {
   		int i = -1;
    	try{
    		FileInputStream in = new FileInputStream(file);
	    	in.skip(index);
	    	i = in.read(b,off,len);
	    	in.close(); 
    	} catch(Exception e) {
    		System.out.println("Exception: " + e.getMessage());
    	} 

    	if(i >= 0)
    		index = index + 1;
    		
    	return i; 
   	}
   	
   	public int read(byte[] b) {
		int i = -1;
    	try{
    		FileInputStream in = new FileInputStream(file);
	    	in.skip(index);
	    	i = in.read(b);
	    	in.close(); 
    	} catch(Exception e) {
    		System.out.println("Exception: " + e.getMessage());
    	} 

    	if(i >= 0)
    		index = index + 1;
    		
    	return i;   		
   	}
   
}