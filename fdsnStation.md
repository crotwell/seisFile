# Introduction #


SeisFile supports the [FDSN Station web service](http://service.iris.edu/fdsnws/station/) and parsing of [StationML ](http://www.fdsn.org/xml/station/). StAX (Streaming API for XML) is used internally for this parsing.

# Java Example #

An example of using the FDSN station web service from within your own code is shown in http://code.google.com/p/seisfile/source/browse/src/example/java/edu/sc/seis/seisFile/example/FDSNStation.java. This uses [FDSNStationQueryParams](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/FDSNStationQueryParams.java) to build the query and [FDSNStationQuerier](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/FDSNStationQuerier.java) to connect to the server and execute the query.

# Example #

An client for the FDSN station web service is in [src/main/java/edu/sc/seis/seisFile/fdsnws/StationClient.java](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/StationClient.java). For example:
```
bin/fdsnstation -n IU -s BBSR --level channel
```
all channels at IU.BBSR.

Usage is:

```
Usage: java edu.sc.seis.seisFile.fdsnws.StationClient
                [-v|--version] [-h|--help] [(-p|--props) <props>] [--printurl] [--raw] [--baseurl <baseurl>] [--host <host>] [(-R|--box-area) <box>] [(-d|--donut) <donut>] [(-b|--begin) <begin>] [(-e|--end) <end>] [(-n|--network) network1,network2,...,networkN ] [(-s|--station) station1,station2,...,stationN ] [(-l|--location) location1,location2,...,locationN ] [(-c|--channel) channel1,channel2,...,channelN ] [(-L|--level) <level>] [--availability] [--restricted] [(-u|--updatedafter) <updatedafter>] [--validate]
```