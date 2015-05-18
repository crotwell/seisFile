# Introduction #

fdsndataselect is an example client for the new IRIS FDSN DataSelect web service that returns [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) data. See the [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) section for an example of how to process the returned miniseed data records.

# Java Example #

An example of using the FDSN dataselect web service from within your own code is shown in http://code.google.com/p/seisfile/source/browse/src/example/java/edu/sc/seis/seisFile/example/FDSNDataSelect.java. This uses [FDSNDataSelectQueryParams](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/FDSNDataSelectQueryParams.java) to build the query and [FDSNDataSelectQuerier](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/FDSNDataSelectQuerier.java) to connect to the server and execute the query.

# Example #

An client for the FDSN dataselect web service is in [src/main/java/edu/sc/seis/seisFile/fdsnws/DataSelectClient.java](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/fdsnws/DataSelectClient.java). For example:
```
bin/fdsndataselect -n IU -s BBSR -l 00 -c BHZ -b 2010-10-01T00:00:00 -e 2010-10-01T00:02:00 -o bbsr.mseed
```
downloads 120 seconds of miniseed data for IU.BBSR.00.BHZ.

Usage is:

```
Usage: java edu.sc.seis.seisFile.fdsnws.DataSelectClient
                [-v|--version] [-h|--help] [(-p|--props) <props>] [--printurl] [--raw] [--baseurl <baseurl>] [--host <host>] (-b|--begin) <begin> (-e|--end) <end> [(-n|--network) network1,network2,...,networkN ] [(-s|--station) station1,station2,...,stationN ] [(-l|--location) location1,location2,...,locationN ] [(-c|--channel) channel1,channel2,...,channelN ] [(-o|--output) <output>] [--user <user>] [--password <password>]
```