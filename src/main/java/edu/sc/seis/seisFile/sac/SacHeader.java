package edu.sc.seis.seisFile.sac;

import static edu.sc.seis.seisFile.sac.SacConstants.DEFAULT_NVHDR;
import static edu.sc.seis.seisFile.sac.SacConstants.FLOAT_UNDEF;
import static edu.sc.seis.seisFile.sac.SacConstants.INT_UNDEF;
import static edu.sc.seis.seisFile.sac.SacConstants.ITIME;
import static edu.sc.seis.seisFile.sac.SacConstants.IntelByteOrder;
import static edu.sc.seis.seisFile.sac.SacConstants.NVHDR_OFFSET;
import static edu.sc.seis.seisFile.sac.SacConstants.STRING16_UNDEF;
import static edu.sc.seis.seisFile.sac.SacConstants.STRING8_UNDEF;
import static edu.sc.seis.seisFile.sac.SacConstants.SunByteOrder;
import static edu.sc.seis.seisFile.sac.SacConstants.TRUE;
import static edu.sc.seis.seisFile.sac.SacConstants.data_offset;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;


/**
 * Class that represents a sac file heder. All headers are have the same names as
 * within the Sac program. 
 * 
 * This reflects the sac header as of version 101.4 in utils/sac.h
 * 
 * Notes: Key to comment flags describing each field: 
 * Column 1: R required by SAC (blank) optional 
 * Column 2: A = settable from a priori knowledge D =
 * available in data F = available in or derivable from SEED fixed data header T
 * = available in SEED header tables (blank) = not directly available from SEED
 * data, header tables, or elsewhere
 * 
 * 
 * @author H. Philip Crotwell
 */
public class SacHeader {

    public SacHeader() {};
    
    public SacHeader(String filename) throws IOException {
        this(new File(filename));
    }
    
    public SacHeader(DataInput indis) throws IOException {
        readHeader(indis);
    }

