FROM lappsgrid/tomcat9

COPY setenv.sh /usr/local/tomcat/bin
RUN mkdir /etc/clew
COPY cleartk.properties /etc/clew
COPY *.war /usr/local/tomcat/webapps
