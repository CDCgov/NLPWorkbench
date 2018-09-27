#!/usr/bin/env bash

lsd convert.lsd $1 $2 $3
curl -d @$2 -u "suderman@cs.vassar.edu:4p+ZoXZ1n(bjgell" http://pubannotation.org/projects/LappsTest/docs/sourcedb/PubMed/sourceid/$3/annotations.json