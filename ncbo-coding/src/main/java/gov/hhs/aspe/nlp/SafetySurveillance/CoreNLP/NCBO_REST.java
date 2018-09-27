package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Ontology.Ontology;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Ontology.OntologyList;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Ontology.OntologyWriter;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.json.JSONObject;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;
/**
 * 
 * Wrapper class to annotate with NCBO web service 
 * Updated Jun 11, 2018
 * @author Guangfan.Zhang
 *
 */
public class NCBO_REST {
	
	JsonNode results = null;
	
	public JsonNode getResults() {
		return results;
	}
	public void setResults(JsonNode results) {
		this.results = results;
	}

	static final String API_URL = "http://data.bioontology.org/";
	static final String PURL_URL = "http://purl.bioontology.org/ontology/";
	static final String API_KEY= "eced9115-fa0e-4d9e-b506-99e0893ac4f9";
	static String origQuery = ""; 
	static final ObjectMapper mapper = new ObjectMapper();
	ArrayList<String> listOntologies = new ArrayList<String>();
	OntologyList ontologyList;
	public OntologyList getOntology() {
		return ontologyList;
	}

	public void setOntology(OntologyList ontology) {
		this.ontologyList = ontology;
	}

	public ArrayList<String> getListOntologies() {
		return listOntologies;
	}

	public void setListOntologies(ArrayList<String> listOntologies) {
		this.listOntologies = listOntologies;
	}

	URL url = null; 
	
	public NCBO_REST() {
		this(false);
        
	}
	
	/**
	 * Initialization of NCBO ontology list, allow user to reload it  or read the information directly from jar file or plain text file
	 * 
	 * @param reload: reload ontology list from NCBO server
	 */
	public NCBO_REST(boolean reload){
		
		String ontologyFileName = "classes\\\\ontology\\\\OntologyList.txt";
        ontologyList = new OntologyList();
		if(reload){
	        // Iterate looking for ontologies
			JsonNode ontologies = null;
			OntologyWriter oWriter = new OntologyWriter(ontologyFileName);
	        String ontologies_string = get(API_URL + "ontologies");
	        ontologies = jsonToNode(ontologies_string);
	        for (JsonNode o: ontologies) {
	        	String strAcry = o.get("acronym").toString();
	        	String strName = o.get("name").toString();
	        	if(strAcry.startsWith("\""))
	        		strAcry = strAcry.substring(1, strAcry.length()-1);
	        	if(strName.startsWith("\""))
	        		strName= strName.substring(1, strName.length()-1);
	        	Ontology curOnt = new Ontology();
	        	strName = strName.trim();
	        	strAcry = strAcry.trim();
	        	curOnt.name = strName;
	        	curOnt.acrononym= strAcry;
	        	ontologyList.acrononym.add(strAcry);
	        	ontologyList.name.add(strName);
	        	ontologyList.nameToAcronym.put(strName,  strAcry);
	        	ontologyList.acronymToName.put(strAcry, strName );
	        	ontologyList.ontologyMapping.put(strAcry, curOnt);
	        }
	        Collections.sort(ontologyList.name);	
	        oWriter.exec(ontologyList);
		}
		else{
			OntologyWriter oWriter = new OntologyWriter();
			System.out.println(new Date(System.currentTimeMillis()) + ": loading NCBO information coding information.");
	        oWriter.readFromJar(ontologyList);
			System.out.println(new Date(System.currentTimeMillis()) + ": NCBO information coding information loaded; Ontology size: " + ontologyList.name.size());
		}
	}
	
