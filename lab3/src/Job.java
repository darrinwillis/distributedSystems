import java.io.*;
import java.util.*;

public interface Job extends Serializable {
    public void setInput(List<String> inputFiles);
    public void setOutput(String outputFile);
    
    public List<String> getInput();
    public String getOutput();

    public int getRecordSize();
    public String getReduceIdentity();

    public List<String[]> map(String key, String val);
    public String reduce(String key, List<String> vals, String init);
}