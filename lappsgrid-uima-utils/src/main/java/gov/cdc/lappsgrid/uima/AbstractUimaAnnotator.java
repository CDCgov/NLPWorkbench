package gov.cdc.lappsgrid.uima;

import gov.cdc.lappsgrid.utils.Utils;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.metadata.ServiceMetadataBuilder;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;

import java.io.IOException;
import java.util.Map;

/**
 * The base class for wrapping a UIMA AnalysisEngine as a LAPPS Grid web service.
 *
 * Subclasses must:
 *
 * - call super() passing the AE to use.
 * - override the initialize method to configure the metadata that will be returned
 *   by the getMetadata() method.
 *
 * Optionally, sub-classes can also override the getCas() method if they handle other
 * discriminator types.
 */
public abstract class AbstractUimaAnnotator implements WebService
{
	// The UIMA AE to expose as a LAPPS Grid WebService.
	protected AnalysisEngine engine;

	// The LAPPS Grid JSON metadata about the service.  The JSON will be generated the
	// first time the getMetadata method is called.
	protected String metadata;

	// Any exception thrown during construction is cached and returned whenever the
	// execute method is invoked.
	protected ResourceInitializationException cachedException;

	public AbstractUimaAnnotator(AnalysisEngineDescription description)
	{
		if (description == null) {
			return;
		}

		try
		{
			this.engine = UIMAFramework.produceAnalysisEngine(description);
		}
		catch (ResourceInitializationException e) {
			this.cachedException = e;
		}
	}

	public AbstractUimaAnnotator(AnalysisEngine ae) {
		this.engine = ae;
	}

	@Override
	public String getMetadata()
	{
		if (metadata != null) {
			return metadata;
		}
		generateMetadata();
		return metadata;
	}

	@Override
	public String execute(String json)
	{
		if (cachedException != null) {
			return new Data(Discriminators.Uri.ERROR, cachedException.getMessage()).asPrettyJson();
		}

		Data data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();
		if (Discriminators.Uri.ERROR.equals(discriminator)) {
			return json;
		}

		CAS cas = null;
		try
		{
			cas = getCas(data);
		}
		catch (UtilsException | ServiceException e)
		{
			return new Data(Discriminators.Uri.ERROR, e.getMessage()).asPrettyJson();
		}

		Data result = null;
		try
		{
			SimplePipeline.runPipeline(cas, engine);
			String xml = Utils.toXml(cas);
			result = new Data<>(Discriminators.Uri.UIMA, xml);
		}
		catch (AnalysisEngineProcessException | IOException e)
		{
			result = new Data<>(Discriminators.Uri.ERROR, e.getMessage());
		}
		return result.asJson();
	}

	// Create a CAS object from the contents of the Data object.  Sub-classes can override
	// this method if they handle other discriminator types, however the three most
	// common are handled automatically.
	protected CAS getCas(Data data) throws UtilsException, ServiceException
	{
		String discriminator = data.getDiscriminator();
		CAS cas = null;
		if (Discriminators.Uri.TEXT.equals(discriminator)) {
			cas = Utils.createCas();
			cas.setDocumentText(data.getPayload().toString());
		}
		else if (Discriminators.Uri.UIMA.equals(discriminator)) {
			cas = Utils.loadCas(data.getPayload().toString());
		}
		else if (Discriminators.Uri.LIF.equals(discriminator)) {
			Container container = new Container((Map)data.getPayload());
			cas = Utils.createCas();
			cas.setDocumentText(container.getText());
			cas.setDocumentLanguage(container.getLanguage());
		}
		else {
			throw new ServiceException("Unexpected discriminator type.");
		}
		return cas;
	}

	/**
	 * A method that sub-classes can use to add metadata or modify jsonMetadata generated
	 * by the parent AbstractCTakesAnnotatorService.
	 *
	 * @param builder The builder used by the abstract parent class to generate the
	 *                metadata for the service.
	 */
	protected abstract void initialize(ServiceMetadataBuilder builder);

	private synchronized void generateMetadata() {
		if (metadata != null) {
			// Double check in case another thread generated metadata while this
			// method was being dispatched.
			return;
		}

		// Initialize the builder with defaults.
		ServiceMetadataBuilder builder = new ServiceMetadataBuilder()
				.vendor("http://aspe.hhs.gov")
				.requireFormats(Discriminators.Uri.TEXT, Discriminators.Uri.LIF, Discriminators.Uri.UIMA)
				.produceFormat(Discriminators.Uri.UIMA)
				.license(Discriminators.Uri.APACHE2)
				.allow(Discriminators.Uri.ANY);
		// Allow sub-classes to add/modify the metadata.
		initialize(builder);

		// Cache the JSON representation.
		metadata = new Data(Discriminators.Uri.META, builder.build()).asPrettyJson();
	}

}
