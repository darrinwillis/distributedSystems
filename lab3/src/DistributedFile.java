import java.io.*;
import java.util.*;

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
    public DistributedFile(File file) throws IOException
    {
        this.filename = file.getName();
        this.blocks = new ArrayList<FilePartition[]>();
        int blockSize = Config.getBlockSize();
        int replicationFactor = Config.getReplicationFactor();
        long fileLength = lineCount(file);
        int numBlocks = (int)(fileLength / blockSize);
        int remainderSize = (int)(fileLength % blockSize);
        if (remainderSize > 0)
            numBlocks++;
        System.out.println("Length is "+fileLength+" blocksize is "+blockSize
            +" numBlocks is "+numBlocks+" remainderSize is "+remainderSize);
        File[] splitFiles = new File[numBlocks];
        for (int i = 0; i < numBlocks; i++)
        {
            FilePartition[] dups = new FilePartition[replicationFactor];
            // Names the file so it is stored locally
            String blockName = "/tmp/dfiles/" + filename + "part" + i;
            int thisSize = (i == numBlocks - 1) && (remainderSize > 0)
                ? remainderSize : blockSize ;
            // Create a new local file to send to a node
            File blockFile = new File(blockName);
            splitFiles[i] = blockFile;
            // Setup each replicated part to have correct info
            for (int k = 0; k < replicationFactor; k++)
            {
                dups[k] = new FilePartition(blockName, i, thisSize);
            }
            this.blocks.add(dups);
        }
        // Copy file parts into new file
        splitFile(file, splitFiles, blockSize);
    }

    private void splitFile(File source, File[] destinations, int size)
    {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(source));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try{
            String line;
            line = reader.readLine();
            for (File eachFile : destinations)
            {
                if (eachFile != null) {
                    eachFile.getParentFile().mkdirs();
                    PrintWriter writer = new PrintWriter(new FileWriter(eachFile));
                    for (int i = 0; (i < size) && (line != null); i++)
                    {
                        writer.println(line);
                        line = reader.readLine();
                    }
                    writer.flush();
                    writer.close();
                } else {
                    System.out.println("file null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<FilePartition[]> getBlocks() {
        return blocks;
    }

    public String getFileName() {
        return filename;
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
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
}




