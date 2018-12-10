package gov.hhs.aspe.nlp.SafetySurveillance.MedTarsqi;

import gov.cdc.lappsgrid.utils.error.UtilsException;
import gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP.MedTARSQI;
import org.apache.uima.cas.CAS;
import gov.cdc.lappsgrid.utils.Utils;

import org.apache.uima.util.CasCopier;
import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.ServiceMetadataBuilder;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.*;

/**
 *
 */
public abstract class MedTarsqiService implements WebService
{
	private static final Logger logger = LoggerFactory.getLogger(MedTarsqiService.class);

	private String metadata;

	protected abstract String getResult(MedTARSQI tarsqi) throws TransformerException, ParserConfigurationException;
	protected abstract void initMetadata(ServiceMetadataBuilder builder);

	@Override
	public String execute(String json)
	{
		Data data = Serializer.parse(json);
		String discriminator = data.getDiscriminator();
		if (Uri.ERROR.equals(discriminator)) {
			return json;
		}

		CAS cas = null;
		String text = null;
		if (Uri.TEXT.equals(discriminator)) {
			text = data.getPayload().toString();
		}
		else if (Uri.LIF.equals(discriminator)) {
			Container container = new Container((Map)data.getPayload());
			text = container.getText();
		}
		else if (Uri.UIMA.equals(discriminator)) {
			try
			{
				cas = Utils.loadCas(data.getPayload().toString());
				text = cas.getDocumentText();
			}
			catch (UtilsException e)
			{
				logger.error("Unable to deserialize CAS.", e);
				return new Data(Uri.ERROR, e.getMessage()).asPrettyJson();
			}
		}
		logger.info("Annotating text. Size: {}", text.length());

		MedTARSQI tarsqi = null;
		try
		{
			tarsqi = new MedTARSQI(text);
		}
		catch (IOException | InterruptedException | ParserConfigurationException | SAXException e)
		{
			logger.error("Unable to process document", e);
			return new Data(Uri.ERROR, e.getMessage()).asPrettyJson();
		}

		String xml = null;
		try {
			xml = getResult(tarsqi);
		}
		catch (TransformerException | ParserConfigurationException e) {
			logger.error("Unable to get the result", e);
			return new Data(Uri.ERROR, e.getMessage()).asPrettyJson();
		}

		if (cas != null) try
		{
			CasCopier.copyCas(Utils.loadCas(xml), cas, false);
			xml = Utils.toXcas(cas);
		}
		catch (UtilsException | IOException e) {
			logger.error("Unable to create CAS from XML", e);
			return new Data(Uri.ERROR, e.getMessage()).asPrettyJson();
		}
		return new Data(Uri.UIMA, xml).asJson();
	}

	@Override
	public String getMetadata()
	{
		if (metadata == null) {
			initMetadata();
		}
		return metadata;
	}

	private synchronized void initMetadata() {
		if (metadata != null) {
			return;
		}
		String home = System.getenv("TTK_ROOT");
		if (home == null) {
			home = "UNSET";
		}
		ServiceMetadataBuilder builder = new ServiceMetadataBuilder()
				.description(home)
				.vendor("http://aspe.hhs.gov")
				.version(Version.getVersion())
				.requireLanguage("en")
				.requireFormats(Uri.TEXT, Uri.LIF, Uri.UIMA)
				.license(Uri.APACHE2)
				.allow(Uri.ALL)
				.produceFormat(Uri.UIMA);

		initMetadata(builder);
		metadata = new Data(Uri.META, builder.build()).asPrettyJson();
	}
}
