## **Note: the IRIS ws-station service has been replaced by the FDSN Station web service. Please use the FDSNStation client instead.** ##

# Introduction #

stationxmlclient is an example client for the [IRIS ws-station web service](http://www.iris.edu/ws/station) as well as for reading StationXML files.

[Client source code](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/stationxml/StationXMLClient.java).


# Example #

Usage is:

```
bin/stationxmlclient -u url
```

Because the URL will have ampersands and question marks, it might need to be enclosed in single quotes

For example, to get a list of BHZ channels from IU.SNZO, you could use the client like this:

```
bin/stationxmlclient -u 'http://www.iris.edu/ws/station/query?net=IU&sta=SNZO&chan=BHZ&level=chan'
StaMessage
Source: IRIS-DMC
Sender: IRIS-DMC
Module: IRIS WEB SERVICE: http://www.iris.edu/ws/station Networks: [IU] Stations: [SNZO] Channels: [BHZ] level:[chan]
SentDate: 2011-06-03T16:00:41
Network: IU Global Seismograph Network (GSN - IRIS/USGS) 1988-01-01T00:00:00 2500-12-12T23:59:59
  Station: IU.SNZO 4
    Station Epoch: IU.SNZO  1992-04-07T00:00:00 to 1997-11-19T00:00:00
      Channel Epoch:   .BHZ  1992-04-09T00:00:00 to 1997-11-19T00:00:00
    Station Epoch: IU.SNZO  1997-11-19T00:00:00 to 2009-09-18T00:00:00
      Channel Epoch:   .BHZ  1997-11-19T00:00:00 to 1999-02-14T22:00:00
      Channel Epoch: 00.BHZ  1999-02-14T22:00:00 to 2003-01-28T06:00:00
      Channel Epoch: 00.BHZ  2003-01-28T06:00:00 to 2009-09-18T00:00:00
      Channel Epoch: 10.BHZ  1999-02-14T22:10:00 to 2006-01-25T22:00:00
      Channel Epoch: 10.BHZ  2006-01-25T22:00:00 to 2009-09-18T00:00:00
    Station Epoch: IU.SNZO  2009-09-18T00:00:00 to 2010-12-10T00:00:00
      Channel Epoch: 00.BHZ  2009-09-18T00:00:00 to 2010-12-10T00:00:00
      Channel Epoch: 10.BHZ  2009-09-18T00:00:00 to 2010-12-10T00:00:00
    Station Epoch: IU.SNZO  2010-12-10T00:00:00 to 2599-12-31T23:59:59
      Channel Epoch: 00.BHZ  2010-12-10T00:00:00 to 2599-12-31T23:59:59
      Channel Epoch: 10.BHZ  2010-12-10T00:00:00 to 2599-12-31T23:59:59
```