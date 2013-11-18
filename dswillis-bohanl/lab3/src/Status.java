import java.io.*;
import java.util.*;

public final class Status implements Serializable
{
    int mapSlots;
    int reduceSlots;
    List<Task> tasks;
}
