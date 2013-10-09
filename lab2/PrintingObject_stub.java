import java.rmi.*;
import java.lang.reflect.*;

public class PrintingObject_stub extends RemoteStub implements Remote440
{
    public PrintingObject_stub() {}

    public String printThis(String s, int i) throws Throwable
    {
        Object[] args = {s, i};
        String name = "printThis";
        Method m = null;
        try{
            m = PrintingObject.class.getMethod(name, new java.lang.Class[] {String.class, int.class});
        } catch(NoSuchMethodException e)
        {
            System.out.println("No such Method");
        }
        return (String) methodCall(m, args);
    }

    public String printThisException(String s, int i) throws Throwable
    {
        Object[] args = {s, i};
        String name = "printThisException";
        Method m = null;
        try{
            m = PrintingObject.class.getMethod(name, new java.lang.Class[] {String.class, int.class});
        } catch(NoSuchMethodException e)
        {
            System.out.println("No such Method");
        }
        return (String) methodCall(m, args);
    }
}
