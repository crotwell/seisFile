# Introduction #

SeisFile is designed to be a library for accessing common seismic file
formats and low level protocols. Hence it is meant to be embedded in other applications. However it does include several examples that can be useful in their own right.


# Example Apps #

There are several example applications included, showing how to use seisFile in different ways. The clients, located in the bin directory, are:
  * saclh - prints all the header fields of sac files, see edu/sc/seis/seisFile/sac/ListHeader.java
  * mseedlh - prints the header and blockettes of seed control and data records, see edu/sc/seis/seisFile/mseed/ListHeader.java
  * seedlinkclient - connects to a SeedLink server and retrieves miniseed data from it
  * lissclient - connects to a liss server and retrieves miniseed data from it
  * dswsclient - connects to the IRIS DMC data select web service and retrieves miniseed data from it
  * stationxmlclient - connects to the IRIS DMC station web service and retrieves information about networks, stations and channels from it.
  * cwbclient - connects to the USGS CWB service and retrieves miniseed data from it.
  * winstonclient - connects to a Winston database and retrieves data from it, either tracebuf2 or sync files.
  * waveserverclient - connects to an Earthworm WaveServer and retrieves data from it, either  a menu or mseed files.
  * syncfile2gmt -- creates a GMT script to make a plot of a syncfile
  * syncfilecompare -- compares two syncfiles, outputing time windows in both, only one or only the other
