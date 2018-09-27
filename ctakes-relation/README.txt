This project contains the cTakes temporal capability and wraps its function with a
single API.


Before program execution, a developer needs to properly configure the program environment.
1. A developer needs to download this project to the local drive. Key files include:
	CTakesTemporal.java
	PrettyTextWriterVaers.java
	JUnitTestCTakesTemporal.java
	pom.xml
	README.txt
	
2. The project is a Maven project and its pom.xml files contains necessary information to
acquire suitable dependent libraries.

3. In order for cTakes to execute, a developer needs to specify two environmental variables
as follows. Please replace "USERNAME" and "PASSWORD" to the developer's own credential for 
the UMLS account. A developer can either set these system variables in the operation system, or 
set them in an IDE environment.
	ctakes.umlsuser=USERNAME
	ctakes.umlsps=PASSWORD 

4. (Optional) More information about cTakes can be found following this link: 
 http://ctakes.apache.org/


In order to make use of this API programmatically, a developer needs to do the followings:

1. To initiate an instance of the class, "CTakesTempora".
 
 	CTakesTemporal ct = new CTakesTemporal();
 
2. To apply the cTakes Temporal pipeline to a given text, e.g., a clinical note. Assuming the 
note is in a String format. Please note that this function returns a JCas.

	ct.processDocument(s1);
	
3. (Optional) If a developer wants to display the annotation result in a reasonable textual format,
the following function can be execute for the current JCas and return a String containing the result
in the Pretty Print format.

	String result = ct.getResultInPrettyPrint();
	