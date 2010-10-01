/*
 * The TauP Toolkit: Flexible Seismic Travel-Time and Raypath Utilities.
 * Copyright (C) 1998-2000 University of South Carolina This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place
 * - Suite 330, Boston, MA 02111-1307, USA. The current version can be found at
 * <A HREF="www.seis.sc.edu">http://www.seis.sc.edu </A> Bug reports and
 * comments should be directed to H. Philip Crotwell, crotwell@seis.sc.edu or
 * Tom Owens, owens@seis.sc.edu
 */
// package edu.sc.seis.TauP;
package edu.sc.seis.seisFile.sac;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Class that represents a sac file. All headers are have the same names as
 * within the Sac program. Can read the whole file or just the header as well as
 * write a file.
 * 
 * This reflects the sac header as of version 101.4 in utils/sac.h
 * 
 * Notes: Key to comment flags describing each field: Column 1: R required by
 * SAC (blank) optional Column 2: A = settable from a priori knowledge D =
 * available in data F = available in or derivable from SEED fixed data header T
 * = available in SEED header tables (blank) = not directly available from SEED
 * data, header tables, or elsewhere
 * 
 * 
 * @version 1.1 Wed Feb 2 20:40:49 GMT 2000
 * @author H. Philip Crotwell
 */
public class SacTimeSeries {

    public SacTimeSeries() {}

    public SacTimeSeries(File file) throws FileNotFoundException, IOException {
        read(file);
    }

    public SacTimeSeries(String filename) throws FileNotFoundException, IOException {
        read(filename);
    }

    public SacTimeSeries(DataInputStream inStream) throws IOException {
        read(inStream);
    }

    /** RF time increment, sec */
    public float delta = FLOAT_UNDEF;

    /** minimum amplitude */
    public float depmin = FLOAT_UNDEF;

    /** maximum amplitude */
    public float depmax = FLOAT_UNDEF;

    /** amplitude scale factor */
    public float scale = FLOAT_UNDEF;

    /** observed time inc */
    public float odelta = FLOAT_UNDEF;

    /** RD initial time - wrt nz* */
    public float b = FLOAT_UNDEF;

    /** RD end time */
    public float e = FLOAT_UNDEF;

    /** event start */
    public float o = FLOAT_UNDEF;

    /** 1st arrival time */
    public float a = FLOAT_UNDEF;

    /** internal use */
    public float fmt = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t0 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t1 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t2 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t3 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t4 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t5 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t6 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t7 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t8 = FLOAT_UNDEF;

    /** user-defined time pick */
    public float t9 = FLOAT_UNDEF;

    /** event end, sec > 0 */
    public float f = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp0 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp1 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp2 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp3 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp4 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp5 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp6 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp7 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp8 = FLOAT_UNDEF;

    /** instrument respnse parm */
    public float resp9 = FLOAT_UNDEF;

    /** T station latititude */
    public float stla = FLOAT_UNDEF;

    /** T station longitude */
    public float stlo = FLOAT_UNDEF;

    /** T station elevation, m */
    public float stel = FLOAT_UNDEF;

    /** T station depth, m */
    public float stdp = FLOAT_UNDEF;

    /** event latitude */
    public float evla = FLOAT_UNDEF;

    /** event longitude */
    public float evlo = FLOAT_UNDEF;

    /** event elevation */
    public float evel = FLOAT_UNDEF;

    /** event depth */
    public float evdp = FLOAT_UNDEF;

    /** magnitude value */
    public float mag = FLOAT_UNDEF;

    /** available to user */
    public float user0 = FLOAT_UNDEF;

    /** available to user */
    public float user1 = FLOAT_UNDEF;

    /** available to user */
    public float user2 = FLOAT_UNDEF;

    /** available to user */
    public float user3 = FLOAT_UNDEF;

    /** available to user */
    public float user4 = FLOAT_UNDEF;

    /** available to user */
    public float user5 = FLOAT_UNDEF;

    /** available to user */
    public float user6 = FLOAT_UNDEF;

