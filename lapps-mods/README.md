# GalaxyMods
The LAPPS Grid modifications to core Galaxy.

In an attempt to keep our [Galaxy fork](https://github.com/ksuderman/Galaxy) as clean 
as possible changes made to Galaxy are kept in a separate repository and then Galaxy's
`galaxy.ini` file is modified to point to these directories. 

The following files and directories are maintained outside of Galaxy:
- **config**<br/>
Modified configuration files.  Currently
  1. datatypes_conf.xml
  1. job_conf.xml
  1. tool_conf.xml
  1. The directory currently contains a `galaxy.ini` file, but it may not be kept up to date.  Always consult the `galaxy.ini` file in the main Galaxy repository.
- **plugins**<br/>
Visualizations (i.e. brat) and Interactive Environments (i.e. Jupyter).
- **tools**<br/>
Galaxy tool wrappers for the LAPPS Grid web services.


