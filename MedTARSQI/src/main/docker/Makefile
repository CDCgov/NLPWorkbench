

docker:
	docker build -t cdc/medtarsqi .

copy:
	cp ../../../target/*.war .

start:
	docker run -d -p 8080:8080 --name medtarsqi cdc/medtarsqi

stop:
	docker rm -f medtarsqi

