package edu.sc.seis.seisFile.earthworm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileBufferingEarthwormExport extends BufferingEarthwormExport {

    public FileBufferingEarthwormExport(int port,
                                        int module,
                                        int institution,
                                        String heartbeatMessage,
                                        int heartbeatSeconds,
                                        int bufferSize,
                                        int sleepMillis) throws UnknownHostException, IOException {
        super(port, module, institution, heartbeatMessage, heartbeatSeconds, bufferSize, sleepMillis);
        fileBuffers = Collections.synchronizedList(new ArrayList<File>());
        popBuffer = Collections.synchronizedList(new ArrayList<TraceBuf2>(maxSize));
    }
    
    
    
    @Override
    public void offer(TraceBuf2 tb) {
        synchronized(buffer) {
            if (buffer.size() == getMaxSize()) {
                List<TraceBuf2> tempBuf = new ArrayList<TraceBuf2>(getMaxSize());
                Collections.copy(tempBuf, buffer);
                buffer.clear();
                File bufFile = nextBufferFile();
                fileBuffers.add(bufFile);
                try {
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(bufFile)));
                    for (TraceBuf2 traceBuf2 : tempBuf) {
                        traceBuf2.write(dos);
                    }
                } catch(FileNotFoundException e) {
                    logger.error("Unable to save to file", e);
                } catch(IOException e) {
                    logger.error("Unable to save to file", e);
                }
                
            }
            super.offer(tb);
        }
    }



    @Override
    public TraceBuf2 pop() {
        while(popBuffer.size() == 0 && fileBuffers.size() != 0) {
            File next = fileBuffers.remove(0);
            DataInputStream in;
            try {
                in = new DataInputStream(new BufferedInputStream(new FileInputStream(next)));
                while(in.available()>0) {
                    TraceBuf2 tb = TraceBuf2.read(in);
                    popBuffer.add(tb);
                }
                return popBuffer.remove(0);
            } catch(FileNotFoundException e) {
                logger.error("Unable to load from file: "+next, e);
            } catch(EOFException e) {
                logger.error("IOException loading from file: "+next, e);
            } catch(IOException e) {
                logger.error("IOException loading from file: "+next, e);
            }
        }
        if (popBuffer.size() != 0) {
            return popBuffer.remove(0);
        }
        return super.pop();
    }

    File nextBufferFile() {
        String filename = ""+fileNum++;
        if (fileNum > maxFiles) { fileNum = 0;}
        while(filename.length() < 4) {
            filename = "0"+filename;
        }
        filename = filePrefix+filename;
        return new File(bufferDir, filename);
    }
    
    File bufferDir = new File("buffer");
    
    String filePrefix = "earthwormBuf";
    
    int fileNum = 1;

    int maxFiles = 1000;
    
    List<File> fileBuffers;
    
    List<TraceBuf2> popBuffer;
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FileBufferingEarthwormExport.class);
}
