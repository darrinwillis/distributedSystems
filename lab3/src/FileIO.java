import java.io.*;
import java.rmi.*;
import java.util.*;

public class FileIO {
    
    public static int BUF_SIZE;
    
    private static final String configFileName = "fileConfig.txt";

    static {
	parseFile(configFileName);
    }
    private static void parseFile(String filename)
    {
        Properties prop = new Properties();
        try{
            prop.load(new FileInputStream(configFileName));
        } catch (FileNotFoundException e) {
            System.out.println("No config file found named: " + configFileName);
            prop = Config.generateConfigFile(); 
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try{
            //Load in all config properties
            BUF_SIZE = Integer.parseInt(prop.getProperty("BUF_SIZE"));
        
        } catch (NumberFormatException e) {
            System.out.println("Incorrectly formatted number " + e.getMessage());
        }
        return;
    }
	
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
    
    public static void upload(FileServerInterface server, File src, 
			      File dest) throws IOException {
        copy (new FileInputStream(src), server.getOutputStream(dest));
    }
    public static void download(FileServerInterface server, File src, 
				File dest) throws IOException {
        copy (server.getInputStream(src), new FileOutputStream(dest));
    }
}