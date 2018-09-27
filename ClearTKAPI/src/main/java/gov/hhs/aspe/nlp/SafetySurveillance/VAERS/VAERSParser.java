package gov.hhs.aspe.nlp.SafetySurveillance.VAERS;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.TLink;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.Term;

/**
 * 
 * This class parses the annotated VAERS report generated by ETHER. 
 */

public class VAERSParser  {

  Element root;

	private HashMap<String, String> vaersFeatureTypeMapping = new HashMap<String, String>();
	private HashMap<String, String> vaersFeatureTypeMapping2 = new HashMap<String, String>();
	private HashMap<String, String> vaersFeatureTypeMapping3 = new HashMap<String, String>();
//	private String[] vaersFeatureTypesShort = new String[]{"SYM", "VAX", "pDx", "sDx", 
//			"CoD","Tx", "FHx", 
//			"MHx", "R/O","Lab","Status"};
	private String[] vaersFeatureTypesShort = new String[]{"SYMPTOM", "VACCINE", "DIAGNOSIS", "SECOND_LEVEL_DIAGNOSIS", 
			"CAUSE_OF_DEATH", "DRUG", "FAMILY_HISTORY", 
			"MEDICAL_HISTORY", "Rule Out","Lab","Status",
			"Duration", "Frequency", "Relative"};
	private String[] vaersFeatureTypes = new String[]{"Symptom", "Vaccine", "PrimaryDiagnosis", "SecondLevelDiagnosis", 
			"CategoryCauseOfDeath", "Drug", "CategoryFamilyHistory", 
			"CategoryMedicalHistory", "RO","Lab","Status",
			"DUR", "FRQ", "REL"};
	private String[] vaersFeatureTypes2 = new String[]{"SYM", "VAX", "pDx", "sDx", 
			"COD", "Tx", "FHx", 
			"MHx", "R/O","Lab","Status",
			"Duration", "Frequency", "Relative"};
	private String[] vaersFeatureTypes3 = new String[]{"SYM", "VAX", "pDx", "sDx", 
			"COD", "Tx", "FHx", 
			"MHx", "RULE_OUT","Lab","Status",
			"Duration", "Frequency", "Relative"};
	Iterator<?> features;
	
  Set<String> posLabels;
  InputStreamReader inputStreamReader;

  XMLOutputter outputter;
  VAERSDataStructure vaersData = null;

  public VAERSDataStructure getVaersData() {
	return vaersData;
}

public void setVaersData(VAERSDataStructure vaersData) {
	this.vaersData = vaersData;
}

public VAERSParser(String fileName ) throws IOException, JDOMException {

	File xmlFile = new File(fileName);
	//    outputter = new XMLOutputter();
	inputStreamReader = new InputStreamReader(new FileInputStream(xmlFile));
	vaersData = parse(fileName);
  }
public VAERSParser(File xmlFile) throws IOException, JDOMException {

//	File xmlFile = new File(fileName);
	//    outputter = new XMLOutputter();
	inputStreamReader = new InputStreamReader(new FileInputStream(xmlFile));
	vaersData = parse(xmlFile.getName());
  }

public VAERSParser(String fileName, String string) throws IOException, JDOMException {

//    outputter = new XMLOutputter();
	inputStreamReader = new InputStreamReader(IOUtils.toInputStream(string));
	vaersData = parse(fileName);
  }

