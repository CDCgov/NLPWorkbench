package gov.hhs.fda.srs.annotation.vaers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
//import org.apache.uima.tutorial.RoomNumber;
import gov.hhs.fda.srs.annotation.vaers.*;
//import gov.fda.nlp.SimplePathology.*;

public class VaersFeatureAnnotator extends JCasAnnotator_ImplBase {

	  // create regular expression pattern for "high grade"
	  private Pattern oneMHx = 
	        Pattern.compile("significant for dizziness/fainting spells");
	  private Pattern vacchines = 
		        Pattern.compile("VAX\\d");
	  private Pattern somePrimaryDiagnosis = 
		        Pattern.compile("myocarditis|died");
	  private Pattern some2ndDiagnosis = 
		        Pattern.compile("shortness of breath|chest pain");
	  private Pattern someSymptoms = 
		        Pattern.compile("vaccination|performed: an electrocardiogram|levels were measure|found");
	  private Pattern oneCOD = 
		        Pattern.compile("heart failure");

	  private Pattern tempDates = 
		        Pattern.compile("10 March 2010|30 March 2010|02 April 2010");
	  private Pattern tempRelatives = 
		        Pattern.compile("Ten days after|Day 20");
	  private Pattern someAnchors = 
		        Pattern.compile("vaccination");


	  private Pattern someTimeTimeRelation = 
		        Pattern.compile("Ten days after");

	  
	  
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		String docText = aJCas.getDocumentText();
//	    System.out.println("text to annotate:\n" + docText + "\n");
	    
	    // search for 'high grade'
	    Matcher matcher = oneMHx.matcher(docText);
	    int pos = 0;
	    while (matcher.find(pos)) { //for Symptoms
	    	CategoryMedicalHistory annotation = new CategoryMedicalHistory(aJCas);
		      annotation.setBegin(matcher.start());
		      annotation.setEnd(matcher.end());
		      annotation.addToIndexes();
		      pos = matcher.end();
	    }

	    matcher = vacchines.matcher(docText);
	    pos = 0;
	    int counter = 0;
	    while (matcher.find(pos)) { //for CategoryMedicalProduct & Vaccine
	    	//	    	CategoryMedicalProduct catVaccine = new CategoryMedicalProduct(aJCas);
	    	Vaccine annotation = new Vaccine(aJCas);

	    	//	    	catVaccine = new CategoryMedicalProduct(aJCas);
	    	int start = matcher.start();
	    	int end = matcher.end();
	    	annotation.setBegin(start);
	    	//		      catVaccine.setBegin(start); //specifically set for double-tagging annotation
	    	annotation.setEnd(end);
	    	//		      catVaccine.setEnd(end); //specifically set for double-tagging annotation
	    	annotation.addToIndexes();
	    	//		      catVaccine.addToIndexes(); //specifically set for double-tagging annotation
	    	pos = matcher.end();

	    	FeatureTimeRelation anno2 = new FeatureTimeRelation(aJCas); //try to add FeatureTimeRelation
	    	if (counter==0) {
	    		anno2.setCID("f2");
	    		counter++;
	    	} else {
	    		anno2.setCID("f3");
	    	}
	    	anno2.setTID("t1");
	    	anno2.setRelation("OVERLAP");
	    	anno2.setBegin(start);
	    	anno2.setEnd(end);
	    	anno2.addToIndexes();
	    }
	    
	    matcher = somePrimaryDiagnosis.matcher(docText);
	    pos = 0;
	    while (matcher.find(pos)) {
	    	PrimaryDiagnosis annotation = new PrimaryDiagnosis(aJCas);
	    	annotation.setBegin(matcher.start());
	    	annotation.setEnd(matcher.end());
	    	annotation.addToIndexes();
	    	pos = matcher.end();
	    }
	    
	    matcher = someSymptoms.matcher(docText);
	    pos = 0;
	    while (matcher.find(pos)) {
	    	Symptom annotation = new Symptom(aJCas);
	    	annotation.setBegin(matcher.start());
	    	annotation.setEnd(matcher.end());
	    	annotation.addToIndexes();
	    	pos = matcher.end();
	    }

	    matcher = some2ndDiagnosis.matcher(docText);
	    pos = 0;
	    while (matcher.find(pos)) {
	    	SecondLevelDiagnosis annotation = new SecondLevelDiagnosis(aJCas);
	    	annotation.setBegin(matcher.start());
	    	annotation.setEnd(matcher.end());
	    	annotation.addToIndexes();
	    	pos = matcher.end();
	    }
	    
	    matcher = oneCOD.matcher(docText);
	    pos = 0;
	    while (matcher.find(pos)) {
	    	CategoryCauseOfDeath annotation = new CategoryCauseOfDeath(aJCas);
	    	annotation.setBegin(matcher.start());
	    	annotation.setEnd(matcher.end());
	    	annotation.addToIndexes();
	    	pos = matcher.end();
	    }
	    
	    
	    matcher = tempDates.matcher(docText);
	    pos = 0;
	    while (matcher.find(pos)) {
	    	Date annotation = new Date(aJCas);
	    	annotation.setBegin(matcher.start());
	    	annotation.setEnd(matcher.end());
	    	annotation.addToIndexes();
	    	pos = matcher.end();
	    }
	    
	    matcher = tempRelatives.matcher(docText);
	    pos = 0;
	    while (matcher.find(pos)) {
	    	Relative annotation = new Relative(aJCas);
	    	annotation.setBegin(matcher.start());
	    	annotation.setEnd(matcher.end());
	    	annotation.addToIndexes();
	    	pos = matcher.end();
	    }

	    matcher = someAnchors.matcher(docText);
	    pos = 0;
	    counter =0;
	    while (matcher.find(pos)) {
	    	Anchor annotation = new Anchor(aJCas);
	    	if (counter==0) {
	    		annotation.setDate("2010-03-20");
	    		counter++;
	    	} else {
	    		annotation.setDate("2010-03-30");
	    	}
	    	annotation.setBegin(matcher.start());
	    	annotation.setEnd(matcher.end());
	    	annotation.addToIndexes();
	    	pos = matcher.end();
	    }
	    
	    
	    matcher = someTimeTimeRelation.matcher(docText);
	    pos = 0;
	    while (matcher.find(pos)) {
	    	TimeTimeRelation annotation = new TimeTimeRelation(aJCas);
	    	annotation.setTID1("t2");
	    	annotation.setTID2("t3");
	    	annotation.setRelation("AFTER");
	    	annotation.setBegin(matcher.start());
	    	annotation.setEnd(matcher.end());
	    	annotation.addToIndexes();
	    	pos = matcher.end();
	    }
	    
	}
}
