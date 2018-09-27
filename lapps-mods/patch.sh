#!/bin/bash

for xml in `find tools -name "*.xml"` ; do
	#echo $xml
	if grep -qi lapps_ $xml ; then
		#echo "Contains lapps_"
		echo "Patching $xml"
		#contents=`cat $xml`
		#echo $contents | sed 's/lapps_//' > $xml
		sed -i .backup 's/lapps_//g' $xml
	else
		echo "Skipping $xml"
	fi
done
echo "Done"

