# Introduction #


SeisFile supports the [FDSN Event web service](http://service.iris.edu/fdsnws/event/) and parsing of [QuakeML ](https://quake.ethz.ch/quakeml). StAX (Streaming API for XML) is used internally for this parsing.

# Java Example #

An example of using the FDSN event web service from within your own code is shown in http://code.google.com/p/seisfile/source/browse/src/example/java/edu/sc/seis/seisFile/example/FDSNEvent.java. This uses [FDSNEventQueryParams](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/FDSNEventQueryParams.java) to build the query and [FDSNEventQuerier](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/FDSNEventQuerier.java) to connect to the server and execute the query.

# Example #

An client for the FDSN event web service is in [src/main/java/edu/sc/seis/seisFile/fdsnws/EventClient.java](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/EventClient.java). For example:
```
bin/fdsnevent -b 2013 -e 2013 -m 7
```
finds all magnitude 7 or larger events in 2013.

Usage is:

```
Usage: java edu.sc.seis.seisFile.fdsnws.EventClient
                [-v|--version] [-h|--help] [(-p|--props) <props>] [--printurl] [--raw] [--baseurl <baseurl>] [--host <host>] [(-R|--box-area) <box>] [(-d|--donut) <donut>] [(-b|--begin) <begin>] [(-e|--end) <end>] [(-m|--magnitude) <magnitude>] [(-t|--types) types1,types2,...,typesN ] [(-D|--depth) <depth>] [(-c|--catalogs) catalogs1,catalogs2,...,catalogsN ] [(-C|--contributors) contributors1,contributors2,...,contributorsN ] [--includeallorigins] [--includeallmagnitudes] [--includearrivals] [--validate]
```