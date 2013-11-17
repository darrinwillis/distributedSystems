import java.io.*;
import java.util.*;

public abstract class Job implements Serializable {
    private int jid;
    private int totalMaps;
    private int totalReduces;
    private String outputFileName;
    private String inputFileName;
    private DistributedFile dFile;

    public Job() {
    }

    public void setDFile(DistributedFile file) {
        dFile = file;
        totalMaps = dFile.getBlocks().size();
    }
    public DistributedFile getDFile() {
        return dFile;
    }
    public void setInput(String inputFile){
	inputFileName = inputFile;
    }
    public void setOutput(String outputFile){
	outputFileName = outputFile;
    }
    public String getInput(){
	return inputFileName;
    }
    public String getOutput(){
	return outputFileName;
    }
    public int getJid() {
	return jid;
    }
    public void setJid(int j){
	jid = j;
    }
    public int getTotalMaps(){
	return totalMaps;
    }
    public void setTotalMaps(int i){
	totalMaps = i;
    }
    public int getTotalReduces(){
	return totalReduces;
    }
    public void setTotalReduces(int i){
	totalReduces = i;
    }
    
    //implemented by subclass
    public abstract String getIdentity();

    public abstract List<String[]> map(String key, String val);
    public abstract String reduce(String key, List<String> vals, String init);
}