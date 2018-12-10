package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * The CTakesWorker class manages an instance of the CTakesClincal class.
 * <p>
 *     The worker class is responsible for loading the UMLS properties file
 *     anc provided <tt>synchronized</tt> access to the <tt>processDocument</tt>
 *     method.
 * </p>
 *
 */
public class CTakesWorker
{
	private static final Logger logger = LoggerFactory.getLogger(CTakesWorker.class);

	/**
	 * The actual cTakes pipeline.
	 */
	private CTakesClinical ctakes;

	private Exception cachedError;

	public CTakesWorker()
	{
		logger.info("Initializing the cTakes worker");
		initializeUmls();
		try
		{
			ctakes = new CTakesClinical();
		}
		catch (ResourceInitializationException | MalformedURLException e)
		{
			logger.error("Unable to initialize cTakes.", e);
			cachedError = e;
		}

	}

	public Exception getError() {
		return cachedError;
	}

	public synchronized JCas processDocument(String text) {
		return ctakes.processDocument(text);
	}

	protected void initializeUmls() {
		File ini = new File("/usr/local/clew/umls.properties");
		if (!ini.exists()) {
			ini = new File("/etc/clew/umls.properties");
			if (!ini.exists()) {
				initFromEnv();
				return;
			}
		}
		Properties props = new Properties();
		try
		{
			props.load(new FileReader(ini));
			System.setProperty("umlsUser", props.getProperty("umlsUser"));
			System.setProperty("umlsPass", props.getProperty("umlsPass"));
			System.setProperty("ctakes.umlsuser", props.getProperty("umlsUser"));
			System.setProperty("ctakes.umlspw", props.getProperty("umlsPass"));
		}
		catch (IOException e)
		{
			logger.error(e.getMessage());
			initFromEnv();
		}
	}

	protected void initFromEnv() {
		logger.info("Initializing from environment.");
		String user = System.getenv("umlsUser");
		if (user != null) {
			System.setProperty("ctakes.umlsuser", user);
			System.setProperty("umlsUser", user);
		}
		String pw = System.getenv("umlsPass");
		if (pw != null) {
			System.setProperty("ctakes.umlspw", pw);
			System.setProperty("umlsPass", pw);
		}
	}

}
