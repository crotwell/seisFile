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
            throw new IOException("empty Sync file: "+f);
        }
        String[] split = line.split("\\|");
        String[] extras = new String[0];
        if (split.length > 2) {
            extras = new String[split.length-2];
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
            String extras = "";
            for (int i = 0; i < extraHeaders.length; i++) {
                extras += "|" + extraHeaders[i];
            }
            out.println(dccName + "|" + dateModified + extras);
            for (SyncLine line : syncLines) {
                out.println(line.formatLine());
            }
            out.close();
        }
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

    private float tolerence = 0.01f;

    public static final String SEPARATOR = "|";
}
