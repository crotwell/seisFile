# Introduction #

A simple script to read a miniseed file and output records from each station into a separate miniseed file. This is an example of using seisFile with a JVM based scripting language like [Groovy](http://groovy.codehaus.org), which you need to install first.


# Details #

```
import edu.sc.seis.seisFile.mseed.SeedRecord

inFile = new DataInputStream(new BufferedInputStream(new FileInputStream(args[0])))
outFile = null
lastStation = null

try {
    while (true) {
        sr = SeedRecord.read(inFile)
        if (sr.header.stationIdentifier != lastStation) {
           outFile?.close()
           lastStation = sr.header.stationIdentifier
           outFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(lastStation.trim() + ".mseed")));
        }
        sr.write(outFile)
    }  
} catch(EOFException e) {
 // done
}
outFile?.close()
```

To run this:
```
groovy -cp lib/seisFile-1.6.3.jar:lib/seedCodec-1.0.10.jar:lib/slf4j-api-1.7.5.jar:lib/slf4j-log4j12-1.7.5.jar:lib/log4j-1.2.17.jar splitmseed.groovy myfile.mseed
```