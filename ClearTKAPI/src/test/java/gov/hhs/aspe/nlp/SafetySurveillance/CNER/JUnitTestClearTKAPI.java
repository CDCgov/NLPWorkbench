package gov.hhs.aspe.nlp.SafetySurveillance.CNER;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

import gov.hhs.aspe.nlp.SafetySurveillance.CNER.RunClearTK;
import junit.framework.TestCase;

public class JUnitTestClearTKAPI extends TestCase {

	
	@Test
	public void test() throws IllegalAccessException, InvocationTargetException, IOException, Exception {

		String report = "This report describes colitis in a female vaccinee of unspecified age who received LYMErix. This report was received as part of litigation proceedings and has not been verified by a physician or other healthcare professional. The medical history included temporomandibular joint syndrome. Concurrent conditions and medications were not provided. The vaccinee reportedly recieved LYMErix; the number of injections was not reported. In a statement of injuries, her attorney alleged that the vaccinee \"experiences joint pain and fatigue. She also suffers from colitis.\" Time to onset following immunization, treatment, and outcome of the alleged events were not reported. The adverse event \"joint pain\" does not meet serious criteria but is being submitted as an expedited report by special FDA request. Follow up 07/01/2002: \"Following the reported onset of ulcerative colitis, the vaccinee experienced iron deficiency anemia, thrush, hypokalemia, anal fissures, hypoalbuminemia, and a left adrenal mass. These events were not reported as adverse events due to vaccine administration, but were found during the course of review of the vaccinee's medical records. Therefore, they are not listed as adverse events. In May 1999, the vaccinee reportedly received LYMErix; the number of injections administered was not reported. In a statement of injuries, her attorney alleged that the vacicnee \"experiences joint pain and fatigue. She also suffers from colitis.\" The following information was obtained from medical records. On 12/12/2000, the vaccinee was seen by a physician. She reported that she had presented to the local emergency room on 12/10/2000 with a complaint of loose stool with diarrhea. Reportedly, the vaccinee's white blood cell count had been elevated. She stated that the emergency room physician had made a diagnosis of gastroenteritis and prescribed diphenoxylate/atropine and dicyclomine \"without much therapy\". Discontinued Lomotil therapy, and prescribed loporamide. The vaccine returned to the physician on 12/14/2000 with a complaint of 'chronic problem with loose bowels now with blood. On Bentyl, legs hurting. Blood is chronic as well. Temperature/chills-lower abdominal pain.\" The physician prescribed ciprofloxacin 500mg twice daily. On 12/18/2000, he prescribed Bentyl 20 mg four times daily. The vaccinee returned to the physician on 12/26/2000. She stated that her symptoms were improving, but weakness and dizziness persisted and she had sweats at night. On 12/29/2000 she had crampy abdominal pain with one to two formed stool with some blood. She reported fevers of 102 to 104 F, in the physician's office, her temperature was 101.6 F. The physician's assessment was \"persistent fever-diarrhea-weight loss-looks toxic, weak. Plan: will hospitalize.\" On 12/29/2000, the patient was admitted to the hospital. The hospital discharge summary indicated that she had experienced weight loss of approximately 10 pounds. It was also noted that she had received intravenious fluids in the emergency room on 12/10/2000. On examination, \"she was febrile. The abdomen was slightly tender in the left lower quadrant.\" The attending physician wrote, \"It was clear that she probably had a granulomatous colitis and she was treated with some intravenious antibiotics and then steriods.\" She was discharged from the hospital on 01/01/2001; discharge medications were prednisone 20 mg daily and metromidazole 500mg three times daily. Discharge diagnoses were \"1) regionalenteritis of the large intestine. 2) Fever.\" The physician completed a disability certification, stating that the vaccinee was unable to work from 12/29/2000 through 001/08/2001, due to \"Crohn's Disease-granulomatous colitis.\" Colonoscopy was performed on 01/03/2001 and showed colitis with a normal ileum. On 01/29/2001, the physician sent the vaccinee to the local emergency room \"for evaluation of colitis\". Medications at that time included prednisone, Tentyl, and Immodium.\" ";
		String modelName = "svm";
		RunClearTK runClearTK = new RunClearTK();
		JCas jCas = runClearTK.runClearTKModel(report, modelName);
		System.out.println(jCas);

//		String result = jCas.

		Collection<org.cleartk.ne.type.NamedEntityMention> annosTTR = JCasUtil.select(jCas, org.cleartk.ne.type.NamedEntityMention.class);
		System.out.println("size: " + annosTTR.size());
		Assert.assertTrue( annosTTR.size() > 0 );

		modelName = "crf";
		jCas = runClearTK.runClearTKModel(report, modelName);
		annosTTR = JCasUtil.select(jCas, org.cleartk.ne.type.NamedEntityMention.class);
		System.out.println("size: " + annosTTR.size());
		Assert.assertTrue( annosTTR.size() > 0 );

	}
}
