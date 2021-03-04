#!/bin/bash

EXPLODE=../../../../build/explode
PATH=${PATH}:${EXPLODE}/bin
CMD_FILES=$(ls *_ex?.sh)
cd ../man-templates/example_output
for exfile in $CMD_FILES ; do
  OUT=${exfile//\.sh/_out}
  echo $exfile
  /bin/rm -f ${OUT}
  while read line ; do
    echo "> $line" >> ${OUT}
    $line >> ${OUT}
  done < ../../man-example/$exfile
done
cd ..
