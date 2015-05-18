# Introduction #

SeisFile supports both reading of binary SAC datafiles, as well as poles and zeros. SAC file related code is in the [edu.sc.seis.seisFile.sac package](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/sac/).


# Examples #
```
SacTimeSeries sac = new SacTimeSeries(filename);
```
or
```
DataInput dis = ...
SacTimeSeries sac = new SacTimeSeries(dis)
```
and reading a polezero file:
```
SacPoleZero spz = new SacPoleZero(filename);
```