package gov.hhs.aspe.nlp.SafetySurveillance.MedTarsqi;

import gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP.MedTARSQI;
import org.lappsgrid.metadata.ServiceMetadataBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 *
 */
public class RelationService extends MedTarsqiService
{
	@Override
	protected String getResult(MedTARSQI tarsqi) throws TransformerException, ParserConfigurationException
	{
		return tarsqi.getRelationXMLContent();
	}

	@Override
	protected void initMetadata(ServiceMetadataBuilder builder)
	{
		builder.name("MedTarsqi Relation Service");
	}
}
