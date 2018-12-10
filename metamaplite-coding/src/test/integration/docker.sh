#!/usr/bin/env bash

if [ -z "$1" ] ; then
    echo "USAGE: ./docker.sh [start|stop]"
    exit 1
fi

# Local installation of MetaMap Lite.
MM=/usr/local/share/public_mm_lite

case $1 in
    start)
        docker run -d -p 8080:8080 --name metamap -v $MM:$MM cdc/metamap-lite
        ;;
    stop)
        docker rm -f metamap
        ;;
    *)
        echo "Invalid option $1"
        echo "USAGE: ./docker.sh [start|stop]"
        exit 1
esac
