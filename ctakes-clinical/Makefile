
JAR=ctakes-clinical.jar
DATA=data/config/*
CTAKES=org/apache/ctakes
SNO=$(CTAKES)/dictionary/lookup/fast/sno_rx_16ab.xml
LVG=$(CTAKES)/lvg/data/config/lvg.properties
CP=$(DATA):$(SNO):$(LVG):$(JAR)
CLASS=gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP.Main

jar:
	mvn package
	
clean:
	mvn clean

test:
	@echo $(CP)
	if [ ! -e target/org ] ; then cp -r src/backups/resources/* target/ ; fi
	cd target && java -cp $(CP) $(CLASS)
	#cd target && java -cp $(JAR) $(CLASS)
	
run:
	#if [ ! -e target/org ] ; then cp -r src/backups/resources/* target/ ; fi
	cd target && java -jar ctakes-clinical.jar

help:
	@echo "Available Goals:"
	@echo "  jar   - builds the jar file"
	@echo "  clean - removes build artifacts"
	@echo "  run   - runs the jar file"
	@echo "  help  - prints this help message."
	@echo
