import java.util.*;
import java.rmi.*;

public interface ProcessManagerClientInterface extends Remote
{
    List<String> getProcesses() throws RemoteException;
    void setProcesses(List<String> processes) throws RemoteException;
}