    /** available to user */
    public float user7 = FLOAT_UNDEF;

    /** available to user */
    public float user8 = FLOAT_UNDEF;

    /** available to user */
    public float user9 = FLOAT_UNDEF;

    /** stn-event distance, km */
    public float dist = FLOAT_UNDEF;

    /** event-stn azimuth */
    public float az = FLOAT_UNDEF;

    /** stn-event azimuth */
    public float baz = FLOAT_UNDEF;

    /** stn-event dist, degrees */
    public float gcarc = FLOAT_UNDEF;

    /** saved b value */
    public float sb = FLOAT_UNDEF;

    /** saved delta value */
    public float sdelta = FLOAT_UNDEF;

    /** mean value, amplitude */
    public float depmen = FLOAT_UNDEF;

    /** T component azimuth */
    public float cmpaz = FLOAT_UNDEF;

    /** T component inclination */
    public float cmpinc = FLOAT_UNDEF;

    /** XYZ X minimum value */
    public float xminimum = FLOAT_UNDEF;

    /** XYZ X maximum value */
    public float xmaximum = FLOAT_UNDEF;

    /** XYZ Y minimum value */
    public float yminimum = FLOAT_UNDEF;

    /** XYZ Y maximum value */
    public float ymaximum = FLOAT_UNDEF;

    /** reserved for future use */
    public float unused6 = FLOAT_UNDEF;

    /** reserved for future use */
    public float unused7 = FLOAT_UNDEF;

    /** reserved for future use */
    public float unused8 = FLOAT_UNDEF;

    /** reserved for future use */
    public float unused9 = FLOAT_UNDEF;

    /** reserved for future use */
    public float unused10 = FLOAT_UNDEF;

    /** reserved for future use */
    public float unused11 = FLOAT_UNDEF;

    /** reserved for future use */
    public float unused12 = FLOAT_UNDEF;

    /** F zero time of file, yr */
    public int nzyear = INT_UNDEF;

    /** F zero time of file, day */
    public int nzjday = INT_UNDEF;

    /** F zero time of file, hr */
    public int nzhour = INT_UNDEF;

    /** F zero time of file, min */
    public int nzmin = INT_UNDEF;

    /** F zero time of file, sec */
    public int nzsec = INT_UNDEF;

    /** F zero time of file, msec */
    public int nzmsec = INT_UNDEF;

    /** R header version number */
    public int nvhdr = DEFAULT_NVHDR;

    /** Origin ID */
    public int norid = INT_UNDEF;

    /** Event ID */
    public int nevid = INT_UNDEF;

    /** RF number of samples */
    public int npts = INT_UNDEF;

    /** saved npts */
    public int nsnpts = INT_UNDEF;

    /** Waveform ID */
    public int nwfid = INT_UNDEF;

    /** XYZ X size */
    public int nxsize = INT_UNDEF;

    /** XYZ Y size */
    public int nysize = INT_UNDEF;

    /** reserved for future use */
    public int unused15 = INT_UNDEF;

    /** RA type of file */
    public int iftype = INT_UNDEF;

    /** type of amplitude */
    public int idep = INT_UNDEF;

    /** zero time equivalence */
    public int iztype = INT_UNDEF;

    /** reserved for future use */
    public int unused16 = INT_UNDEF;

    /** recording instrument */
    public int iinst = INT_UNDEF;

    /** stn geographic region */
    public int istreg = INT_UNDEF;

    /** event geographic region */
    public int ievreg = INT_UNDEF;

    /** event type */
    public int ievtyp = INT_UNDEF;

    /** quality of data */
    public int iqual = INT_UNDEF;

    /** synthetic data flag */
    public int isynth = INT_UNDEF;

    /** magnitude type */
    public int imagtyp = INT_UNDEF;

    /** magnitude source */
    public int imagsrc = INT_UNDEF;

    /** reserved for future use */
    public int unused19 = INT_UNDEF;

    /** reserved for future use */
    public int unused20 = INT_UNDEF;

    /** reserved for future use */
    public int unused21 = INT_UNDEF;

