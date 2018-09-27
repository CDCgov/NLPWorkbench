package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class MedTARSQITest {

	String s1 = "Information has been received on 06 Dec 2006 from a physician concerning a 5 year old female who on 29 Nov 2006 was vaccinated with MMR II 0.5ml, IME lot 651429/03/86R. There was no concomitant medications. On 04 Dec 2006, the girl complained of whole body discomfort with intolerable itch. It appeared red swelling in her neck, face, oxter and fold inguen. Also there were some little white pustules which had merged into one piece and spread along. The girl was taken to clinic and prescribed with Cetirizine, 5mg, OD, Cefuroxin sodium, 0.25 BID, antiscorbic acid, 0.1 QD and Mupirocin for external use. On 13 Dec 2006, new information was received from the physician. It was confirmed that the girl experienced rash on 03 Dec 2006 instead of 04 Dec 2006. Meanwhile, her face was flushed and pustules appeared in her neck and oxter. On 05 Dec 2006, the girl developed fever (details unknown). On 06 Dec 2006, the girl was hospitalized with the diagnosis of drug eruption. Physical examination revealed her body temperature was 38.5. Her face was flushed with pustules in her neck,, fem intern and greater lip of pudendum. Her throat was in congestion and there was not enlargement in her tonsils. Blood examination showed white blood cell count 21.72 and neutrophil count 81.4. During hospitalization, she was placed on therapy with Rocephin, Clarityne and Calcium gluconate (Detailed regimen unknown). At the time of reporting, rash was disappearing and desquamation. White blood cell count decreased to 16 and neutrophil count decreased to 65. On 13 Dec 2006, the girl was discharged from hospitalization while she was recovering from drug eruption. The reporter considered drug eruption was definitely related to MMR II. Additional information has been requested.";

	@Test
	public void test() throws ParserConfigurationException, TransformerException, IOException, InterruptedException, SAXException  {

		MedTARSQI medTARSQI = new MedTARSQI(s1);

		String result = medTARSQI.getClinicalXMLContent();
		Assert.assertTrue(result.contains("ClinicalFeature"));
	
//	System.out.println("1");
//	System.out.println(result);

		result = medTARSQI.getTemporalXMLContent();
		Assert.assertTrue(result.contains("Date"));
//	System.out.println("2");
//	System.out.println(result);

		result = medTARSQI.getRelationXMLContent();
		Assert.assertTrue(result.contains("Relation"));
//	System.out.println("3");
//	System.out.println(result);

	}

}
