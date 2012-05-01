package edu.sc.seis.seisFile.syncFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.sc.seis.seisFile.SeisFileException;

/**
 * Compares two sync files, creating 3 psuedo-sync files. This assumes that the
 * input sync files are for a single channel and are sorted.
 * <ul>
 * <li>In A and In B</li>
 * <li>In A and Not In B</li>
 * <li>Not In A and In B</li>
 * </ul>
 * 
 * @author crotwell
 * 
 */
public class SyncFileCompare {

    public SyncFileCompare(SyncFile a, SyncFile b) {
        this.a = a;
        this.b = b;
        process();
    }

    void process() {
        String now = SyncLine.dateToString(new Date());
        if (!a.isEmpty()) {
            earliest = a.getEarliest();
            latest = a.getLatest();
        }
        if (!b.isEmpty()) {
            Date tmp = b.getEarliest();
            if (earliest == null || tmp.before(earliest)) {
                earliest = tmp;
            }
            tmp = b.getLatest();
            if (latest == null || tmp.after(latest)) {
                latest = tmp;
            }
        }
        inAinB = new SyncFile(a.dccName + " " + b.getDccName(), now, new String[] {"sync compare inAinB"});
        notAinB = new SyncFile("not " + a.dccName + " in " + b.getDccName(), now, new String[] {"sync compare notAinB"});
        inAnotB = new SyncFile("in " + a.dccName + " not " + b.getDccName(), now, new String[] {"sync compare inAnotB"});
        List<SyncLine> aLines = a.getSyncLines();
        Collections.sort(aLines);
        List<SyncLine> bLines = b.getSyncLines();
        Collections.sort(bLines);
        Iterator<SyncLine> bIterator = bLines.iterator();
        Iterator<SyncLine> aIterator = aLines.iterator();
        SyncLine[] out = new SyncLine[] {null, null};
        while (out[0] != null || out[1] != null || aIterator.hasNext() || bIterator.hasNext()) {
            if (out[0] == null && aIterator.hasNext()) {
                out[0] = aIterator.next();
            }
            if (out[1] == null && bIterator.hasNext()) {
                out[1] = bIterator.next();
            }
            out = processItem(out[0], out[1], inAinB, notAinB, inAnotB);
        }
    }

    static SyncLine[] processItem(SyncLine aLine, SyncLine bLine, SyncFile inAinB, SyncFile notAinB, SyncFile inAnotB) {
        if (aLine != null && bLine != null) {
            if (aLine.getEndTime().before(bLine.getStartTime())) {
                // both sorted, so know only in A
                inAnotB.addLine(aLine, true);
                aLine = null;
            } else if (bLine.getEndTime().before(aLine.getStartTime())) {
                // both sorted, so know bL only in B
                notAinB.addLine(bLine, true);
                bLine = null;
            } else {
                // some time overlap between aL and bL
                if (aLine.getStartTime().equals(bLine.getStartTime()) && aLine.getEndTime().equals(bLine.getEndTime())) {
                    // same
                    inAinB.addLine(aLine);
                    aLine = null;
                    bLine = null;
                } else if (aLine.getStartTime().equals(bLine.getStartTime())) {
                    // only differ in endtime
                    if (aLine.getEndTime().before(bLine.getEndTime())) {
                        inAinB.addLine(aLine);
                        bLine = bLine.split(aLine.getEndTime())[1];
                        aLine = null;
                    } else {
                        inAinB.addLine(bLine);
                        aLine = aLine.split(bLine.getEndTime())[1];
                        bLine = null;
                    }
                } else if (aLine.getStartTime().before(bLine.getStartTime())) {
                    // part of a before B
                    SyncLine[] split = aLine.split(bLine.getStartTime());
                    inAnotB.addLine(split[0]);
                    if (split.length != 1) {
                        return processItem(split[1], bLine, inAinB, notAinB, inAnotB);
                    }
                } else if (bLine.getStartTime().before(aLine.getStartTime())) {
                    // part of b before a, swap order and reprocess
                    SyncLine[] split = bLine.split(aLine.getStartTime());
                    notAinB.addLine(split[0]);
                    if (split.length != 1) {
                        return processItem(aLine, split[1], inAinB, notAinB, inAnotB);
                    }
                }
            }
        } else if (aLine != null) {
            // bLine is null, so only in A
            inAnotB.addLine(aLine);
            aLine = null;
        } else if (bLine != null) {
            // aLine is null, so only in B
            notAinB.addLine(bLine);
            bLine = null;
        }
        return new SyncLine[] {aLine, bLine};
    }

