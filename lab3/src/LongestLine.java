import java.util.*;

class LongestLine extends Job {

    public String getIdentity() {
        return "0";
    }

    public List<String[]> map(String key, String val) {
        List<String[]> output = new ArrayList<String[]>();

        output.add(new String[] { "line", val});
                        
        return output;
    }

    public String reduce(String key, List<String> vals) {
        int maxLen = Integer.parseInt(getIdentity());
        String longestLine = "";
        for (String s : vals) {
            if (s.length() > maxLen) {
                maxLen = s.length();
                longestLine = s;
            }
        }
                
        return longestLine;
    }

}