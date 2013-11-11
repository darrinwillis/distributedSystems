import java.io.Serializable;
import java.util.List;

public class ReduceTask implements Serializable, Task {        
    private int slaveId;
    private int tid;
    private int jid;
    private List<String> inputFiles;
    private Job job;
    private String outputFile;
    private Status status;
        
    public ReduceTask(int taskId, int jobId, List<String> i, Job j,
                      String o) {
        this.tid = taskId;
        this.jid = jobId;
        this.inputFiles = i;
        this.job = j;
        this.outputFile = o;
        this.status = Status.NONE;
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
    public List<String> getInputFiles() {
        return inputFiles;
    }
    public void setInputFiles(List<String> inputFiles) {
        this.inputFiles = inputFiles;
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
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
        
    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

}