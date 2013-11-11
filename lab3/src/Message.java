import java.io.*;

public class Message implements Serializable{
    public MessageType type;
    public Task task;
    public Job job;

    public Message(MessageType t) {
	type = t;
    }
}