	public JsonNode getAnnotations(String inputText) {
		return getAnnotations(inputText, null, null);
	}
	/**
	 * Get annotations using NCBO web service 
	 * @param inputText
	 * @param selectedOntologies
	 * @param selectedUMLS
	 * @return
	 */
	public JsonNode getAnnotations(String inputText, 
			ArrayList<String> selectedOntologies,
			ArrayList<String> selectedUMLS) {
		if (inputText.isEmpty())
			return null;
		String strInput = inputText;
		JSONObject result = null;
		JsonNode annotationResults = null;
		strInput = GeneralUtility.encodeURIComponent(strInput);
		String strOnt = "&ontologies=";
		String strSemanticTypes= "&semantic_types=";
		String strURL = "";
		int thresholdCounter = 5;
		int maxHold = 10;
		try {
			if(selectedOntologies == null )
				strOnt = "";
			else
			{
				if(selectedOntologies.size() == 0 )
					strOnt = "";
				else
				{
					for(int i = 0; i < selectedOntologies.size(); i++)
					{
						String curOnt = selectedOntologies.get(i);
						if(i < selectedOntologies.size()-1 )
							strOnt = strOnt + curOnt + ",";
						else
							strOnt = strOnt + curOnt;
					}
				}
			}
			if(selectedUMLS == null )
				strSemanticTypes= "";
			else
			{
				if(selectedUMLS.size() == 0 )
					strSemanticTypes = "";
				else
				{
					for(int i = 0; i < selectedUMLS.size(); i++)
					{
						String curST = selectedUMLS.get(i);
						if(i < selectedUMLS.size()-1 )
							strSemanticTypes= strSemanticTypes+ curST + ",";
						else
							strSemanticTypes= strSemanticTypes+ curST;
					}
				}
			}
			strURL = API_URL + "annotator?apikey=" + API_KEY ;
			strURL = strURL + "&longest_only=" + "false"; 
			strURL = strURL + "&whole_word_only=" + "true"; 
			strURL = strURL + "&include_mappings=" + "false"; 
			strURL = strURL + "&exclude_numbers=" + "false"; 
			strURL = strURL + "&exclude_synonyms=" + "false"; 
			strURL = strURL + strOnt +  strSemanticTypes + "&text=" + strInput.trim() ;
			
			url = new URL(strURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			
			if (conn.getResponseCode() != 200) {
				System.out.println("No response from NCBO server, please try again later: error code " + conn.getResponseCode());
				return null;
			}
			InputStream inputStream = conn.getInputStream();
			String strLen= conn.getHeaderField("Content-Length");
			int contentLen = Integer.parseInt(strLen);
			int hold= 0; 
			int len = inputStream.available();
			int prevLen = 0;
			
			int maxCounter = 0; 

			int len1;
			List<byte[]> listFinal = new ArrayList<byte[]>();
			byte[] buffer = new byte[4096];
			byte[] allBytes = null; 
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (-1 != (len1 = inputStream.read(buffer))) {
//				bos.write(buffer, 0, len);
			  byte[] curBuffer = Arrays.copyOfRange(buffer, 0, len1);
			  listFinal.add(curBuffer);
			  allBytes = ArrayUtils.addAll(allBytes, curBuffer);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));

			InputStream inputStream2 = null;
			String line = "";
			StringBuilder responseStrBuilder = new StringBuilder();
			inputStream2 = new ByteArrayInputStream(allBytes);
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(inputStream2));
			String temp = null;
//			System.out.println(contentLen);
			while((temp = bfReader.readLine()) != null){
			    responseStrBuilder.append(temp);
			}
			inputStream2.close();
			inputStream.close();
			String strResponse = responseStrBuilder.toString();
			annotationResults = jsonToNode(strResponse);
			conn.disconnect();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return annotationResults;

	}
	
	/**
	 * Get perferred term for a classid
	 * @param strLink
	 * @param ontology
	 * @param classID
	 * @return
	 */
	public String getPT(String strLink, String ontology, String classID) {
		if (classID.equals(""))
			return "";
		
		String strURL = PURL_URL + ontology+ "/" + classID ;
		String strURL2 = "";
		strURL = API_URL +"ontologies/" + ontology + "/classes/" +  GeneralUtility.encodeURIComponent(strURL) + "?apikey=" + API_KEY ;
		strURL2 = strLink + "?apikey=" + API_KEY ;
		String strPT = "";
		
		int thresholdCounter = 5000;
		try {
			url = new URL(strURL2);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				url = new URL(strURL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
//				System.out.println("PT is not found for " + strLink );
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != 200) {
					return "";
				}
			}
			InputStream inputStream = conn.getInputStream();
			String strLen= conn.getHeaderField("Content-Length");
			int contentLen = Integer.parseInt(strLen);
			int maxCounter = 0; 
			while(inputStream.available()!= contentLen && maxCounter < thresholdCounter){
				Thread.sleep(1);
				maxCounter++;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));

