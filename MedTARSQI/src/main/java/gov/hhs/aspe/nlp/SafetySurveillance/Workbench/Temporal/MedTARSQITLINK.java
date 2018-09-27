package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Temporal;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class load TLINK (relation) tags from a MedTARSQI output file.
 * 
 * @author Wei.Chen
 *
 */
public class MedTARSQITLINK {
	String origin = null;
	String syntax = null;
	String relType = null;
	String lid = null;
	String eventInstanceID = null;
	String relatedToEventInstance = null;
	String relatedToTime = null;

	public MedTARSQITLINK() {
	}

	public MedTARSQITLINK(Node e1) {
		Element e = (Element) e1;
		if (e.getAttributeNode("origin") != null)
			this.origin = e.getAttributeNode("origin").getValue();
		if (e.getAttributeNode("syntax") != null)
			this.syntax = e.getAttributeNode("syntax").getValue();
		if (e.getAttributeNode("relType") != null)
			this.relType = e.getAttributeNode("relType").getValue();
		if (e.getAttributeNode("lid") != null)
			this.lid = e.getAttributeNode("lid").getValue();
		if (e.getAttributeNode("eventInstanceID") != null)
			this.eventInstanceID = e.getAttributeNode("eventInstanceID").getValue();
		if (e.getAttributeNode("relatedToEventInstance") != null)
			this.relatedToEventInstance = e.getAttributeNode("relatedToEventInstance").getValue();
		if (e.getAttributeNode("relatedToTime") != null)
			this.relatedToTime = e.getAttributeNode("relatedToTime").getValue();
	}

	void printMedTARSQITLINK() {
		System.out.println("TLINK:\n\t" + this.origin + "\t" + this.relType + "\t" + this.lid + "\t"
				+ this.eventInstanceID + "\t" + this.relatedToTime);
	}
}
