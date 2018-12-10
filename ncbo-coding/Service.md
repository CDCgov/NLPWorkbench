# LAPPS Grid Service Wrapper

This project provides the LAPPS Grid SOAP service wrappers to be packaged and deployed in a WAR file.

## Requirements

1. Java 1.8
1. Maven 3.x
1. [Lappsgrid Services DSL](https://github.com/lappsgrid-incubator/org.anc.lapps.dsl) (Optional)

## SOAP Service Configuration

The SOAP services are defined in the `src/main/webapp/WEB-INF/serviceimpl/*.xml` files.  All of the XML files are identical except for the single `<bean/>` element that defines the `class` that implements the SOAP service.  The only thing that can be changed in the *web.xml* file is the `<display-name/>` located at the top of the file.

## Building The Project

```bash
mvn clean package
```

A *.war* file will be generated in the `target/` folder.  The *.war* file can be deployed to any Java applicaton server (Tomcat, Glassfish, JBoss, etc)

## Runtime Configuration

None required.

## Docker

The `src/main/docker` directory contains a Dockerfile for building a Tomcat 9 container with the  service WAR file deployed.  A `Makefile` is provided for building the Docker container on MacOS/Linux systems.

```bash
cd src/main/docker
make copy
make
```

### Building The Docker Image

Always ensure that you are using the latest *.war* file that was built.

```bash
cp target/*.war src/main/docker
```

The `make copy` goal can be used is using *Make* to build the Docker image.

```bash
docker build -t cdc/ncbo .
```

Note the period at the end of the command line!  This will generate a Docker image named *cdc/ncbo* that contains a Tomcat 9 instance with the *.war* installed as a web service.

### Staring and Stopping the Container

```bash
docker run -d -p 8080:8080 --name ncbo cdc/ncbo
```

- **-d** starts the container in a detached shell (daemon mode).
- **-p** connects localhost:8080 to port 8080 in the container.
- **--name** specify a name for the container. Used to interact with the container.

```bash
docker rm -f ncbo
```
Kills and removes the container.

### Tomcat Manager Application

The Tomcat 9 container has the HTML Manager application enabled. For enhanced security a 16 character random password is generated for the *admin* user every time the container is started.  To be able to connect to the Manager application you will need to read the password from the `tomcat-users.xml` config file:

```bash
docker exec ncbo cat /usr/local/tomcat/conf/tomcat-users.xml
```

The `docker exec` command will run the `cat` program inside the `ncbo` container to display the contents of the *tomcat-users.xml* file.

You can also use the `docker exec` command to *login* to a running container.

```bash
docker exec -it ncbo /bin/bash
```
The **-it** (or **-i -t**) options tell Docker that we want to run `/bin/bash` in an interactive TTY. This is useful to connect to the container to read log files or check the runtime environment etc.

## Integration Testing

A simple test script is provided that runs a short piece of text through each of the services.  The test scripts requires the LAPPS Services DSL (LSD) to be installed to run the script.

MacOS/Linux users can install the LSD with:

```bash
curl -sSL http://downloads.lappsgrid.org/scripts/install-lsd.sh | bash
```

Windows users can refer to the [Git repository](https://github.com/lappsgrid-incubator/org.anc.lapps.dsl) for installation instructions (basically you just download an unpack a *.tgz* file).

```bash
java -jar lsd-x.y.z.jar test.lsd
```

Replace `x.y.z` with the current version number (2.2.4-SNAPSHOT at the time of this writing.) The *test.lsd* script expects the `cdc/ncbo` Docker container to be running on port 8080.
 
 