/******************************************************************
 This is an interface for any processes which wish to be migratable
 using this framework. TransactionalIO is another class which may be
 used to make the process more robust and functional
 ******************************************************************/

import java.lang.Runnable;
import java.io.*;

public interface MigratableProcess extends Runnable, Serializable
{
    //Run here is not strictly necessary, due to extending Runnable
    void run();
    void suspend();
}
