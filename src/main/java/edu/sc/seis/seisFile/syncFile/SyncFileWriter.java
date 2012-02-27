package edu.sc.seis.seisFile.syncFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class SyncFileWriter {

    public SyncFileWriter(String dccName, String filename) throws IOException {
        this(dccName, new PrintWriter(new BufferedWriter(new FileWriter(filename))));
    }

    public SyncFileWriter(String dccName, PrintWriter writer) {
        this(dccName, SyncLine.dateToString(new Date()), new String[0], writer);
    }

    public SyncFileWriter(String dccName, String dateModified, PrintWriter writer) {
        this(dccName, dateModified, new String[0], writer);
    }

    public SyncFileWriter(String dccName, String dateModified, String[] extraHeaders, PrintWriter writer) {
        super();
        this.dccName = dccName;
        this.dateModified = dateModified;
        this.extraHeaders = extraHeaders;
        this.writer = writer;
    }

    public void appendAll(SyncFile sFile) {
        appendAll(sFile, true);
    }

    public void appendAll(SyncFile sFile, boolean consolidate) {
        for (SyncLine line : sFile.getSyncLines()) {
            appendLine(line, consolidate);
        }
    }

    public void appendLine(SyncLine line) {
        appendLine(line, true);
    }

    public void appendLine(SyncLine line, boolean consolidate) {
        if (consolidate) {
            if (previous == null) {
                previous = line;
            } else if (previous.isContiguous(line, SyncFile.DEFAULT_TOLERENCE)) {
                previous = previous.concat(line);
            } else {
                writer.println(previous.formatLine());
                previous = line;
            }
        } else {
            writer.println(line.formatLine());
        }
    }

    public void close() {
        if (writer != null) {
            flush();
            writer.close();
            writer = null;
        }
    }
    
    public void flush() {
        if (writer != null) {
            if (previous != null) {
                writer.println(previous.formatLine());
                previous = null;
            }
            writer.flush();
        }
    }

    protected void finalize() throws Throwable {
        close();
    }

    SyncLine previous = null;

    String dccName;

    String dateModified;

    String[] extraHeaders;

    protected PrintWriter writer;
}
