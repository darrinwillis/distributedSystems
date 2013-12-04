import java.util.*;

public class DNA implements DataInterface
{
    public String value;

    public DNA(String v){
        this.value = v;
    }

    public int distance(DataInterface d) throws IllegalArgumentException
    {
        if (!d.getClass().equals(DNA.class)) {
            throw new IllegalArgumentException();
        }
        DNA dna = (DNA)d;
        //Returns number of bases - different bases
        int length = dna.value.length();
        int differences = 0;
        char[] ca1 = this.value.toCharArray();
        char[] ca2 = dna.value.toCharArray();
        for (int i = 0; i < length; i++)
        {
            if (ca1[i] != ca2[i]) differences++;
        }
        return differences;
    }

    public DataInterface average(List<DataInterface> list) throws IllegalArgumentException
    {
        if (list.size() == 0)
            throw new IllegalArgumentException();
        if (!list.get(0).getClass().equals(DNA.class)) {
            throw new IllegalArgumentException();
        }

        List<DNA> dnaList = (List<DNA>)(List<?>) list;

        DNA[] dnas = dnaList.toArray(new DNA[0]);
        int length = dnas[0].value.length();
        String average = "";
        for (int i = 0; i < length; i++)
        {
            int as = 0;
            int ts = 0;
            int cs = 0;
            int gs = 0;

            // Find most common letter
            for (int j = 0; j < dnas.length; j++)
            {
                char c = dnas[j].value.charAt(i);
                if (c == 'a')
                    as++;
                else if (c == 't')
                    ts++;
                else if (c == 'c')
                    cs++;
                else if (c == 'g')
                    gs++;
            }
            //Places most common letter
            
            int max = Math.max(Math.max(Math.max(as, ts), cs), gs);
            if (max == as)
                average += 'a';
            else if (max == cs)
                average += 'c';
            else if (max == gs)
                average += 'g';
            else if (max == ts)
                average += 't';
        }
        return new DNA(average);
    }

    public String toString()
    {
        return this.value;
    }
}
