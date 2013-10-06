public class PrintingObject
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
}
