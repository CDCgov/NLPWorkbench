package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import gov.cdc.lappsgrid.utils.Utils;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasCopier;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.metadata.ServiceMetadataBuilder;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * The LAPPS Grid SOAP service
 */
public class CTakesTemporalService implements WebService
{
	private static final Logger logger = LoggerFactory.getLogger(CTakesTemporalService.class);

	/**
	 * Maintain a static final instance of the worker so that the
	 * CTakesTemporal service is only initialized once.
	 */
	private static final CTakesTemporalWorker worker = new CTakesTemporalWorker();

	/**
	 * JSON for the metadata uses lazy initialization and will be stored
	 * here when generated.
	 */
	private static String metadata = null;

	public CTakesTemporalService()
	{

	}

	public String execute(String json)
	{
		logger.info("Executing json. Size: {}", json.length());
		Data data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();

		if (Discriminators.Uri.ERROR.equals(discriminator)) {
			logger.info("received error input");
			return json;
		}
		Exception cachedError = worker.getError();
		if (cachedError != null) {
			logger.info("Cached error {}", cachedError.getMessage());
			return new Data(Discriminators.Uri.ERROR, cachedError.getMessage()).asPrettyJson();
		}

		// If we have a CAS as input we need to preserve any existing annotations.
		CAS cas = null;

		// The text to be annotated.
		String text = null;
		if (Discriminators.Uri.TEXT.equals(discriminator)) {
			text = data.getPayload().toString();
		}
		else if (Discriminators.Uri.LIF.equals(discriminator)) {
			Container container = new Container((Map)data.getPayload());
			text = container.getText();
		}
		else if (Discriminators.Uri.UIMA.equals(discriminator)) {
			String xml = data.getPayload().toString();
			try
			{
				cas = Utils.loadCas(xml);
				text = cas.getDocumentText();
			}
			catch (UtilsException e)
			{
				return new Data(Discriminators.Uri.ERROR, e.getMessage()).asPrettyJson();
			}

		}
		else {
			return new Data(Discriminators.Uri.ERROR, "Unsupported discriminator type: " + discriminator).asPrettyJson();
		}

		JCas jCas = worker.processDocument(text);
		String xml;
		try
		{
			CAS newCas = jCas.getCas();
			if (cas != null) {
				CasCopier.copyCas(newCas, cas, false);
				newCas = cas;
			}
			xml = Utils.toXcas(newCas);
		}
		catch (IOException e)
		{
			String message = "Unable to serialize CAS to xml.\n" + e.getMessage();
			return new Data(Discriminators.Uri.ERROR, message).asPrettyJson();
		}
		return new Data(Discriminators.Uri.UIMA, xml).asJson();
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
				.description("cTakes Temporal annotation pipeline")
				.vendor("http://aspe.hhs.gov")
				.version(Version.getVersion())
				.requireFormats(Discriminators.Uri.TEXT, Discriminators.Uri.LIF, Discriminators.Uri.UIMA)
				.produceFormat(Discriminators.Uri.UIMA)
				.license(Discriminators.Uri.APACHE2)
				.allow(Discriminators.Uri.ALL);

		ServiceMetadata md = builder.build();
		metadata = new Data(Discriminators.Uri.META, md).asPrettyJson();
	}

}
