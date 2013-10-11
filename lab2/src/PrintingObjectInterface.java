public interface PrintingObjectInterface extends Remote440
{
    public int getCounter() throws Remote440Exception; 
    public int printThis(String s, int i) throws Remote440Exception;
    public String printThisException(String s, int i) throws Remote440Exception;
}
