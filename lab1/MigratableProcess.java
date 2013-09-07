import java.lang.Runnable;
import java.io.Serializable;

public interface MigratableProcess implements Runnable, Serializable
{
    void run();
    void suspend();
}
