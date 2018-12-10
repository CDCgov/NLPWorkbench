

docker:
	docker build -t cdc/metamap-lite .

copy:
	cp ../../../target/*.war .

start:
	docker run -d -p 8080:8080 -v /usr/local/share/public_mm_lite:/usr/local/share/public_mm_lite --name metamap cdc/metamap-lite

stop:
	docker rm -f metamap

