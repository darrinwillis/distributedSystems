public interface Task {
    public int getSlaveId();
    public void setSlaveId(int slaveId);
    public int getTaskId();
    public void setTaskId(int tid);
    public int getJobId();
    public void setJobId(int jid);
    public Job getJob();
    public void setJob(Job job);
    public char getStatus();
    public void setStatus(char status);
        
    public String getOutputFile();
}