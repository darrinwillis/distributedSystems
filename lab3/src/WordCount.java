
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
        String[] ws = val.split(" ");
        List<String[]> out = new ArrayList<String[]>(ws.length);
        for (String s : ws) {
            out.add(new String[] {s, "1"});
        }
                    
        return out;
    }

    public String reduce(String key, List<String> vals) {
        int c = Integer.parseInt(getIdentity());
        for (String s : vals) {
            c += Integer.parseInt(s.trim());
            
        }
        return ""+c;
    }

}
