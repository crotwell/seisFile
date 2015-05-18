# Introduction #

Support for SeedLink is included in the [edu.sc.seis.seisFile.seedlink](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/seedlink) package. The [edu.sc.seis.seisFile.seedlink.Client](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/seedlink/Client.java) class is an example client application. See the [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) section for an example of how to process the returned miniseed data records.


# Example #

For example:
```
bin/seedlinkclient -n II -c BHZ  -m 8
```
will print a summary of 8 packets from the II network and
```
bin/seedlinkclient -n IU -s KONO -c BHZ -o kono.mseed  -m 8
```
will store the next 8 packets for KONO in a miniseed file.

Usage is:

```
java edu.sc.seis.seisFile.seedlink.Client [-n net][-s sta][-l loc][-c chan][-h host][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]
```