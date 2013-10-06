//This is a wrapper for any exceptions that the remote object
//might throw because of internal issues

import java.io.*;

public class RMIException implements Serializable
{
    public Throwable theException;

    public RMIException(Throwable e)
    {
        theException = e;
    }
}
