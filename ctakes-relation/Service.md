# LAPPS Grid Service Wrapper

This project provides the LAPPS Grid SOAP service wrappers to be packaged and deployed in a WAR file.

## Requirements

1. Java 1.8
1. Maven 3.x
1. [Lappsgrid Services DSL](https://github.com/lappsgrid-incubator/org.anc.lapps.dsl) (Optional)
1. LVG and Snomed files from [http://downloads.lappsgrid.org/clew.tgz](http://downloads.lappsgrid.org/clew.tgz)

Extract the *clew.tgz* file to `/usr/local/clew`.  If you need to extract the files to a different location (say on a Windows server) you will need to edit the *lvg.properties* and *snomed_rx_16ab.xml* files in the `src/main/resources` directory.

#### lvg.properties
Change the `LVG_DIR` variable to point to the new location of the *lvg* directory. See `src/main/resources/org/apache/ctakes/lvg/data/config/`

#### sno_rx_16ab.xml
Update the path in the two `jdbcUrl` properties.  No other changes are required.

**Note** While it is possible to set the UMLS username and password in the *sno_rx_16ab.xml* file it is strongly recommended to use one of the methods [described below](#runtime-configuration) and that passwords are not checked into source control

See `src/main/resources/org/apache/ctakes/dictionary/lookup/fast/`

## SOAP Service Configuration

The SOAP services are defined in the `src/main/webapp/WEB-INF/serviceimpl/*.xml` files.  All of the XML files are identical except for the single `<bean/>` element that defines the `class` that implements the SOAP service.  The only thing that can be changed in the *web.xml* file is the `<display-name/>` located at the top of the file.

## Building The Project

```bash
mvn clean package
```

A *.war* file will be generated in the `target/` folder.  The *.war* file can be deployed to any Java applicaton server (Tomcat, Glassfish, JBoss, etc)

## Runtime Configuration

The cTakes Temporal service requires UMLS login credentials to query the UMLS online database.  The UMLS credentials can be provided by:

1. The file `/usr/local/clew/umls.properties` if it exists.
1. The file `/etc/clew/umls.properties` it if exists, or
1. In the environment variables **umlsUser** and **umlsPass**

How the environment variable set and made visible to the service depends on how the *.war* is deployed in production.  

## Docker

The `src/main/docker` directory contains a Dockerfile for building a Tomcat 9 container with the  service WAR file deployed.  A `Makefile` is provided for building the Docker container on MacOS/Linux systems.

```bash
cd src/main/docker
make copy
make
```

### External Data

There are two options for dealing with the `clew` directory:

1. Copy the contents of `/usr/local/clew` into the container when it is built.
1. Bind mount the `/usr/local/clew` directory into the container when it is launched.

To reduce the size of the Docker container these instructions assume the directory will be bind mounted into the container.  To include the contents of the `clew` directory into the container you will need to:

1. Copy the *clew.tgz* file into the `src/main/docker` directory.
1. Add the following line immediately after the `FROM` statement<br/>
`ADD clew.tgz /usr/local` 
1. Omit the **-v** option in all of the commands below.

### Building The Docker Image

Always ensure that you are using the latest *.war* file that was built.

```bash
cp target/*.war src/main/docker
```

The `make copy` goal can be used when using *Make* to build the Docker image.

```bash
docker build -t cdc/ctakes-temporal .
```

Note the period at the end of the command line!  This will generate a Docker image named *cdc/ctakes-temporal* that contains a Tomcat 9 instance with the *.war* installed as a web service.

### Staring and Stopping the Container

```bash
docker run -d -p 8080:8080 --name temporal -v /var/lib/clew:/usr/local/clew cdc/ctakes-temporal
```

- **-d** starts the container in a detached shell (daemon mode).
- **-p** connects localhost:8080 to port 8080 in the container.
- **--name** specify a name for the container. Used to interact with the container.
- **-v** bind mounts the local `/var/lib/clew` directory as `/usr/local/clew` inside the container.  Change `/var/lib/clew` to the actual path on the local system.

```bash
docker rm -f temporal
```
Kills and removes the container.

### Tomcat Manager Application

The Tomcat 9 container has the HTML Manager application enabled. For enhanced security a 16 character random password is generated for the *admin* user every time the container is started.  To be able to connect to the Manager application you will need to read the password from the `tomcat-users.xml` config file:

```bash
docker exec temporal cat /usr/local/tomcat/conf/tomcat-users.xml
```

The `docker exec` command will run the `cat` program inside the `temporal` container to display the contents of the *tomcat-users.xml* file.

You can also use the `docker exec` command to *login* to a running container.

```bash
docker exec -it temporal /bin/bash
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

Replace `x.y.z` with the current version number (2.2.4-SNAPSHOT at the time of this writing.) The *test.lsd* script expects the `cdc/ctakes-temporal` Docker container to be running on port 8080.
 
 