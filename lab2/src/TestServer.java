import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class TestServer
{
    private static String testString = "This is a test string";

    public static void main(String[] args)
    {
        PrintingObject po1 = new PrintingObject();
        Communicate.rebind("printing1", po1);
        PrintingObject po2 = new PrintingObject();
        Communicate.rebind("printing2", po2);
    }
}
