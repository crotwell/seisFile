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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.sc.seis.seisFile.SeisFileException;


public class FileBufferingEarthwormExport extends BufferingEarthwormExport {

    public FileBufferingEarthwormExport(int port,
                                        int module,
                                        int institution,
                                        String heartbeatMessage,
                                        int heartbeatSeconds,
                                        int bufferSize,
                                        int sleepMillis,
                                        String bufferDirPath) throws UnknownHostException, IOException, SeisFileException {
        super(port, module, institution, heartbeatMessage, heartbeatSeconds, bufferSize, sleepMillis);
        if (bufferDirPath != null) {
            this.bufferDir = new File(bufferDirPath);
            if ( ! this.bufferDir.exists()) {
                if ( ! this.bufferDir.mkdirs()) {
                    throw new SeisFileException("Unable to create all parent dirs for "+bufferDirPath);
                }
            }
        }
        fileBuffers = Collections.synchronizedList(new ArrayList<File>());
        popBuffer = Collections.synchronizedList(new ArrayList<TraceBuf2>(maxSize));
        List<String> bufferFileList =  Arrays.asList(bufferDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(filePrefix);
            }
            
        }));
        Collections.sort(bufferFileList);
        for (String filename : bufferFileList) {
            fileBuffers.add(new File(bufferDir, filename));
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                synchronized(buffer) {
                    saveBufferToFile();
                    buffer.notifyAll();
                }
            }
        });
    }
    
    
    
    @Override
    public void offer(TraceBuf2 tb) {
            if (buffer.size() >= getMaxSize()) {
                saveBufferToFile();
            }
            logger.info("call super.offer");
            super.offer(tb);
    }
    
    void saveBufferToFile() {
        logger.info("save buffer to file "+buffer.size());
        List<TraceBuf2> tempBuf = new ArrayList<TraceBuf2>(buffer.size()+1);
        synchronized(buffer) {
            logger.info("In Sync");
            for (TraceBuf2 bufTB : buffer) {
                tempBuf.add(bufTB);
            }
            buffer.clear();
            logger.info("after clear");
            buffer.notifyAll();
        }
        logger.info("mem copied buffer.size()="+buffer.size()+" "+tempBuf.size());
        File bufFile = nextBufferFile(tempBuf.get(0));
        logger.info("Writing "+tempBuf.size()+" tb to "+bufFile.getAbsolutePath());
        fileBuffers.add(bufFile);
        try {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(bufFile)));
            for (TraceBuf2 traceBuf2 : tempBuf) {
                traceBuf2.write(dos);
            }
            dos.close();
        } catch(FileNotFoundException e) {
            logger.error("Unable to save to file", e);
        } catch(IOException e) {
            logger.error("Unable to save to file", e);
        }
        logger.info("Done writing to "+bufFile.getAbsolutePath());
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
                next.delete();
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

    File nextBufferFile(TraceBuf2 tb) {
        String filename = filePrefix+sdf.format(tb.getStartDate());
        return new File(bufferDir, filename);
    }
    
    File bufferDir = new File("buffer");
    
    String filePrefix = "earthwormBuf";
    
    List<File> fileBuffers;
    
    List<TraceBuf2> popBuffer;
    
    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH:mm:ss.SSS");
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FileBufferingEarthwormExport.class);
}