    /** reserved for future use */
    public int unused22 = INT_UNDEF;

    /** reserved for future use */
    public int unused23 = INT_UNDEF;

    /** reserved for future use */
    public int unused24 = INT_UNDEF;

    /** reserved for future use */
    public int unused25 = INT_UNDEF;

    /** reserved for future use */
    public int unused26 = INT_UNDEF;

    /** RA data-evenly-spaced flag */
    public int leven = INT_UNDEF;

    /** station polarity flag */
    public int lpspol = INT_UNDEF;

    /** overwrite permission */
    public int lovrok = INT_UNDEF;

    /** calc distance, azimuth */
    public int lcalda = INT_UNDEF;

    /** reserved for future use */
    public int unused27 = INT_UNDEF;

    /** F station name */
    public String kstnm = STRING8_UNDEF;

    /** event name */
    public String kevnm = STRING16_UNDEF;

    /** man-made event name */
    public String khole = STRING8_UNDEF;

    /** event origin time id */
    public String ko = STRING8_UNDEF;

    /** 1st arrival time ident */
    public String ka = STRING8_UNDEF;

    /** time pick 0 ident */
    public String kt0 = STRING8_UNDEF;

    /** time pick 1 ident */
    public String kt1 = STRING8_UNDEF;

    /** time pick 2 ident */
    public String kt2 = STRING8_UNDEF;

    /** time pick 3 ident */
    public String kt3 = STRING8_UNDEF;

    /** time pick 4 ident */
    public String kt4 = STRING8_UNDEF;

    /** time pick 5 ident */
    public String kt5 = STRING8_UNDEF;

    /** time pick 6 ident */
    public String kt6 = STRING8_UNDEF;

    /** time pick 7 ident */
    public String kt7 = STRING8_UNDEF;

    /** time pick 8 ident */
    public String kt8 = STRING8_UNDEF;

    /** time pick 9 ident */
    public String kt9 = STRING8_UNDEF;

    /** end of event ident */
    public String kf = STRING8_UNDEF;

    /** available to user */
    public String kuser0 = STRING8_UNDEF;

    /** available to user */
    public String kuser1 = STRING8_UNDEF;

    /** available to user */
    public String kuser2 = STRING8_UNDEF;

    /** F component name */
    public String kcmpnm = STRING8_UNDEF;

    /** network name */
    public String knetwk = STRING8_UNDEF;

    /** date data read */
    public String kdatrd = STRING8_UNDEF;

    /** instrument name */
    public String kinst = STRING8_UNDEF;

    public float[] y;

    public float[] x;

    public float[] real;

    public float[] imaginary;

    public float[] amp;

    public float[] phase;

    // undef values for sac
    public static float FLOAT_UNDEF = -12345.0f;

    public static int INT_UNDEF = -12345;

    public static String STRING8_UNDEF = "-12345  ";

    public static String STRING16_UNDEF = "-12345          ";

    public static final int DEFAULT_NVHDR = 6;

    /* TRUE and FLASE defined for convenience. */
    public static final int TRUE = 1;

    public static final int FALSE = 0;

    /* Constants used by sac. This corresponds to utils/sac.h in sac 101.4 */
    /** Undocumented */
    public static final int IREAL = 0;

    /** Time series file */
    public static final int ITIME = 1;

    /** Spectral file-real/imag */
    public static final int IRLIM = 2;

    /** Spectral file-ampl/phase */
    public static final int IAMPH = 3;

    /** General x vs y file */
    public static final int IXY = 4;

    /** Unknown */
    public static final int IUNKN = 5;

    /** Displacement (NM) */
    public static final int IDISP = 6;

    /** Velocity (NM/SEC) */
    public static final int IVEL = 7;

    /** Acceleration (NM/SEC/SEC) */
    public static final int IACC = 8;

    /** Begin time */
    public static final int IB = 9;

    /** GMT day */
    public static final int IDAY = 10;

    /** Event origin time */
    public static final int IO = 11;

    /** First arrival time */
    public static final int IA = 12;

    /** User defined time pick 0 */
    public static final int IT0 = 13;

