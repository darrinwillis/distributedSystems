import java.io.Serializable;
import java.util.List;
import java.util.*;

public class ReduceTask implements Serializable, Task {        
    private int slaveId;
    private int tid;
    private int jid;
    private SortedMap<String, List<String>> kvs; 
    private Job job;
    private String outputFile;   
        
    public ReduceTask(int taskId, int jobId, SortedMap<String, List<String>> k, Job j, String o) {
        this.tid = taskId;
        this.jid = jobId;
        this.kvs = k;
        this.job = j;
        this.outputFile = o;
    }
        
    public int getTaskId() {
        return tid;
    }
    public void setTaskId(int tid) {
        this.tid = tid;
    }
    public int getJobId() {
        return jid;
    }
    public void setJobId(int jid) {
        this.jid = jid;
    }
    public SortedMap<String, List<String>> getKvs() {
        return kvs;
    }
    public void setKvs(SortedMap<String, List<String>> k) {
        this.kvs = k;
    }
    public Job getJob() {
        return job;
    }
    public void setJob(Job job) {
        this.job = job;
    }
    public String getOutputFile() {
        return outputFile;
    }
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

}