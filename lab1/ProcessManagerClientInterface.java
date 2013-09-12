import java.util.*;
import java.rmi.*;

public interface ProcessManagerClientInterface extends Remote
{
    List<MigratableProcess> getProcesses() throws RemoteException;
    void setProcesses(List<MigratableProcess> processes) throws RemoteException;
}
