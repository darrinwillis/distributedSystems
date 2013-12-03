import java.util.*;

public class Pair implements DataInterface
{
    public int x;
    public int y;

    public Pair(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int distance(DataInterface d) throws IllegalArgumentException
    {
        if (!d.getClass().equals(Pair.class)) {
            throw new IllegalArgumentException();
        }
        Pair p = (Pair)d;
        //Returns x2 + y2
        return ( (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y) );
    }

    public DataInterface average(List<DataInterface> list) throws IllegalArgumentException
    {
        if (list.size() == 0)
            throw new IllegalArgumentException();
        if (!list.get(0).getClass().equals(Pair.class)) {
            throw new IllegalArgumentException();
        }

        long xsum = 0;
        long ysum = 0;
        int num = list.size();
        Iterator<DataInterface> iter = list.iterator();
        while (iter.hasNext())
        {
            Pair p = (Pair) iter.next();
            xsum += p.x;
            ysum += p.y;
        }
        int xavg = (int)(xsum / num);
        int yavg = (int)(ysum / num);
        return new Pair(xavg, yavg);
    }

    public String toString()
    {
        return ("("+x+", "+y+")");
    }
}
