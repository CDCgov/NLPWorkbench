package gov.hhs.aspe.nlp.SafetySurveillance.ClearTK;

import gov.cdc.lappsgrid.utils.Utils;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import gov.hhs.aspe.nlp.SafetySurveillance.CNER.RunClearTK;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasCopier;
import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.metadata.ServiceMetadataBuilder;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lappsgrid.discriminator.Discriminators.*;

import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class ClearTKService implements WebService
{
	private final Logger logger = LoggerFactory.getLogger(ClearTKService.class);

	private String metadata;
	private Exception cachedError;
	private RunClearTK service;

	public ClearTKService()
	{
		service = new RunClearTK();
	}

	public String execute(String json)
	{
		logger.info("Executing json. Size: {}", json.length());
		Data data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();

		if (Uri.ERROR.equals(discriminator)) {
			logger.info("received error input");
			return json;
		}


		if (cachedError != null) {
			logger.info("Cached error {}", cachedError.getMessage());
			return new Data(Uri.ERROR, cachedError.getMessage()).asPrettyJson();
		}

		// If we have a CAS as input we need to preserve any existing annotations.
		CAS cas = null;

		// The text to be annotated.
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
			return new Data(Uri.ERROR, "Unsupported discriminator type: " + discriminator).asPrettyJson();
		}
		String model = null;
		Object param = data.getParameter("model");
		if (param == null) {
			model = Models.CRF;
		}
		else {
			model = param.toString();
			if (! (Models.CRF.equals(model) || Models.SVM.equals(model)) ) {
				return new Data(Uri.ERROR, "Invalid model name: " + model).asJson();
			}
		}

		String xml = null;
		try
		{
			xml = null;
			JCas jCas = service.runClearTKModel(text, model);

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
				.description("ClearTK Annotator")
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
