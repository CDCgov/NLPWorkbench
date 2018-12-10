package gov.hhs.aspe.nlp.SafetySurveillance.MedTarsqi;

import gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP.MedTARSQI;
import org.lappsgrid.metadata.ServiceMetadataBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 *
 */
public class ClinicalService extends MedTarsqiService
{
	protected void initMetadata(ServiceMetadataBuilder builder) {
		builder.name("MedTarsqi Clinical Service");
	}

	protected String getResult(MedTARSQI tarsqi) throws TransformerException, ParserConfigurationException
	{
		return tarsqi.getClinicalXMLContent();
	}
}
