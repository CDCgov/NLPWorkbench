package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import org.apache.uima.resource.ResourceInitializationException;

import java.net.MalformedURLException;

/**
 *
 */
public class Main
{
	static final String input = "A 33 year-old man with past medical history significant for dizziness/fainting spells received the following vaccines on 10 March 2010: VAX1 (lot number not reported); and VAX2 (lot number not reported either). Ten days after vaccination, he developed shortness of breath and chest pain and was subsequently diagnosed with myocarditis. On Day 20 (30 March 2010) post vaccination, the following tests were performed: an electrocardiogram which was reported to be normal and troponin I levels were measured and found to be 12.3 ng/ml (abnormal). Patient died on 02 April 2010. COD: heart failure. List of documents held by sender: None.";

	public Main()
	{

	}

	public static void main(String[] args) throws MalformedURLException, ResourceInitializationException
	{
		set("umlsUser", "ctakes.umlsuser");
		set("umlsPass", "ctakes.umlspw");
//		set("jdbcUrl");
		CTakesClinical clinical = new CTakesClinical();
		clinical.processDocument(input);
		String result = clinical.getResultInPrettyPrint();
		System.out.println(result);
	}

	private static void set(String key, String altKey) {
		String value = System.getenv(key);
		if (value != null) {
			System.setProperty(key, value);
			System.setProperty(altKey, value);
			System.out.printf("%s=%s\n", key, value);
		}
		else {
			System.out.printf("Key %s not set\n", key);
		}
	}

}
