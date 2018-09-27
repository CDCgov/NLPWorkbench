#!/usr/bin/env bash

curl -X POST -H "Content-type: application/json" -d @$1 -o $2 