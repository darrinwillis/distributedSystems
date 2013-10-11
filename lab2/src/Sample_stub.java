import java.lang.reflect.Method;

public final class Sample_stub extends RemoteStub
  implements SampleInterface
{
  private static final long serialVersionUID = 2L;
  private static Method $method_hello_0;

  static
  {
    try
    {
      $method_hello_0 = SampleInterface.class.getMethod("hello", new Class[] { Integer.TYPE });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError("stub class initialization failed");
    }
  }

  public String hello(int paramInt) 
  {
      Integer i =new Integer(paramInt);
      Object[] args = new Object[1];
      args[0] = i;
      try{
	  return (String)methodCall($method_hello_0, args);	  
      } catch(Throwable t) {
	  return "";
      }
  }
}

/* Location:           C:\Users\Bohan\Desktop\lab2\
 * Qualified Name:     Sample_Stub
 * JD-Core Version:    0.6.2
 */