public interface Task {
    public int getTaskId();
    public void setTaskId(int tid);
    public int getJobId();
    public void setJobId(int jid);
    public Job getJob();
    public void setJob(Job job);
}
