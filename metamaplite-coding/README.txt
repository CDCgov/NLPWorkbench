
The initialization and execution of MetMap-Lite functionality is wrapped as a class and some APIs, as follows, to provide an easy-to-access interface to software developers and integrators. 

Further information regarding MetaMapLite can be found in this web site:
https://metamap.nlm.nih.gov/MetaMapLite.shtml

1. The initialization of MetaMapLite


 1.1. MetaMapLite needs to be downloaded from the NLM/NIH web page here:
https://metamap.nlm.nih.gov/MetaMapLite.shtml

 Specifically, the actual zip file to download is located here:
https://metamap.nlm.nih.gov/download/metamaplite/public_mm_lite_3.6.1p1.zip

 1.2. After the above zip file is downloaded, please extract it to a local directory, such as C:\Software\metamaplite\public_mm_lite.
 MetaMapLite needs local installation and please see this page for instructions:
https://metamap.nlm.nih.gov/Docs/README_MetaMapLite_3.6.1.html

 1.3. Additionally, "Installing metamaplite and dependencies into local Maven repository" is required. Please read the documentation following the above link, or just execute the following Maven commands in the metamaplite installation directory, e.g., C:\Software\metamaplite\public_mm_lite.

 * From public_mm_lite directory install Context, BioC, and NLS NLP libraries
 
$ mvn install:install-file -Dfile=lib/context-2012.jar -DgroupId=context -DartifactId=context -Dversion=2012 -Dpackaging=jar

$ mvn install:install-file -Dfile=lib/bioc-1.0.1.jar -DgroupId=bioc -DartifactId=bioc -Dversion=1.0.1 -Dpackaging=jar

$ mvn install:install-file -Dfile=lib/nlp-2.4.C.jar -DgroupId=gov.nih.nlm.nls -DartifactId=nlp -Dversion=2.4.C  -Dpackaging=jar

 * Then install metamaplite into your local Maven repository:
$ mvn install

*** Please note that the above process may take quite some time (say, 15 minutes) depending on your machine and Internet connectivity. ***

2. *** VERY IMPORTANT ***
 Please change the metamaplite version value in the POM.xml file. Without this change, the program throws exceptions.
 Depending on the version of the metamaplite package, say, Version 3.6.1p1, please update the value in the following snippet in POM.xml in the root directory of the project.

 		<dependency>
			<groupId>gov.nih.nlm.nls</groupId>
			<artifactId>metamaplite</artifactId>
			<version>3.6.1p1</version>
		</dependency>
		
3. *** VERY IMPORTANT ***
 Please change, if needed, the directory value to where the installed MetaMapLite is actually located in the configuration file, named "MetaMapLiteAPI.Properties".
 For example, if the MetaMapLite is installed in C:/software/public_mm_lite/, then the content of the "MetaMapLiteAPI.Properties" file should be like this:
	MetaMapLiteDirectory=C:/software/public_mm_lite/
 
4. To initialize the wrapping class.

	(a) 		MetaMap metaMap = new MetaMap();

	The program reads the value from "MetaMapLiteAPI.Properties" without taking any parameter to the constructor as shown above.

	(b) 		MetaMap metaMap = new MetaMap("C:/software/public_mm_lite/");
	
	Alternatively, a specific path can be set as the parameter to the constructor.
	
		

5. To execute the key function of "processText()" to obtain the MetaMapLite Coding result.

		HashMap<String, ArrayList<Term>> codedResults = metaMap.processText(sampleText, null, null, 0);

	The function takes four paraemters:
		sampleText: the raw text to process;
		selectedOntologies: the ontology a user needs the MetaMapLite ontology to specify; this value is recommended to set as 'null';
		resources: the resources of the coding a user needs it to specify; if this variable is empty, it means the specification of all resource types; this value is recommended to set as 'null'.
		range: an integer specifying the context of the extracted text spans, this value is recommended to set as '0'.

	The function returns the coding results stored in a HashMap data structure, specifically, HashMap<String, ArrayList<Term>>.
		HashMap<String, ArrayList<Term>> codedResults = null;
	
	Specific examples are shown below. Specific values can be obtained from the link here: https://metamap.nlm.nih.gov/MetaMapLite.shtml
		
	
6. Result storage

	(a)		HashMap<String, ArrayList<Term>> codedResults = metaMap.processText(sampleText, null, null, 0);
	
	The results can be stored in a HashMap structure as specified above. 
	
	(b) 	TermToXML ttx = new TermToXML(codedResults, sampleText);
			String resultXMLStr = ttx.generateXMLString(codedResults);

	As an additional convenience, the results can be stored in a String form that contains the XML content being VAERS Data Type System compliant. In order to get the String result, a utility class of "TermToXML" needs to be instantiated. And the function of "generateXMLString()" can be executed to return the expected XML content in a String.
	