    /** User defined time pick 1 */
    public static final int IT1 = 14;

    /** User defined time pick 2 */
    public static final int IT2 = 15;

    /** User defined time pick 3 */
    public static final int IT3 = 16;

    /** User defined time pick 4 */
    public static final int IT4 = 17;

    /** User defined time pick 5 */
    public static final int IT5 = 18;

    /** User defined time pick 6 */
    public static final int IT6 = 19;

    /** User defined time pick 7 */
    public static final int IT7 = 20;

    /** User defined time pick 8 */
    public static final int IT8 = 21;

    /** User defined time pick 9 */
    public static final int IT9 = 22;

    /** Radial (NTS) */
    public static final int IRADNV = 23;

    /** Tangential (NTS) */
    public static final int ITANNV = 24;

    /** Radial (EVENT) */
    public static final int IRADEV = 25;

    /** Tangential (EVENT) */
    public static final int ITANEV = 26;

    /** North positive */
    public static final int INORTH = 27;

    /** East positive */
    public static final int IEAST = 28;

    /** Horizontal (ARB) */
    public static final int IHORZA = 29;

    /** Down positive */
    public static final int IDOWN = 30;

    /** Up positive */
    public static final int IUP = 31;

    /** LLL broadband */
    public static final int ILLLBB = 32;

    /** WWSN 15-100 */
    public static final int IWWSN1 = 33;

    /** WWSN 30-100 */
    public static final int IWWSN2 = 34;

    /** High-gain long-period */
    public static final int IHGLP = 35;

    /** SRO */
    public static final int ISRO = 36;

    /** Nuclear event */
    public static final int INUCL = 37;

    /** Nuclear pre-shot event */
    public static final int IPREN = 38;

    /** Nuclear post-shot event */
    public static final int IPOSTN = 39;

    /** Earthquake */
    public static final int IQUAKE = 40;

    /** Foreshock */
    public static final int IPREQ = 41;

    /** Aftershock */
    public static final int IPOSTQ = 42;

    /** Chemical explosion */
    public static final int ICHEM = 43;

    /** Other */
    public static final int IOTHER = 44;

    /** Good */
    public static final int IGOOD = 45;

    /** Gliches */
    public static final int IGLCH = 46;

    /** Dropouts */
    public static final int IDROP = 47;

    /** Low signal to noise ratio */
    public static final int ILOWSN = 48;

    /** Real data */
    public static final int IRLDTA = 49;

    /** Velocity (volts) */
    public static final int IVOLTS = 50;

    /** General XYZ (3-D) file */
    public static final int IXYZ = 51;

    /* These 18 added to describe magnitude type and source maf 970205 */
    /** Bodywave Magnitude */
    public static final int IMB = 52;

    /** Surface Magnitude */
    public static final int IMS = 53;

    /** Local Magnitude */
    public static final int IML = 54;

    /** Moment Magnitude */
    public static final int IMW = 55;

    /** Duration Magnitude */
    public static final int IMD = 56;

    /** User Defined Magnitude */
    public static final int IMX = 57;

    /** INEIC */
    public static final int INEIC = 58;

    /** IPDEQ */
    public static final int IPDEQ = 59;

    /** IPDEW */
    public static final int IPDEW = 60;

    /** IPDE */
    public static final int IPDE = 61;

    /** IISC */
    public static final int IISC = 62;

    /** IREB */
    public static final int IREB = 63;

    /** IUSGS */
    public static final int IUSGS = 64;

    /** IBRK */
    public static final int IBRK = 65;

    /** ICALTECH */
    public static final int ICALTECH = 66;

    /** ILLNL */
    public static final int ILLNL = 67;

    /** IEVLOC */
    public static final int IEVLOC = 68;

    /** IJSOP */
    public static final int IJSOP = 69;

    /** IUSER */
    public static final int IUSER = 70;

    /** IUNKNOWN */
    public static final int IUNKNOWN = 71;

    /* These 17 added for ievtyp. maf 970325 */
    /** Quarry or mine blast confirmed by quarry */
    public static final int IQB = 72;

