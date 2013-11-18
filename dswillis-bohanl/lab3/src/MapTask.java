import java.io.Serializable;
import java.util.*;

public class MapTask implements Serializable, Task{    
    private int tid;
    private int jid;
    private int slaveId; //location of the node
    private FilePartition partition;
    private Job job;
    private List<String> outputFiles;
        
    public MapTask(int t, int ji, FilePartition p, Job j) {
        this.tid = t;
        this.jid = ji;
        this.partition = p;
        this.job = j;
        this.outputFiles = new LinkedList<String>();
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
    public FilePartition getPartition() {
        return partition;
    }
    public void setPartition(FilePartition partition) {
        this.partition = partition;
    }
    public Job getJob() {
        return job;
    }
    public void setJob(Job job) {
        this.job = job;
    }
    public List<String> getOutputFiles() {
        return outputFiles;
    }
    public void setOutputFile(List<String> outputFiles) {
        this.outputFiles = outputFiles;
    }
}
