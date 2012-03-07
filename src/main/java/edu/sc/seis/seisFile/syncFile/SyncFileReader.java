package edu.sc.seis.seisFile.syncFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.SeisFileRuntimeException;


public class SyncFileReader implements Iterator<SyncLine> {
    
    public SyncFileReader(String filename) throws IOException {
        this(new File(filename));
    }
    
    public SyncFileReader(File inFile) throws IOException {
        this(new BufferedReader(new FileReader(inFile)));
    }
    
    public SyncFileReader(BufferedReader in) throws IOException {
        this.in = in;
        header = in.readLine();
    }
    
    BufferedReader in;

    String header;
    
    SyncLine nextLine;
    
    void loadNext() throws SeisFileRuntimeException {
        try {
        if (nextLine == null && in != null) {
            String line = in.readLine();
            if (line != null) {
                nextLine = SyncLine.parse(line);
            } else {
                try {
                in.close();
                } catch (IOException e) {
                    // oh well...
                }
                in = null;
            }
        }
        } catch (SeisFileException e) {
            throw new SeisFileRuntimeException(e);
        } catch(IOException e) {
            throw new SeisFileRuntimeException("Unable to read next line.", e);
        }
    }
    
    @Override
    public boolean hasNext() {
        loadNext();
        return nextLine == null;
    }

    @Override
    public SyncLine next() {
        if (hasNext()) {
            SyncLine out = nextLine;
            nextLine = null;
            return out;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported");
    }
}
