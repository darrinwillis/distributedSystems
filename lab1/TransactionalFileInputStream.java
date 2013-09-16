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
    private static final long serialVersionUID = 123456789;

    public TransactionalFileInputStream(String path) {
    	file = path;
    	index = 0;
    }
    
    public TransactionalFileInputStream(String path, long i) {
    	file = path;
    	index = i;
    }
    
    public int read() throws IOException {
    	FileInputStream in = new FileInputStream(file);
	    in.skip(index);
	    int i = in.read();
	    in.close(); 

    	if(i >= 0)
    		index++;
    		
    	return i; 
    }
    
   	public int read(byte[] b, int off, int len) throws IOException {
   		FileInputStream in = new FileInputStream(file);
	    in.skip(index);
	    int i = in.read(b,off,len);
	    in.close(); 

    	if(i >= 0)
    		index = index + i;
    		
    	return i;
   	}
   	
   	public int read(byte[] b) throws IOException {
		FileInputStream in = new FileInputStream(file);
	    in.skip(index);
	    int i = in.read(b);
	    in.close(); 

    	if(i >= 0)
    		index = index + i;
    		
    	return i; 		
   	}
   
}
