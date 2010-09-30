
SeisFile

This is a library of utilities for reading various seismic file types.
Current support includes SAC, mseed and PSN. It is released under the GPL
license.

The jar file is in the lib directory and 
the distribution can be downloaded here:
http://www.seis.sc.edu/downloads/seisFile


SAC

SAC files can be read with code similar to the following.
       
SacTimeSeries sac = new SacTimeSeries(filename);
or
SacTimeSeries sac = new SacTimeSeries(dataInputStream)


MSeed

Miniseed support is only for straight miniseed, ie only binary "data records" and no ascii "control records". A mseed file can be read like this.

DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
MiniSeedRead mseed = new MiniSeedRead(dis);
try {
    while (true) {
        SeedRecord sr = mseed.getNextRecord();
    }
} catch (EOFException e) {
}

Seedlink

An example of seedlink support is in
src/main/java/edu/sc/seis/seisFile/seedlink/Client.java
which can also be run as a client. For example:

bin/seedlinkclient -n II -c BHZ  -m 8

will print a summary of 8 packets from the II network and

bin/seedlinkclient -n IU -s KONO -c BHZ -o kono.mseed  -m 8

will store the next 8 packets for KONO in a miniseed file.

Usage is:
java edu.sc.seis.seisFile.seedlink.Client [-n net][-s sta][-l loc][-c chan][-h host][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]

seisFile can be recompiled with the gradlew script, like
./gradlew jar
which will place a recompiled jar in the build/libs directory. See gradle.org
for more information.

