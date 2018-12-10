package gov.hhs.aspe.nlp.SafetySurveillance.ncbo;


import gov.cdc.lappsgrid.utils.Utils;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP.NCBO_REST;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.TermToXML;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasCopier;
import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.metadata.ServiceMetadataBuilder;
import org.lappsgrid.serialization.*;
import org.lappsgrid.serialization.lif.Container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.*;

/**
 *
 */
public class NcboService implements WebService
{
	private String metadata;

	private NCBO_REST service;

	public NcboService()
	{
		service = new NCBO_REST();
	}

	@Override
	public String execute(String json)
	{
		Data data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();
		if (Uri.ERROR.equals(discriminator)) {
			return json;
		}
		CAS existing = null;
		String text = null;
		if (Uri.TEXT.equals(discriminator)) {
			text = data.getPayload().toString();
		}
		else if (Uri.LIF.equals(discriminator)) {
			Container container = new Container((Map) data.getPayload());
			text = container.getText();
		}
		else if (Uri.UIMA.equals(discriminator)) {
			String xml = data.getPayload().toString();
			try
			{
				existing = Utils.loadCas(xml);
				text = existing.getDocumentText();
			}
			catch (UtilsException e)
			{
				return new Data(Uri.ERROR, e.getMessage()).asPrettyJson();
			}

		}
		else {
			return new Data(Uri.ERROR, "Unsupported discriminator type: " + discriminator).asPrettyJson();
		}

		System.out.println("getting ontologies");
		ArrayList<String> ontologies = getStringList(data.getParameter("ontologies"));
		System.out.println("getting umls");
		ArrayList<String> umls = getStringList(data.getParameter("umls"));
		int range = 10;
		Object object = data.getParameter("range");
		if (object != null) {
			if (object instanceof Integer) {
				System.out.println("range specified");
				range = (Integer) object;
			}
			else {
				return new Data(Uri.ERROR, "Invalid range parameter. Expected an Integer.").asPrettyJson();
			}
		}

		HashMap<String, ArrayList<Term>> codedResults = service.processText(text, ontologies, umls, range);

		TermToXML ttx = new TermToXML(codedResults, text);
		String xml = ttx.generateXMLString(codedResults);
		CAS cas = null;
		if (existing != null) try {
			cas = Utils.loadCas(xml);
			CasCopier.copyCas(cas, existing, false);
			xml = Utils.toXcas(cas);
		}
		catch (UtilsException | IOException e) {
			return new Data(Uri.ERROR, e.getMessage()).asPrettyJson();
		}

		return new Data(Uri.UIMA, xml).asJson();
	}

	private ArrayList<String> getStringList(Object object) {
		if (object instanceof List) {
			System.out.println("found a list");
			return (ArrayList<String>) object;
		}
		System.out.println("returning an empty list.");
		return new ArrayList<String>();
	}

	@Override
	public String getMetadata()
	{
		if (metadata == null) {
			init();
		}
		return metadata;
	}

	private synchronized void init() {
		if (metadata != null) {
			return;
		}

		ServiceMetadata md = new ServiceMetadataBuilder()
				.name(NcboService.class.getName())
				.description("NCBO BioPortal Annotator")
				.version(Version.getVersion())
				.produceFormat(Uri.UIMA)
				.requireFormats(Uri.TEXT, Uri.LIF, Uri.UIMA)
				.build();
		metadata = new Data(Uri.META, md).asPrettyJson();
	}

}
