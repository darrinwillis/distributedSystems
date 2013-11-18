
import java.util.ArrayList;
import java.util.List;


public class Grep extends Job {
    private String ref = "and";
    
    public Grep() {
        super();
    }

    public String getIdentity() {
        return "0";
    }
    
    public List<String[]> map(String key, String val) {
        List<String[]> out = new ArrayList<String[]>();
        if (val.toLowerCase().contains(ref.toLowerCase()))
            out.add(new String[] {"yes", val});
                    
        return out;
    }

    public String reduce(String key, List<String> vals, String initVal) {
        String sum = "";
        for (String s : vals) {
            try {
                sum += vals + "\n";
            } catch (Exception e) {
                System.err.println("Error parsing number string:" + s);
            }
        }
        return sum;
    }

}
