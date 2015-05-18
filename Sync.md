# Introduction #

SeisFile supports both reading and writing of IRIS-style sync files. Sync file related code is in the [edu.sc.seis.seisFile.syncFile package](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/syncFile/).


# Examples #
To load a sync file all at once:
```
SyncFile sync = SyncFile.load(filename);
```
or one line at at time:
```
BufferedReader in = ...
SyncFileReader sync = new SyncFileReader(in);
SyncLine line;
while(sync.hasNext()) {
    line = sync.next();
    // ...do something...
}
```
Creating a sync file:
```
SyncFile sync = new SyncFile("myDCC")
while(...) {
    SyncLine line = new SyncLine(...);
    sync.addLine(line);
}
```
Writing at once:
```
SyncFile sync = ...
sync.saveToFile("outfile.sync")
```
of writing incrementally for large sync files:
```
SyncFileWriter sync = new SyncFileWriter("myDCC", "outfile.sync");
while(...) {
    SyncLine line = ...
    sync.appendLine(line);
}
sync.close();
```

A sync file for many channels can be split into one sync file per channel with
```
SyncFile sf = SyncFile.load(new File(filename));
HashMap<String, SyncFile> syncfileMap = sf.splitByChannel();
```

Comparing two sync files is possible, using SyncFileCompare:
```
SyncFile a = ...
SyncFile b = ...
SyncFileCompare sfc = new SyncFileCompare(a, b);
sfc.getInAinB().saveToFile("inAinB.sync");
sfc.getNotAinB().saveToFile("notAinB.sync");
sfc.getInAnotB().saveToFile("inAnotB.sync");
```

A GMT script to create a plot of a collection of sync files can be created with the GMTSyncFile class:
```
SyncFile sf = SyncFile.load(new File(inFilename));
HashMap<String, SyncFile> byChan = sf.splitByChannel();
PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("syncPlot.gmt")));
GMTSyncFile gmtPlotter = new GMTSyncFile(numChannels+1, earliest, latest, out);
gmtPlotter.gmtHeader();
int chanIndex = 0;
for (String key : byChan.keySet()) {
    chanIndex++;
    gmtPlotter.plot(byChan.get(key), chanIndex);
    gmtPlotter.setJustify("LB");
    gmtPlotter.setTextColor(Color.BLACK);
    gmtPlotter.label(earliest, chanIndex, key);
}
gmtPlotter.gmtTrailer();
out.close();
```