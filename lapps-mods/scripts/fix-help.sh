#!/usr/bin/env bash

for f in `find ../tools -name "*.xml" | grep "\S]]>"` ; do
	echo $f
done