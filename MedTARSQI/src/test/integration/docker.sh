#!/usr/bin/env bash

if [ -z "$1" ] ; then
    echo "USAGE: ./docker.sh [start|stop]"
    exit 1
fi

case $1 in
    start)
        docker run -d -p 8080:8080 --name medtarsqi cdc/medtarsqi
        ;;
    stop)
        docker rm -f medtarsqi
        ;;
    *)
        echo "Invalid option $1"
        echo "USAGE: ./docker.sh [start|stop]"
        exit 1
esac
