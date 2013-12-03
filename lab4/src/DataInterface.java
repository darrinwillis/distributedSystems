import java.util.*;

public interface DataInterface
{
    //Provides a distance to another piece of data
    public int distance(DataInterface d) throws IllegalArgumentException;
    // This could be static, but java interface methods can't be static unti Java 8
    public DataInterface average(List<DataInterface> list) throws IllegalArgumentException;
}
