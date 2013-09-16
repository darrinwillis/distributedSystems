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
	public long index;
	
    private static final long serialVersionUID = 987654321;
    public TransactionalFileOutputStream(String path, boolean append) {
    	file = path;
    	File f = new File(path);
    	if (append) 
    		index = f.length();
    	else
    		index = 0;
    }
    public void write(int b) throws IOException {
    	RandomAccessFile out = new RandomAccessFile(file,"rws");
    	out.seek(index);
    	out.write(b);
    	out.close();	
    	index++;
    }
    public void write(byte[] b,int off,int len) throws IOException {
    	RandomAccessFile out = new RandomAccessFile(file,"rws");
    	out.seek(index);
    	out.write(b,off,len);
    	out.close();
    	index = off + len;
    }
    public void write(byte[] b) throws IOException {
		RandomAccessFile out = new RandomAccessFile(file,"rws");
    	out.seek(index);
    	out.write(b);
    	out.close();
    	index = index + b.length;
    }
}
