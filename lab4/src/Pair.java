public class Pair implements DataInterface
{
    public int x;
    public int y;

    public Pair(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int distance(DataInterface d) throws IllegalArgumentException{
        if (!d.getClass().equals(Pair.class)) {
            throw new IllegalArgumentException();
        }
        Pair p = (Pair)d;
        //Returns x2 + y2
        return ( (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y) );
    }

    public String toString()
    {
        return ("("+x+", "+y+")");
    }
}
