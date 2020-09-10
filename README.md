SeisFile is a library for reading and writing seismic file formats in java. See the [wiki](https://github.com/crotwell/seisFile/wiki/Intro) for more information.

**[Downloads](http://www.seis.sc.edu/downloads/seisFile/)** are now hosted at the University of South Carolina due to Google ending support for downloads.

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

These are low level routines that provide basic connection to the services and basic parsing of the file formats into objects that closely mirror those formats. Hence they are intended to be used as a library rather than an application. The clients, although functional, mainly serve as an example of how to connect to these services. Full SEED support was also not intended. However there are cases of almost miniseed, such as one or two control records before the data records. SeisFile can handle the basic structure of these control records, allowing these mixed files to be read without crashing, but not all blockettes are parsed into fields. Support for the many blockette types in full SEED could be added in the future, although there is a large amount of bookkeeping to implement that and seisFile was intended to be small and focused.
