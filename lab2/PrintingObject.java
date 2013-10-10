import java.rmi.*;

public class PrintingObject implements PrintingObjectInterface
{
    public int counter;
    public PrintingObject() {}

    public String printThis(String s, int i) throws Remote440Exception
    {
        for (int j = 0; j < i; j++)
        {
            System.out.println(s);
        }
	counter++;
        return ("Called " + counter + " Times");
    }

    public String printThisException(String s, int i) throws Remote440Exception
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
