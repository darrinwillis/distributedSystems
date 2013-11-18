import java.io.Serializable;
import java.util.List;
import java.util.*;

public class ReduceTask implements Serializable, Task {        
    private int nid;
    private int tid;
    private int jid;
    private List<String> inputFiles; 
    private List<Node> nodeList;
    private Job job;
    private String outputFile;   
        
    public ReduceTask(int taskId, int jobId, List<String> in, Job j, String o) {
        this.tid = taskId;
        this.jid = jobId;
        this.inputFiles = in;
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
    public int getNodeId() {
        return nid;
    }
    public void setNodeId(int nid) {
        this.nid = nid;
    }
    public List<Node> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<Node> nl) {
        this.nodeList = nl;
    }
    public List<String> getInputFiles() {
        return inputFiles;
    }
    public void setInputFiles(List<String> in) {
        this.inputFiles = in;
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