
The initialization and execution of NCBO BioPortal Annotator functionality is wrapped as a class and some APIs, as follows, to provide an easy-to-access interface to software developers and integrators. 

https://bioportal.bioontology.org/annotator 


1. To initialize the wrapping class.

	The parameter is set to 'false', which is used to indicate whether the ontologies are needed to be reloaded or not. Again, the default is always 'false'.
	
	NCBO_REST application = new NCBO_REST(false);

	
2. To execute the key function of "processText()" to obtain the NCBO Coding result.

	The function takes four paraemters:
		sampleText: the raw text to process;
		selectedOntologies: the ontology a user needs the NCBO BioPortal Annotator to specify; e.g., "MEDDRA" could be specified as one valid ontology;
		selectedUMLS: the UMLS semantic types a user needs it to specify; if this variable is empty, it means the specification of all UMLS types.
		range: an integer specifying the context of the extracted text spans, i.e., the number of words before and after the extracted text spans. This value could be set to 0 to ignore any context information, or 10.

	The function returns the coding results stored in a HashMap data structure, specifically, HashMap<String, ArrayList<Term>>.
	
	Specific examples are shown below. Specific values can be obtained from the link here: https://bioportal.bioontology.org/annotator 
		
		codedResults = application.processText(sampleText, selectedOntologies, selectedUMLS, range);

		
		ArrayList<String> selectedOntologies = new ArrayList<String>();
		selectedOntologies.add("MEDDRA");
	
		ArrayList<String> selectedUMLS = new ArrayList<String>();
	
		HashMap<String, ArrayList<Term>> codedResults = null;
	
3. Result storage

	The results can be stored in a HashMap structure as specified above. 
	
	Alternatively, the returned result can be stored in a JsonNode as follows.

		JsonNode jn = application.getResults();

	As an additional convenience, the results can be stored in a String form that contains the XML content being VAERS Data Type System compliant. In order to get the String result, a utility class of "TermToXML" needs to be instantiated as follows. And the function of "generateXMLString()" can be executed to return the expected XML content in a String.
	
		TermToXML ttx = new TermToXML(codedResults, sampleText);
		String resultXMLStr = ttx.generateXMLString(codedResults);
