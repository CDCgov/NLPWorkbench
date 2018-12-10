

docker:
	docker build -t cdc/ncbo .

copy:
	cp ../../../target/*.war .

start:
	docker run -d -p 8082:8080 --name ncbo cdc/ncbo

stop:
	docker rm -f ncbo
