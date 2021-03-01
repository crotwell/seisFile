[![Maven Central](https://img.shields.io/maven-central/v/edu.sc.seis/seisFile.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22edu.sc.seis%22%20AND%20a:%22seisFile%22)
 [![javadoc](https://javadoc.io/badge2/edu.sc.seis/seisFile/javadoc.svg)](https://javadoc.io/doc/edu.sc.seis/seisFile)

SeisFile is a library for reading and writing seismic file formats in java. See the [wiki](https://github.com/crotwell/seisFile/wiki/Intro) for more information.


See also the [IRIS SeisCode page.](https://seiscode.iris.washington.edu/projects/seisfile)

Currently support exists for:
  * SAC
  * MiniSEED with limited support for full SEED
  * FDSN Station web service, which returns FDSN StationXML
  * FDSN Event web service, which returns QuakeML
  * FDSN DataSelect web service, which returns miniseed
  * SeedLink
  * Datalink
  * Earthworm/Waveserver and ImportGeneric/ExportGeneric

These are low level routines that provide basic connection to the services and basic parsing of the file formats into objects that closely mirror those formats. Hence they are intended to be used as a library rather than an application. The clients, available in a separate jar to keep the main library small, are functional, but mainly serve as an example of how to connect to these services.

Full SEED support was also not intended. However there are cases of almost miniseed, such as one or two control records before the data records. SeisFile can handle the basic structure of these control records, allowing these mixed files to be read without crashing, but not all blockettes are parsed into fields. Support for the many blockette types in full SEED could be added in the future, although there is a large amount of bookkeeping to implement that and seisFile was intended to be small and focused.

Significant clean up and code removal occurred between SeisFile versions 1.8 and 2.0. This included the remove of code related to PSN, LISS, CWB, Winston, Sync files and GCF as I was no longer able to support development for these. If you need access to one of these, the last [version 1 release](https://search.maven.org/search?q=g:%22edu.sc.seis%22%20AND%20a:%22seisFile%22) can still be used, but no further development will occur.
