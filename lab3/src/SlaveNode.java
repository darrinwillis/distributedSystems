import java.io.*;
import java.net.*;
import java.util.*;

public class SlaveNode {
    public List<Task> tasks = new ArrayList<Task>();

    public String[][] parseFile(FilePartition p) {
        String name = p.getFileName();
        int index = p.getIndex();
        int size = p.getSize();
         
        RandomAccessFile f = null;
        try{
            f = new RandomAccessFile(name,"r");
            f.seek(index);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[][] kvs = new String[size][2];
        String line;
        
        for(int i = 0; i < size; i++) {
            try {
            line = f.readLine();
            kvs[i][0] = name;
            kvs[i][1] = line;
            } catch(Exception e) {
            e.printStackTrace();
            }
        }
        try{
            f.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return kvs;
    }

    public List<String[]> parseReduceFile(String fileName){
        RandomAccessFile f = null;

        try{
            f = new RandomAccessFile(fileName,"r");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String[]> kvs = new LinkedList<String[]>();
        String line = "";
        try{
            line = f.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        int index;
        String[] kv = new String[2];
        
        while(line != null) {
            try {
            index = line.indexOf("~");
            if(index == -1)
                break;
            kv[0] = line.substring(0,index);
            kv[1] = line.substring(index+1,line.length());
            kvs.add(kv.clone());
            line = f.readLine();
            } catch(Exception e) {
            e.printStackTrace();
            }
        }
        try{
            f.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return kvs;
    }

    public List<String[]> parseReduceFiles(List<String> inputFiles){
        int total = 0;
        List<String[]> kvs = new LinkedList<String[]>();
        
        for(String s : inputFiles){
            System.out.println("File " + s);
            for(String[] kv : parseReduceFile(s)){
            System.out.println(kv[0] + " " + kv[1]);
            kvs.add(kv);
            }
        }
        return kvs;
    }
	

    public void doMap(MapTask t){
        System.out.println("Doing Map");
        synchronized(tasks) {
            tasks.add(t);
        }
        Job j = t.getJob();
        FilePartition p = t.getPartition();
        
        String[][] kvs = parseFile(p);
        List<String[]> outputs = new ArrayList<String[]>();
        
        for(int i = 0; i < kvs.length; i++){
            outputs.addAll(j.map(kvs[i][0],kvs[i][1]));
        }
        String s;
        try {
            RandomAccessFile out = new RandomAccessFile(t.getOutputFile(),"rws");
        
            for(String[] kv : outputs) {
            s = kv[0] + "~" + kv[1] + "\n";
            byte[] b = s.getBytes();
            out.write(b);
            }

            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        synchronized(tasks) {
            tasks.remove(t);
        }
    }
                
    public void doReduce(ReduceTask t){
        System.out.println("Doing Reduce");
        synchronized(tasks) {
            tasks.add(t);
        }
        
        Job j = t.getJob();
        List<String[]> inputs = parseReduceFiles(t.getInputFiles());
        HashMap<String, List<String>> kvs = new HashMap<String, List<String>>();

        List<String> vals;
        for (String[] kv : inputs) {
            vals = kvs.get(kv[0]);
            if (vals == null)
            vals = new LinkedList<String>();
            vals.add(kv[1]);
            kvs.put(kv[0],vals);
        }

        System.out.println(kvs);

        List<String[]> outputs = new LinkedList<String[]>();
        
        Iterator it = kvs.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            String value = j.reduce(key,kvs.get(key),j.getIdentity());
            outputs.add(new String[]{key,value});
            it.remove(); 
        }
        String s;
        try {
            RandomAccessFile out = new RandomAccessFile(t.getOutputFile(),"rws");
            
            for(String[] kv : outputs) {
            s = kv[0] + "~" + kv[1] + "\n";
            byte[] b = s.getBytes();
            out.write(b);
            }

            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        synchronized(tasks) {
            tasks.remove(t);
        }
    }
    
}

    
	    
	
    
	    
	
	
