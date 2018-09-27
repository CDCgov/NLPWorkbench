package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Temporal.MedTARSQIResultFileReader;

/**
 * This class is the wrapper class that allows a developer to input a raw text
 * in String and obtain clinical/temporal/relation results in a String compliant
 * to the VAERS Data Type System.
 * 
 * @author Wei.Chen
 *
 */
public class MedTARSQI {

	String rawText = null;
	MedTARSQIResultFileReader ttkResultReaderWriter = null;
	TTKWrapper tw = null;
	String xmlResultContent = null;

	/**
	 * The constructor
	 * 
	 * @param rawText
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public MedTARSQI(String rawText) throws IOException, InterruptedException, ParserConfigurationException, SAXException
			 {
		this.rawText = rawText;

		this.tw = new TTKWrapper();
		this.xmlResultContent = this.tw.extract(rawText);
		this.ttkResultReaderWriter = new MedTARSQIResultFileReader(this.xmlResultContent);
	}

	/**
	 * The function returns the clinical result in a String compliant to the
	 * VAERS Data Type System.
	 * 
	 * @return
	 * @throws TransformerException 
	 * @throws ParserConfigurationException 
	 */
	public String getClinicalXMLContent() throws ParserConfigurationException, TransformerException  {
		return ttkResultReaderWriter.generateVaersXMLFromMedTARSQI(ttkResultReaderWriter.nlEvent, 1);
	}

	/**
	 * The function returns the temporal entity result in a String compliant to the
	 * VAERS Data Type System.
	 * 
	 * @return
	 * @throws TransformerException 
	 * @throws ParserConfigurationException 
	 */
	public String getTemporalXMLContent() throws ParserConfigurationException, TransformerException  {
		return ttkResultReaderWriter.generateVaersXMLFromMedTARSQI(ttkResultReaderWriter.nlTIMEX3, 2);
	}

	/**
	 * The function returns the relation result in a String compliant to the
	 * VAERS Data Type System.
	 * 
	 * @return
	 * @throws TransformerException 
	 * @throws ParserConfigurationException 
	 */
	public String getRelationXMLContent() throws ParserConfigurationException, TransformerException  {
		return ttkResultReaderWriter.generateVaersXMLFromMedTARSQI(ttkResultReaderWriter.nlTLINK, 3);
	}

}
