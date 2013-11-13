import java.io.*;

public class DistributedFile {
    private String filename;
    private ArrayList<FilePartition[]> blocks;

    public DistributedFile(String filename, ArrayList<FilePartition[]> blocks)
    {
        this.filename = filename;
        this.blocks = blocks;
    }

    //Returns simple datastructure without making any files
    //Requires this file to exist
    public DistributedFile(File file, String filename) throws IOException
    {
        long blockSize = Config.getBlockSize();
        int replicationFactor = Config.getReplicationFactor();
        long fileLength = lineCount(file);
        long numBlocks = fileLength / blockSize;
        long remainderSize = fileLength % blockSize;
        if (remainderSize > 0)
            numBlocks++;
        for (long i = 0; i < numBlocks; i+= blockSize)
        {
            FilePartition[] dups = new FilePartition[replicationFactor];
            String blockName = filename + "p" + i + ".dist";
            long thisSize = (i == numBlocks - 1) && (remainderSize > 0)
                ? blockSize : remainderSize;
            for (int k = 0; k < replicationFactor; k++)
            {
                dups[k].location = null;
                dups[k].fileName = fileName;
                dups[k].index = i;
                dups[k].size = thisSize;
            }
        }

    }

    //Credit to http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
    //For a heavily optimized linecount
    private long lineCount(File file) throws IOException
    {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            int bufferSize = 1024;
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1)
            {
                empty = false;
                for (int i = 0; i < readChars; i++)
                {
                    if (c[i] == '\n')
                        count++;
                }
            }
            return (count == 0 && ~empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
}