    /** Quarry or mine blast with designed shot information-ripple fired */
    public static final int IQB1 = 73;

    /** Quarry or mine blast with observed shot information-ripple fired */
    public static final int IQB2 = 74;

    /** Quarry or mine blast - single shot */
    public static final int IQBX = 75;

    /** Quarry or mining-induced events: tremors and rockbursts */
    public static final int IQMT = 76;

    /** Earthquake */
    public static final int IEQ = 77;

    /** Earthquakes in a swarm or aftershock sequence */
    public static final int IEQ1 = 78;

    /** Felt earthquake */
    public static final int IEQ2 = 79;

    /** Marine explosion */
    public static final int IME = 80;

    /** Other explosion */
    public static final int IEX = 81;

    /** Nuclear explosion */
    public static final int INU = 82;

    /** Nuclear cavity collapse */
    public static final int INC = 83;

    /** Other source of known origin */
    public static final int IO_ = 84;

    /** Local event of unknown origin */
    public static final int IL = 85;

    /** Regional event of unknown origin */
    public static final int IR = 86;

    /** Teleseismic event of unknown origin */
    public static final int IT = 87;

    /** Undetermined or conflicting information */
    public static final int IU = 88;

    /* These 9 added for ievtype to keep up with database. maf 000530 */
    /** Damaging Earthquake */
    public static final int IEQ3 = 89;

    /** Probable earthquake */
    public static final int IEQ0 = 90;

    /** Probable explosion */
    public static final int IEX0 = 91;

    /** Mine collapse */
    public static final int IQC = 92;

    /** Probable Mine Blast */
    public static final int IQB0 = 93;

    /** Geyser */
    public static final int IGEY = 94;

    /** Light */
    public static final int ILIT = 95;

    /** Meteroic event */
    public static final int IMET = 96;

    /** Odors */
    public static final int IODOR = 97;

    public static final int data_offset = 632;

    public static final int NVHDR_OFFSET = 76 * 4;

    public static final int NPTS_OFFSET = 79 * 4;

    boolean byteOrder = SunByteOrder;

    public static final boolean SunByteOrder = true;

    public static final boolean IntelByteOrder = false;

    public boolean getByteOrder() {
        return byteOrder;
    }

    /**
     * reads the sac file specified by the filename. Only a very simple check is
     * made to be sure the file really is a sac file.
     * 
     * @throws FileNotFoundException
     *             if the file cannot be found
     * @throws IOException
     *             if it isn't a sac file or if it happens :)
     */
    public void read(String filename) throws FileNotFoundException, IOException {
        File sacFile = new File(filename);
        read(sacFile);
    }

    public void read(File sacFile) throws FileNotFoundException, IOException {
        if (sacFile.length() < data_offset) {
            throw new IOException(sacFile.getName() + " does not appear to be a sac file! File size ("
                    + sacFile.length() + " is less than sac's header size (" + data_offset + ")");
        }
        FileInputStream fis = new FileInputStream(sacFile);
        BufferedInputStream buf = new BufferedInputStream(fis);
        DataInputStream dis = new DataInputStream(buf);
        readHeader(dis);
        if (leven == 1 && sacFile.length() != npts * 4 + data_offset) {
            throw new IOException(sacFile.getName() + " does not appear to be a sac file! npts(" + npts
                    + ") * 4 + header(" + data_offset + ") !=  file length=" + sacFile.length() + "\n  as linux: npts("
                    + swapBytes(npts) + ")*4 + header(" + data_offset + ") !=  file length=" + sacFile.length());
        } else if (leven == 0 && sacFile.length() != npts * 4 * 2 + data_offset) {
            throw new IOException(sacFile.getName() + " does not appear to be a uneven sac file! npts(" + npts
                    + ") * 4 *2 + header(" + data_offset + ") !=  file length=" + sacFile.length()
                    + "\n  as linux: npts(" + swapBytes(npts) + ")*4*2 + header(" + data_offset + ") !=  file length="
                    + sacFile.length());
        }
        readData(dis);
        dis.close();
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

    public void read(DataInputStream dis) throws IOException {
        readHeader(dis);
        readData(dis);
    }

    /**
     * reads just the sac header specified by the filename. No checks are made
     * to be sure the file really is a sac file.
     */
    public void readHeader(String filename) throws FileNotFoundException, IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        readHeader(dis);
        dis.close();
    }

