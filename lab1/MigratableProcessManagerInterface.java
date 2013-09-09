import java.util.*;

public interface MigratableProcessManagerInterface
{
    public List<MigratableProcess> lookupStarts();
    public List<MigratableProcess> lookupEnds();
}
