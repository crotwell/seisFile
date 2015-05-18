# Introduction #

A simple script to read tracebufs from a waveserver, convert them to miniseed, and print out the header information, and save the result to a file. This is an example of using seisFile with a JVM based scripting language like [Groovy](http://groovy.codehaus.org), which you need to install first.


# Details #

```
import java.util.TimeZone
import java.text.SimpleDateFormat
import edu.sc.seis.seisFile.waveserver.WaveServer

ws = new WaveServer('eeyore.seis.sc.edu', 16022)

dateFormat = new SimpleDateFormat('yyyy-MM-dd\'T\'HH:mm:ssz')
dateFormat.setTimeZone(TimeZone.getTimeZone('GMT'))

begin = dateFormat.parse('2014-04-02T12:34:00 GMT')
end = new Date(begin.getTime()+300*1000)

outFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("waveserverData.mseed")));

traceBufList = ws.getTraceBuf('CO', 'CASEE', '00', 'HHZ', begin, end)
traceBufList.each { tb -> 
   mseed = tb.toMiniSeed(12, false)
   mseed.each { msrecord ->
       msrecord.write(outFile)
       println msrecord
   }
} 
```

To run this script:
```
groovy -cp lib/seisFile-1.6.2.jar:lib/seedCodec-1.0.10.jar:lib/slf4j-api-1.7.5.jar:lib/slf4j-log4j12-1.7.5.jar:lib/log4j-1.2.17.jar readFromWaveserver.groovy
```