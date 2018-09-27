package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasIOUtils;
import org.apache.uima.util.InvalidXMLException;
import org.junit.Assert;
import org.junit.Test;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.TermToCAS;

/**
 * This class tests the execution of the NCBO-Coding API. The API is
 * 
 * @author Wei.Chen & Geoffrey Zhang
 *
 */
public class NcboCodingTest {

	String sampleText = "My son received Smallpox vaccination on 4/21/2006 in left deltoid. 12 days after he developed increased left arm pain and pleuritic substernal chest pain. 5/11/06 transferred to hospital with chest pain, right arm pain. Final dx of acute myopericarditis, serum reaction, allergic reaction, anemia, abnormal reaction to vaccine. Medical records from previous hospitalization obtained on 5/14/06 showed PMHx of Stevens-Johnson syndrome; family hx reveals patient's father had myocardial infarction.";
	NCBO_REST application = new NCBO_REST(false);
	int range = 10;

	ArrayList<String> selectedOntologies = new ArrayList<String>();
	ArrayList<String> selectedUMLS = new ArrayList<String>();
	HashMap<String, ArrayList<Term>> codedResults = null;

	@Test
	public void test() throws IOException, InvalidXMLException, ResourceInitializationException, CASException
	{

		selectedOntologies.add("MEDDRA");

		// As the first step, application.processText() is REQUIRED to be
		// executed.
		// After this step, the results are parsed and stored in a HashMap<String, ArrayList<Term>> for easy access. 
		
		// Importantly, a user needs to specify the "ontology" and "UMLS Types" if needed.
		// The ontology used below (as an example) is "MEDRA".
		// Please see this page for detailed info:
		// 	https://bioportal.bioontology.org/annotator
		
		codedResults = application.processText(sampleText, selectedOntologies, selectedUMLS, range);

		// Now, the returned result as a JsonNode can be obtained as follows.

//		JsonNode jn = application.getResults();

		CAS cas1 = null;
		
		TermToCAS ttc = new TermToCAS(codedResults, cas1, sampleText);
		cas1 = ttc.generateCASFromTerm();
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		CasIOUtils.save(cas1, bao, SerialFormat.XMI);
		
		System.out.println(bao.toString());
		
		Assert.assertTrue(bao.toString().contains("vaers"));
		
/*
		// just for simply getting a visual.
		application.printResult(codedResults);
		
		TermToXML ttx = new TermToXML(codedResults, sampleText);
		String resultXMLStr = ttx.generateXMLString(codedResults);
		System.out.println(resultXMLStr);

		URL url = this.getClass().getResource("/VaersTypeSystem.xml");
		if (url == null) {
			throw new IOException("Could not find type system");
		}
		XMLInputSource inputSource = new XMLInputSource(url);
		XMLParser parser = UIMAFramework.getXMLParser();
		TypeSystemDescription tsd = parser.parseTypeSystemDescription(inputSource);
		CAS cas = CasCreationUtils.createCas(tsd, null, null, null);
		InputStream stream = new ByteArrayInputStream(resultXMLStr.getBytes());
		CasIOUtils.load(stream, cas);
*/

	}

}
