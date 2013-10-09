import java.rmi.*;

public class PrintingObject implements Remote440
{
    public PrintingObject() {}

    public String printThis(String s, int i)
    {
        for (int j = 0; j < i; j++)
        {
            System.out.println(s);
        }
        return "This is the intended return value";
    }

    public String printThisException(String s, int i)
    {
        for (int j = 0; j < i; j++)
        {
            System.out.println(s);
        }
        int z = 0;
        int k = 1/z;
        return "This is the intended return value";
    }
}
