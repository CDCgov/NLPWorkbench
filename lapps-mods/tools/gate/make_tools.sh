#!/usr/bin/env bash

for f in `ls *.xml` ; do
	newname=`echo $f | sed 's/2.0.0/2.1.0/g'`
	cat $f | sed 's/2.0.0/2.1.0/g' > $newname
	echo "Wrote $newname"
done
echo "Done"