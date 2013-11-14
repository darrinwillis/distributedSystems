import java.io.Serializable;

public class MapTask implements Serializable, Task{    
    private int tid;
    private int jid;
    private int slaveId; //location of the node
    private FilePartition partition;
    private Job job;
    private String outputFile;
        
    public MapTask(int t, int ji, FilePartition p, Job j,
                   String o) {
        this.tid = t;
        this.jid = ji;
        this.partition = p;
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
    public String getOutputFile() {
        return outputFile;
    }
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
}
