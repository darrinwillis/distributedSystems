
import java.util.ArrayList;
import java.util.List;


public class WordCount implements Job {
    /**
     *
     */
    private static final long serialVersionUID = -6217380880151520602L;
    private List<String> inputFiles;
    private String outputFile;
        
    @Override
        public void setInput(List<String> inputFiles) {
	this.inputFiles = inputFiles;
    }

    @Override
        public void setOutput(String outputFile) {
	this.outputFile = outputFile;
    }

    @Override
        public List<String> getInput() {
	return inputFiles;
    }

    @Override
        public String getOutput() {
	return outputFile;
    }

    @Override
        public String getIdentity() {
	return "0";
    }

    @Override
        public List<String[]> map(String key, String val) {
	System.out.println("Map");
	String[] words = val.split(" ");
	List<String[]> out = new ArrayList<String[]>(words.length);
	for (String s : words) {
	    out.add(new String[] {s, "1"});
	}
                
	return out;
    }

    @Override
        public String reduce(String key, List<String> vals, String initVal) {
	System.out.println("Reduce");
	int count = Integer.parseInt(initVal);
	for (String s : vals) {
	    try {
		count += Integer.parseInt(s.trim());
	    } catch (Exception e) {
		System.err.println("Error parsing number string:" + s);
	    }
	}
	return ""+count;
    }

}