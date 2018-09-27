package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import org.junit.Assert;
import org.junit.Test;

public class JUnitTestCTakesTemporal {

	String s1 = "Information has been received on 06 Dec 2006 from a physician concerning a 5 year old female who on 29 Nov 2006 was vaccinated with MMR II 0.5ml, IME lot 651429/03/86R. There was no concomitant medications. On 04 Dec 2006, the girl complained of whole body discomfort with intolerable itch. It appeared red swelling in her neck, face, oxter and fold inguen. Also there were some little white pustules which had merged into one piece and spread along. The girl was taken to clinic and prescribed with Cetirizine, 5mg, OD, Cefuroxin sodium, 0.25 BID, antiscorbic acid, 0.1 QD and Mupirocin for external use. On 13 Dec 2006, new information was received from the physician. It was confirmed that the girl experienced rash on 03 Dec 2006 instead of 04 Dec 2006. Meanwhile, her face was flushed and pustules appeared in her neck and oxter. On 05 Dec 2006, the girl developed fever (details unknown). On 06 Dec 2006, the girl was hospitalized with the diagnosis of drug eruption. Physical examination revealed her body temperature was 38.5. Her face was flushed with pustules in her neck,, fem intern and greater lip of pudendum. Her throat was in congestion and there was not enlargement in her tonsils. Blood examination showed white blood cell count 21.72 and neutrophil count 81.4. During hospitalization, she was placed on therapy with Rocephin, Clarityne and Calcium gluconate (Detailed regimen unknown). At the time of reporting, rash was disappearing and desquamation. White blood cell count decreased to 16 and neutrophil count decreased to 65. On 13 Dec 2006, the girl was discharged from hospitalization while she was recovering from drug eruption. The reporter considered drug eruption was definitely related to MMR II. Additional information has been requested.";
	String s2 = "Vaccine given to left arm per client on 11/2/15. She began having itching on 11/3 to left arm and chest. Developed rash on 11/8 to (L) arm, (L) breast and chest, 2-3 spots to center of back. Most spots on breast. Itching/burning worse today. No blisters.";

	@Test
	public void test() {

		CTakesTemporal ct = new CTakesTemporal();
		
		ct.processDocument(s1);
		String result = ct.getResultInPrettyPrint();
		Assert.assertTrue(result.contains("Event"));
		Assert.assertTrue(result.contains("Timex"));
		Assert.assertTrue(result.contains("TLINKS"));
		
		ct.processDocument(s2);
		result = ct.getResultInPrettyPrint();
		Assert.assertTrue(result.contains("Event"));
		Assert.assertTrue(result.contains("Timex"));
		Assert.assertTrue(result.contains("TLINKS"));
		
	}

}
