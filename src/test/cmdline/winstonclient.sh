
bin/winstonclient -b 2012-01-01 -e 2012-01-02  --sync -p Winston.config -n CO -s '*' -l 00 -c '.H[ZNE]' > winston.sync

bin/winstonclient -b 2012-01-01T12:34:56 -e 2012-01-01T12:56:32  -p /ra/scsn/winston/Winston1.1/Winston.config -n CO -s '*' -l 00 -c '.H[ZNE]' -o winstondata.mseed


