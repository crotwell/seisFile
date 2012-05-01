package edu.sc.seis.seisFile.syncFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.sc.seis.seisFile.SeisFileException;

/**
 * Compares two sync files, creating 3 psuedo-sync files.
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
        inAinB = new SyncFile(a.dccName + " " + b.getDccName(), now, new String[] {"sync compare inAinB"});
        notAinB = new SyncFile("not "+a.dccName + " in " + b.getDccName(), now, new String[] {"sync compare notAinB"});
        inAnotB = new SyncFile("in "+a.dccName + " not " + b.getDccName(), now, new String[] {"sync compare inAnotB"});
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

    public void outputToGMTScript(String filename) throws IOException {
        Date earliest = null;
        Date latest = null;
        if ( ! getInAinB().isEmpty()) {
            earliest = getInAinB().getEarliest();
            latest = getInAinB().getLatest();
        }
        if ( ! getInAnotB().isEmpty() ) {
            Date tmp = getInAnotB().getEarliest();
            if (earliest == null || tmp.before(earliest)) {
                earliest = tmp;
            }
            tmp = getInAnotB().getLatest();
            if (latest == null || tmp.after(latest)) {
                latest = tmp;
            }
        }
        if ( ! getNotAinB().isEmpty() ) {
            Date tmp = getNotAinB().getEarliest();
            if (earliest == null || tmp.before(earliest)) {
                earliest = tmp;
            }
            tmp = getNotAinB().getLatest();
            if (latest == null || tmp.after(latest)) {
                latest = tmp;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        out.println("BASE=\"sync\"");
        out.println("PS=${BASE}.ps");
        out.println();
        String labelStep = "1Y";
        if (latest.getTime()-earliest.getTime() < 1000l*86400*180) {
            labelStep = "1O";
        } 
        if (latest.getTime()-earliest.getTime() < 1000l*86400*30) {
            labelStep = "1D";
        } 
        if (latest.getTime()-earliest.getTime() < 1000l*86400*1) {
            labelStep = "1H";
        } 
        out.println("psbasemap -R"+sdf.format(earliest)+"/"+sdf.format(latest)+"/0/3 -JX6i -B"+labelStep+"/1 -K > $PS");
        out.println("pstext -R -JX -Ggreen -O -K >> $PS <<END");
        out.println(sdf.format(earliest)+" 2.7 12 0 1 LM Both");
        out.println("END");

        out.println("pstext -R -JX -Gred -O -K >> $PS <<END");
        out.println(sdf.format(earliest)+" 2.5 12 0 1 LM "+getNotAinB().getDccName());
        out.println("END");
        out.println("pstext -R -JX -Gblue -O -K >> $PS <<END");
        out.println(sdf.format(earliest)+" 2.3 12 0 1 LM "+getInAinB().getDccName());
        out.println("END");
        out.println("psxy -R -JX -Wobese,green -m -O -K >> $PS <<END");
        for (SyncLine sline : getInAinB()) {
            out.println("> ");
            out.println(sdf.format(sline.getStartTime())+" "+2);
            out.println(sdf.format(sline.getEndTime())+" "+2);
        }
        out.println("END");
        out.println("psxy -R -JX -Wobese,red -m -O -K >> $PS <<END");
        for (SyncLine sline : getNotAinB()) {
            out.println("> ");
            out.println(sdf.format(sline.getStartTime())+" "+1);
            out.println(sdf.format(sline.getEndTime())+" "+1);
        }
        out.println("END");
        out.println("psxy -R -JX -Wobese,blue -m -O -K >> $PS <<END");
        for (SyncLine sline : getInAnotB()) {
            out.println("> ");
            out.println(sdf.format(sline.getStartTime())+" "+1);
            out.println(sdf.format(sline.getEndTime())+" "+1);
        }
        out.println("END");
        out.println("psxy -R -JX -O  >> $PS <<END");
        out.println("END");
        out.close();
    }

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
                file1Name = args[i+1];
                i++;
            } else if ("-b".equals(args[i])) {
                file2Name = args[i+1];
                i++;
            } else {
                System.err.println("I don't understand '"+args[i]+"'");
                printUsage();
                return;
            }
        }
        if (file1Name.length() == 0 || file2Name.length() == 0) {
            System.err.println("Both a and b filenames are required: '"+file1Name+"' '"+file2Name+"'");
            printUsage();
            return;
        }
        String file1Base = trimDotSync(file1Name);
        String file2Base = trimDotSync(file2Name);
        SyncFile file1 = SyncFile.load(new File(file1Name));
        SyncFile file2 = SyncFile.load(new File(file2Name));
        SyncFileCompare sfc = new SyncFileCompare(file1, file2);
        sfc.getInAinB().saveToFile("in_" + file1Base + "_in_" + file2Base + ".sync");
        sfc.getNotAinB().saveToFile("not_" + file1Base + "_in_" + file2Base + ".sync");
        sfc.getInAnotB().saveToFile("in_" + file1Base + "_not_" + file2Base + ".sync");
        if (args[0].equals("--gmt")) {
            sfc.outputToGMTScript("syncCompare.gmt");
        }
        System.out.println("Done: A: " + file1.getSyncLines().size() + " B: " + file2.getSyncLines().size()
                + " inAinB: " + sfc.getInAinB().getSyncLines().size() + " notAinB: "
                + sfc.getNotAinB().getSyncLines().size() + " inAnotB: " + sfc.getInAnotB().getSyncLines().size());
    }
}
