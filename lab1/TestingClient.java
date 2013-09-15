import java.rmi.*;

class TestingClient
{

    static final String DefaultMasterServerURL = "rmi://unix12.andrew.cmu.edu/processDelegationServer";

    public TestingClient(String masterServerURL)
    {
        try
        {
            System.setSecurityManager (new RMISecurityManager());
            MasterServerInterface processMaster = (MasterServerInterface) Naming.lookup(masterServerURL);
            for (int i = 0; i < 1000; i++)
            {
                Class<GrepProcess> processClass = GrepProcess.class;
                String[] strings = {" ", "ProcessDelegationServer.java", "out.txt"};
                Object[] arguments = {strings};
                try {
                    processMaster.addProcess(processClass, arguments);
                } catch (RemoteException e)
                {
                    System.out.println("Failed to add process to server");
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main (String []args)
    {
        String MasterServerURL = DefaultMasterServerURL;
        if (args.length != 0)
            MasterServerURL = args[0];
        TestingClient client = new TestingClient(MasterServerURL);
    }

}
