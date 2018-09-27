package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.w3c.dom.NamedNodeMap;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;
import gov.hhs.fda.srs.annotation.vaers.Age;
import gov.hhs.fda.srs.annotation.vaers.Anchor;
import gov.hhs.fda.srs.annotation.vaers.CategoryCauseOfDeath;
import gov.hhs.fda.srs.annotation.vaers.CategoryFamilyHistory;
import gov.hhs.fda.srs.annotation.vaers.CategoryMedicalHistory;
import gov.hhs.fda.srs.annotation.vaers.CategoryStatus;
import gov.hhs.fda.srs.annotation.vaers.ClinicalFeature;
import gov.hhs.fda.srs.annotation.vaers.Date;
import gov.hhs.fda.srs.annotation.vaers.Drug;
import gov.hhs.fda.srs.annotation.vaers.Duration;
import gov.hhs.fda.srs.annotation.vaers.Frequency;
import gov.hhs.fda.srs.annotation.vaers.Other;
import gov.hhs.fda.srs.annotation.vaers.PrimaryDiagnosis;
import gov.hhs.fda.srs.annotation.vaers.Relative;
import gov.hhs.fda.srs.annotation.vaers.RuleOutDiagnosis;
import gov.hhs.fda.srs.annotation.vaers.SecondLevelDiagnosis;
import gov.hhs.fda.srs.annotation.vaers.Symptom;
import gov.hhs.fda.srs.annotation.vaers.TemporalFeature;
import gov.hhs.fda.srs.annotation.vaers.TemporalRelation;
import gov.hhs.fda.srs.annotation.vaers.Time;
import gov.hhs.fda.srs.annotation.vaers.Vaccine;
import gov.hhs.fda.srs.annotation.vaers.Weekday;

public class TermToCAS {
	ArrayList<Term> terms = null;
	CAS cas = null;
	String rawText;

	public TermToCAS(ArrayList<Term> terms, CAS cas, String rawText) {
		this.terms = terms;
		this.cas = cas;
		this.rawText = rawText;
	}

	public CAS generateCASFromETHERTerms() throws CASException, IOException, InvalidXMLException, ResourceInitializationException {
		if (cas == null) {
			URL url = this.getClass().getClassLoader().getResource("vaers.xml");
			if (url == null) {
				throw new IOException("Could not find type system");
			}
			XMLInputSource inputSource = new XMLInputSource(url);
			XMLParser parser = UIMAFramework.getXMLParser();
			TypeSystemDescription tsd = parser.parseTypeSystemDescription(inputSource);
			cas = CasCreationUtils.createCas(tsd, null, null, null);
		}
		
		JCas jcas = cas.getJCas();
		for (Term term : terms) {
			
			if (term.eventTemporalAssociation == 0) {
				// The term is a clinical feature
				
				ClinicalFeature cf = null;
				if (term.mentionType.equals("DIAGNOSIS")) {
					cf = new PrimaryDiagnosis(jcas);
				}
				else if (term.mentionType.equals("RULEOUT")) {
					cf = new RuleOutDiagnosis(jcas);
				}
				else if (term.mentionType.equals("SECOND_LEVEL_DIAGNOSIS")) {
					cf = new SecondLevelDiagnosis(jcas);
				}
				else if (term.mentionType.equals("SYMPTOM")) {
					cf = new Symptom(jcas);
				}
				else if (term.mentionType.equals("DRUG")) {
					cf = new Drug(jcas);
				}
				else if (term.mentionType.equals("VACCINE")) {
					cf = new Vaccine(jcas);
				}
				else if (term.mentionType.equals("MEDICAL_HISTORY")) {
					cf = new CategoryMedicalHistory(jcas);
				}
				else if (term.mentionType.equals("FAMILY_HISTORY")) {
					cf = new CategoryFamilyHistory(jcas);
				}
				else if (term.mentionType.equals("CAUSE_OF_DEATH")) {
					cf = new CategoryCauseOfDeath(jcas);
				}
				else if (term.mentionType.equals("STATUS")) {
					cf = new CategoryStatus(jcas);
				}
				else {
					System.out.println("Warning: unrecognized Term.mentionType: \"" + term.mentionType + "\"");
					continue;
				}
				
				int begin =  term.from;
				int end = term.to;
				String text = rawText.substring(begin, end);
				
				cf.setBegin(begin);
				cf.setEnd(end);
				cf.setText(text);
				cf.setID(term.id);
				jcas.addFsToIndexes(cf);
				
			}
			else if (term.eventTemporalAssociation == 1) {
				// the term is a temporal feature
				
				TemporalFeature tf = null;
				if (term.mentionType.equals("DATE")) {
					tf = new Date(jcas);
				}
				else if (term.mentionType.equals("RELATIVE")) {
					tf = new Relative(jcas);
				}
				else if (term.mentionType.equals("DURATION")) {
					tf = new Duration(jcas);
				}
				else if (term.mentionType.equals("WEEKDAY")) {
					tf = new Weekday(jcas);
				}
				else if (term.mentionType.equals("FREQUENCY")) {
					tf = new Frequency(jcas);
				}
				else if (term.mentionType.equals("AGE")) {
					tf = new Age(jcas);
				}
				else if (term.mentionType.equals("TIME")) {
					tf = new Time(jcas);
				}
				else if (term.mentionType.equals("ANCHOR")) {
					tf = new Anchor(jcas);
				}
				else if (term.mentionType.equals("OTHER")) {
					tf = new Other(jcas);
				}
				else {
					System.out.println("Warning: unrecognized Term.mentionType: \"" + term.mentionType + "\"");
					continue;
				}
				
				int begin =  term.from;
				int end = term.to;
				String text = rawText.substring(begin, end);
				
				tf.setBegin(begin);
				tf.setEnd(end);
				tf.setText(text);
				tf.setID(term.id);
				if (term.date != null && term.date.length() > 0) {
					tf.setDate(term.date);
				}
				jcas.addFsToIndexes(tf);
				
			}
			else {
				// the term is an association between a clinical feature and a temporal feature
				
				TemporalRelation tr = new TemporalRelation(jcas);
				tr.setAtype(term.tlink.linkType);
				tr.setCoreID(term.tlink.coreID);
				tr.setCoreText(term.tlink.coreText);
				tr.setRefID(term.tlink.refID);
				tr.setRefText(term.tlink.refText);
				
				jcas.addFsToIndexes(tr);
				
			}
		}
		return jcas.getCas();

	}
	
}
