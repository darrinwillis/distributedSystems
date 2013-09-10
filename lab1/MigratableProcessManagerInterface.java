import java.util.*;
import java.rmi.*;

public interface MigratableProcessManagerInterface extends Remote
{
    public List<MigratableProcess> lookupCurrentProcesses() throws RemoteException;
}
