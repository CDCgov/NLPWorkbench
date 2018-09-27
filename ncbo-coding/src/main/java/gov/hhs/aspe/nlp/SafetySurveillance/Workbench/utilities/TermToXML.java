package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;

/**
 * This class accepts a HashMa<String, ArrayList<Term>> data structure and
 * generate an XML file specified by the file path, for G. :-)
 * 
 * @author Wei.Chen
 *
 */
public class TermToXML {

	HashMap<String, ArrayList<Term>> codedResults = null;
	String rawText = null;
	File xmlFile = null;

	/**
	 * the contructor to accept the parameters
	 * 
	 * @param codedResults
	 *            the coded results from NCBO in a HashMap format
	 * @param rawText
	 *            The raw text - the textual clinical note
	 * @param xmlFile
	 *            the file to write to
	 */
	public TermToXML(HashMap<String, ArrayList<Term>> codedResults, String rawText, File xmlFile) {

		this.codedResults = codedResults;
		this.rawText = rawText;
		this.xmlFile = xmlFile;

		generateXMLFile(codedResults, rawText, xmlFile);
	}

	public TermToXML(HashMap<String, ArrayList<Term>> codedResults, String rawText) {

		this.codedResults = codedResults;
		this.rawText = rawText;
//		this.xmlFile = xmlFile;

		generateXMLString(codedResults);
	}
	
	
	/**
	 * The key function to implement the parsing of the codedResult and the
	 * writing of the content to the XML file as specified
	 * 
	 * @param codedResults
	 * @param rawText
	 * @param xmlFile
	 */
	public void generateXMLFile(HashMap<String, ArrayList<Term>> codedResults, String rawText, File xmlFile) {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		StringBuffer extractedTermIDString = new StringBuffer("1");

		try {

			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			doc.setXmlStandalone(true);

			Element rootElement = doc.createElementNS("http://www.omg.org/XMI", "XMI");
			rootElement.setPrefix("xmi"); // Element.setPrefix can specify the

			rootElement.setAttribute("xmlns:tcas", "http:///uima/tcas.ecore");
			rootElement.setAttribute("xmlns:cas", "http:///uima/cas.ecore");
			rootElement.setAttribute("xmlns:vaers", "http:///gov/hhs/fda/srs/annotation/vaers.ecore");
			Attr attr1 = doc.createAttributeNS("http://www.omg.org/XMI", "version");
			attr1.setPrefix("xmi");
			attr1.setValue("2.0");
			rootElement.setAttributeNode(attr1);
			doc.appendChild(rootElement);

			Element e1 = doc.createElement("cas:NULL");
			e1.setAttribute("xmi:id", "0");
			rootElement.appendChild(e1);

			Element e2 = doc.createElement("tcas:DocumentAnnotation");
			e2.setAttribute("xmi:id", "1");
			e2.setAttribute("sofa", "6");
			e2.setAttribute("begin", "0");
			e2.setAttribute("end", (new Integer(rawText.length() - 1)).toString());

			rootElement.appendChild(e2);

			int maxXMLId = 2;

			for (Entry<String, ArrayList<Term>> entry : codedResults.entrySet()) {
				String word = entry.getKey();
				ArrayList<Term> terms = entry.getValue();
				for (Term term : terms) {
					Element tmpElement = doc.createElement("vaers:ClinicalFeature");

					// xmi:id
					int intString = maxXMLId;
					maxXMLId = maxXMLId+1;
					String idString = new Integer(intString).toString();
					tmpElement.setAttribute("xmi:id", idString);
					extractedTermIDString.append(" " + idString);
					
					// sofa
					tmpElement.setAttribute("sofa", "6");

					int from = term.from-1;
					tmpElement.setAttribute("begin", new Integer(from).toString());
					int to = term.to;
					tmpElement.setAttribute("end", new Integer(to).toString());
					tmpElement.setAttribute("text", rawText.substring(from, to));
					tmpElement.setAttribute("preferred_term", term.strCls);
					tmpElement.setAttribute("ontology", term.context);
					tmpElement.setAttribute("class", term.strType);
					tmpElement.setAttribute("type", "");
					tmpElement.setAttribute("mentionType", "");
					tmpElement.setAttribute("mentionTypeGold", "");
					rootElement.appendChild(tmpElement);

				}
			}

			Element e3 = doc.createElement("cas:Sofa");
			String tmpS = (new Integer(maxXMLId).toString());
			e3.setAttribute("xmi:id", tmpS);
			e3.setAttribute("sofaNum", "1");
			e3.setAttribute("sofaID", "_InitialView");
			e3.setAttribute("mimeType", "text");
			e3.setAttribute("sofaString", rawText);
			extractedTermIDString.append(" " + tmpS); // for the 'members'
			rootElement.appendChild(e3);

			Element e4 = doc.createElement("cas:View");
			e4.setAttribute("sofa", "6");
			e4.setAttribute("members", extractedTermIDString.toString());
			rootElement.appendChild(e4);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(doc);

			StreamResult file = null;
			file = new StreamResult(xmlFile);
			transformer.transform(source, file);


		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * The key function to implement the parsing of the codedResult and the
	 * return of the XML content as a String.
	 * 
	 * this function is generated for wrapping MetaMapLite as a Java API such
	 * that this function can generate key info (e.g., begin, end, text, 
	 * preferred_term) into a XML string compliant
	 * to the VAERS Data Type System.
	 * 
	 * @param codedResults
	 */
	public String generateXMLString(HashMap<String, ArrayList<Term>> codedResults) {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		StringBuffer extractedTermIDString = new StringBuffer("1");

		try {

			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			doc.setXmlStandalone(true);

			Element rootElement = doc.createElementNS("http://www.omg.org/XMI", "XMI");
			rootElement.setPrefix("xmi"); // Element.setPrefix can specify the

			rootElement.setAttribute("xmlns:tcas", "http:///uima/tcas.ecore");
			rootElement.setAttribute("xmlns:cas", "http:///uima/cas.ecore");
			rootElement.setAttribute("xmlns:vaers", "http:///gov/hhs/fda/srs/annotation/vaers.ecore");
			Attr attr1 = doc.createAttributeNS("http://www.omg.org/XMI", "version");
			attr1.setPrefix("xmi");
			attr1.setValue("2.0");
			rootElement.setAttributeNode(attr1);
			doc.appendChild(rootElement);

			Element e1 = doc.createElement("cas:NULL");
			e1.setAttribute("xmi:id", "0");
			rootElement.appendChild(e1);

			Element e2 = doc.createElement("tcas:DocumentAnnotation");
			e2.setAttribute("xmi:id", "1");
			e2.setAttribute("sofa", "6");
			e2.setAttribute("begin", "0");
			e2.setAttribute("end", (new Integer(rawText.length() - 1)).toString());

			rootElement.appendChild(e2);

			int maxXMLId = 2;

			for (Entry<String, ArrayList<Term>> entry : codedResults.entrySet()) {
				String word = entry.getKey();
				ArrayList<Term> terms = entry.getValue();
				for (Term term : terms) {
					Element tmpElement = doc.createElement("vaers:ClinicalFeature");

					// xmi:id
					int intString = maxXMLId;
					maxXMLId = maxXMLId+1;
					String idString = new Integer(intString).toString();
					tmpElement.setAttribute("xmi:id", idString);
					extractedTermIDString.append(" " + idString);
					
					// sofa
					tmpElement.setAttribute("sofa", "6");

					int from = term.from-1;
					tmpElement.setAttribute("begin", new Integer(from).toString());
					int to = term.to;
					tmpElement.setAttribute("end", new Integer(to).toString());
					tmpElement.setAttribute("text", rawText.substring(from, to));
					tmpElement.setAttribute("preferred_term", term.strCls);
//					tmpElement.setAttribute("ontology", term.context);
//					tmpElement.setAttribute("class", term.strType);
//					tmpElement.setAttribute("type", "");
//					tmpElement.setAttribute("mentionType", "");
//					tmpElement.setAttribute("mentionTypeGold", "");
					rootElement.appendChild(tmpElement);

				}
			}

			Element e3 = doc.createElement("cas:Sofa");
			String tmpS = (new Integer(maxXMLId).toString());
			e3.setAttribute("xmi:id", tmpS);
			e3.setAttribute("sofaNum", "1");
			e3.setAttribute("sofaID", "_InitialView");
			e3.setAttribute("mimeType", "text");
			e3.setAttribute("sofaString", rawText);
			extractedTermIDString.append(" " + tmpS); // for the 'members'
			rootElement.appendChild(e3);

			Element e4 = doc.createElement("cas:View");
			e4.setAttribute("sofa", "6");
			e4.setAttribute("members", extractedTermIDString.toString());
			rootElement.appendChild(e4);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(doc);

			ByteArrayOutputStream baos = new ByteArrayOutputStream(); // ByteArrayOutputStream
			StreamResult strXMLContent = new StreamResult(baos);
			transformer.transform(source, strXMLContent);
			return baos.toString();


		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	/**
	 * The main class to initiate the class.
	 * 
	 * @param args
	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//		String sampleText = "My son received Smallpox vaccination on 4/21/2006 in left deltoid. 12 days after he developed increased left arm pain and pleuritic substernal chest pain. 5/11/06 transferred to hospital with chest pain, right arm pain. Final dx of acute myopericarditis, serum reaction, allergic reaction, anemia, abnormal reaction to vaccine. Medical records from previous hospitalization obtained on 5/14/06 showed PMHx of Stevens-Johnson syndrome; family hx reveals patient's father had myocardial infarction.";
//		ArrayList<String> selectedOntologies = new ArrayList<String>();
//		ArrayList<String> selectedUMLS = new ArrayList<String>();
//		selectedOntologies.add("MEDDRA");
//
//		NCBO_REST application = new NCBO_REST(false);
//		HashMap<String, ArrayList<Term>> codedResults = application.processText(sampleText, selectedOntologies,
//				selectedUMLS, 10);
//
//		Path p = Paths.get("test.xml");
//		File xmlFile = new File(p.toString());
//
//		TermToXML ttx = new TermToXML(codedResults, sampleText, xmlFile);
//	}
}
