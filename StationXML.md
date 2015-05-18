# Introduction #

SeisFile supports parsing of [FDSN StationXML ](http://www.fdsn.org/xml/station/). StAX (Streaming API for XML) is used internally for this parsing.


# Example #

Reading an stationXML file from an FDSN station web service could be done like [this](http://code.google.com/p/seisfile/source/browse/src/example/java/edu/sc/seis/seisFile/example/FDSNStation.java):
```

            FDSNStationQueryParams queryParams = new FDSNStationQueryParams();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            queryParams.area(30, 35, -83, -79)
                    .setStartTime(sdf.parse("2010-03-15"))
                    .setEndTime(sdf.parse("2013-03-21"))
                    .appendToNetwork("CO")
                    .appendToChannel("?HZ")
                    .setLevel(FDSNStationQueryParams.LEVEL_CHANNEL);
            FDSNStationQuerier querier = new FDSNStationQuerier(queryParams);
            FDSNStationXML xml = querier.getFDSNStationXML();
            if (!xml.checkSchemaVersion()) {
                System.out.println("");
                System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
                System.out.println("XmlSchema (code): " + QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION);
                System.out.println("XmlSchema (doc): " + xml.getSchemaVersion());
            }
            NetworkIterator nIt = xml.getNetworks();
            while (nIt.hasNext()) {
                Network n = nIt.next();
                System.out.println("Network: " + n.getCode() + "  " + n.getDescription());
                StationIterator sIt = n.getStations();
                while (sIt.hasNext()) {
                    Station s = sIt.next();
                    System.out.println(s.getLatitude() + "/" + s.getLongitude() + " " + s.getCode() + " "
                            + s.getSite().getName() + " " + s.getStartDate());
                    List<Channel> chanList = s.getChannelList();
                    for (Channel channel : chanList) {
                        System.out.println("        " + channel.getLocCode() + "." + channel.getCode() + " "
                                + channel.getAzimuth() + "/" + channel.getDip() + " " + channel.getDepth().getValue()
                                + " " + channel.getDepth().getUnit() + " " + channel.getStartDate());
                    }
                }
            }

```