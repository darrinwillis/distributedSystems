import java.io.*;

public class FilePartition implements Serializable {       
    Node location;
    String fileName;
    int index;
    int size;

    public FilePartition (String n, int i, int s, Node location) {
        fileName = n;
        index = i;
        size = s;
        this.location = location;
    }
    
    public FilePartition (String n, int i, int s) {
        fileName = n;
        index = i;
        size = s;
        location = null;
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

    public Node getLocation() {
        return location;
    }

    public void setLocation(Node loc) {
        this.location = loc;
    }
}