			String line = "";
			StringBuilder responseStrBuilder = new StringBuilder();
			while((line =  br.readLine()) != null){

			    responseStrBuilder.append(line);
			}
			inputStream.close();
			String finalStr = responseStrBuilder.toString();
			if(finalStr.length() <= 2)
				return null;
			finalStr = finalStr.substring(1, finalStr.length()-1);
	        if(finalStr.contains("prefLabel\":"))
	        {
	        	int index = finalStr.indexOf("prefLabel\":");
	        	finalStr= finalStr.substring(index + "prefLabel\":".length());
	        	finalStr = finalStr.substring(finalStr.indexOf("\"")+1);
	        	strPT = finalStr.substring(0, finalStr.indexOf("\""));
	        }
	        
			conn.disconnect();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return strPT;
	}
	/**
	 * Entry function to call NCBO web service
	 * @param rawText
	 * @param selectedOntologies
	 * @param selectedUMLS
	 * @param range
	 * @return
	 */
	public HashMap<String, ArrayList<Term>> processText(String rawText, 
			ArrayList<String> selectedOntologies,
			ArrayList<String> selectedUMLS, int range){
		
		boolean isETHER = false, isMetaMap = false; 
		if(selectedOntologies != null)
		{
			if(selectedOntologies.contains("ETHER")){
				selectedOntologies.remove("ETHER");
				isETHER = true;
			}

			if(selectedOntologies.contains("MetaMapLite")){
				isMetaMap = true;
				selectedOntologies.remove("MetaMapLite");
			}
			if(selectedOntologies.size() == 0 )
			{
				if(isETHER)
					selectedOntologies.add("ETHER");
				if(isMetaMap)
					selectedOntologies.add("MetaMapLite");
				return null;
			}
		}
//		JsonNode results = null;
		if(selectedOntologies.contains("NCBO"))
		{	
			selectedOntologies.remove("NCBO");
			results = getAnnotations(rawText, selectedOntologies, selectedUMLS);
			selectedOntologies.add("NCBO");
		}
		else
			results = getAnnotations(rawText, selectedOntologies, selectedUMLS);
		if(results == null ){
			if(isETHER)
				selectedOntologies.add("ETHER");
			if(isMetaMap)
				selectedOntologies.add("MetaMapLite");			
			return null;
		}

		String context = "";
		String strSep = ". ";
		HashMap<String, ArrayList<Term>> termMapping = new HashMap<String, ArrayList<Term>> ();
		int shift = 0; 
		for (JsonNode res : results) {
			JsonNode ant1 = res.get("annotations");
			for (JsonNode annotated : ant1) {
				Term term = new Term();
				String from = (String)annotated.get("from").toString();
				String to= (String)annotated.get("to").toString();
				String matchType = (String)annotated.get("matchType").toString();
				if(matchType.startsWith("\""))
					matchType = matchType.substring(1, matchType.length()-1);
				String text = (String)annotated.get("text").toString();
				if(text.startsWith("\""))
					text = text.substring(1, text.length()-1);
				String strCls = text;
				String strPT = strCls;
				String strOntology = "";
				
				JsonNode ontologyNode = res.get("annotatedClass");
				String classID = "";
				classID = (String)ontologyNode.get("@id").toString();
				if(classID!=null){
					if(classID.contains("/"))
						classID = classID.substring(classID.lastIndexOf("/")+1, classID.length()-1);
				}

				strOntology = (String)ontologyNode.get("links").get("ontology").toString();
				if(strOntology.startsWith("\""))
					strOntology = strOntology.substring(1, strOntology.length()-1);

				String strLink = (String)ontologyNode.get("links").get("self").toString().replaceAll("\"", "");
				strOntology = strOntology.substring(strOntology.lastIndexOf("/")+1);
				
				strCls = getPT(strLink, strOntology, classID);
				if(strCls.equals(""))
					strCls = text;
				
				term.word = text;
				term.ontology = strOntology;
				term.strType = matchType;
				term.from = Integer.parseInt(from);
				term.to = Integer.parseInt(to);
				if(term.from > rawText.length()-1)
					System.out.println("Out of boundary!");
				term.context = GeneralUtility.getContext(rawText, strCls, term.from, term.to, range);
				term.strCls= strCls;
				String key= text + "-" + term.ontology + "-" + term.from;
				ArrayList<Term> curTerm = null;
				if(termMapping.containsKey(key))
				{
					curTerm = termMapping.get(key);
				}
				else
					curTerm = new ArrayList<Term>();
				
				curTerm.add(term);
				termMapping.put(text + "-" + term.ontology + "-" + term.from,  curTerm);
			}
		}
		if(isETHER)
			selectedOntologies.add("ETHER");
		if(isMetaMap)
			selectedOntologies.add("MetaMapLite");
		return termMapping;

	}
	
	/**
	 * Generate a url and web service connection
	 * @param urlToGet
	 * @return
	 */
    private static String get(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
        	
            url = new URL(urlToGet );
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
//            System.out.println(result);
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
    /**
     * Convert content to a json node
     * @param json
     * @return
     */
    private static JsonNode jsonToNode(String json) {
        JsonNode root = null;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }    
    
    public void printResult(HashMap<String, ArrayList<Term>> codedResults) {
		for (Entry<String, ArrayList<Term>> entry : codedResults.entrySet()) {
			String word = entry.getKey();
			ArrayList<Term> terms = entry.getValue();

			System.out.println("\n");

			for (Term term : terms) {
				int from = term.from;
				int to = term.to;
				String cls = term.strCls;
				String type = term.strType;
				String context = term.context;
				String ontology = term.ontology;
				System.out.println(word + ": " + word + "; from " + from + " to " + to);
				System.out.println(context);
				System.out.println("Ontology: " + ontology + "; " + "Type = " + type + "; Class = " + cls + "\n");
			}
		}
	}

	public static void main(String args[]) {
		String sampleText = "My son received Smallpox vaccination on 1/11/2001 in right deltoid. 10 days after he developed increased left arm pain and pleuritic substernal chest pain. "
				+ "4/12/06 transferred to hospital with chest pain";
		NCBO_REST application = new NCBO_REST(false);
		
		ArrayList<String> selectedOntologies = null, selectedUMLS = null;
		int range = 10;
		System.out.println("No ontology or UMLS semantic type is specified, all ontologies and semantic types are used...");
		
		selectedOntologies = new ArrayList<String>();
		selectedUMLS = new ArrayList<String>();
		selectedOntologies.add("MEDDRA");
		HashMap<String, ArrayList<Term>> codedResults= null;
		for(int i = 0; i < 100; i++){
			codedResults = application.processText(sampleText, selectedOntologies, selectedUMLS, range);
			codedResults.clear();
		}
		codedResults = application.processText(sampleText, selectedOntologies, selectedUMLS, range);
		for(Entry<String, ArrayList<Term>> entry:codedResults.entrySet()){
			ArrayList<Term> terms = entry.getValue();
			for(Term term: terms){
				int from = term.from;
				int to = term.to;
				String cls = term.strCls;
				String type = term.strType;
				String context = term.context;
				String ontology = term.ontology;
			}
		}
		selectedOntologies.remove("MEDDRA");
		selectedOntologies.add("SNOMEDCT");
		selectedUMLS.add("T079");//Temporal concept
		codedResults = application.processText(sampleText, selectedOntologies, selectedUMLS, range);
		for(Entry<String, ArrayList<Term>> entry:codedResults.entrySet()){
			String word = entry.getKey();
			ArrayList<Term> terms = entry.getValue();
			for(Term term: terms){
				int from = term.from;
				int to = term.to;
				String cls = term.strCls;
				String type = term.strType;
				String context = term.context;
				String ontology = term.ontology;
			}
		}

		selectedOntologies.clear();
		selectedUMLS.clear();
		codedResults = application.processText(sampleText, selectedOntologies, selectedUMLS, range);
		for(Entry<String, ArrayList<Term>> entry:codedResults.entrySet()){
			String word = entry.getKey();
			ArrayList<Term> terms = entry.getValue();
			for(Term term: terms){
				int from = term.from;
				int to = term.to;
				String cls = term.strCls;
				String type = term.strType;
				String context = term.context;
				String ontology = term.ontology;
			}
		}
	}
}