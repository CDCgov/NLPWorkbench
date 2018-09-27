This project contains the MedTARSQI package and wraps its function with a
single API.

1. Some software package installations are required as a precondition for the project.

The 'ttk' package has been released as part of the project resources, located in src/main/resources. However, if a user wants to ensure the compatibility of the MedTARSQI with his/her computing environment, a general version of med-ttk can be obtained from the GITHUB here:
https://github.com/tarsqi/ttk/.
However, the MedTARSQI distribution within this project is different from the ttk available at GITHUB. This MedTARSQI has a different version of the temporal expression module which is based on temporal expressions found in medical documents.

 (1) Python 2.7 (e.g., python-2.7.15). The MedTARSQI development team advised that the Python needs to be version 2.7.
https://www.python.org/downloads/release/python-2715/
 
 (2) Perl 5.8 (e.g., Strawberry Perl 5.8.8).
  http://strawberryperl.com/releases.html
  http://strawberryperl.com/download/5.8.8/strawberry-perl-5.8.8.4.zip
 
 (3) TreeTagger
   TreeTagger has been distributed together with the 'ttk' package as part of the resources under the directory of 'src/main/resources/ttk/build\'. However, if a user needs to ensure its compatibility  with his/her environment, it is fine to down TreeTagger on his/her own. The instructions are as follows, if needed.
   
   If a fresh TreeTagger is needed, please download it from this link www.cis.uni-muenchen.de/~schmid/tools/TreeTagger, specifically the particular version should be downloaded to fit your target operation system, e.g., in Windows,
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/#Windows, and unzip under ttk\build\ folder. The config.txt file is in the root directory of ‘'src/main/resources/ttk’.
	The home path for the treetagger is required in the config.txt file.
	
	Additionally, please download TreeTagger language model file (English model) from http://txm.sourceforge.net/installtreetagger_en.html and paste it in ttk\build\TreeTagger\lib folder. TreeTagger should be all set now.

	Again, the above model files have been downloaded as part of the TTK package, however, a user can make her/his own downloaded if needed.
 
 (4) Mallet
   Mallet has been distributed together with the 'ttk' package as part of the resources under the directory of 'src/main/resources/ttk/build\'. However, if a user needs to ensure its compatibility  with his/her environment, it is fine to down Mallet on his/her own. The instructions are as follows, if needed.
    
	Download Mallet from http://mallet.cs.umass.edu/dist/mallet-2.0.8.tar.gz and unzip under ttk\build\Mallet. The home path for the Mallet is required in the config.txt file.
	Alternatively, you can just run the shell script file named install-mallet-osx.sh to install mallet for some compatible operating systems.

	After TreeTagger and Mallet are installed, please make sure their paths in the config.txt file are correct and pointing to the location where you just installed them.
	
	**************** 
	Please note: if the target operating system is case sensitive (e.g., Linux/Unix), please use cause to input the accurate folder/package names, e.g., "TreeTagger" instead of "Treetagger" based on the installation setup, in the 'config.txt' file for MedTARSQI.
	****************

2. Instructions for Developers
	
 In order to make use of this API programmatically, a developer needs to do the followings:

1. To initiate an instance of the class, "CTakesTempora", where inputString contains raw text to process.
 
		MedTARSQI medTARSQI = new MedTARSQI(inputString);
 
2. To run the following three APIs to get the String form of the XML content for Clinical, Temporal and 
Association Relationship respectively.

		String result = medTARSQI.getClinicalXMLContent();
		
		result = medTARSQI.getTemporalXMLContent();
		
		result = medTARSQI.getRelationXMLContent();
	