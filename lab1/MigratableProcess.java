import java.lang.Runnable;
import java.io.*;

public interface MigratableProcess extends Runnable, Serializable
{
    void run();
    void suspend();
}
