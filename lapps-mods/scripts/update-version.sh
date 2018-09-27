#!/usr/bin/env bash

function check {
	if [ -z $2 ] ; then
		echo $1
		exit
	fi
}

while getopts :d:p:f:t: flag; do
	case $flag in
		d) dir=$OPTARG ;;
		p) pattern=$OPTARG ;;
		f) v1=$OPTARG ;;
		t) v2=$OPTARG ;;		
		?)
			echo "Invalid option $OPTARG"
			exit 1
			;;
		:)
			echo "Mission option for $OPTARG"
			;;
		*)
			echo "Matched *"
			;;
	esac
done

check "No directory specified." $dir
check "No pattern specified." $pattern
check "No source version specified." $v1
check "No target version specified." $v2

if [ ! -e $dir ] ; then	
	echo "No such directory $dir"
	exit
fi

#echo "Directory: $dir"
#echo "Pattern  : $pattern"
#echo "From     : $v1"
#echo "To       : $v2"

cd $dir

spec=$pattern*$v1.xml
#pwd
#echo "spec = $spec"
#ls -l $spec

for a in $(ls $spec) ; do 
	b=$(echo $a | sed 's/_2.0.0/_2.1.0/') 
	cat $a | sed "s/$v1/$v2/g" > $b
	echo "Wrote $b"
done