  public VAERSDataStructure parse(String fileName) throws JDOMException, IOException {
	    
//	  System.out.println(xmlFile.getName());
	  if(vaersFeatureTypeMapping.size() == 0){
			for(int i = 0; i < vaersFeatureTypesShort.length; i++){
//				vaersFeatureTypeMapping.put(vaersFeatureTypesShort[i], vaersFeatureTypes[i] );
				vaersFeatureTypeMapping.put(vaersFeatureTypes[i].toUpperCase(), vaersFeatureTypesShort[i].toUpperCase() );
			}
			for(int i = 0; i < vaersFeatureTypesShort.length; i++){
//				vaersFeatureTypeMapping.put(vaersFeatureTypesShort[i], vaersFeatureTypes[i] );
				vaersFeatureTypeMapping2.put(vaersFeatureTypes2[i].toUpperCase(), vaersFeatureTypesShort[i].toUpperCase() );
			}
			for(int i = 0; i < vaersFeatureTypesShort.length; i++){
//				vaersFeatureTypeMapping.put(vaersFeatureTypesShort[i], vaersFeatureTypes[i] );
				vaersFeatureTypeMapping3.put(vaersFeatureTypes3[i].toUpperCase(), vaersFeatureTypesShort[i].toUpperCase() );
			}
	  }
	  SAXBuilder builder = new SAXBuilder();
	  builder.setDTDHandler(null);
	  Reader reader = new BufferedReader(inputStreamReader);	  
	  root = builder.build(reader).getRootElement();
	  
	  List<Element> elements = root.getChildren();
	  if(elements == null)
		  return null;

	  VAERSDataStructure vaersData = new VAERSDataStructure();
	  vaersData.setFileName(fileName);
	  int size = elements.size();
	  ArrayList<Term> elementsVAERS = vaersData.getVaersElements();
	  if(elementsVAERS == null )
		  elementsVAERS = new ArrayList<Term>();

	  Namespace namespace = Namespace.getNamespace("http://www.omg.org/XMI");
	  if(vaersData.getVaersElements() == null)
		  for(int i = 0; i < size; i++)
		  {
			  Element e = elements.get(i);
			  if(e.getName()== null )
				  continue;
			  
			  if(e.getName().equals("Sofa"))
			  {
				  String fullText = (e.getAttributeValue("sofaString"));
//				  if(fullText.toLowerCase().contains("dx'd"))
//				  {
//					  fullText = fullText.replaceAll("dx'd", "diagnosed");
//							  
//				  }
				  vaersData.setRawText(fullText);
			  }else{
				  boolean dataExists = false;
				  Term elementVAERS = new Term();
				  elementVAERS.id = e.getAttributeValue("id", namespace);
				  String sofa =e.getAttributeValue("sofa"); 
				  if(sofa !=null)
					  elementVAERS.sofaID = Integer.parseInt(sofa);

				  if(e.getAttributeValue("text")!=null  || e.getAttributeValue("refText")!=null  ||
					  e.getAttributeValue("CoreText")!=null  )
				  {
					  dataExists = true;
				  }
				  else
					  continue;
				  
				  String strBegin = e.getAttributeValue("begin");
				  if(strBegin !=null)
					  elementVAERS.from = Integer.parseInt(strBegin);
				  
				  String strEnd= e.getAttributeValue("end");

				  if(strEnd!=null)
					  elementVAERS.to = Integer.parseInt(strEnd);
				  elementVAERS.word = e.getAttributeValue("text");
				  
				  String type = e.getAttributeValue("type");
				  if(type==null)
				  {
					  type = e.getName();
				  }
				  if(type!=null){
					  type = type.replaceAll("/",  ""); //handle "/" in vaers type, such as "R/O"
					  try{
						  elementVAERS.mentionType  = type;
					  }
					  catch(Exception ev)
					  {
//						  System.out.println(type + " is not within VAERS_TYPE");
					  }
							  
					  
//					  if(type.toLowerCase().contains("rule") || type.toLowerCase().contains("ro") ||
//					  	type.toLowerCase().contains("r/o"))
//					  {
//						  System.out.println(type);
//					  }
					  if(vaersFeatureTypeMapping.containsKey(type.toUpperCase()))
						  type = vaersFeatureTypeMapping.get(type.toUpperCase());
					  if(vaersFeatureTypeMapping2.containsKey(type.toUpperCase()))
						  type = vaersFeatureTypeMapping2.get(type.toUpperCase());
					  if(vaersFeatureTypeMapping3.containsKey(type.toUpperCase()))
						  type = vaersFeatureTypeMapping3.get(type.toUpperCase());
					  if(type.toLowerCase().contains("rule") || type.toLowerCase().contains("ro") ||
							  type.toLowerCase().contains("r/o") ||
							  type.toLowerCase().contains("ro"))
					  {
						  System.out.println(type);
						  type = "ruleout";
					  }
//					  if(type.toLowerCase().equals("ro"))
//						  System.out.println("type");
					  elementVAERS.mentionType = type.toUpperCase();
				  }
				  else{
					  System.out.println("Type does not exist");
				  }
				  String pt = e.getAttributeValue("preferred_term");
				  if(pt!=null)
					  elementVAERS.PT = pt;
				  String ontology = e.getAttributeValue("ontology");
				  if(ontology!=null)
					  elementVAERS.ontology = ontology;
				  String strCls= e.getAttributeValue("class");
				  if(strCls!=null)
					  elementVAERS.strCls= strCls;

				  String temporalValue= e.getAttributeValue("Date");
				  if(temporalValue !=null)
				  {
					  elementVAERS.date = temporalValue;
				  }
				  temporalValue= e.getAttributeValue("date");
				  if(temporalValue !=null)
				  {
					  elementVAERS.date = temporalValue;
				  }
				  if(elementVAERS.id .startsWith("TL"))
					  elementVAERS.eventTemporalAssociation = 2;
				  else if(elementVAERS.id .startsWith("T"))
				  {
					  elementVAERS.eventTemporalAssociation = 1;
				  }
				  else 
					  elementVAERS.eventTemporalAssociation = 0;
				  
				  String relationValue= e.getAttributeValue("Relation");
				  if(relationValue !=null)
				  {
					  elementVAERS.eventTemporalAssociation = 2;
					  if(!elementVAERS.id.startsWith("TL"))
						  elementVAERS.id = "TL" + elementVAERS.id;
				  }

				  if(elementVAERS.eventTemporalAssociation == 2)
				  {
					  TLink tlink = new TLink();
					  tlink.id = elementVAERS.id;
					  elementVAERS.tlink = tlink;
					  if(relationValue!= null)
						  tlink.linkType = relationValue;
					  String refID = e.getAttributeValue("refID");
					  if(refID!=null )
						  tlink.refID = refID;
					  else{
						  refID = e.getAttributeValue("RID");
						  if(refID!=null )
							  tlink.refID = refID;
					  }
					  tlink.linkType = type;
					  if(relationValue !=null)
						  tlink.linkType = relationValue;
					  
					  String refText = e.getAttributeValue("refText");
					  if(refText!=null)
						  tlink.refText = refText;

					  if(temporalValue!=null)
						  tlink.date = temporalValue;
					  
					  String coreText = e.getAttributeValue("coreText");
					  if(coreText==null)
						  coreText = e.getAttributeValue("CoreText");

					  tlink.coreText = coreText;
					  if(coreText!=null)
						  tlink.coreText = coreText;
					  
					  String coreID = e.getAttributeValue("coreID");
					  if(coreID == null)
						  coreID = e.getAttributeValue("CID");
						  
					  tlink.coreID = coreID;
				  }
				  elementsVAERS.add(elementVAERS);
			  }
			  
		  }
	  vaersData.setVaersElements(elementsVAERS);

	  return vaersData;
  }
  
