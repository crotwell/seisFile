package edu.sc.seis.seisFile.syncFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncFile {
    
    public SyncFile(String dccName) {
        this(dccName, SyncLine.dateToString(new Date()));
    }
                    
    public SyncFile(String dccName, String dateModified) {
        this(dccName, dateModified, new String[0]);
    }
    
    public SyncFile(String dccName, String dateModified, String[] extraHeaders) {
        super();
        this.dccName = dccName;
        this.dateModified = dateModified;
        this.extraHeaders = extraHeaders;
    }

    public void addLine(SyncLine line) {
        addLine(line, false);
    }

    public void addLine(SyncLine line, boolean consolidate) {
        if (consolidate) {
            if (syncLines.get(syncLines.size()-1).isContiguous(line, tolerence)) {
                
            }
        }
        syncLines.add(line);
    }


    public void saveToFile(String filename) throws IOException {
        saveToFile(new File(filename));
    }
    
    public void saveToFile(File f) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
        for (SyncLine line : syncLines) {
            out.println(line.formatLine());
        }
    }
    
    public List<SyncLine> getSyncLines() {
        return syncLines;
    }
    
    String dccName;
    
    String dateModified;
    
    String[] extraHeaders;
    
    List<SyncLine> syncLines = new ArrayList<SyncLine>();

    private float tolerence = 0.01f;

    public static final String SEPARATOR = "|";
}
