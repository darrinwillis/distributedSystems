/**
 * @(#)TransactionalFileOutputStream.java
 *
 *
 * @author 
 * @version 1.00 2013/9/8
 */

import java.io.*;

public class TransactionalFileOutputStream extends java.io.OutputStream implements java.io.Serializable {
	String file;
	public int index;
	public boolean append;
	
    public TransactionalFileOutputStream(String path, boolean b) {
    	file = path;
    	index = 0;
    	append = b;
    }
    public void write(int b) {
    	try{
    		OutputStream out = new FileOutputStream(file);
    		out.write(b);
    		out.close();
    	} catch(Exception e) {
    		System.out.println("Exception: " + e.getMessage());
    	}
    }
    public void write(byte[] b,int off,int len) {
    	try{
    		OutputStream out = new FileOutputStream(file);
    		out.write(b,off,len);
    		out.close();
    	} catch(Exception e) {
    		System.out.println("Exception: " + e.getMessage());
    	}
    }
    public void write(byte[] b) {
    	try{
    		OutputStream out = new FileOutputStream(file);
    		out.write(b);
    		out.close();
    	} catch(Exception e) {
    		System.out.println("Exception: " + e.getMessage());
    	}
    }
}
