package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Temporal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class loads a MedTARSQI result file and generates 3 separate files for
 * storing Clinical, Temporal and Relation features.
 * 
 * @author Wei.Chen
 *
 */
public class MedTARSQIResultFileReader {
	String theFileString = null;
	String rawText = null;
	public String clinicalOutputXML = null; 

	public String temporalOutputXML = null;
	public String relationOutputXML = null;

	public NodeList nlEvent = null; 
	public NodeList nlTIMEX3 = null;
	public NodeList nlTLINK = null;

	public boolean importedEventExists = false;

	/**
	 * This function receives the XML file content from MedTARSQI execution and
	 * extracts the elements of "EVENT", "TIMEX3", and "TLINK".
	 * 
	 * @param tarsqiResultContentXML
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public MedTARSQIResultFileReader(String tarsqiResultContentXML)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(tarsqiResultContentXML));
		Document doc = builder.parse(is);

		this.rawText = doc.getElementsByTagName("text").item(0).getTextContent();

		Node tarsqiTagsNode = doc.getElementsByTagName("tarsqi_tags").item(0);
		nlEvent = ((Element) tarsqiTagsNode).getElementsByTagName("EVENT");
		for (int i = 0; i < nlEvent.getLength(); i++) {
			Node nTmp = nlEvent.item(i);
			MedTARSQIEvent medTTmp = new MedTARSQIEvent(nTmp);
//			medTTmp.printMedTARSQIEvent();
		}

		nlTIMEX3 = doc.getElementsByTagName("TIMEX3");
		for (int i = 0; i < nlTIMEX3.getLength(); i++) {
			Node nTmp = nlTIMEX3.item(i);
			MedTARSQITIMEX3 medTTmp = new MedTARSQITIMEX3(nTmp);
//			medTTmp.printMedTARSQITIMEX3();
		}

		nlTLINK = doc.getElementsByTagName("TLINK");
		for (int i = 0; i < nlTLINK.getLength(); i++) {
			Node nTmp = nlTLINK.item(i);
			MedTARSQITLINK medTTmp = new MedTARSQITLINK(nTmp);
//			medTTmp.printMedTARSQITLINK();
		}


	}

	/**
	 * This function generates VAERS XML files from MedTARSQI outputs
	 * 
	 * @param nl
	 *            The data structure to contain the extracted data from
	 *            MedTARSQI
	 * @param type
	 *            The type of data to generate. 1: Clinical, and the file name
	 *            should be FileNameClinical.xml; 2: Temporal, and the file name
	 *            should be FileNameTemporal.xml; and 3: Relation, and the
	 *            filename is FileNameRelation.xml
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	public String generateVaersXMLFromMedTARSQI(NodeList nl, int type) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		ByteArrayOutputStream baos = null;
		StringBuffer extractedTermIDString = new StringBuffer("1");

//		try {

			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			doc.setXmlStandalone(true);

			Element rootElement = doc.createElementNS("http://www.omg.org/XMI", "XMI");
			rootElement.setPrefix("xmi"); 
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
			e2.setAttribute("end", (new Integer(this.rawText.length() - 1)).toString());

			rootElement.appendChild(e2);

			int numElements = nl.getLength();
			int maxXMLId = 0;

			switch (type) {
			case 1: {
				for (int i = 0; i < numElements; i++) {
					Element tmpElement = doc.createElement("vaers:ClinicalFeature");

					Node tmpNode = nl.item(i); 
					NamedNodeMap nnp0 = tmpNode.getAttributes();

					int intString = 0;
					if (nnp0.getNamedItem("eiid") != null) {
						intString = Integer.parseInt(nnp0.getNamedItem("eiid").getNodeValue().substring(2));
					}
					intString++;
					if (intString > maxXMLId)
						maxXMLId = intString;
					String idString = new Integer(intString).toString();
					tmpElement.setAttribute("xmi:id", idString);
					extractedTermIDString.append(" " + idString);

					tmpElement.setAttribute("sofa", "6");

					if (nnp0.getNamedItem("begin") != null) {
						tmpElement.setAttribute("begin", nnp0.getNamedItem("begin").getNodeValue());
					}

					if (nnp0.getNamedItem("end") != null) {
						tmpElement.setAttribute("end", nnp0.getNamedItem("end").getNodeValue());
					}

					String originStr = null;
					if (nnp0.getNamedItem("origin") != null) {
						originStr = nnp0.getNamedItem("origin").getNodeValue();
					} else {
						System.out.println("!!!no <origin> tag available in <EVENT>!!!");
					}
					boolean isImportedEvent = false;
					if (originStr != null) {
						if (originStr.contains("EVITA-import")) {
							isImportedEvent = true;
						}
					}
					if (nnp0.getNamedItem("form") != null) {
						if (isImportedEvent) {
							tmpElement.setAttribute("text", nnp0.getNamedItem("full-event").getNodeValue());
						} else {
							tmpElement.setAttribute("text", nnp0.getNamedItem("form").getNodeValue());
						}
					}

					if (nnp0.getNamedItem("form") != null) {
						tmpElement.setAttribute("preferred_term", nnp0.getNamedItem("form").getNodeValue());
					}

					tmpElement.setAttribute("ontology", "MedTARSQI");

					if (nnp0.getNamedItem("class") != null) {
						tmpElement.setAttribute("class", nnp0.getNamedItem("class").getNodeValue());
					}

					rootElement.appendChild(tmpElement);
				}
				break;

			}
			case 2: {
				for (int i = 0; i < numElements; i++) {

					Element tmpElement = null;

					Node tmpNode = nl.item(i); 
					NamedNodeMap nnp0 = tmpNode.getAttributes();

					Attr tmpAttr = (Attr) nnp0.getNamedItem("type");
					if (tmpAttr != null && tmpAttr.getNodeValue().equals("DATE")) {
						tmpElement = doc.createElement("vaers:Date");
					} else if (tmpAttr != null && tmpAttr.getNodeValue().equals("TIME")) {
						continue;
					} else {
						tmpElement = doc.createElement("vaers:TemporalFeature");
					}

					int intString = Integer.parseInt(nnp0.getNamedItem("tid").getNodeValue().substring(1));
					intString++;
					if (intString > maxXMLId)
						maxXMLId = intString;
					String idString = new Integer(intString).toString();
					tmpElement.setAttribute("xmi:id", idString);
					extractedTermIDString.append(" " + idString);

					tmpElement.setAttribute("sofa", "6");

					String beginIndex = nnp0.getNamedItem("begin").getNodeValue();

					tmpElement.setAttribute("begin", beginIndex);

					String endIndex = nnp0.getNamedItem("end").getNodeValue();
					tmpElement.setAttribute("end", endIndex);

					tmpElement.setAttribute("text",
							this.rawText.substring(Integer.parseInt(beginIndex), Integer.parseInt(endIndex)));

					if (nnp0.getNamedItem("type").getNodeValue().equals("DATE")) {
						String dateValue = nnp0.getNamedItem("value").getNodeValue();
						dateValue = dateValue.substring(0, 4) + "-" + dateValue.substring(4, 6) + "-"
								+ dateValue.substring(6, 8);
						tmpElement.setAttribute("Date", dateValue);
					}

					rootElement.appendChild(tmpElement);
				}
				break;
			}
			case 3: {

				int effectiveTlinkCounter = 0;
				for (int i = 0; i < numElements; i++) {
					Element tmpElement = null;
					Node tmpNode = nl.item(i); 
					NamedNodeMap nnp0 = tmpNode.getAttributes();

					Attr tmpAttrTid = (Attr) nnp0.getNamedItem("relatedToTime");
					Attr tmpEventInstanceId = (Attr) nnp0.getNamedItem("eventInstanceID");
					if ((tmpAttrTid == null || tmpEventInstanceId == null)) {
						continue;
					} else {
						tmpElement = doc.createElement("vaers:TemporalRelation");
						effectiveTlinkCounter++;
					}
//					System.out.println("\n effectiveTlinkCounter:  " + effectiveTlinkCounter);
					int intString = Integer.parseInt(nnp0.getNamedItem("lid").getNodeValue().substring(1));
					if (intString > maxXMLId)
						maxXMLId = intString;
					String idString = new Integer(intString).toString();
					tmpElement.setAttribute("xmi:id", idString);
					extractedTermIDString.append(" " + idString);

					tmpElement.setAttribute("sofa", "6");

					String relation = nnp0.getNamedItem("relType").getNodeValue();
					tmpElement.setAttribute("Relation", relation);

					int rid = Integer.parseInt(nnp0.getNamedItem("relatedToTime").getNodeValue().substring(1));
					rid++;
					tmpElement.setAttribute("RID", "T" + new Integer(rid).toString());

					int cid = Integer.parseInt(nnp0.getNamedItem("eventInstanceID").getNodeValue().substring(2));
					cid++;
					tmpElement.setAttribute("CID", "E" + new Integer(cid).toString());

					for (int j = 0; j < this.nlTIMEX3.getLength(); j++) {
						Node tmpN = nlTIMEX3.item(j); 
						NamedNodeMap nnp01 = tmpN.getAttributes();
						String idStrTmp = nnp01.getNamedItem("tid").getNodeValue().substring(1);
						int idIntTmp = Integer.parseInt(idStrTmp);
						if (rid - 1 == idIntTmp) {
							String dateStr = nnp01.getNamedItem("value").getNodeValue();
							if (dateStr.length() != 8) {
								continue;
							}
							dateStr = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-"
									+ dateStr.substring(6, 8);
							tmpElement.setAttribute("Date", dateStr);
						
							int beginIndex = Integer.parseInt(nnp01.getNamedItem("begin").getNodeValue());
							int endIndex = Integer.parseInt(nnp01.getNamedItem("end").getNodeValue());
							String refText = rawText.substring(beginIndex, endIndex);
							tmpElement.setAttribute("RefText", refText);
						} else
							continue;
					}

					for (int j = 0; j < this.nlEvent.getLength(); j++) {
						Node tmpN = nlEvent.item(j); 
						NamedNodeMap nnp01 = tmpN.getAttributes();
						if (nnp01.getNamedItem("eiid") == null) {
							continue;
						}
						String idStrTmp = nnp01.getNamedItem("eiid").getNodeValue().substring(2);
						int idIntTmp = Integer.parseInt(idStrTmp);
						if (cid - 1 == idIntTmp) {
							String coreTextStr = nnp01.getNamedItem("form").getNodeValue();
							String originStr = nnp0.getNamedItem("origin").getNodeValue();
							if (originStr.length() > 10) {
								String originStrSub = originStr.substring(0, 9) + "...";
								tmpElement.setAttribute("CoreText", coreTextStr + "(" + originStrSub + ")");
							} else {
								tmpElement.setAttribute("CoreText", coreTextStr + "(" + originStr + ")");
							}
						} else
							continue;
					}

					rootElement.appendChild(tmpElement);
				}

				break;
			}
			}


			Element e3 = doc.createElement("cas:Sofa");
			String tmpS = (new Integer(++maxXMLId).toString());
			e3.setAttribute("xmi:id", tmpS);
			e3.setAttribute("sofaNum", "1");
			e3.setAttribute("sofaID", "_InitialView");
			e3.setAttribute("mimeType", "text");
			e3.setAttribute("sofaString", this.rawText);
			extractedTermIDString.append(" " + tmpS); 
			rootElement.appendChild(e3);

			Element e4 = doc.createElement("cas:View");
			e4.setAttribute("sofa", "6");
			e4.setAttribute("members", extractedTermIDString.toString());
			rootElement.appendChild(e4);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(doc);

			baos = new ByteArrayOutputStream(); // ByteArrayOutputStream
			StreamResult stringResult = new StreamResult(baos);
			transformer.transform(source, stringResult);
			return baos.toString();

//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
	}

}