    /**
     * reads the header from the given stream. The NVHDR value (shoudld be 6) is
     * checked to see if byte swapping is needed. If so, all header values are
     * byte swapped and the byteOrder is set to IntelByteOrder (false) so that
     * the data section will also be byte swapped on read. Extra care is taken
     * to do all byte swapping before the byte values are transformed into
     * floats as java can do very funny things if the byte-swapped float happens
     * to be a NaN.
     */
    public void readHeader(DataInputStream indis) throws FileNotFoundException, IOException {
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

    /** read the data portion of the given File */
    public void readData(DataInputStream fis) throws IOException {
        y = new float[npts];
        readDataArray(fis, y);
        if (leven == SacTimeSeries.FALSE || iftype == SacTimeSeries.IRLIM || iftype == SacTimeSeries.IAMPH) {
            x = new float[npts];
            readDataArray(fis, x);
            if (iftype == SacTimeSeries.IRLIM) {
                real = y;
                imaginary = x;
            }
            if (iftype == SacTimeSeries.IAMPH) {
                amp = y;
                phase = x;
            }
        }
    }

    private void readDataArray(DataInputStream fis, float[] d) throws IOException {
        byte[] dataBytes = new byte[d.length * 4];
        int numAdded = 0;
        int i = 0;
        fis.readFully(dataBytes);
        while (numAdded < d.length) {
            if (byteOrder == IntelByteOrder) {
                y[numAdded++] = Float.intBitsToFloat(((dataBytes[i++] & 0xff) << 0) + ((dataBytes[i++] & 0xff) << 8)
                        + ((dataBytes[i++] & 0xff) << 16) + ((dataBytes[i++] & 0xff) << 24));
            } else {
                y[numAdded++] = Float.intBitsToFloat(((dataBytes[i++] & 0xff) << 24) + ((dataBytes[i++] & 0xff) << 16)
                        + ((dataBytes[i++] & 0xff) << 8) + ((dataBytes[i++] & 0xff) << 0));
            }
        }
    }

    /** read the data portion of the given File */
    public void readDataNewOld(DataInputStream fis) throws IOException {
        InputStream in = fis;
        y = new float[npts];
        int numAdded = 0;
        int numRead;
        int i;
        byte[] overflow = new byte[4];
        byte[] prevoverflow = new byte[4];
        int overflowBytes = 0;
        int prevoverflowBytes = 0;
        byte[] buf = new byte[4096]; // buf length must be == 0 % 4
        // and for efficiency, should be
        // a multiple of the disk sector size
        while (numAdded < npts) {
            if ((numRead = in.read(buf)) == 0) {
                continue;
            } else if (numRead == -1) {
                // EOF
                throw new EOFException();
            }
            overflowBytes = (numRead + prevoverflowBytes) % 4;
            if (overflowBytes != 0) {
                // partial read of bytes for last value
                // save in overflow
                System.arraycopy(buf, numRead - overflowBytes, overflow, 0, overflowBytes);
            }
            i = 0;
            if (prevoverflowBytes != 0) {
                int temp = 0;
                // use leftover bytes
                for (i = 0; i < prevoverflowBytes; i++) {
                    temp <<= 8;
                    temp += (prevoverflow[i] & 0xff);
                }
                // use first new bytes as needed
                for (i = 0; i < 4 - prevoverflowBytes; i++) {
                    temp <<= 8;
                    temp += (buf[i] & 0xff);
                }
                if (byteOrder == IntelByteOrder) {
                    y[numAdded++] = Float.intBitsToFloat(swapBytes(temp));
                } else {
                    y[numAdded++] = Float.intBitsToFloat(temp);
                }
            }
            // i is now set to first unused byte in buf
            while (i <= numRead - 4 && numAdded < npts) {
                if (byteOrder == IntelByteOrder) {
                    y[numAdded++] = Float.intBitsToFloat(((buf[i++] & 0xff) << 0) + ((buf[i++] & 0xff) << 8)
                            + ((buf[i++] & 0xff) << 16) + ((buf[i++] & 0xff) << 24));
                } else {
                    y[numAdded++] = Float.intBitsToFloat(((buf[i++] & 0xff) << 24) + ((buf[i++] & 0xff) << 16)
                            + ((buf[i++] & 0xff) << 8) + ((buf[i++] & 0xff) << 0));
                }
            }
            System.arraycopy(overflow, 0, prevoverflow, 0, overflowBytes);
            prevoverflowBytes = overflowBytes;
        }
    }

    /**
     * reads the data portion from the given stream. Uses readFloat repeatedly
     * resulting in MUCH slower read times than the slightly more confusing
     * method above.
     */
    protected void readDataOld(DataInputStream dis) throws FileNotFoundException, IOException {
        y = new float[npts];
        for (int i = 0; i < npts; i++) {
            y[i] = dis.readFloat();
        }
        if (leven == SacTimeSeries.FALSE || iftype == SacTimeSeries.IRLIM || iftype == SacTimeSeries.IAMPH) {
            x = new float[npts];
            for (int i = 0; i < npts; i++) {
                x[i] = dis.readFloat();
            }
            if (iftype == SacTimeSeries.IRLIM) {
                real = y;
                imaginary = x;
            }
            if (iftype == SacTimeSeries.IAMPH) {
                amp = y;
                phase = x;
            }
        }
    }

    /** writes this object out as a sac file. */
    public void write(String filename) throws FileNotFoundException, IOException {
        File f = new File(filename);
        write(f);
    }

    /** writes this object out as a sac file. */
    public void write(File file) throws FileNotFoundException, IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        writeHeader(dos);
        writeData(dos);
        dos.close();
    }

