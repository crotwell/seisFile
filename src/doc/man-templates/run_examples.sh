#!/bin/bash

for exfile in *_ex?.sh ; do
  OUT=example_output/${exfile//\.sh/_out}
  echo $exfile
  /bin/rm -f ${OUT}
  while read line ; do
    echo "> $line" >> ${OUT}
    ../../../build/explode/bin/$line >> ${OUT}
  done < $exfile
done
