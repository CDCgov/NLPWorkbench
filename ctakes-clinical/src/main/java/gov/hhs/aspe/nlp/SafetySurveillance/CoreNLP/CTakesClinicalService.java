package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import gov.cdc.lappsgrid.utils.Utils;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCopier;
import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.metadata.ServiceMetadataBuilder;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;

import static org.lappsgrid.discriminator.Discriminators.*;
/**
 * The LAPPS Grid SOAP service.
 */
public class CTakesClinicalService implements WebService
{
	private static final Logger logger = LoggerFactory.getLogger(CTakesClinicalService.class);

	/**
	 * Maintain a static final instance of the worker so that the
	 * CTakesClinical service is only initialized once.
	 */
	private static final CTakesWorker worker = new CTakesWorker();

	/**
	 * JSON for the metadata uses lazy initialization and will be stored
	 * here when generated.
	 */
	private String metadata;

	public CTakesClinicalService()
	{
		logger.info("Initializing cTakes Clinical");
	}

	public String execute(String json)
	{
		System.out.println("Executing json");
		Data data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();

		if (Uri.ERROR.equals(discriminator)) {
			logger.info("received error input");
			return json;
		}
		Exception cachedError = worker.getError();
		if (cachedError != null) {
			logger.error("Cached error", cachedError);
			return new Data(Uri.ERROR, cachedError.getMessage()).asPrettyJson();
		}

		// If the input was a CAS object then we need to maintain it
		// and preserve any annotations it contains.
		CAS cas = null;

		// Get the raw text from the input.
		String text = null;
		if (Uri.TEXT.equals(discriminator)) {
			text = data.getPayload().toString();
		}
		else if (Uri.LIF.equals(discriminator)) {
			Container container = new Container((Map)data.getPayload());
			text = container.getText();
		}
		else if (Uri.UIMA.equals(discriminator)) {
			String xml = data.getPayload().toString();
			try
			{
				cas = Utils.loadCas(xml);
				text = cas.getDocumentText();
			}
			catch (UtilsException e)
			{
				return new Data(Uri.ERROR, e.getMessage()).asPrettyJson();
			}

		}
		else {
			// The input was not an allowed input type.
			return new Data(Uri.ERROR, "Unsupported discriminator type: " + discriminator).asPrettyJson();
		}

		JCas jCas = worker.processDocument(text);
		String xml;
		try
		{
			CAS newCas = jCas.getCas();
			if (cas != null) {
				// Copy any existing annotations from the input CAS.
				CasCopier.copyCas(newCas, cas, false);
				newCas = cas;
			}
			xml = Utils.toXcas(newCas);
		}
		catch (IOException e)
		{
			String message = "Unable to serialize CAS to xml.\n" + e.getMessage();
			return new Data(Uri.ERROR, message).asPrettyJson();
		}
		return new Data(Uri.UIMA, xml).asJson();
	}

	public String getMetadata()
	{
		if (metadata == null) {
			generateMetadata();
		}
		return metadata;
	}

	protected synchronized void generateMetadata() {
		if (metadata != null) {
			return;
		}
		ServiceMetadataBuilder builder = new ServiceMetadataBuilder()
				.name(this.getClass().getName())
				.description("cTakes Clinical annotation pipeline")
				.vendor("http://aspe.hhs.gov")
				.version(Version.getVersion())
				.requireFormats(Uri.TEXT, Uri.LIF, Uri.UIMA)
				.produceFormat(Uri.UIMA)
				.license(Uri.APACHE2)
				.allow(Uri.ALL);

		ServiceMetadata md = builder.build();
		metadata = new Data(Uri.META, md).asPrettyJson();
	}

}
