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
import org.jdom2.JDOMException;
import org.junit.Assert;
import org.junit.Test;

import gov.hhs.aspe.nlp.SafetySurveillance.VAERS.VAERSParser;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.TermToCAS;

public class TestETHERTemporal {

	@Test
	public void test() throws IOException, JDOMException, CASException, InvalidXMLException, ResourceInitializationException {
		
		String[] testStrings =
				new String[] {"Information has been received from a certified medical assistant referring to a patient of unknown age and gender. On 29-DEC-2015 the patient inadvertently received a dose of RECOMBIVAX HB (lot reported as L030581 expiration date: 26-JUN-2017, dose was unknown) intramuscularly in the ventrogluteal site (drug administered at inappropriate site). No adverse effects were reported. Additional information has been requested.",
						"Case received from a healthcare professional via (HA's number HIB-505) on 29 Mar 2013. A 5-month-old male patient (weight at birth: 3, 550 g), with no relevant medical history, had received simultaneously his subcutaneous third primary dose of ACT-HIB (batch number H1547, site of administration not reported), his subcutaneous third primary dose of PREVENAR (other manufacturer, batch number 12F02A, site of administration not reported), his subcutaneous second primary dose of DTP-IPV (other manufacturer, batch number 4K02A, site of administration not reported) and his first primary dose of hepatitis B vaccine (other manufacturer, batch number Y69B, route and site of administration not reported) on 11 March 2013, at 02:30 p.m. Body temperature before the vaccination was 36.0 degrees C. The patient had no history of adverse events to previous administration of vaccine or drugs. On 11 March 2013, several minutes after the vaccination, the patient slept and woke up in the evening. After waking up, the patient experienced vomiting twice. After 11:00 p.m., the mother noticed short cry of the patient and found he was experiencing cyanosis and rolling of the eyes. Emergency call was made and the patient was hospitalized on 12 March 2013. Thirty minutes after arrival, the patient's facial color was improved and he started crying. No appreciable founding in blood test performed on an unspecified date. The patient was discharged on 13 March 2013. On 13 March 2013, the patient was recovered.",
						"This is a spontaneous report from a contactable consumer and from a contactable physician. A 89-year-old male patient received PREVNAR 13 (Lot/Batch H86212), on 20Nov2014 between 8 and 9 in the morning in the right arm at single dose. Patient had anaemia from Jan2015 and ongoing, ongoing hypertension for 40 or 50 years. Concomitant medication included amlodipine for blood pressure decreased from 2013 at 10 mg daily and from Nov2014 at 5 mg daily and ongoing, Vitamin D3 orally for low Vitamin D and lack of sun at 1000 units once a day from 2015 and ongoing, lisinopril for heart orally at 40 mg, 1x/day (at night) from 2010 and ongoing and atenolol for heart at 50 mg twice a day from 1990 and ongoing, garlic 1000mg and vitamin B2 50 mg. Patient specified he was taking amlodipine in the morning and that 7 days prior to the injection, he was told to cut the amlodipine down from 10 mg daily to 5 mg daily. 50-60 years before, the patient received cholera vaccine and had poisoning and body reaction as he got a big bubble. On 10Jan2015 lab tests were done with the following results: triglycerides and glucose were good and high density lipoprotein level was a little low. On 18Jan2015 patient got rash on his legs, internal itching effect on whole body, rough skin and was retaining fluid in ankle and calves. Events \"rash\" and \"itching\" were treated with Prednisone and he took BENADRYL which helped to hold down the itch a little bit. The patient visited a physician, who told him about the possibility that the retaining fluid could be caused by his blood pressure medication. Amlodipine decreased from 10 mg to 5 mg helped to decreased the fluid level a little bit. \"Fluid retention\" as considered serious as it led to hospitalization. Outcome for \"rough skin\" and \"Fluid retention\" was not recovered; patient recovered from the other events. The reporting physician stated that the patient mentioned he got the vaccine about 2 months before the report and then had the rash 1 month later. However the physician was not sure if it was related to the vaccine and was not aware of the reported adverse events. Follow-up (30Mar2015): New information from a contactable physician includes: causality assessment detail. Follow-up(01May2015): New information from a contactable consumer includes: concomitant, vaccine and details, seriousness. Follow-up attempts completed. No further information expected.",
						"This case was reported by a consumer and described the occurrence of stomach soreness in a 41-year-old male subject who was vaccinated with Twinrix (GlaxoSmithKline) for prophylaxis.  A physician or other health care professional has not verified this report.  Previous vaccination included 1st dose of Twinrix (GlaxoSmithKline) given in August 2006.  On 11 December 2006 the subject received 2nd dose of Twinrix (unknown).  On 12 December 2006, 1 day after vaccination with Twinrix, the subject became aware of stomach soreness and went to a walk-in clinic where he was told that he had a pulled stomach muscle.  Over the next week, the soreness in the subject's stomach worsened.  He then developed numbness in his left arm and hand.  The subject went to see his family doctor.  His wife had to drive him due to the numbness in his arm.  His family physician sent him to the emergency room where he was admitted overnight for testing.  Over the next week, the numbness became worse and by this time, the subject was also experiencing numbness on the left side of the face.  The subject returned to the hospital and was referred to a neurologist.  The subject's condition worsened so rapidly that he did not make it to the scheduled neurologist appointment and he again returned to the hospital.  The subject was numb from the waist down on the left side and his left arm and hand were numb.  The subject said that his face was so distorted that his mouth drooped and his one eye was distorted and not focusing.  The subject reported that he generally has excellent vision, but during the drive to the hospital, his vision was so blurry that he could barely read the highway signs.  He had difficulty walking for the past week and upon arriving at the hospital, the subject's wife had to help the subject in with a wheelchair.  He was immediately admitted to the emergency room where specialists (Infectious Disease Specialist, Internal Medicine Specialist and a Neurologist) tested and questioned him all night.  The next day (04 January 2007), t",
						"This case was reported by a other health professional via regulatory authority and described the occurrence of hypotonia in a 2-month-old female patient who received ENGERIX B (batch number AHBVC457BA, expiry date unknown). Co-suspect products included PREVENAR 13 (batch number M20724, expiry date unknown) and PENTAVAC (batch number M2117, expiry date unknown). Concurrent medical conditions included hemangioma. Additional patient notes included The patient has an haemangioma in lip. Concomitant products included Propranolol. On 2nd March 2016, the patient received ENGERIX B (intramuscular) .5 ml, PREVENAR 13 (intramuscular) .5 ml at an unknown frequency and PENTAVAC (intramuscular) at an unknown dose and frequency. On 2nd March 2016, 2 min after receiving ENGERIX B, the patient experienced hypotonia (serious criteria disability), pallor (serious criteria disability), presyncope (serious criteria disability) and livedo reticularis (serious criteria disability). On 2nd March 2016, the outcome of the hypotonia, pallor, presyncope and livedo reticularis were recovered/resolved. The reporter considered the hypotonia, pallor, presyncope and livedo reticularis to be probably related to ENGERIX B. Additional details: The spontaneous report refers to a case sent by a nurse regarding a female infant, with 2 months of age, the showed hypotony, pale, not reactive, recovering spontaneously and staying with marbled skin, associated to the use of PREVENAR (batch M20724), ENGERIX B  (batch AHBVC457BA) and PENTAXIM (batch M2117). Referred previous events of mouth haemangioma (lip), taking Propranolol 2.5 mg for its treatment. The adverse reaction started minutes after the administration of the suspect  drugs and lasted about 3 minutes. The adverse reaction involve whole body and skin and led to the use of recovery maneuvers, having the child recovered spontaneously. There is reference to the intake of the following concomitant drug Propranolol 2.5 mg per OS, for the treatment of the haemangioma, present from birth, two days before the administration of the vaccines. Additional details: the infant took Propranolol 2.5 mg at 08:00 am, having being vaccinated at 09:20 am, in the same day. Outcome: cure.",
		};
		
		for (String s : testStrings) {
			ETHERModule etherVar = new ETHERModule();
			
			/**
			 * IMPORTANTLY, the output of processETHERTemporal() method has changed (as of 2018-08-30).
			 * It now returns the name of the ETHERNLP output file, rather than the full contents of that file.
			 */
			String resultFilename = etherVar.processETHERTemporal(s);
			
			try {
				// The following few lines will produce a CAS object containing the information from the output file.
				
				VAERSParser parser = new VAERSParser(resultFilename);
				
				//Fill in the term structure with context and other information
				ArrayList<Term> terms = parser.convertAllToTerms();
				
				CAS cas1 = null;
				
				TermToCAS ttc = new TermToCAS(terms, cas1, parser.getVaersData().getRawText());
				cas1 = ttc.generateCASFromETHERTerms();
				
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				CasIOUtils.save(cas1, bao, SerialFormat.XMI);
				
//				System.out.println(bao.toString());
				
				Assert.assertTrue(bao.toString().contains("vaers"));
				
			} finally {
				etherVar.cleanUpTemporalFiles();
			}
		}


	}

}