    /**
     * reads just the sac header specified by the filename. Limited checks are made
     * to be sure the file really is a sac file.
     */
    public SacHeader(File sacFile) throws IOException {
        if (sacFile.length() < data_offset) {
            throw new IOException(sacFile.getName() + " does not appear to be a sac file! File size ("
                    + sacFile.length() + " is less than sac's header size (" + data_offset + ")");
        }
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(sacFile)));
        try {
            readHeader(dis);
        } finally {
            dis.close();
        }
    }

    public static SacHeader createEmptyEvenSampledTimeSeriesHeader() {
        SacHeader header = new SacHeader();
        header.leven = TRUE;
        header.iftype = ITIME;
        header.npts   = 0;
        header.b      = 0.0f;
        header.e      = 0.0f;   
        header.idep   = SacConstants.IUNKN;
        return header;
    }
    
    /**
     * reads the header from the given stream. The NVHDR value (should be 6) is
     * checked to see if byte swapping is needed. If so, all header values are
     * byte swapped and the byteOrder is set to IntelByteOrder (false) so that
     * the data section will also be byte swapped on read. Extra care is taken
     * to do all byte swapping before the byte values are transformed into
     * floats as java can do very funny things if the byte-swapped float happens
     * to be a NaN.
     */
     void readHeader(DataInput indis) throws IOException {
        byte[] headerBuf = new byte[data_offset];
        indis.readFully(headerBuf);
        if (headerBuf[NVHDR_OFFSET] == 6 && headerBuf[NVHDR_OFFSET + 1] == 0 && headerBuf[NVHDR_OFFSET + 2] == 0
                && headerBuf[NVHDR_OFFSET + 3] == 0) {
            byteOrder = IntelByteOrder;
            // little endian byte order, swap bytes on first 110 4-byte values
            // in header, rest are text
            for (int i = 0; i < 110 * 4; i += 4) {
                byte tmp = headerBuf[i];
                headerBuf[i] = headerBuf[i + 3];
                headerBuf[i + 3] = tmp;
                tmp = headerBuf[i + 1];
                headerBuf[i + 1] = headerBuf[i + 2];
                headerBuf[i + 2] = tmp;
            }
        } else if ( ! (headerBuf[NVHDR_OFFSET] == 0 && headerBuf[NVHDR_OFFSET + 1] == 0 && headerBuf[NVHDR_OFFSET + 2] == 0
                &&headerBuf[NVHDR_OFFSET + 3] == 6)) {
            throw new IOException("Does not appear to be a SAC file, NVHDR header bytes should be (int) 6 but found "+
                    headerBuf[NVHDR_OFFSET] +" "+ headerBuf[NVHDR_OFFSET + 1] +" "+ headerBuf[NVHDR_OFFSET + 2] +" "+
                    headerBuf[NVHDR_OFFSET + 3]);
        }
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(headerBuf));
        delta = dis.readFloat();
        depmin = dis.readFloat();
        depmax = dis.readFloat();
        scale = dis.readFloat();
        odelta = dis.readFloat();
        b = dis.readFloat();
        e = dis.readFloat();
        o = dis.readFloat();
        a = dis.readFloat();
        fmt = dis.readFloat();
        t0 = dis.readFloat();
        t1 = dis.readFloat();
        t2 = dis.readFloat();
        t3 = dis.readFloat();
        t4 = dis.readFloat();
        t5 = dis.readFloat();
        t6 = dis.readFloat();
        t7 = dis.readFloat();
        t8 = dis.readFloat();
        t9 = dis.readFloat();
        f = dis.readFloat();
        resp0 = dis.readFloat();
        resp1 = dis.readFloat();
        resp2 = dis.readFloat();
        resp3 = dis.readFloat();
        resp4 = dis.readFloat();
        resp5 = dis.readFloat();
        resp6 = dis.readFloat();
        resp7 = dis.readFloat();
        resp8 = dis.readFloat();
        resp9 = dis.readFloat();
        stla = dis.readFloat();
        stlo = dis.readFloat();
        stel = dis.readFloat();
        stdp = dis.readFloat();
        evla = dis.readFloat();
        evlo = dis.readFloat();
        evel = dis.readFloat();
        evdp = dis.readFloat();
        mag = dis.readFloat();
        user0 = dis.readFloat();
        user1 = dis.readFloat();
        user2 = dis.readFloat();
        user3 = dis.readFloat();
        user4 = dis.readFloat();
        user5 = dis.readFloat();
        user6 = dis.readFloat();
        user7 = dis.readFloat();
        user8 = dis.readFloat();
        user9 = dis.readFloat();
        dist = dis.readFloat();
        az = dis.readFloat();
        baz = dis.readFloat();
        gcarc = dis.readFloat();
        sb = dis.readFloat();
        sdelta = dis.readFloat();
        depmen = dis.readFloat();
        cmpaz = dis.readFloat();
        cmpinc = dis.readFloat();
        xminimum = dis.readFloat();
        xmaximum = dis.readFloat();
        yminimum = dis.readFloat();
        ymaximum = dis.readFloat();
        unused6 = dis.readFloat();
        unused7 = dis.readFloat();
        unused8 = dis.readFloat();
        unused9 = dis.readFloat();
        unused10 = dis.readFloat();
        unused11 = dis.readFloat();
        unused12 = dis.readFloat();
        nzyear = dis.readInt();
        nzjday = dis.readInt();
        nzhour = dis.readInt();
        nzmin = dis.readInt();
        nzsec = dis.readInt();
        nzmsec = dis.readInt();
        nvhdr = dis.readInt();
        norid = dis.readInt();
        nevid = dis.readInt();
        npts = dis.readInt();
        nsnpts = dis.readInt();
        nwfid = dis.readInt();
        nxsize = dis.readInt();
        nysize = dis.readInt();
        unused15 = dis.readInt();
        iftype = dis.readInt();
        idep = dis.readInt();
        iztype = dis.readInt();
        unused16 = dis.readInt();
        iinst = dis.readInt();
        istreg = dis.readInt();
        ievreg = dis.readInt();
        ievtyp = dis.readInt();
        iqual = dis.readInt();
        isynth = dis.readInt();
        imagtyp = dis.readInt();
        imagsrc = dis.readInt();
        unused19 = dis.readInt();
        unused20 = dis.readInt();
        unused21 = dis.readInt();
        unused22 = dis.readInt();
        unused23 = dis.readInt();
        unused24 = dis.readInt();
        unused25 = dis.readInt();
        unused26 = dis.readInt();
        leven = dis.readInt();
        lpspol = dis.readInt();
        lovrok = dis.readInt();
        lcalda = dis.readInt();
        unused27 = dis.readInt();
        byte[] eightBytes = new byte[8];
        byte[] sixteenBytes = new byte[16];
        dis.readFully(eightBytes);
        kstnm = new String(eightBytes);
        dis.readFully(sixteenBytes);
        kevnm = new String(sixteenBytes);
        dis.readFully(eightBytes);
        khole = new String(eightBytes);
        dis.readFully(eightBytes);
        ko = new String(eightBytes);
        dis.readFully(eightBytes);
        ka = new String(eightBytes);
        dis.readFully(eightBytes);
        kt0 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt1 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt2 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt3 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt4 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt5 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt6 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt7 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt8 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt9 = new String(eightBytes);
        dis.readFully(eightBytes);
        kf = new String(eightBytes);
        dis.readFully(eightBytes);
        kuser0 = new String(eightBytes);
        dis.readFully(eightBytes);
        kuser1 = new String(eightBytes);
        dis.readFully(eightBytes);
        kuser2 = new String(eightBytes);
        dis.readFully(eightBytes);
        kcmpnm = new String(eightBytes);
        dis.readFully(eightBytes);
        knetwk = new String(eightBytes);
        dis.readFully(eightBytes);
        kdatrd = new String(eightBytes);
        dis.readFully(eightBytes);
        kinst = new String(eightBytes);
    }

    /** write the float to the stream, swapping bytes if needed. */
    final void writeFloat(DataOutput dos, float val) throws IOException {
        if (byteOrder == IntelByteOrder) {
            // careful here as dos.writeFloat() will collapse all NaN floats to
            // a single NaN value. But we are trying to write out byte swapped
            // values
            // so different floats that are all NaN are different values in the
            // other byte order. Solution is to swap on the integer bits, not
            // the float
            dos.writeInt(swapBytes(Float.floatToRawIntBits(val)));
        } else {
            dos.writeFloat(val);
        } // end of else
    }

    /** write the float to the stream, swapping bytes if needed. */
    private final void writeInt(DataOutput dos, int val) throws IOException {
        if (byteOrder == IntelByteOrder) {
            dos.writeInt(swapBytes(val));
        } else {
            dos.writeInt(val);
        } // end of else
    }

    /** writes this object out as a sac file. */
    public void writeHeader(File file) throws FileNotFoundException, IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        try {
            writeHeader(dos);
        } finally {
            dos.close();
        }
    }
    
    public void writeHeader(DataOutput dos) throws IOException {
        writeFloat(dos, delta);
        writeFloat(dos, depmin);
        writeFloat(dos, depmax);
        writeFloat(dos, scale);
        writeFloat(dos, odelta);
        writeFloat(dos, b);
        writeFloat(dos, e);
        writeFloat(dos, o);
        writeFloat(dos, a);
        writeFloat(dos, fmt);
        writeFloat(dos, t0);
        writeFloat(dos, t1);
        writeFloat(dos, t2);
        writeFloat(dos, t3);
        writeFloat(dos, t4);
        writeFloat(dos, t5);
        writeFloat(dos, t6);
        writeFloat(dos, t7);
        writeFloat(dos, t8);
        writeFloat(dos, t9);
        writeFloat(dos, f);
        writeFloat(dos, resp0);
        writeFloat(dos, resp1);
        writeFloat(dos, resp2);
        writeFloat(dos, resp3);
        writeFloat(dos, resp4);
        writeFloat(dos, resp5);
        writeFloat(dos, resp6);
        writeFloat(dos, resp7);
        writeFloat(dos, resp8);
        writeFloat(dos, resp9);
        writeFloat(dos, stla);
        writeFloat(dos, stlo);
        writeFloat(dos, stel);
        writeFloat(dos, stdp);
        writeFloat(dos, evla);
        writeFloat(dos, evlo);
        writeFloat(dos, evel);
        writeFloat(dos, evdp);
        writeFloat(dos, mag);
        writeFloat(dos, user0);
        writeFloat(dos, user1);
        writeFloat(dos, user2);
        writeFloat(dos, user3);
        writeFloat(dos, user4);
        writeFloat(dos, user5);
        writeFloat(dos, user6);
        writeFloat(dos, user7);
        writeFloat(dos, user8);
        writeFloat(dos, user9);
        writeFloat(dos, dist);
        writeFloat(dos, az);
        writeFloat(dos, baz);
        writeFloat(dos, gcarc);
        writeFloat(dos, sb);
        writeFloat(dos, sdelta);
        writeFloat(dos, depmen);
        writeFloat(dos, cmpaz);
        writeFloat(dos, cmpinc);
        writeFloat(dos, xminimum);
        writeFloat(dos, xmaximum);
        writeFloat(dos, yminimum);
        writeFloat(dos, ymaximum);
        writeFloat(dos, unused6);
        writeFloat(dos, unused7);
        writeFloat(dos, unused8);
        writeFloat(dos, unused9);
        writeFloat(dos, unused10);
        writeFloat(dos, unused11);
        writeFloat(dos, unused12);
        writeInt(dos, nzyear);
        writeInt(dos, nzjday);
        writeInt(dos, nzhour);
        writeInt(dos, nzmin);
        writeInt(dos, nzsec);
        writeInt(dos, nzmsec);
        writeInt(dos, nvhdr);
        writeInt(dos, norid);
        writeInt(dos, nevid);
        writeInt(dos, npts);
        writeInt(dos, nsnpts);
        writeInt(dos, nwfid);
        writeInt(dos, nxsize);
        writeInt(dos, nysize);
        writeInt(dos, unused15);
        writeInt(dos, iftype);
        writeInt(dos, idep);
        writeInt(dos, iztype);
        writeInt(dos, unused16);
        writeInt(dos, iinst);
        writeInt(dos, istreg);
        writeInt(dos, ievreg);
        writeInt(dos, ievtyp);
        writeInt(dos, iqual);
        writeInt(dos, isynth);
        writeInt(dos, imagtyp);
        writeInt(dos, imagsrc);
        writeInt(dos, unused19);
        writeInt(dos, unused20);
        writeInt(dos, unused21);
        writeInt(dos, unused22);
        writeInt(dos, unused23);
        writeInt(dos, unused24);
        writeInt(dos, unused25);
        writeInt(dos, unused26);
        writeInt(dos, leven);
        writeInt(dos, lpspol);
        writeInt(dos, lovrok);
        writeInt(dos, lcalda);
        writeInt(dos, unused27);
        dos.writeBytes(trimLen(kstnm, 8));
        dos.writeBytes(trimLen(kevnm, 16));
        dos.writeBytes(trimLen(khole, 8));
        dos.writeBytes(trimLen(ko, 8));
        dos.writeBytes(trimLen(ka, 8));
        dos.writeBytes(trimLen(kt0, 8));
        dos.writeBytes(trimLen(kt1, 8));
        dos.writeBytes(trimLen(kt2, 8));
        dos.writeBytes(trimLen(kt3, 8));
        dos.writeBytes(trimLen(kt4, 8));
        dos.writeBytes(trimLen(kt5, 8));
        dos.writeBytes(trimLen(kt6, 8));
        dos.writeBytes(trimLen(kt7, 8));
        dos.writeBytes(trimLen(kt8, 8));
        dos.writeBytes(trimLen(kt9, 8));
        dos.writeBytes(trimLen(kf, 8));
        dos.writeBytes(trimLen(kuser0, 8));
        dos.writeBytes(trimLen(kuser1, 8));
        dos.writeBytes(trimLen(kuser2, 8));
        dos.writeBytes(trimLen(kcmpnm, 8));
        dos.writeBytes(trimLen(knetwk, 8));
        dos.writeBytes(trimLen(kdatrd, 8));
        dos.writeBytes(trimLen(kinst, 8));
    }


    /**
     * Sets the byte order when writing to output. Does not change the internal
     * representation of the data.
     */
    public final void setLittleEndian() {
        byteOrder = IntelByteOrder;
    }

    /**
     * Sets the byte order when writing to output. Does not change the internal
     * representation of the data.
     */
    public final void setBigEndian() {
        byteOrder = SunByteOrder;
    }

    public final static short swapBytes(short val) {
        return (short)(((val & 0xff00) >> 8) + ((val & 0x00ff) << 8));
    }
    
    public final static int swapBytes(int val) {
        return ((val & 0xff000000) >>> 24) + ((val & 0x00ff0000) >> 8) + ((val & 0x0000ff00) << 8)
                + ((val & 0x000000ff) << 24);
    }

    public final static long swapBytes(long val) {
        return ((val & 0xffl << 56) >>> 56) + ((val & 0xffl << 48) >> 40) + ((val & 0xffl << 40) >> 24)
                + ((val & 0xffl << 32) >> 8) + ((val & 0xffl << 24) << 8) + ((val & 0xffl << 16) << 24)
                + ((val & 0xffl << 8) << 40) + ((val & 0xffl) << 56);
    }

    public static String trimLen(String s, int len) {
        if (s.length() > len) {
            s = s.substring(0, len - 1);
        }
        while (s.length() < len) {
            s += " ";
        }
        return s;
    }

    public static final DecimalFormat decimalFormat = new DecimalFormat("#####.####");

    public static String format(String label, float f) {
        return format(label, decimalFormat.format(f), 10, 8);
    }

    public static String format(String label, String val, int labelWidth, int valWidth) {
        while (label.length() < labelWidth) {
            label = " " + label;
        }
        while (val.length() < valWidth) {
            val = " " + val;
        }
        return label + " = " + val;
    }

    public static String formatLine(String s1,
                                    float f1,
                                    String s2,
                                    float f2,
                                    String s3,
                                    float f3,
                                    String s4,
                                    float f4,
                                    String s5,
                                    float f5) {
        return format(s1, f1) + format(s2, f2) + format(s3, f3) + format(s4, f4) + format(s5, f5);
    }

    public void printHeader() {
        printHeader(new PrintWriter(System.out, true));
    }

    public void printHeader(PrintWriter out) {
        out.println(formatLine("delta", delta, "depmin", depmin, "depmax", depmax, "scale", scale, "odelta", odelta));
        out.println(formatLine("b", b, "e", e, "o", o, "a", a, "fmt", fmt));
        out.println(formatLine("t0", t0, "t1", t1, "t2", t2, "t3", t3, "t4", t4));
        out.println(formatLine("t5", t5, "t6", t6, "t7", t7, "t8", t8, "t9", t9));
        out.println(formatLine("f", f, "resp0", resp0, "resp1", resp1, "resp2", resp2, "resp3", resp3));
        out.println(formatLine("resp4", resp4, "resp5", resp5, "resp6", resp6, "resp7", resp7, "resp8", resp8));
        out.println(formatLine("resp9", resp9, "stla", stla, "stlo", stlo, "stel", stel, "stdp", stdp));
        out.println(formatLine("evla", evla, "evlo", evlo, "evel", evel, "evdp", evdp, "mag", mag));
        out.println(formatLine("user0", user0, "user1", user1, "user2", user2, "user3", user3, "user4", user4));
        out.println(formatLine("user5", user5, "user6", user6, "user7", user7, "user8", user8, "user9", user9));
        out.println(formatLine("dist", dist, "az", az, "baz", baz, "gcarc", gcarc, "sb", sb));
        out.println(formatLine("sdelta",
                               sdelta,
                               "depmen",
                               depmen,
                               "cmpaz",
                               cmpaz,
                               "cmpinc",
                               cmpinc,
                               "xminimum",
                               xminimum));
        out.println(formatLine("xmaximum",
                               xmaximum,
                               "yminimum",
                               yminimum,
                               "ymaximum",
                               ymaximum,
                               "unused6",
                               unused6,
                               "unused7",
                               unused7));
        out.println(formatLine("unused8",
                               unused8,
                               "unused9",
                               unused9,
                               "unused10",
                               unused10,
                               "unused11",
                               unused11,
                               "unused12",
                               unused12));
        out.println(formatLine("nzyear", nzyear, "nzjday", nzjday, "nzhour", nzhour, "nzmin", nzmin, "nzsec", nzsec));
        out.println(formatLine("nzmsec", nzmsec, "nvhdr", nvhdr, "norid", norid, "nevid", nevid, "npts", npts));
        out.println(formatLine("nsnpts",
                               nsnpts,
                               "nwfid",
                               nwfid,
                               "nxsize",
                               nxsize,
                               "nysize",
                               nysize,
                               "unused15",
                               unused15));
        out.println(formatLine("iftype", iftype, "idep", idep, "iztype", iztype, "unused16", unused16, "iinst", iinst));
        out.println(formatLine("istreg", istreg, "ievreg", ievreg, "ievtyp", ievtyp, "iqual", iqual, "isynth", isynth));
        out.println(formatLine("imagtyp",
                               imagtyp,
                               "imagsrc",
                               imagsrc,
                               "unused19",
                               unused19,
                               "unused20",
                               unused20,
                               "unused21",
                               unused21));
        out.println(formatLine("unused22",
                               unused22,
                               "unused23",
                               unused23,
                               "unused24",
                               unused24,
                               "unused25",
                               unused25,
                               "unused26",
                               unused26));
        out.println(formatLine("leven",
                               leven,
                               "lpspol",
                               lpspol,
                               "lovrok",
                               lovrok,
                               "lcalda",
                               lcalda,
                               "unused27",
                               unused27));
        int labelWidth = 10;
        int wideValWidth = 31;
        int valWidth = 10;
        out.println(format("kstnm", kstnm, labelWidth, valWidth) + format("kevnm", kevnm, labelWidth, wideValWidth)
                + format("khole", khole, labelWidth+2, valWidth));
        out.println(format("ko", ko, labelWidth, valWidth) + format("ka = ", ka, labelWidth, valWidth)
                + format("kt0", kt0, labelWidth, valWidth) + format("kt1", kt1, labelWidth, valWidth));
        out.println(format("kt2", kt2, labelWidth, valWidth) + format("kt3 = ", kt3, labelWidth, valWidth)
                + format("kt4", kt4, labelWidth, valWidth) + format("kt5", kt5, labelWidth, valWidth));
        out.println(format("kt6", kt6, labelWidth, valWidth) + format("kt7 = ", kt7, labelWidth, valWidth)
                + format("kt8", kt8, labelWidth, valWidth) + format("kt9", kt9, labelWidth, valWidth));
        out.println(format("kf", kf, labelWidth, valWidth) + format("kuser0 = ", kuser0, labelWidth, valWidth)
                + format("kuser1", kuser1, labelWidth, valWidth) + format("kuser2", kuser2, labelWidth, valWidth));
        out.println(format("kcmpnm", kcmpnm, labelWidth, valWidth) + format("knetwk = ", knetwk, labelWidth, valWidth)
                + format("kdatrd", kdatrd, labelWidth, valWidth) + format("kinst", kinst, labelWidth, valWidth));
    }

    
    boolean byteOrder = SunByteOrder;

    
    public boolean getByteOrder() {
        return byteOrder;
    }
    
    /** RF time increment, sec */
    private float delta = FLOAT_UNDEF;

    /** minimum amplitude */
    private float depmin = FLOAT_UNDEF;

    /** maximum amplitude */
    private float depmax = FLOAT_UNDEF;

    /** amplitude scale factor */
    private float scale = FLOAT_UNDEF;

    /** observed time inc */
    private float odelta = FLOAT_UNDEF;

    /** RD initial time - wrt nz* */
    private float b = FLOAT_UNDEF;

    /** RD end time */
    private float e = FLOAT_UNDEF;

    /** event start */
    private float o = FLOAT_UNDEF;

    /** 1st arrival time */
    private float a = FLOAT_UNDEF;

    /** internal use */
    private float fmt = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t0 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t1 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t2 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t3 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t4 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t5 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t6 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t7 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t8 = FLOAT_UNDEF;

    /** user-defined time pick */
    private float t9 = FLOAT_UNDEF;

    /** event end, sec > 0 */
    private float f = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp0 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp1 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp2 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp3 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp4 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp5 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp6 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp7 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp8 = FLOAT_UNDEF;

    /** instrument respnse parm */
    private float resp9 = FLOAT_UNDEF;

    /** T station latititude */
    private float stla = FLOAT_UNDEF;

    /** T station longitude */
    private float stlo = FLOAT_UNDEF;

    /** T station elevation, m */
    private float stel = FLOAT_UNDEF;

    /** T station depth, m */
    private float stdp = FLOAT_UNDEF;

    /** event latitude */
    private float evla = FLOAT_UNDEF;

    /** event longitude */
    private float evlo = FLOAT_UNDEF;

    /** event elevation */
    private float evel = FLOAT_UNDEF;

    /** event depth */
    private float evdp = FLOAT_UNDEF;

    /** magnitude value */
    private float mag = FLOAT_UNDEF;

    /** available to user */
    private float user0 = FLOAT_UNDEF;

    /** available to user */
    private float user1 = FLOAT_UNDEF;

    /** available to user */
    private float user2 = FLOAT_UNDEF;

    /** available to user */
    private float user3 = FLOAT_UNDEF;

    /** available to user */
    private float user4 = FLOAT_UNDEF;

    /** available to user */
    private float user5 = FLOAT_UNDEF;

    /** available to user */
    private float user6 = FLOAT_UNDEF;

    /** available to user */
    private float user7 = FLOAT_UNDEF;

    /** available to user */
    private float user8 = FLOAT_UNDEF;

    /** available to user */
    private float user9 = FLOAT_UNDEF;

    /** stn-event distance, km */
    private float dist = FLOAT_UNDEF;

    /** event-stn azimuth */
    private float az = FLOAT_UNDEF;

    /** stn-event azimuth */
    private float baz = FLOAT_UNDEF;

    /** stn-event dist, degrees */
    private float gcarc = FLOAT_UNDEF;

    /** saved b value */
    private float sb = FLOAT_UNDEF;

    /** saved delta value */
    private float sdelta = FLOAT_UNDEF;

    /** mean value, amplitude */
    private float depmen = FLOAT_UNDEF;

    /** T component azimuth */
    private float cmpaz = FLOAT_UNDEF;

    /** T component inclination */
    private float cmpinc = FLOAT_UNDEF;

    /** XYZ X minimum value */
    private float xminimum = FLOAT_UNDEF;

    /** XYZ X maximum value */
    private float xmaximum = FLOAT_UNDEF;

    /** XYZ Y minimum value */
    private float yminimum = FLOAT_UNDEF;

    /** XYZ Y maximum value */
    private float ymaximum = FLOAT_UNDEF;

    /** reserved for future use */
    private float unused6 = FLOAT_UNDEF;

    /** reserved for future use */
    private float unused7 = FLOAT_UNDEF;

    /** reserved for future use */
    private float unused8 = FLOAT_UNDEF;

    /** reserved for future use */
    private float unused9 = FLOAT_UNDEF;

    /** reserved for future use */
    private float unused10 = FLOAT_UNDEF;

    /** reserved for future use */
    private float unused11 = FLOAT_UNDEF;

    /** reserved for future use */
    private float unused12 = FLOAT_UNDEF;

    /** F zero time of file, yr */
    private int nzyear = INT_UNDEF;

    /** F zero time of file, day */
    private int nzjday = INT_UNDEF;

    /** F zero time of file, hr */
    private int nzhour = INT_UNDEF;

    /** F zero time of file, min */
    private int nzmin = INT_UNDEF;

    /** F zero time of file, sec */
    private int nzsec = INT_UNDEF;

    /** F zero time of file, msec */
    private int nzmsec = INT_UNDEF;

    /** R header version number */
    private int nvhdr = DEFAULT_NVHDR;

    /** Origin ID */
    private int norid = INT_UNDEF;

    /** Event ID */
    private int nevid = INT_UNDEF;

    /** RF number of samples */
    private int npts = INT_UNDEF;

    /** saved npts */
    private int nsnpts = INT_UNDEF;

    /** Waveform ID */
    private int nwfid = INT_UNDEF;

    /** XYZ X size */
    private int nxsize = INT_UNDEF;

    /** XYZ Y size */
    private int nysize = INT_UNDEF;

    /** reserved for future use */
    private int unused15 = INT_UNDEF;

    /** RA type of file */
    private int iftype = INT_UNDEF;

    /** type of amplitude */
    private int idep = INT_UNDEF;

    /** zero time equivalence */
    private int iztype = INT_UNDEF;

    /** reserved for future use */
    private int unused16 = INT_UNDEF;

    /** recording instrument */
    private int iinst = INT_UNDEF;

    /** stn geographic region */
    private int istreg = INT_UNDEF;

    /** event geographic region */
    private int ievreg = INT_UNDEF;

    /** event type */
    private int ievtyp = INT_UNDEF;

    /** quality of data */
    private int iqual = INT_UNDEF;

    /** synthetic data flag */
    private int isynth = INT_UNDEF;

    /** magnitude type */
    private int imagtyp = INT_UNDEF;

    /** magnitude source */
    private int imagsrc = INT_UNDEF;

    /** reserved for future use */
    private int unused19 = INT_UNDEF;

    /** reserved for future use */
    private int unused20 = INT_UNDEF;

    /** reserved for future use */
    private int unused21 = INT_UNDEF;

    /** reserved for future use */
    private int unused22 = INT_UNDEF;

    /** reserved for future use */
    private int unused23 = INT_UNDEF;

    /** reserved for future use */
    private int unused24 = INT_UNDEF;

    /** reserved for future use */
    private int unused25 = INT_UNDEF;

    /** reserved for future use */
    private int unused26 = INT_UNDEF;

    /** RA data-evenly-spaced flag */
    private int leven = INT_UNDEF;

    /** station polarity flag */
    private int lpspol = INT_UNDEF;

    /** overwrite permission */
    private int lovrok = INT_UNDEF;

    /** calc distance, azimuth */
    private int lcalda = INT_UNDEF;

    /** reserved for future use */
    private int unused27 = INT_UNDEF;

    /** F station name */
    private String kstnm = STRING8_UNDEF;

    /** event name */
    private String kevnm = STRING16_UNDEF;

    /** man-made event name */
    private String khole = STRING8_UNDEF;

    /** event origin time id */
    private String ko = STRING8_UNDEF;

    /** 1st arrival time ident */
    private String ka = STRING8_UNDEF;

    /** time pick 0 ident */
    private String kt0 = STRING8_UNDEF;

    /** time pick 1 ident */
    private String kt1 = STRING8_UNDEF;

    /** time pick 2 ident */
    private String kt2 = STRING8_UNDEF;

    /** time pick 3 ident */
    private String kt3 = STRING8_UNDEF;

    /** time pick 4 ident */
    private String kt4 = STRING8_UNDEF;

    /** time pick 5 ident */
    private String kt5 = STRING8_UNDEF;

    /** time pick 6 ident */
    private String kt6 = STRING8_UNDEF;

    /** time pick 7 ident */
    private String kt7 = STRING8_UNDEF;

    /** time pick 8 ident */
    private String kt8 = STRING8_UNDEF;

    /** time pick 9 ident */
    private String kt9 = STRING8_UNDEF;

    /** end of event ident */
    private String kf = STRING8_UNDEF;

    /** available to user */
    private String kuser0 = STRING8_UNDEF;

    /** available to user */
    private String kuser1 = STRING8_UNDEF;

    /** available to user */
    private String kuser2 = STRING8_UNDEF;

    /** F component name */
    private String kcmpnm = STRING8_UNDEF;

    /** network name */
    private String knetwk = STRING8_UNDEF;

    /** date data read */
    private String kdatrd = STRING8_UNDEF;

    /** instrument name */
    private String kinst = STRING8_UNDEF;

    
    public float getDelta() {
        return delta;
    }

    
    public void setDelta(float delta) {
        this.delta = delta;
    }

    
    public float getDepmin() {
        return depmin;
    }

    
    public void setDepmin(float depmin) {
        this.depmin = depmin;
    }

    
    public float getDepmax() {
        return depmax;
    }

    
    public void setDepmax(float depmax) {
        this.depmax = depmax;
    }

    
    public float getScale() {
        return scale;
    }

    
    public void setScale(float scale) {
        this.scale = scale;
    }

    
    public float getOdelta() {
        return odelta;
    }

    
    public void setOdelta(float odelta) {
        this.odelta = odelta;
    }

    
    public float getB() {
        return b;
    }

    
    public void setB(float b) {
        this.b = b;
    }

    
    public float getE() {
        return e;
    }

    
    public void setE(float e) {
        this.e = e;
    }

    
    public float getO() {
        return o;
    }

    
    public void setO(float o) {
        this.o = o;
    }

    
    public float getA() {
        return a;
    }

    
    public void setA(float a) {
        this.a = a;
    }

    
    public float getFmt() {
        return fmt;
    }

    
    public void setFmt(float fmt) {
        this.fmt = fmt;
    }

    public float getTHeader(int index) {
        switch(index){
            case 0:
                return getT0();
            case 1:
                return getT1();
            case 2:
                return getT2();
            case 3:
                return getT3();
            case 4:
                return getT4();
            case 5:
                return getT5();
            case 6:
                return getT6();
            case 7:
                return getT7();
            case 8:
                return getT8();
            case 9:
                return getT9();
            default:
                throw new IllegalArgumentException("Illegal T header index, "+index+", must be 0-9");
        }
    }
    
    public void setTHeader(int index, float val) {
        switch(index){
            case 0:
                setT0(val);
                break;
            case 1:
                setT1(val);
                break;
            case 2:
                setT2(val);
                break;
            case 3:
                setT3(val);
                break;
            case 4:
                setT4(val);
                break;
            case 5:
                setT5(val);
                break;
            case 6:
                setT6(val);
                break;
            case 7:
                setT7(val);
                break;
            case 8:
                setT8(val);
                break;
            case 9:
                setT9(val);
                break;
            default:
                throw new IllegalArgumentException("Illegal T header index, "+index+", must be 0-9");
        }
    }
    
    /**
     * Sets T header specified by the index to val, and sets the corresponding
     * KT header to be the label. indices 0-9 map to T0-T9 and index 10 maps to 
     * the A header.
     */
    public void setTHeader(int index, float val, String kLabel) {
        switch(index){
            case 0:
                setT0(val);
                setKt0(kLabel);
                break;
            case 1:
                setT1(val);
                setKt1(kLabel);
                break;
            case 2:
                setT2(val);
                setKt2(kLabel);
                break;
            case 3:
                setT3(val);
                setKt3(kLabel);
                break;
            case 4:
                setT4(val);
                setKt4(kLabel);
                break;
            case 5:
                setT5(val);
                setKt5(kLabel);
                break;
            case 6:
                setT6(val);
                setKt6(kLabel);
                break;
            case 7:
                setT7(val);
                setKt7(kLabel);
                break;
            case 8:
                setT8(val);
                setKt8(kLabel);
                break;
            case 9:
                setT9(val);
                setKt9(kLabel);
                break;
            default:
                throw new IllegalArgumentException("Illegal T header index, "+index+", must be 0-9");
        }
    }

    public String getKTHeader(int index) {
        switch(index){
            case 0:
                return getKt0();
            case 1:
                return getKt1();
            case 2:
                return getKt2();
            case 3:
                return getKt3();
            case 4:
                return getKt4();
            case 5:
                return getKt5();
            case 6:
                return getKt6();
            case 7:
                return getKt7();
            case 8:
                return getKt8();
            case 9:
                return getKt9();
            default:
                throw new IllegalArgumentException("Illegal T header index, "+index+", must be 0-9");
        }
    }

    public void setKtHeader(int index, String val) {
        switch(index){
            case 0:
                setKt0(val);
                break;
            case 1:
                setKt1(val);
                break;
            case 2:
                setKt2(val);
                break;
            case 3:
                setKt3(val);
                break;
            case 4:
                setKt4(val);
                break;
            case 5:
                setKt5(val);
                break;
            case 6:
                setKt6(val);
                break;
            case 7:
                setKt7(val);
                break;
            case 8:
                setKt8(val);
                break;
            case 9:
                setKt9(val);
                break;
            default:
                throw new IllegalArgumentException("Illegal T header index, "+index+", must be 0-9");
        }
    }
    
    public float getT0() {
        return t0;
    }

    
    public void setT0(float t0) {
        this.t0 = t0;
    }

    
    public float getT1() {
        return t1;
    }

    
    public void setT1(float t1) {
        this.t1 = t1;
    }

    
    public float getT2() {
        return t2;
    }

    
    public void setT2(float t2) {
        this.t2 = t2;
    }

    
    public float getT3() {
        return t3;
    }

    
    public void setT3(float t3) {
        this.t3 = t3;
    }

    
    public float getT4() {
        return t4;
    }

    
    public void setT4(float t4) {
        this.t4 = t4;
    }

    
    public float getT5() {
        return t5;
    }

    
    public void setT5(float t5) {
        this.t5 = t5;
    }

    
    public float getT6() {
        return t6;
    }

    
    public void setT6(float t6) {
        this.t6 = t6;
    }

    
    public float getT7() {
        return t7;
    }

    
    public void setT7(float t7) {
        this.t7 = t7;
    }

    
    public float getT8() {
        return t8;
    }

    
    public void setT8(float t8) {
        this.t8 = t8;
    }

    
    public float getT9() {
        return t9;
    }

    
    public void setT9(float t9) {
        this.t9 = t9;
    }

    
    public float getF() {
        return f;
    }

    
    public void setF(float f) {
        this.f = f;
    }

    
    public float getResp0() {
        return resp0;
    }

    
    public void setResp0(float resp0) {
        this.resp0 = resp0;
    }

    
    public float getResp1() {
        return resp1;
    }

    
    public void setResp1(float resp1) {
        this.resp1 = resp1;
    }

    
    public float getResp2() {
        return resp2;
    }

    
    public void setResp2(float resp2) {
        this.resp2 = resp2;
    }

    
    public float getResp3() {
        return resp3;
    }

    
    public void setResp3(float resp3) {
        this.resp3 = resp3;
    }

    
    public float getResp4() {
        return resp4;
    }

    
    public void setResp4(float resp4) {
        this.resp4 = resp4;
    }

    
    public float getResp5() {
        return resp5;
    }

    
    public void setResp5(float resp5) {
        this.resp5 = resp5;
    }

    
    public float getResp6() {
        return resp6;
    }

    
    public void setResp6(float resp6) {
        this.resp6 = resp6;
    }

    
    public float getResp7() {
        return resp7;
    }

    
    public void setResp7(float resp7) {
        this.resp7 = resp7;
    }

    
    public float getResp8() {
        return resp8;
    }

    
    public void setResp8(float resp8) {
        this.resp8 = resp8;
    }

    
    public float getResp9() {
        return resp9;
    }

    
    public void setResp9(float resp9) {
        this.resp9 = resp9;
    }

    
    public float getStla() {
        return stla;
    }

    
    public void setStla(float stla) {
        this.stla = stla;
    }

    
    public float getStlo() {
        return stlo;
    }

    
    public void setStlo(float stlo) {
        this.stlo = stlo;
    }

    
    public float getStel() {
        return stel;
    }

    
    public void setStel(float stel) {
        this.stel = stel;
    }

    
    public float getStdp() {
        return stdp;
    }

    
    public void setStdp(float stdp) {
        this.stdp = stdp;
    }

    
    public float getEvla() {
        return evla;
    }

    
    public void setEvla(float evla) {
        this.evla = evla;
    }

    
    public float getEvlo() {
        return evlo;
    }

    
    public void setEvlo(float evlo) {
        this.evlo = evlo;
    }

    
    public float getEvel() {
        return evel;
    }

    
    public void setEvel(float evel) {
        this.evel = evel;
    }

    
    public float getEvdp() {
        return evdp;
    }

    
    public void setEvdp(float evdp) {
        this.evdp = evdp;
    }

    
    public float getMag() {
        return mag;
    }

    
    public void setMag(float mag) {
        this.mag = mag;
    }

    public float getUserHeader(int index) {
        switch(index){
            case 0:
                return getUser0();
            case 1:
                return getUser1();
            case 2:
                return getUser2();
            case 3:
                return getUser3();
            case 4:
                return getUser4();
            case 5:
                return getUser5();
            case 6:
                return getUser6();
            case 7:
                return getUser7();
            case 8:
                return getUser8();
            case 9:
                return getUser9();
            default:
                throw new IllegalArgumentException("Illegal User header index, "+index+", must be 0-9");
        }
    }
    
    public void setUserHeader(int index, float val) {
        switch(index){
            case 0:
                setUser0(val);
                break;
            case 1:
                setUser1(val);
                break;
            case 2:
                setUser2(val);
                break;
            case 3:
                setUser3(val);
                break;
            case 4:
                setUser4(val);
                break;
            case 5:
                setUser5(val);
                break;
            case 6:
                setUser6(val);
                break;
            case 7:
                setUser7(val);
                break;
            case 8:
                setUser8(val);
                break;
            case 9:
                setUser9(val);
                break;
            default:
                throw new IllegalArgumentException("Illegal User header index, "+index+", must be 0-9");
        }
    }
    
    public float getUser0() {
        return user0;
    }

    
    public void setUser0(float user0) {
        this.user0 = user0;
    }

    
    public float getUser1() {
        return user1;
    }

    
    public void setUser1(float user1) {
        this.user1 = user1;
    }

    
    public float getUser2() {
        return user2;
    }

    
    public void setUser2(float user2) {
        this.user2 = user2;
    }

    
    public float getUser3() {
        return user3;
    }

    
    public void setUser3(float user3) {
        this.user3 = user3;
    }

    
    public float getUser4() {
        return user4;
    }

    
    public void setUser4(float user4) {
        this.user4 = user4;
    }

    
    public float getUser5() {
        return user5;
    }

    
    public void setUser5(float user5) {
        this.user5 = user5;
    }

    
    public float getUser6() {
        return user6;
    }

    
    public void setUser6(float user6) {
        this.user6 = user6;
    }

    
    public float getUser7() {
        return user7;
    }

    
    public void setUser7(float user7) {
        this.user7 = user7;
    }

    
    public float getUser8() {
        return user8;
    }

    
    public void setUser8(float user8) {
        this.user8 = user8;
    }

    
    public float getUser9() {
        return user9;
    }

    
    public void setUser9(float user9) {
        this.user9 = user9;
    }

    
    public float getDist() {
        return dist;
    }

    
    public void setDist(float dist) {
        this.dist = dist;
    }

    
    public float getAz() {
        return az;
    }

    
    public void setAz(float az) {
        this.az = az;
    }

    
    public float getBaz() {
        return baz;
    }

    
    public void setBaz(float baz) {
        this.baz = baz;
    }

    
    public float getGcarc() {
        return gcarc;
    }

    
    public void setGcarc(float gcarc) {
        this.gcarc = gcarc;
    }

    
    public float getSb() {
        return sb;
    }

    
    public void setSb(float sb) {
        this.sb = sb;
    }

    
    public float getSdelta() {
        return sdelta;
    }

    
    public void setSdelta(float sdelta) {
        this.sdelta = sdelta;
    }

    
    public float getDepmen() {
        return depmen;
    }

    
    public void setDepmen(float depmen) {
        this.depmen = depmen;
    }

    
    public float getCmpaz() {
        return cmpaz;
    }

    
    public void setCmpaz(float cmpaz) {
        this.cmpaz = cmpaz;
    }

    
    public float getCmpinc() {
        return cmpinc;
    }

    
    public void setCmpinc(float cmpinc) {
        this.cmpinc = cmpinc;
    }

    
    public float getXminimum() {
        return xminimum;
    }

    
    public void setXminimum(float xminimum) {
        this.xminimum = xminimum;
    }

    
    public float getXmaximum() {
        return xmaximum;
    }

    
    public void setXmaximum(float xmaximum) {
        this.xmaximum = xmaximum;
    }

    
    public float getYminimum() {
        return yminimum;
    }

    
    public void setYminimum(float yminimum) {
        this.yminimum = yminimum;
    }

    
    public float getYmaximum() {
        return ymaximum;
    }

    
    public void setYmaximum(float ymaximum) {
        this.ymaximum = ymaximum;
    }

    
    public float getUnused6() {
        return unused6;
    }

    
    public void setUnused6(float unused6) {
        this.unused6 = unused6;
    }

    
    public float getUnused7() {
        return unused7;
    }

    
    public void setUnused7(float unused7) {
        this.unused7 = unused7;
    }

    
    public float getUnused8() {
        return unused8;
    }

    
    public void setUnused8(float unused8) {
        this.unused8 = unused8;
    }

    
    public float getUnused9() {
        return unused9;
    }

    
    public void setUnused9(float unused9) {
        this.unused9 = unused9;
    }

    
    public float getUnused10() {
        return unused10;
    }

    
    public void setUnused10(float unused10) {
        this.unused10 = unused10;
    }

    
    public float getUnused11() {
        return unused11;
    }

    
    public void setUnused11(float unused11) {
        this.unused11 = unused11;
    }

    
    public float getUnused12() {
        return unused12;
    }

    
    public void setUnused12(float unused12) {
        this.unused12 = unused12;
    }

    
    public int getNzyear() {
        return nzyear;
    }

    
    public void setNzyear(int nzyear) {
        this.nzyear = nzyear;
    }

    
    public int getNzjday() {
        return nzjday;
    }

    
    public void setNzjday(int nzjday) {
        this.nzjday = nzjday;
    }

    
    public int getNzhour() {
        return nzhour;
    }

    
    public void setNzhour(int nzhour) {
        this.nzhour = nzhour;
    }

    
    public int getNzmin() {
        return nzmin;
    }

    
    public void setNzmin(int nzmin) {
        this.nzmin = nzmin;
    }

    
    public int getNzsec() {
        return nzsec;
    }

    
    public void setNzsec(int nzsec) {
        this.nzsec = nzsec;
    }

    
    public int getNzmsec() {
        return nzmsec;
    }

    
    public void setNzmsec(int nzmsec) {
        this.nzmsec = nzmsec;
    }

    
    public int getNvhdr() {
        return nvhdr;
    }

    
    public void setNvhdr(int nvhdr) {
        this.nvhdr = nvhdr;
    }

    
    public int getNorid() {
        return norid;
    }

    
    public void setNorid(int norid) {
        this.norid = norid;
    }

    
    public int getNevid() {
        return nevid;
    }

    
    public void setNevid(int nevid) {
        this.nevid = nevid;
    }

    
    public int getNpts() {
        return npts;
    }

    
    public void setNpts(int npts) {
        this.npts = npts;
    }

    
    public int getNsnpts() {
        return nsnpts;
    }

    
    public void setNsnpts(int nsnpts) {
        this.nsnpts = nsnpts;
    }

    
    public int getNwfid() {
        return nwfid;
    }

    
    public void setNwfid(int nwfid) {
        this.nwfid = nwfid;
    }

    
    public int getNxsize() {
        return nxsize;
    }

    
    public void setNxsize(int nxsize) {
        this.nxsize = nxsize;
    }

    
    public int getNysize() {
        return nysize;
    }

    
    public void setNysize(int nysize) {
        this.nysize = nysize;
    }

    
    public int getUnused15() {
        return unused15;
    }

    
    public void setUnused15(int unused15) {
        this.unused15 = unused15;
    }

    
    public int getIftype() {
        return iftype;
    }

    
    public void setIftype(int iftype) {
        this.iftype = iftype;
    }

    
    public int getIdep() {
        return idep;
    }

    
    public void setIdep(int idep) {
        this.idep = idep;
    }

    
    public int getIztype() {
        return iztype;
    }

    
    public void setIztype(int iztype) {
        this.iztype = iztype;
    }

    
    public int getUnused16() {
        return unused16;
    }

    
    public void setUnused16(int unused16) {
        this.unused16 = unused16;
    }

    
    public int getIinst() {
        return iinst;
    }

    
    public void setIinst(int iinst) {
        this.iinst = iinst;
    }

    
    public int getIstreg() {
        return istreg;
    }

    
    public void setIstreg(int istreg) {
        this.istreg = istreg;
    }

    
    public int getIevreg() {
        return ievreg;
    }

    
    public void setIevreg(int ievreg) {
        this.ievreg = ievreg;
    }

    
    public int getIevtyp() {
        return ievtyp;
    }

    
    public void setIevtyp(int ievtyp) {
        this.ievtyp = ievtyp;
    }

    
    public int getIqual() {
        return iqual;
    }

    
    public void setIqual(int iqual) {
        this.iqual = iqual;
    }

    
    public int getIsynth() {
        return isynth;
    }

    
    public void setIsynth(int isynth) {
        this.isynth = isynth;
    }

    
    public int getImagtyp() {
        return imagtyp;
    }

    
    public void setImagtyp(int imagtyp) {
        this.imagtyp = imagtyp;
    }

    
    public int getImagsrc() {
        return imagsrc;
    }

    
    public void setImagsrc(int imagsrc) {
        this.imagsrc = imagsrc;
    }

    
    public int getUnused19() {
        return unused19;
    }

    
    public void setUnused19(int unused19) {
        this.unused19 = unused19;
    }

    
    public int getUnused20() {
        return unused20;
    }

    
    public void setUnused20(int unused20) {
        this.unused20 = unused20;
    }

    
    public int getUnused21() {
        return unused21;
    }

    
    public void setUnused21(int unused21) {
        this.unused21 = unused21;
    }

    
    public int getUnused22() {
        return unused22;
    }

    
    public void setUnused22(int unused22) {
        this.unused22 = unused22;
    }

    
    public int getUnused23() {
        return unused23;
    }

    
    public void setUnused23(int unused23) {
        this.unused23 = unused23;
    }

    
    public int getUnused24() {
        return unused24;
    }

    
    public void setUnused24(int unused24) {
        this.unused24 = unused24;
    }

    
    public int getUnused25() {
        return unused25;
    }

    
    public void setUnused25(int unused25) {
        this.unused25 = unused25;
    }

    
    public int getUnused26() {
        return unused26;
    }

    
    public void setUnused26(int unused26) {
        this.unused26 = unused26;
    }

    
    public int getLeven() {
        return leven;
    }

    
    public void setLeven(int leven) {
        this.leven = leven;
    }

    
    public int getLpspol() {
        return lpspol;
    }

    
    public void setLpspol(int lpspol) {
        this.lpspol = lpspol;
    }

    
    public int getLovrok() {
        return lovrok;
    }

    
    public void setLovrok(int lovrok) {
        this.lovrok = lovrok;
    }

    
    public int getLcalda() {
        return lcalda;
    }

    
    public void setLcalda(int lcalda) {
        this.lcalda = lcalda;
    }

    
    public int getUnused27() {
        return unused27;
    }

    
    public void setUnused27(int unused27) {
        this.unused27 = unused27;
    }

    
    public String getKstnm() {
        return kstnm;
    }

    
    public void setKstnm(String kstnm) {
        this.kstnm = kstnm;
    }

    
    public String getKevnm() {
        return kevnm;
    }

    
    public void setKevnm(String kevnm) {
        this.kevnm = kevnm;
    }

    
    public String getKhole() {
        return khole;
    }

    
    public void setKhole(String khole) {
        this.khole = khole;
    }

    
    public String getKo() {
        return ko;
    }

    
    public void setKo(String ko) {
        this.ko = ko;
    }

    
    public String getKa() {
        return ka;
    }

    
    public void setKa(String ka) {
        this.ka = ka;
    }

    
    public String getKt0() {
        return kt0;
    }

    
    public void setKt0(String kt0) {
        this.kt0 = kt0;
    }

    
    public String getKt1() {
        return kt1;
    }

    
    public void setKt1(String kt1) {
        this.kt1 = kt1;
    }

    
    public String getKt2() {
        return kt2;
    }

    
    public void setKt2(String kt2) {
        this.kt2 = kt2;
    }

    
    public String getKt3() {
        return kt3;
    }

    
    public void setKt3(String kt3) {
        this.kt3 = kt3;
    }

    
    public String getKt4() {
        return kt4;
    }

    
    public void setKt4(String kt4) {
        this.kt4 = kt4;
    }

    
    public String getKt5() {
        return kt5;
    }

    
    public void setKt5(String kt5) {
        this.kt5 = kt5;
    }

    
    public String getKt6() {
        return kt6;
    }

    
    public void setKt6(String kt6) {
        this.kt6 = kt6;
    }

    
    public String getKt7() {
        return kt7;
    }

    
    public void setKt7(String kt7) {
        this.kt7 = kt7;
    }

    
    public String getKt8() {
        return kt8;
    }

    
    public void setKt8(String kt8) {
        this.kt8 = kt8;
    }

    
    public String getKt9() {
        return kt9;
    }

    
    public void setKt9(String kt9) {
        this.kt9 = kt9;
    }

    
    public String getKf() {
        return kf;
    }

    
    public void setKf(String kf) {
        this.kf = kf;
    }

    
    public String getKuser0() {
        return kuser0;
    }

    
    public void setKuser0(String kuser0) {
        this.kuser0 = kuser0;
    }

    
    public String getKuser1() {
        return kuser1;
    }

    
    public void setKuser1(String kuser1) {
        this.kuser1 = kuser1;
    }

    
    public String getKuser2() {
        return kuser2;
    }

    
    public void setKuser2(String kuser2) {
        this.kuser2 = kuser2;
    }

    
    public String getKcmpnm() {
        return kcmpnm;
    }

    
    public void setKcmpnm(String kcmpnm) {
        this.kcmpnm = kcmpnm;
    }

    
    public String getKnetwk() {
        return knetwk;
    }

    
    public void setKnetwk(String knetwk) {
        this.knetwk = knetwk;
    }

    
    public String getKdatrd() {
        return kdatrd;
    }

    
    public void setKdatrd(String kdatrd) {
        this.kdatrd = kdatrd;
    }

    
    public String getKinst() {
        return kinst;
    }

    
    public void setKinst(String kinst) {
        this.kinst = kinst;
    }

    
    public void setByteOrder(boolean byteOrder) {
        this.byteOrder = byteOrder;
    }
}
