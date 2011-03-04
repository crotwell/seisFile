package edu.sc.seis.seisFile.sac;


public class SacConstants {

    // undef values for sac
    public static final float FLOAT_UNDEF = -12345.0f;

    public static final int INT_UNDEF = -12345;

    public static final String STRING8_UNDEF = "-12345  ";

    public static final String STRING16_UNDEF = "-12345          ";

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
    
    public static final boolean SunByteOrder = true;

    public static final boolean IntelByteOrder = false;

    public static final boolean LITTLE_ENDIAN = IntelByteOrder;
    
    public static final boolean BIG_ENDIAN = SunByteOrder;
    
    private SacConstants() {}
}
