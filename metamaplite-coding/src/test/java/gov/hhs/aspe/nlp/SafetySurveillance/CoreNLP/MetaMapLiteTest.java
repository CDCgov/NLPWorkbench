package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.TermToXML;

public class MetaMapLiteTest {
	
	String sampleText = "My son received Smallpox vaccination on 4/21/2006 in left deltoid. 12 days after he developed increased left arm pain and pleuritic substernal chest pain. 5/11/06 transferred to hospital with chest pain, right arm pain. Final dx of acute myopericarditis, serum reaction, allergic reaction, anemia, abnormal reaction to vaccine. Medical records from previous hospitalization obtained on 5/14/06 showed PMHx of Stevens-Johnson syndrome; family hx reveals patient's father had myocardial infarction.";
	
	@Test
	public void test() throws IllegalAccessException, InvocationTargetException, IOException, Exception {

		// the program read the value from MetaMapLiteAPI.Properties without
		// having any parameter to the constructor
		MetaMap metaMap = new MetaMap();

		// alternatively, a specific path can be set as the parameter to the
		// constructor.
		// MetaMap metaMap = new MetaMap("C:/software/public_mm_lite/");

		HashMap<String, ArrayList<Term>> codedResults = metaMap.processText(sampleText, null, null, 0);

		// to show some results in the console
		metaMap.printResult(codedResults);

		// to initialize a utility class of "TermToXML", whose function of
		// "generateXMLString()" accepts the returned HashMap data structure and
		// generates the XML content conforming to the VAERS Data Type System in
		// a String.
		TermToXML ttx = new TermToXML(codedResults, sampleText);
		String resultXMLStr = ttx.generateXMLString(codedResults);
		Assert.assertTrue(resultXMLStr.contains("vaers"));

//		System.out.println(resultXMLStr);

	}

}
