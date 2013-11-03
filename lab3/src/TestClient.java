import java.io.*;
import java.rmi.*;

public class TestClient {
    
    final public static int BUF_SIZE = 1024 * 64;
    
    public static void copy(InputStream in, OutputStream out) 
	throws IOException {
	if (in instanceof RMIInputStream) {
	    System.out.println("using RMIPipe of RMIInputStream");
	    ((RMIInputStream) in).transfer(out);
	    return;
	}
    
	if (out instanceof RMIOutputStream) {
	    System.out.println("using RMIPipe of RMIOutputStream");
	    ((RMIOutputStream) out).transfer(in);
	    return;
	}
	System.out.println("using byte[] read/write");
	byte[] b = new byte[BUF_SIZE];
	int len;
	while ((len = in.read(b)) >= 0) {
	    out.write(b, 0, len);
	}
	in.close();
	out.close();
    }
    
    public static void upload(MasterFileServerInterface server, File src, 
			      File dest) throws IOException {
        copy (new FileInputStream(src), server.getOutputStream(dest));
    }
    public static void download(MasterFileServerInterface server, File src, 
				File dest) throws IOException {
        copy (server.getInputStream(src), new FileOutputStream(dest));
    }
    public static void main(String[] args) throws Exception {
        
        String url = "rmi://unix12.andrew.cmu.edu/masterServer";
        MasterFileServerInterface server = (MasterFileServerInterface) Naming.lookup(url);
        server.addNewFile("hello");
        
        File testFile = new File("out.txt");
        
        download(server, testFile, new File("download.txt"));
        System.out.println("downloaded");
        
        upload(server, new File("download.txt"), new File("in.txt"));
        System.out.println("uploaded");
    }
}