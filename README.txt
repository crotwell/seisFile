
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


seisFile can be recompiled with the gradlew script, like
./gradlew jar
which will place a recompiled jar in the build/libs directory. See gradle.org
for more information.