  public ArrayList<Term> convertAllToTerms(){
	  ArrayList<Term> terms = new ArrayList<Term>();
	  try {
		  int span = 10;

		  String report = vaersData.getRawText();
		  HashMap<String, Term> fromTo = new HashMap<String, Term>();
		  ArrayList<Term> elementsSER = vaersData.getVaersElements();
		  Term term = null;
		  for(Term element:vaersData.getVaersElements()){
			  	if(element.eventTemporalAssociation ==2 ){
			  		TLink tlink= element.tlink;
			  		String eventID = tlink.coreID;
			  		String temporalID = tlink.refID;
//			  		if(eventID.startsWith("E"))
//			  			eventID = eventID.substring(1);
//
//			  		if(temporalID.startsWith("T"))
//			  		{
//			  			temporalID = temporalID.substring(1);
//			  		}
			  		Term elementEvent = GeneralUtility.getElementByID(elementsSER, eventID);
			  		Term elementTemporal = GeneralUtility.getElementByID(elementsSER, temporalID);

			  		if(elementEvent == null || elementTemporal == null )
			  		{
			  			System.out.println("Error: VAERS element data is not found!");
			  			continue;
			  		}
					term = element.copy();

			  		term.word = tlink.coreText;
			  		tlink.fromText = elementEvent.from;
			  		tlink.toText = elementEvent.to;
			  		tlink.fromTemporal= elementTemporal.from;
			  		tlink.toTemporal = elementTemporal.to;
			  		term.from = tlink.fromText;
			  		tlink.refText = elementTemporal.word;
			  		tlink.contextEvent = GeneralUtility.getContext(report,  tlink.coreText, tlink.fromText, tlink.toText, span);
			  		tlink.contextTemporal= GeneralUtility.getContext(report,  tlink.refText, tlink.fromTemporal, tlink.toTemporal, span);
			  		term.tlink = tlink;
			  		term.eventTemporalAssociation = element.eventTemporalAssociation;

			  		terms.add(term);			  		
			  	}
			  	else
			  	{
			  		String curFT = element.from + "-" + element.to;

			  		if(fromTo.containsKey(curFT)){
			  			term = fromTo.get(curFT);
			  			int loc = terms.indexOf(term);
			  			term.ontology = GeneralUtility.joinTwoStrings(term.ontology, element.ontology);
			  			term.strCls = GeneralUtility.joinTwoStrings(term.strCls, element.strCls);
			  			term.strType =GeneralUtility.joinTwoStrings(term.strType, element.PT);
				  		term.eventTemporalAssociation = element.eventTemporalAssociation;

			  			terms.set(loc, term);
			  		}
			  		else
			  		{
			  			term = element.copy();
//			  			if(term.word == null)
//			  				System.out.println(report);
//			  			if(term.word.contains("liva"))
//			  				System.out.println(report);
//			  			System.out.println(report);
//			  			System.out.println(term.word);
//			  			System.out.println(term.from + " - " + term.to);
			  			term.context = GeneralUtility.getContext(report,  term.word, term.from, term.to, span);
			  			if(element.mentionType.equals("True"))
			  				term.mentionType = "MED";
			  			else
			  				term.mentionType = element.mentionType;
			  			terms.add(term);
			  		}
			  		fromTo.put(curFT, term);
			  		term.eventTemporalAssociation = element.eventTemporalAssociation;
			  	}
		  }
	  }
	  catch (Exception e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  
	  return terms;
  }
  public ArrayList<Term> convertToTerms(){
	  ArrayList<Term> terms = new ArrayList<Term>();
	  try {
		  int span = 10;

		  String report = vaersData.getRawText();
		  HashMap<String, Term> fromTo = new HashMap<String, Term>();
		  for(Term element:vaersData.getVaersElements()){
			  	if(element.eventTemporalAssociation !=2 )
			  	{
				  	String curFT = element.from + "-" + element.to;
				  	Term term = null;
				  	if(fromTo.containsKey(curFT)){
				  		term = fromTo.get(curFT);
				  		int loc = terms.indexOf(term);
				  		term.ontology = GeneralUtility.joinTwoStrings(term.ontology, element.ontology);
				  		term.strCls = GeneralUtility.joinTwoStrings(term.strCls, element.strCls);
				  		term.strType =GeneralUtility.joinTwoStrings(term.strType, element.PT);
					  	
					  	terms.set(loc, term);
				  	}
				  	else
				  	{
				  		term = new Term();
					  	term.word = element.word;
					  	term.from = element.from;
					  	term.to = element.to;
//				  		if(term.word == null)
//				  			System.out.println(report);
//					  	System.out.println(report);
//					  	System.out.println(term.word);
//					  	System.out.println(term.from + " - " + term.to);
					  	term.context = GeneralUtility.getContext(report,  term.word, term.from, term.to, span);
					  	term.ontology = element.ontology;
					  	term.date = element.date;
					  	term.strCls = element.strCls;
					  	term.strType = element.PT;
					  	if(element.mentionType.equals("True"))
					  		term.mentionType = "MED";
					  	else
					  		term.mentionType = element.mentionType;
					  	terms.add(term);
				  	}
				  	fromTo.put(curFT, term);			  		
			  	}
			  	else{
			  		
			  	}
			  	
			  }
	  }
	  catch (Exception e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  return terms;
  }
  public ArrayList<Term> convertToRelationTerms(File serFile, File temporalFile){
	  if(!serFile.exists() || !temporalFile.exists())
		  return null;
	  
	  ArrayList<Term> terms = new ArrayList<Term>();
	  try {
		  int span = 10;
		  String report = vaersData.getRawText();
		  VAERSParser parserSER = null;
		  parserSER = new VAERSParser(serFile);
		  ArrayList<Term> elementsSER = parserSER.getVaersData().getVaersElements();
		  VAERSParser parserTemporal= null;
		  parserTemporal = new VAERSParser(temporalFile);
		  ArrayList<Term> elementsTemporal= parserTemporal.getVaersData().getVaersElements();
		  
		  for(Term element:vaersData.getVaersElements()){
			  TLink tlink= element.tlink;
			  String eventID = tlink.coreID;
			  String temporalID = tlink.refID;
			  if(eventID.startsWith("E"))
				  eventID = eventID.substring(1);
			  
			  if(temporalID.startsWith("T"))
			  {
				  temporalID = temporalID.substring(1);
			  }
			  Term elementEvent = GeneralUtility.getElementByID(elementsSER, eventID);
			  Term elementTemporal = GeneralUtility.getElementByID(elementsTemporal, temporalID);
			  
			  if(elementEvent == null || elementTemporal == null )
			  {
				  System.out.println("Error: VAERS element data is not found!");
				  continue;
			  }
			  Term term = null;
			  term = element.copy();
//			  term = new Term();
			  term.word = tlink.coreText;
			  tlink.fromText = elementEvent.from;
			  tlink.toText = elementEvent.to;
			  tlink.fromTemporal= elementTemporal.from;
			  tlink.toTemporal = elementTemporal.to;
			  term.from = tlink.fromText;
			  tlink.refText = elementTemporal.word;
			  tlink.contextEvent = GeneralUtility.getContext(report,  tlink.coreText, tlink.fromText, tlink.toText, span);
			  tlink.contextTemporal= GeneralUtility.getContext(report,  tlink.refText, tlink.fromTemporal, tlink.toTemporal, span);
			  term.tlink = tlink;
			  terms.add(term);
		  }
	  }
	  catch (Exception e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  return terms;
  }


  public static void main(String[] args) {
	  try {
		  System.out.print("loading VAERS data...");
		  //      String xmlFileName = args[0];
		  String xmlFileName = "data/vaers/150083-1.xml";
		  VAERSParser parser = new VAERSParser(xmlFileName);
		  System.out.println("done.");
	  } catch (Exception e) {
		  e.printStackTrace();
	  }

  }
  
  public ArrayList<Term> getEventTemporalAssociationElement(int eventTemporalAssociation)
  {
	  return vaersData.getVaersElementsByType(eventTemporalAssociation);
  }
}