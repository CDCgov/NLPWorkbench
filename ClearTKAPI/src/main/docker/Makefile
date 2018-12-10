

docker:
	docker build -t cdc/cleartk .

copy:
	cp ../../../target/*.war .

start:
	docker run -d -p 8080:8080 -v /usr/local/clew:/usr/local/clew --name cleartk cdc/cleartk

stop:
	docker rm -f cleartk

