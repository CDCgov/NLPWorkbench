

docker:
	docker build -t cdc/ctakes-clinical .

copy:
	cp ../../../target/*.war .

start:
	docker run -d -p 8080:8080 -v /usr/local/clew:/usr/local/clew --name clinical cdc/ctakes-clinical

stop:
	docker rm -f clinical
