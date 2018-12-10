FROM lappsgrid/tomcat9

RUN mkdir /var/log/clew
COPY setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY *.war /usr/local/tomcat/webapps
