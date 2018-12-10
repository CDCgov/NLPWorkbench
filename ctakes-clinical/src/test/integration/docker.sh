#!/usr/bin/env bash

if [ -z "$1" ] ; then
    echo "USAGE: ./docker.sh [start|stop]"
    exit 1
fi

case $1 in
    start)
        docker run -d -p 8080:8080 -v `pwd`/target:/usr/local/tomcat/webapps -v /usr/local/clew:/usr/local/clew --name tomcat lappsgrid/tomcat9
        ;;
    stop)
        docker rm -f tomcat
        ;;
    *)
        echo "Invalid option $1"
        echo "USAGE: ./docker.sh [start|stop]"
        exit 1
esac
