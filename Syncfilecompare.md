# Introduction #

syncfilecompare is an example client that compares two syncfiles, producing three output syncfiles per channel corresponding to time windows that are in both, only in the first and only in the second. Additionally, a [GMT](http://gmt.soest.hawaii.edu/) script can be output that generates a plot of the three sync files.


# Example #

This produces per channel sync files and a postscript, .ps, file with a plot of the contents of the two sync files colored according to whether they are in both or in one or the other.

```
cp ../../src/test/resources/edu/sc/seis/seisFile/syncFile/CO_Jan2012* .
bin/syncfilecompare --gmt -a CO_Jan2012.sync -b CO_Jan2012_winston.sync
bash syncCompare.gmt
```


# Usage #

```
Usage: syncFileCompare [--gmt] -a file1.sync -b file2.sync
```