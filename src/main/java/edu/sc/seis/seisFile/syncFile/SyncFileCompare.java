package edu.sc.seis.seisFile.syncFile;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
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
            if ( ! aLine.isSameChannel(bLine)) {
                // find one that is sorted earlier and output it
                if (aLine.compareTo(bLine) < 0) {
                    inAnotB.addLine(aLine, true);
                    aLine = null;
                } else {
                    notAinB.addLine(bLine, true);
                    bLine = null;
                }
            } else  if (aLine.getEndTime().before(bLine.getStartTime())) {
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

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Date getEarliest() {
        return earliest;
    }

    public Date getLatest() {
        return latest;
    }

    Date earliest;

    Date latest;

    SyncFile a;

    SyncFile b;

    SyncFile inAinB;

    SyncFile notAinB;

    SyncFile inAnotB;

    boolean verbose = false;

    static void printUsage() {
        System.err.println("Usage: syncFileCompare [--gmt] -a file1.sync -b file2.sync");
    }

    static String trimDotSync(String filename) {
        String fileBase = new File(filename).getAbsoluteFile().getName();
        if (fileBase.endsWith(".sync")) {
            fileBase = fileBase.substring(0, fileBase.lastIndexOf(".sync"));
        }
        return fileBase;
    }

    static Date earliest(Date earliest, SyncFile sf) {
        if (!sf.isEmpty()) {
            Date tmp = sf.getEarliest();
            if (earliest == null || tmp.before(earliest)) {
                earliest = tmp;
            }
        }
        return earliest;
    }

    static Date latest(Date latest, SyncFile sf) {
        if (!sf.isEmpty()) {
            Date tmp = sf.getLatest();
            if (latest == null || tmp.after(latest)) {
                latest = tmp;
            }
        }
        return latest;
    }

    public static Date[] range(Collection<SyncFile> sfSet) {
        Date earliest = null;
        Date latest = null;
        for (SyncFile syncFile : sfSet) {
            earliest = earliest(earliest, syncFile);
            latest = latest(latest, syncFile);
        }
        return new Date[] {earliest, latest};
    }

    public static void main(String[] args) throws IOException, SeisFileException {
        boolean doGMT = false;
        String file1Name = "", file2Name = "";
        for (int i = 0; i < args.length; i++) {
            if ("--help".equals(args[i])) {
                printUsage();
                return;
            } else if ("--gmt".equals(args[i])) {
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
        chanKeys.addAll(file2Map.keySet());
        List<String> chanKeyList = new ArrayList<String>(chanKeys);
        Collections.sort(chanKeyList);
        HashMap<String, SyncFileCompare> compareMap = new HashMap<String, SyncFileCompare>();
        Date[] range1 = range(file1Map.values());
        Date[] range2 = range(file2Map.values());
        Date earliest = range1[0].before(range2[0]) ? range1[0] : range2[0];
        Date latest = range1[1].after(range2[1]) ? range1[1] : range2[1];
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
            earliest = earliest(earliest, a);
            earliest = earliest(earliest, b);
            latest = latest(latest, a);
            latest = latest(latest, b);
            SyncFileCompare sfc = new SyncFileCompare(a, b);
            compareMap.put(key, sfc);
            sfc.getInAinB().saveToFile(chanPrefix + "in_" + file1Base + "_in_" + file2Base + ".sync");
            sfc.getNotAinB().saveToFile(chanPrefix + "not_" + file1Base + "_in_" + file2Base + ".sync");
            sfc.getInAnotB().saveToFile(chanPrefix + "in_" + file1Base + "_not_" + file2Base + ".sync");
            if (sfc.isVerbose()) {
                System.out.println("Done: " + chanPrefix + "A: " + a.getSyncLines().size() + " B: "
                        + b.getSyncLines().size() + " inAinB: " + sfc.getInAinB().getSyncLines().size() + " notAinB: "
                        + sfc.getNotAinB().getSyncLines().size() + " inAnotB: "
                        + sfc.getInAnotB().getSyncLines().size());
            }
        }
        if (doGMT) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("syncCompare.gmt")));
            int numChannels = chanKeyList.size();
            GMTSyncFile gmtPlotter = new GMTSyncFile(numChannels + 2, earliest, latest, out);
            Color bothColor = new Color(51, 255, 102); // green
            Color file1Color = new Color(51, 102, 255); // blue
            Color file2Color = new Color(255, 51, 102); // red
            gmtPlotter.gmtHeader();
            gmtPlotter.setTextColor(bothColor);
            gmtPlotter.setJustify("CM");
            gmtPlotter.label(new Date((earliest.getTime() + latest.getTime()) / 2), (numChannels + 1), "Both");
            gmtPlotter.setTextColor(file1Color);
            gmtPlotter.setJustify("LM");
            gmtPlotter.label(earliest, (numChannels + 1), "Only " + file1Base);
            gmtPlotter.setTextColor(file2Color);
            gmtPlotter.setJustify("RM");
            gmtPlotter.label(latest, (numChannels + 1), "Only " + file2Base);
            int chanIndex = 0;
            Collections.reverse(chanKeyList);
            for (String chanKey : chanKeyList) {
                SyncFileCompare sfc = compareMap.get(chanKey);
                chanIndex++;
                gmtPlotter.setLineColor(bothColor);
                gmtPlotter.plot(sfc.getInAinB(), chanIndex);
                gmtPlotter.setLineColor(file1Color);
                gmtPlotter.plot(sfc.getInAnotB(), chanIndex);
                gmtPlotter.setLineColor(file2Color);
                gmtPlotter.plot(sfc.getNotAinB(), chanIndex);
                gmtPlotter.setJustify("LB");
                gmtPlotter.setTextColor(Color.BLACK);
                gmtPlotter.label(earliest, chanIndex, chanKey);
            }
            gmtPlotter.gmtTrailer();
            out.close();
        }
    }
}