    /** write the float to the stream, swapping bytes if needed. */
    private final void writeFloat(DataOutputStream dos, float val) throws IOException {
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
    private final void writeInt(DataOutputStream dos, int val) throws IOException {
        if (byteOrder == IntelByteOrder) {
            dos.writeInt(swapBytes(val));
        } else {
            dos.writeInt(val);
        } // end of else
    }

    public void writeHeader(DataOutputStream dos) throws IOException {
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

    public void writeData(DataOutputStream dos) throws IOException {
        for (int i = 0; i < npts; i++) {
            writeFloat(dos, y[i]);
        }
        if (leven == SacTimeSeries.FALSE || iftype == SacTimeSeries.IRLIM || iftype == SacTimeSeries.IAMPH) {
            for (int i = 0; i < npts; i++) {
                writeFloat(dos, x[i]);
            }
        }
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

    /**
     * just for testing. Reads the filename given as the argument, writes out
     * some header variables and then writes it back out as "outsacfile".
     */
    public static void main(String[] args) {
        SacTimeSeries data = new SacTimeSeries();
        if (args.length != 1) {
            System.out.println("Usage: java SacTimeSeries sacsourcefile ");
            return;
        }
        try {
            data.read(args[0]);
            // data.y = new float[100000];
            // for (int i=0; i<100000; i++) {
            // data.y[i] = (float)Math.sin(Math.PI*i/18000)/1000000.0f;
            // data.y[i] = (float)Math.sin(Math.PI*i/18000);
            // //System.out.println("point is " + data.y[i]);
            // }
            // data.npts = data.y.length;
            // data.printHeader();
            System.out.println("stla original: " + data.stla + " npts=" + data.npts);
            // data.setLittleEndian();
            data.write("outsacfile");
            data.read("outsacfile");
            System.out.println("stla after read little endian: " + data.stla + " npts=" + data.npts);
            System.out.println("Done writing");
        } catch(FileNotFoundException e) {
            System.out.println("File " + args[0] + " doesn't exist.");
        } catch(IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
