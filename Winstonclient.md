# Introduction #

winstonclient is an example client for direct access of a Winston style MySQL database that returns [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) data or a syncfile. See the [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) section for an example of how to process the returned miniseed data records.

The network, station, location and channel arguments accept regular expressions. Also, a lone asterisk, '**', is converted to the regular expression '.**'.

# Example #

To generate a syncfile for a channel:

```
bin/winstonclient -b 2012-01-01 -e 2012-01-02  --sync -p Winston.config -n CO -s '*' -l 00 -c '.H[ZNE]' > winston.sync
```

and to retrieve miniseed data:

```
bin/winstonclient -b 2012-01-01T12:34:56 -e 2012-01-01T12:56:32  -p /ra/scsn/winston/Winston1.1/Winston.config -n CO -s '*' -l 00 -c '.H[ZNE]' -o winstondata.mseed
```

The usage is:

```
java edu.sc.seis.seisFile.winston.WinstonClient [-n net][-s sta][-l loc][-c chan][-b yyyy-MM-dd[THH:mm:ss.SSS]][-e yyyy-MM-dd[THH:mm:ss.SSS]][-d seconds][-o outfile][-m maxpackets][--verbose][--version][--help][-p <winston.config file>][-u databaseURL][--sync][--steim1][--recLen len(8-12)]
```
