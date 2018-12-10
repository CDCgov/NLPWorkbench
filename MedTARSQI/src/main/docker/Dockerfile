FROM lappsgrid/tomcat9

RUN apk add --update python python-dev py-pip perl
COPY setenv.sh /usr/local/tomcat/bin
COPY *.war /usr/local/tomcat/webapps
