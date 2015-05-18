# Introduction #

Support for [LISS](http://aslwww.cr.usgs.gov/Seismic_Data/connect.htm) is included in the [edu.sc.seis.seisFile.liss](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/liss) package. The [edu.sc.seis.seisFile.liss.Client](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/liss/Client.java) class is an example client application. LISS is largely just a socket connection over which miniseed is sent, so this relies heavily on the [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) part of seisFile. See the [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) section for an example of how to process the returned miniseed data records.

# Example #

A example client can be run as:
```
bin/lissclient -n IU -s ANMO -l 00 -c BHZ -m 3
```

Usage is:

```
bin/lissclient  [[-n net][-s sta]|[-h host]][-l loc][-c chan][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]
```