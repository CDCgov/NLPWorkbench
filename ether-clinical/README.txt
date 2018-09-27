This project provides a convenient API for developers to gain the functionalities of ETHER Clinical.
The utilization of this API consists of the following steps:

0. ETHER Package Installation: the package of ETHERNLP should be copied to "C:\software\ETHERNLP" by default.
 A developer can actually copy the package to any location. However, the value of the "ETHERNLPDirectory"
 property in the "Clew.Properties" file needs to be updated accordingly. 
 
1. Initialization: ETHERModule is the main class that wraps the ETHER package.
 The following statement initializes a new instance of the class. 

		ETHERModule etherVar = new ETHERModule();

	By default, the directory of the ETHERNLP package is located in a predefined directory of 
	"C:\software\ETHERNLP". This directory is also specified in the enclosed file named "Clew.Properties", 
	whose field value pair could be: 
	
 	ETHERNLPDirectory=C\:/software/ETHERNLP/

 	A developer is able to change the default location of the ETHERNLP package, such as 
 	"C\:/packages/ETHERNLP/". In this case, the developer needs to change the value of the 
 	"ETHERNLPDirectory" property accordingly in the "Clew.Properties" file. 

	Alternatively, the developer is able to specify this parameter on a command line, or the 
	run configuration of an IDE, as follows:
	"java -ETHERNLPDirectory C\:/ETHERNLP/ -jar ether-clinical.jar" (Omitting other options and parameters).
	
	Please note that if the command line parameter is specified by the developer, the value in the "Clew.Properties" file
	will be superseded. 
	
2. Calling the function to obtain result: the following function will accept a String containing the raw text,
 and generates a String containing the filename for the XML content of the clinical result conforming to the VAERS Type System.

		String resultFilename =
				etherVar.processETHERClinical("Information has been received from a certified medical assistant referring to a patient of unknown age and gender. On 29-DEC-2015 the patient inadvertently received a dose of RECOMBIVAX HB (lot reported as L030581 expiration date: 26-JUN-2017, dose was unknown) intramuscularly in the ventrogluteal site (drug administered at inappropriate site). No adverse effects were reported. Additional information has been requested.");
		
3. Clean up temporary file(s): some temporary files will be removed accordingly.

		etherVar.cleanUpFiles();