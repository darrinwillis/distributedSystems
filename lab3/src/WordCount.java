
import java.util.ArrayList;
import java.util.List;


public class WordCount extends Job {
    
    public WordCount() {
        super();
    }

    public String getIdentity() {
        return "0";
    }
    
    public List<String[]> map(String key, String val) {
        String[] words = val.split(" ");
        List<String[]> out = new ArrayList<String[]>(words.length);
        for (String s : words) {
            out.add(new String[] {s, "1"});
        }
                    
        return out;
    }

    public String reduce(String key, List<String> vals, String initVal) {
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
