package edu.sc.seis.seisFile.syncFile;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GMTSyncFile {

    public GMTSyncFile(int numChannels, Date start, Date end, PrintWriter out) {
        this.start = start;
        this.end = end;
        this.numChannels = numChannels;
        this.out = out;
    }

    public void gmtHeader() {
        out.println("BASE=\"" + baseFilename + "\"");
        out.println("PS=${BASE}.ps");
        out.println();
        String labelStep = "-Bpa3of1o -Bsa1YS";
        if (end.getTime() - start.getTime() < 1000l * 86400 * 366) { // 366 days
            labelStep = "-Bpa1of1o -Bsa1YS";
        }
        if (end.getTime() - start.getTime() < 1000l * 86400 * 180) { // 180 days
            labelStep = "-Bpa7Rf1d -Bsa1OS";
        }
        if (end.getTime() - start.getTime() < 1000l * 86400 * 30) { // 30 days
            labelStep = "-Bpa1Rf6h -Bsa1OS";
        }
        if (end.getTime() - start.getTime() < 1000l * 86400 * 5) { // 5 days
            out.println("gmtset PLOT_CLOCK_FORMAT hh:mm");
            labelStep = "-Bpa6Hf1h -Bsa1DS";
        }
        if (end.getTime() - start.getTime() < 1000l * 86400) { // one day
            labelStep = "-Bpa15mf5m -Bsa1HS";
        }
        out.println("psbasemap -R" + sdf.format(start) + "/" + sdf.format(end) + "/0/" + (numChannels) + " -JX"
                + plotWidth + plotSizeUnit + "/" + plotHeight + plotSizeUnit + " " + labelStep + " -K > $PS");
    }

    public void gmtTrailer() {}

    public void plot(SyncFile sf, int index) {
        out.println("psxy -R -JX -W"+getLineWidth()+"," + colorToGMTRGB(lineColor) + " -m -O -K >> $PS <<END");
        for (SyncLine sline : sf) {
            out.println("> ");
            out.println(sdf.format(sline.getStartTime()) + " " + index);
            out.println(sdf.format(sline.getEndTime()) + " " + index);
        }
        out.println("END");
    }

    public void label(Date xLoc, int yLoc, String text) {
        out.println("pstext -R -JX -G" + colorToGMTRGB(textColor) + " -O -K >> $PS <<END");
        out.println(sdf.format(xLoc) + " " + yLoc + " " + textSize + " " + textAngle + " " + font + " " + justify + " "
                + text);
        out.println("END");
    }

    public static String colorToGMTRGB(Color color) {
        return color.getRed() + "/" + color.getGreen() + "/" + color.getBlue();
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextAngle() {
        return textAngle;
    }

    public void setTextAngle(int textAngle) {
        this.textAngle = textAngle;
    }

    public int getFont() {
        return font;
    }

    public void setFont(int font) {
        this.font = font;
    }

    public String getJustify() {
        return justify;
    }

    public void setJustify(String justify) {
        this.justify = justify;
    }

    public int getPlotWidth() {
        return plotWidth;
    }

    public void setPlotWidth(int plotWidth) {
        this.plotWidth = plotWidth;
    }

    public int getPlotHeight() {
        return plotHeight;
    }

    public void setPlotHeight(int plotHeight) {
        this.plotHeight = plotHeight;
    }

    public String getPlotSizeUnit() {
        return plotSizeUnit;
    }

    public void setPlotSizeUnit(String plotSizeUnit) {
        this.plotSizeUnit = plotSizeUnit;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public void setNumChannels(int numChannels) {
        this.numChannels = numChannels;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getBaseFilename() {
        return baseFilename;
    }

    public void setBaseFilename(String baseFilename) {
        this.baseFilename = baseFilename;
    }
    
    public static void main(String[] args) throws Exception {
        String inFilename = args[0];
        SyncFile sf = SyncFile.load(new File(inFilename));
        sf.sort();
        HashMap<String, SyncFile> byChan = sf.splitByChannel();
        String fileBase = SyncFileCompare.trimDotSync(inFilename);
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileBase+".gmt")));
        Set<String> chanKeys = new HashSet<String>(byChan.keySet());
        List<String> chanKeyList = new ArrayList<String>(chanKeys);
        Collections.sort(chanKeyList);
        Collections.reverse(chanKeyList);
        int numChannels = chanKeyList.size();
        Date[] range = SyncFileCompare.range(byChan.values());
        Date earliest = range[0];
        Date latest = range[1];
        
        GMTSyncFile gmtPlotter = new GMTSyncFile(numChannels+1, earliest, latest, out);
        gmtPlotter.setBaseFilename(fileBase);
        gmtPlotter.gmtHeader();
        int chanIndex = 0;
        for (String key : chanKeyList) {
            chanIndex++;
            gmtPlotter.plot(byChan.get(key), chanIndex);
            gmtPlotter.setJustify("LB");
            gmtPlotter.setTextColor(Color.BLACK);
            gmtPlotter.label(earliest, chanIndex, key);
        }
        gmtPlotter.gmtTrailer();
        out.close();
    }

    Color lineColor = new Color(51, 102, 255); // blue

    int lineWidth = 18;

    Color textColor = Color.BLACK;

    int textSize = 10;

    int textAngle = 0;

    int font = 1;

    String justify = "LM";

    int plotWidth = 10;

    int plotHeight = 6;

    String plotSizeUnit = "i";

    int numChannels;

    Date start;

    Date end;

    String baseFilename = "syncPlot";

    PrintWriter out;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
}
