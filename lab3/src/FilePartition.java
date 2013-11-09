import java.io.*;

public class FilePartition implements Serializable {       
    String fileName;
    int index;
    int size;

    public FilePartition (String n, int i, int s) {
        fileName = n;
        index = i;
        size = s;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return size;
    }

    public String getFileName() {
        return fileName;
    }
}