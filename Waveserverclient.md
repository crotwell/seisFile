# Introduction #

waveserverclient is an example client for access to a Earthworm WaveServer or Winston WaveServer that returns [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) data or a syncfile. See the [miniseed](http://code.google.com/p/seisfile/wiki/MiniSeed) section for an example of how to process the returned miniseed data records.

A listing of available channels, as well as their latency, can be retrieved using the --menu option.

Compression via the Steim1 algorithm can be done with the --stiem1 flag and the default record size, 12 with give 4096, can be changed with the --recLen argument.

# Example #

To list the available channels:
```
bin/waveserverclient --menu -h eeyore.seis.sc.edu -p 16022 
```

and to retrieve miniseed data:

```
bin/waveserverclient -h eeyore.seis.sc.edu -p 16022 -n CO -s JSC -l 00 -c HHZ  -b 2012-01-01T12:34:56 -e 2012-01-01T12:56:32 -o waveserver.mseed --steim1 --recLen 8
```

The usage is:

```
java edu.sc.seis.seisFile.waveserver.WaveServerClient [-n net][-s sta][-l loc][-c chan][-b yyyy-MM-dd[THH:mm:ss.SSS]][-e yyyy-MM-dd[THH:mm:ss.SSS]][-d seconds][-o outfile][-m maxpackets][--verbose][--version][--help][-h host][-p port][--menu][--steim1][--recLen len(8-12)]
```
