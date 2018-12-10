

docker:
	docker build -t cdc/ctakes-temporal .

copy:
	cp ../../../target/*.war .

start:
	docker run -d -p 8081:8080 -v /usr/local/clew:/usr/local/clew --name temporal cdc/ctakes-temporal

stop:
	docker rm -f temporal
