package edu.sc.seis.seisFile;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class QueryParams {

    public QueryParams(String[] args) throws SeisFileException {
        PrintWriter out = new PrintWriter(System.out, true);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                network = args[i + 1];
            } else if (args[i].equals("-s")) {
                station = args[i + 1];
            } else if (args[i].equals("-l")) {
                location = args[i + 1];
            } else if (args[i].equals("-c")) {
                channel = args[i + 1];
            } else if (args[i].equals("-b")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                String beginStr = args[i + 1].trim();
                if (beginStr.matches(".+\\d")) {
                    beginStr = beginStr + " GMT";
                }
                try {
                    begin = dateFormat.parse(beginStr);
                } catch(ParseException e) {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                    try {
                        begin = dateFormat.parse(beginStr);
                    } catch(ParseException ee) {
                        throw new SeisFileException("Illegal date format, should be:  yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd'T'HH:mm:ss.SSS",
                                                    ee);
                    }
                }
            } else if (args[i].equals("-d")) {
                duration = Float.parseFloat(args[i + 1]);
            } else if (args[i].equals("-o")) {
                outFile = args[i + 1];
            } else if (args[i].equals("-m")) {
                maxRecords = Integer.parseInt(args[i + 1]);
                if (maxRecords < -1) {
                    maxRecords = -1;
                }
            } else if (args[i].equals("--verbose")) {
                verbose = true;
            } else if (args[i].equals("--version")) {
                printVersion = true;
            } else if (args[i].equals("--help")) {
                printHelp = true;
            }
        }
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.SECOND, -1 * Math.round(duration));
        cal.add(Calendar.MILLISECOND, -1000 * Math.round(duration - Math.round(duration)));
        if (begin == null) {begin = cal.getTime();}
        cal.setTime(begin);
        cal.add(Calendar.SECOND, Math.round(duration));
        cal.add(Calendar.MILLISECOND, -1000 * Math.round(duration - Math.round(duration)));
        end = cal.getTime();
    }

    String network;

    String station;

    String location;

    String channel;

    Date begin;

    Date end;

    Float duration = 600f;

    int maxRecords = -1;

    String outFile = null;

    boolean verbose = false;

    boolean printVersion = false;

    boolean printHelp = false;

    DataOutputStream dos = null;

    public String getNetwork() {
        return network;
    }

    public String getStation() {
        return station;
    }

    public String getLocation() {
        return location;
    }

    public String getChannel() {
        return channel;
    }

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public Float getDuration() {
        return duration;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public String getOutFile() {
        return outFile;
    }

    public boolean isPrintVersion() {
        return printVersion;
    }

    public boolean isPrintHelp() {
        return printHelp;
    }

    public DataOutputStream getDataOutputStream() throws FileNotFoundException {
        if (dos == null) {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        }
        return dos;
    }
}