    public SyncFile getA() {
        return a;
    }

    public SyncFile getB() {
        return b;
    }

    public SyncFile getInAinB() {
        return inAinB;
    }

    public SyncFile getNotAinB() {
        return notAinB;
    }

    public SyncFile getInAnotB() {
        return inAnotB;
    }

    public static void headerToGMTScript(PrintWriter out, SyncFile a, SyncFile b, int numChannels, Date earliest, Date latest) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        out.println("BASE=\"sync\"");
        out.println("PS=${BASE}.ps");
        out.println("THICK=\"obese\"");
        out.println();
        String labelStep = "-Bpa3Of1o -Bsa1YS";
        if (latest.getTime() - earliest.getTime() < 1000l * 86400 * 180) {
            labelStep = "-Bpa7Rf1d -Bsa1OS";
        }
        if (latest.getTime() - earliest.getTime() < 1000l * 86400 * 30) {
            labelStep = "-Bpa1Rf6h -Bsa1OS";
        }
        if (latest.getTime() - earliest.getTime() < 1000l * 86400 * 5) {
            out.println("gmtset PLOT_CLOCK_FORMAT hh:mm");
            labelStep = "-Bpa6Hf1h -Bsa1DS";
        }
        if (latest.getTime() - earliest.getTime() < 1000l * 86400) {
            labelStep = "-Bpa15mf5m -Bsa1HS";
        }
        out.println("psbasemap -R" + sdf.format(earliest) + "/" + sdf.format(latest) + "/0/"+(numChannels+2)+" -JX10i/6i " + labelStep
                + " -K > $PS");
        out.println("pstext -R -JX -Ggreen -O -K >> $PS <<END");
        out.println(sdf.format(new Date((earliest.getTime()+latest.getTime())/2)) +" "+ (numChannels+1)+" 12 0 1 CM Both");
        out.println("END");
        out.println("pstext -R -JX -Gred -O -K >> $PS <<END");
        out.println(sdf.format(earliest) +" "+ (numChannels+1)+" 12 0 1 LM Only '" + b.getDccName() + "'");
        out.println("END");
        out.println("pstext -R -JX -Gblue -O -K >> $PS <<END");
        out.println(sdf.format(latest) +" "+ (numChannels+1)+" 12 0 1 RM Only '" + a.getDccName() + "'");
        out.println("END");
    }

    public void dataToGMTScript(int index, String label, PrintWriter out) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        out.println("psxy -R -JX -W${THICK},green -m -O -K >> $PS <<END");
        for (SyncLine sline : getInAinB()) {
            out.println("> ");
            out.println(sdf.format(sline.getStartTime()) + " " + index);
            out.println(sdf.format(sline.getEndTime()) + " " + index);
        }
        out.println("END");
        out.println("psxy -R -JX -W${THICK},red -m -O -K >> $PS <<END");
        for (SyncLine sline : getNotAinB()) {
            out.println("> ");
            out.println(sdf.format(sline.getStartTime()) + " " + index);
            out.println(sdf.format(sline.getEndTime()) + " " + index);
        }
        out.println("END");
        out.println("psxy -R -JX -W${THICK},blue -m -O -K >> $PS <<END");
        for (SyncLine sline : getInAnotB()) {
            out.println("> ");
            out.println(sdf.format(sline.getStartTime()) + " " + index);
            out.println(sdf.format(sline.getEndTime()) + " " + index);
        }
        out.println("END");
        out.println("pstext -R -JX -Wyellow -O -K >> $PS <<END");
        out.println(sdf.format(earliest) +" "+ (index)+" 12 0 1 LM " + label);
        out.println("END");
    }

    public static void tailToGMTScript(PrintWriter out) throws IOException {
        out.println("psxy -R -JX -O  >> $PS <<END");
        out.println("END");
        out.close();
    }

    public void outputToGMTScript(String filename) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        headerToGMTScript(out, getA(), getB(), 1, earliest, latest);
        dataToGMTScript(1, "", out);
        tailToGMTScript(out);
    }

    Date earliest;
    
    Date latest;
    
    SyncFile a;

    SyncFile b;

    SyncFile inAinB;

    SyncFile notAinB;

    SyncFile inAnotB;

    static void printUsage() {
        System.err.println("Usage: syncFileCompare [--gmt] -a file1.sync -b file2.sync");
    }

    static String trimDotSync(String filename) {
        String fileBase = filename;
        if (filename.endsWith(".sync")) {
            fileBase = filename.substring(0, filename.lastIndexOf(".sync"));
        }
        return fileBase;
    }

    public static void main(String[] args) throws IOException, SeisFileException {
        boolean doGMT = false;
        String file1Name = "", file2Name = "";
        for (int i = 0; i < args.length; i++) {
            if ("--gmt".equals(args[i])) {
                doGMT = true;
            } else if ("-a".equals(args[i])) {
                file1Name = args[i + 1];
                i++;
            } else if ("-b".equals(args[i])) {
                file2Name = args[i + 1];
                i++;
            } else {
                System.err.println("I don't understand '" + args[i] + "'");
                printUsage();
                return;
            }
        }
        if (file1Name.length() == 0 || file2Name.length() == 0) {
            System.err.println("Both a and b filenames are required: '" + file1Name + "' '" + file2Name + "'");
            printUsage();
            return;
        }
        String file1Base = trimDotSync(file1Name);
        String file2Base = trimDotSync(file2Name);
        SyncFile file1 = SyncFile.load(new File(file1Name));
        SyncFile file2 = SyncFile.load(new File(file2Name));
        HashMap<String, SyncFile> file1Map = file1.splitByChannel();
        HashMap<String, SyncFile> file2Map = file2.splitByChannel();
        Set<String> chanKeys = new HashSet<String>(file1Map.keySet());
        chanKeys.addAll(file1Map.keySet());
        List<String> chanKeyList = new ArrayList<String>(chanKeys);
        Collections.sort(chanKeyList);
        HashMap<String, SyncFileCompare> compareMap = new HashMap<String, SyncFileCompare>();
        for (String key : chanKeyList) {
            String chanPrefix = "";
            if (chanKeys.size() != 1) {
                chanPrefix = key + "_";
            }
            SyncFile a = file1Map.get(key);
            if (a == null) {
                a = new SyncFile(file1.dccName);
            }
            SyncFile b = file2Map.get(key);
            if (b == null) {
                b = new SyncFile(file2.dccName);
            }
            SyncFileCompare sfc = new SyncFileCompare(a, b);
            compareMap.put(key, sfc);
            sfc.getInAinB().saveToFile(chanPrefix + "in_" + file1Base + "_in_" + file2Base + ".sync");
            sfc.getNotAinB().saveToFile(chanPrefix + "not_" + file1Base + "_in_" + file2Base + ".sync");
            sfc.getInAnotB().saveToFile(chanPrefix + "in_" + file1Base + "_not_" + file2Base + ".sync");
            System.out.println("Done: A: " + a.getSyncLines().size() + " B: " + b.getSyncLines().size() + " inAinB: "
                    + sfc.getInAinB().getSyncLines().size() + " notAinB: " + sfc.getNotAinB().getSyncLines().size()
                    + " inAnotB: " + sfc.getInAnotB().getSyncLines().size());
        }
        if (doGMT) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("syncCompare.gmt")));Date earliest = null;
            Date latest = null;
            if (!file1.isEmpty()) {
                earliest = file1.getEarliest();
                latest = file1.getLatest();
            }
            if (!file2.isEmpty()) {
                Date tmp = file2.getEarliest();
                if (earliest == null || tmp.before(earliest)) {
                    earliest = tmp;
                }
                tmp = file2.getLatest();
                if (latest == null || tmp.after(latest)) {
                    latest = tmp;
                }
            }
            
            headerToGMTScript(out, file1, file2, chanKeyList.size(), earliest, latest);
            int chanIndex = 0;
            Collections.reverse(chanKeyList);
            for (String chanKey : chanKeyList) {
                SyncFileCompare sfc = compareMap.get(chanKey);
                chanIndex++;
                sfc.dataToGMTScript(chanIndex, chanKey, out);
            }
            tailToGMTScript(out);
        }
    }
}
