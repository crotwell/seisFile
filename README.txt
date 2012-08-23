
SeisFile

SeisFile is a library for reading and writing seismic file formats in java. Currently support exists for SAC, MiniSEED and PSN with limited support for full SEED. Support for the IRIS DMC DataSelect, USGS LISS and Geofon SeedLink  protocols are also included. These are low level routines that provide basic parsing of the file formats into objects that closely mirror those formats. Full SEED support was not intended, however there are cases of almost miniseed, such a one or two control records before the data records. SeisFile can handle the basic structure of these control records, allowing these mixed files to be read without crashing, but not all blockettes are parsed into fields. Support for the many blockette types in full SEED could be added in the future, although there is a large amount of bookkeeping to implement that and seisFile was intended to be small and focused.

SeisFile is release under the GNU Public License, v3 or later.

Example Apps

There are several example applications included, showing how to use seisFile in different ways. The clients, located in the bin directory, are:

   1. saclh - prints all the header fields of sac files
   2. mseedlh - prints the header and blockettes of seed control and data records
   3. seedlinkclient - connects to a SeedLink server and retrieves miniseed data from it
   4. lissclient - connects to a liss server and retrieves miniseed data from it
   5. dswsclient - connects to the data select web service and retrieves miniseed data from it

SAC

SAC supports both reading of binary SAC datafiles, as well as poles and zeros.

SacTimeSeries sac = new SacTimeSeries(filename);

or

DataInput dis = ...
SacTimeSeries sac = new SacTimeSeries(dis)

and reading a polezero file:

SacPoleZero spz = new SacPoleZero(filename);


SEED and MiniSEED

Miniseed support is good for straight miniseed, ie only binary "data records" and no ascii "control records". It is less complete for the control blockettes in full SEED. SeisFile also does not include routines to decompress seed data, please see SeedCodec for these routines. A miniseed file can be read like this.

DataInput dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
PrintWriter out = new PrintWriter(System.out, true);
try {
    while (true) {
        SeedRecord sr = SeedRecord.read(inStream, 4096);
        // maybe print it out...
        sr.writeASCII(out);
        if (sr instanceof DataRecord) {
            DataRecord dr = (DataRecord)sr;
            // now do something with the data...
        }
    }
} catch (EOFException e) {
}



PSN

PSNDataFile psnData = new PSNDataFile(filename);


SeedLink

An example of seedlink support is in src/main/java/edu/sc/seis/seisFile/seedlink/Client.java which can also be run as a client. For example:

bin/seedlinkclient -n II -c BHZ  -m 8

will print a summary of 8 packets from the II network and

bin/seedlinkclient -n IU -s KONO -c BHZ -o kono.mseed  -m 8

will store the next 8 packets for KONO in a miniseed file.

Usage is:


java edu.sc.seis.seisFile.seedlink.Client [-n net][-s sta][-l loc][-c chan][-h host][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]


Download

the distribution can be downloaded here: http://www.seis.sc.edu/downloads/seisFile and the jar file will be in the lib subdirectory.

seisFile can be recompiled with the gradlew script, like ./gradlew jar which will place a recompiled jar in the build/libs directory. See gradle.org for more information.

If you have any comments, please email us.

