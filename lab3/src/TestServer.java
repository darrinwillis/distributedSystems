import java.rmi.Naming;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class TestServer {
    public static void main(String[] args) throws Exception {
        try {
	    ServerSocket ss = new ServerSocket(15444);
	    Socket slave = ss.accept();
	    Socket s1 = new Socket("ww109-c3.ww.andrew.cmu.edu",15444);
	    Socket s2 = new Socket("unix1.andrew.cmu.edu",15444);
	    System.out.println("Slave connected");

	    ObjectInputStream fromSlave = new ObjectInputStream(slave.getInputStream());
	    ObjectOutputStream toSlave1 = new ObjectOutputStream(s1.getOutputStream());
	    ObjectOutputStream toSlave2 = new ObjectOutputStream(s2.getOutputStream());

	    Message m = null;

	    m = (Message) fromSlave.readObject();
	    
	    System.out.println("Recieved Message");

	    System.out.println("Relaying Message");
	    

	    m.type = MessageType.MAP;
	    m.task = new MapTask(1,1,new FilePartition("in.txt",0,3),m.job,"out.txt");	    
	    Message m2 = new Message(MessageType.MAP);
	    m2.task = new MapTask(2,1,new FilePartition("in.txt",0,3),m.job,"out1.txt");

	    toSlave1.writeObject(m);
	    toSlave1.flush();
	    toSlave2.writeObject(m2);
	    toSlave2.flush();
	    
	    System.out.println("Waiting for ACK");
	    Message in = (Message)fromSlave.readObject();
	    System.out.println(in.type);
	    
	    System.out.println("Finished Mapping");

	    m.type = MessageType.REDUCE;
	    List<String> inputs = new LinkedList<String>();
	    inputs.add("out.txt");
	    inputs.add("out1.txt");

	    m.task = new ReduceTask(2,1,inputs,m.job,"final.txt");
	    
	    toSlave1.reset();
	    toSlave1.writeObject(m);
	    toSlave1.flush();
	    
	    System.out.println("Waiting for ACK");
	    fromSlave.readObject();

	    System.out.println("Finished Reducing");

	} catch (Exception e){
	    e.printStackTrace();
	}

	
    }
}
