# Introduction #

mseedlh is an example client for parsing and printing the contents of a [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) data file, and to a limited extent full seed. See the [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) section for an example of how to process the returned miniseed data records.


# Example #

An example is in [src/main/java/edu/sc/seis/seisFile/mseed/ListHeader.java](https://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/mseed/ListHeader.java) which can also be run as a client. For example:
```
bin/mseedlh test.mseed
    DataRecord      seq=1 type=D cont=false
      CI.DEC.  .HHZ start=2009,246,12:05:02.3185 numPTS=350 sampFac=100 sampMul=1 ac=0 io=32 qual=0 numBlockettes=1 blocketteOffset=48 dataOffset=64 tcor=0
        Blockette1000 encod=11 wOrder=1 recLen=9
Finished: Wed May 21 20:47:11 EDT 2014
```

and the usage is:
```
bin/mseedlh  [-n net][-s sta][-l loc][-c chan][-o mseedOutfile][-m maxrecords][--verbose][--version][--timed][--help] <filename> [<filename>...]
```