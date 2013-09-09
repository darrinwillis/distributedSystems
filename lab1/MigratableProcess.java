import java.lang.Runnable;
import java.io.Serializable;

public interface MigratableProcess extends Runnable, Serializable
{
    //void run();
    void suspend();
}
