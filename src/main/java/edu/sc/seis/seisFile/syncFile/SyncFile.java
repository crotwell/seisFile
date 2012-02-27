package edu.sc.seis.seisFile.syncFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SyncFile {

    public static SyncFile load(File f) throws IOException, NumberFormatException, ParseException {
        BufferedReader r = new BufferedReader(new FileReader(f));
        String line;
        // first line is header line with dccName | date | extra | extra...
        line = r.readLine();
        if (line == null) {
            throw new IOException("empty Sync file: " + f);
        }
        String[] split = line.split("\\|");
        String[] extras = new String[0];
        if (split.length > 2) {
            extras = new String[split.length - 2];
            System.arraycopy(split, 2, extras, 0, extras.length);
        }
        SyncFile sync = new SyncFile(split[0], split[1], extras);
        while ((line = r.readLine()) != null) {
            sync.addLine(SyncLine.parse(line));
        }
        r.close();
        return sync;
    }

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

    public SyncFile(String dccName, String dateModified, String[] extraHeaders, List<SyncLine> lines) {
        this(dccName, dateModified, extraHeaders);
        syncLines = lines;
    }

    public SyncFile concatenate(SyncFile other) {
        SyncFile out = new SyncFile(getDccName());
        out.syncLines.addAll(getSyncLines());
        List<SyncLine> otherLines = new ArrayList<SyncLine>();
        otherLines.addAll(other.getSyncLines());
        if (out.getSyncLines().size() != 0 && other.getSyncLines().size() != 0) {
            SyncLine last = out.getSyncLines().get(getSyncLines().size() - 1);
            SyncLine otherFirst = otherLines.get(0);
            if (last.isContiguous(otherFirst, tolerence)) {
                out.getSyncLines().remove(out.getSyncLines().size() - 1);
                last = last.concat(otherFirst);
                out.addLine(last, true);
                otherLines.remove(0);
            }
        }
        out.syncLines.addAll(otherLines);
        return out;
    }

    public void sort() {
        Collections.sort(syncLines);
    }

    public void addLine(SyncLine line) {
        addLine(line, false);
    }

    public void addLine(SyncLine line, boolean consolidate) {
        if (consolidate && syncLines.size() != 0) {
            SyncLine previous = syncLines.get(syncLines.size() - 1);
            if (previous.isContiguous(line, tolerence)) {
                syncLines.remove(syncLines.size() - 1);
                line = previous.concat(line);
            }
        }
        syncLines.add(line);
    }

    public void saveToFile(String filename) throws IOException {
        saveToFile(new File(filename));
    }

    public void saveToFile(File f) throws IOException {
        if (syncLines.size() != 0) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            appendToWriter(out, true);
            out.close();
        }
    }

    public void appendToWriter(PrintWriter writer, boolean writeHeader) {
        if (writeHeader) {
            writer.println(getHeaderLine());
        }
        for (SyncLine line : syncLines) {
            writer.println(line.formatLine());
        }
        writer.flush();
    }
    
    public String getHeaderLine() {
        String extras = "";
        for (int i = 0; i < extraHeaders.length; i++) {
            extras += "|" + extraHeaders[i];
        }
        return dccName + "|" + dateModified + extras;
    }

    public String getDccName() {
        return dccName;
    }

    public void setDccName(String dccName) {
        this.dccName = dccName;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String[] getExtraHeaders() {
        return extraHeaders;
    }

    public void setExtraHeaders(String[] extraHeaders) {
        this.extraHeaders = extraHeaders;
    }

    public List<SyncLine> getSyncLines() {
        return syncLines;
    }

    String dccName;

    String dateModified;

    String[] extraHeaders;

    List<SyncLine> syncLines = new ArrayList<SyncLine>();

    private float tolerence = DEFAULT_TOLERENCE;
    
    public static final float DEFAULT_TOLERENCE = 0.01f;

    public static final String SEPARATOR = "|";
}
