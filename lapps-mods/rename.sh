#!/bin/bash
unset CDPATH
cd tools

for dir in `ls -d lapps_*` ; do
	newname=`echo $dir | sed 's/lapps_//'`
	echo "Renaming $dir"
	#git rm $dir
	mv $dir $newname
	git add $newname
done
