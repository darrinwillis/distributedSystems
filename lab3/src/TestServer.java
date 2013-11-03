import java.rmi.Naming;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class TestServer {
    public static void main(String[] args) throws Exception {
        ServerImpl server = new ServerImpl();
	Naming.rebind("TestServer", server);
        server.start();
        Thread.sleep(5 * 60 * 1000); // run for 5 minutes
        server.stop();
    }
}