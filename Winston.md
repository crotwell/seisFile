# Introduction #

Support for the Winston database format is included in the [edu.sc.seis.seisFile.winston](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/winston) package. The [edu.sc.seis.seisFile.winston.WinstonClient](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/winston/WinstonClient.java) class is an example client application.

The client can extract tracebuf2 data, and convert it to miniseed. Also via the syncFile package it can extract an IRIS-style syncfile.

Lastly, it can act as an earthworm export\_generic and export winston data to another earthworm or to ewexport2ringserver.

# Example #

For example:
```
bin/winstonclient -n XX -s ABC -l 00 -c BHZ  -b 2010-10-01T00:00:00.0 -d 120 -o output.mseed -u 'jdbc:mysql://localhost/?user=wwsuser&password=xyz123'
```
will extract tracebuf2s for this channel for 120 seconds and save them as a miniseed file. For smaller file sizes you may wish to add --recLen 9 if your data can fit into 512 byte records. The default is 12 yeilding 4096 byte records. The --steim1 flag will optionally compress the data.
```
bin/winstonclient -n XX -s ABC -l 00 -c BHZ  -b 2011-10-01 -e 2011-10-05 -o output.sync --sync  -u 'jdbc:mysql://localhost/?user=wwsuser&password=xyz123'
```
will extract the time coverage of data for those 5 days in the database and save it as an IRIS-style sync file.

```
bin/winstonclient -n XX -s ABC -l 00 -c BHZ  -b 2011-10-01 -e 2011-10-05 --export 16021 --module 99 --inst 255 -p winston.config
```
will extract data for the given channel and make it available via an earthworm export for feeding into another earthworm or for sending to ewexport2ringserver.

Usage is:

```
winstonClient [-n net][-s sta][-l loc][-c chan][-b yyyy-MM-dd[THH:mm:ss.SSS]][-e yyyy-MM-dd[THH:mm:ss.SSS]][-d seconds][-o outfile][-m maxpackets][--append][--verbose][--version][--help][-p <winston.config file>][-u databaseURL][--sync][--steim1][--recLen len(8-12)][[--export port][--chunk sec][--module modNum][--inst institutionNum][--heartbeat sec][--heartbeatverbose]]
```