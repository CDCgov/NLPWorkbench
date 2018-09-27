
The initialization and execution of the ClearTK Machine Learning Classifier is wrapped as a class and a simple API, as follows, to provide an easy-to-access interface to software developers and integrators. 

Further information regarding ClearTK can be found in this web site:
https://cleartk.github.io/cleartk/

0. To configure the parameters properly.

A couple of key values need to be specified for the program to run successfully in the file named "ClearTKAPI.Properties", including
	(a) MetaMapLiteDirectory=C:/software/public_mm_lite/
	
	The specification of the MetaMapLite installation directory. Please also see the related project of "metamaplite-coding" project for details.
	
	(b) ModelDirectory=C:/software/Data/Models/
	
	The specification of the directory that contains the Machine Learning models. This directory can be any place in the target environment, such as "C:/software/Data/Models/" that has been tested to function correctly.

1. To initialize the wrapping class.

	(a) 		RunClearTK runClearTK = new RunClearTK();

	The RunClearTK wrapper class is intialized.

	(b) 		JCas jCas = runClearTK.runClearTKModel(report, modelName);
	
	The execution of the key API, "runClearTK.runClearTKModel(String report, modelName)". The parameters are 
		String report: the raw text for processing; and
		String modelName: one of the machine learning model names, i.e., "svm" and "crf". SVM stands for Support Vector Machine; CRF stands for Conditional Random Field.
		
	The return of the API is a standard JCas structure as shown above.
	
	

