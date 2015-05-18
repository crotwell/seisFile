# Introduction #

An example of support for the USGS CWB service is in [src/main/java/edu/sc/seis/seisFile/usgsCWB/Client.java](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/usgsCWB/Client.java) which can also be run as a client. See the [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) section for an example of how to process the returned miniseed data records.


# Example #

For example:
```
bin/cwbclient -n US -s DUG -l 00 -c BHZ -b 2010-10-01T00:00:00.0 -d 120 -o cwb.mseed
```
downloads 120 seconds of miniseed data for US.DUG.00.BHZ.

Usage is:

```
java edu.sc.seis.seisFile.usgsCWB.Client [-n net][-s sta][-l loc][-c chan][-b yyyy-MM-dd[THH:mm:ss.SSS]][-e yyyy-MM-dd[THH:mm:ss.SSS]][-d seconds][-o outfile][-m maxpackets][--verbose][--version][--help][-h host][-p port]
```