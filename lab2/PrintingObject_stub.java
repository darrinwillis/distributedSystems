import java.rmi.*;
import java.lang.reflect.*;

public class PrintingObject_stub extends RemoteStub implements PrintingObjectInterface
{
    public PrintingObject_stub(RemoteObjectReference ref) {
        super(ref);
        }

    public String printThis(String s, int i) throws Remote440Exception
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

    public String printThisException(String s, int i) throws Remote440Exception 
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
