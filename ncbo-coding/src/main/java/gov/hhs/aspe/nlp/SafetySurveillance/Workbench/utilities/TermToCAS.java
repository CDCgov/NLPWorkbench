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
import gov.hhs.fda.srs.annotation.vaers.ClinicalFeature;

public class TermToCAS {
	HashMap<String, ArrayList<Term>> codedResults = null;
	CAS cas = null;
	String rawText;

	public TermToCAS(HashMap<String, ArrayList<Term>> codedResults, CAS cas, String rawText) {
		this.codedResults = codedResults;
		this.cas = cas;
		this.rawText = rawText;
	}

	public CAS generateCASFromTerm() throws CASException, IOException, InvalidXMLException, ResourceInitializationException {
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
		for (Entry<String, ArrayList<Term>> entry : codedResults.entrySet()) {
			String word = entry.getKey();
			ArrayList<Term> terms = entry.getValue();
			for (Term term : terms) {
				int begin =  term.from-1;
				int end = term.to;
				String text = rawText.substring(begin, end);
				String preferred_term = term.strCls;
				ClinicalFeature cf = new ClinicalFeature(jcas);
				cf.setBegin(begin);
				cf.setEnd(end);
				cf.setText(text);
				cf.setPreferred_term(preferred_term);
				jcas.addFsToIndexes(cf);
				
			}
		}
		return jcas.getCas();

	}
	
}
