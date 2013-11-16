import java.io.*;
import java.net.*;
import java.util.*;

public class SlaveNode {
    public List<Task> tasks;
    
    private HashMap<Integer,RandomAccessFile> files;

    public SlaveNode() {
	files = new HashMap<Integer,RandomAccessFile>();
	tasks = new ArrayList<Task>(); 
    }

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
            for(String[] kv : parseReduceFile(s)){
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
        String s;
        String[][] kvs = parseFile(p);
        try {
	    for(int i = 0; i < kvs.length; i++){
		List<String[]> outputs = j.map(kvs[i][0],kvs[i][1]);
		for(String[] kv : outputs) {
                    int code = kv[0].hashCode() % j.getTotalReduces();
                    RandomAccessFile f = files.get(code);
		    if(f == null) {
			f = new RandomAccessFile(j.getJid() + "reduce" + code,"rws");
			files.put(code,f);
		    }
		    s = kv[0] + "~" + kv[1] + "\n";
		    f.write(s.getBytes());
		}
	    }
	    Iterator it = files.entrySet().iterator();
	    while (it.hasNext()) {
		Map.Entry pairs = (Map.Entry)it.next();
		RandomAccessFile value = (RandomAccessFile) pairs.getValue();
		if(value != null)
		    value.close();
		it.remove(); 
	    }
        } catch(Exception e) {
            e.printStackTrace();
        }
        synchronized(tasks) {
            tasks.remove(t);
        }
    }	

    public SortedMap<String, List<String>> sort(List<String> files) {
	List<String[]> inputs = parseReduceFiles(files);
	SortedMap<String, List<String>> kvs = new TreeMap<String, List<String>>();

        List<String> vals;
        for (String[] kv : inputs) {
            vals = kvs.get(kv[0]);
            if (vals == null)
		vals = new LinkedList<String>();
            vals.add(kv[1]);
            kvs.put(kv[0],vals);
        }
	return kvs;
    }		    
                
    public void doReduce(ReduceTask t){
        System.out.println("Doing Reduce");
        synchronized(tasks) {
            tasks.add(t);
        }
	Job j = t.getJob();
        List<String[]> outputs = new LinkedList<String[]>();
        
	SortedMap<String, List<String>> kvs = sort(t.getInputFiles());
        
        String s;
        try {
            RandomAccessFile out = new RandomAccessFile(t.getOutputFile(),"rws");           
	    Iterator it = kvs.keySet().iterator();
	    while (it.hasNext()) {
		String key = (String)it.next();
		String value = j.reduce(key,kvs.get(key),j.getIdentity());
		s = key + "~" + value + "\n";
		out.write(s.getBytes());
		it.remove(); 
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

    
	    
	
    
	    
	
	
