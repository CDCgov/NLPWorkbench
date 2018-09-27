package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Temporal;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class load TIMEX3 tags from a MedTARSQI output file.
 * @author Wei.Chen
 *
 */
public class MedTARSQITIMEX3 {
	int begin;
	int end;
	String value = null;
	String type = null;
	String origin = null;
	String tid = null; 
	
	public MedTARSQITIMEX3 (){
	}

	public MedTARSQITIMEX3 (Node e1){
		Element e = (Element)e1;
		if (e.getAttributeNode("begin") != null)
			this.begin = Integer.parseInt(e.getAttributeNode("begin").getValue());
		if (e.getAttributeNode("end") != null)
			this.end = Integer.parseInt(e.getAttributeNode("end").getValue());
		if (e.getAttributeNode("value") != null)
			this.value= e.getAttributeNode("value").getValue();
		if (e.getAttributeNode("origin") != null)
			this.origin = e.getAttributeNode("origin").getValue();
		if (e.getAttributeNode("type") != null)
			this.type = e.getAttributeNode("type").getValue();
		if (e.getAttributeNode("origin") != null)
			this.origin = e.getAttributeNode("origin").getValue();
		if (e.getAttributeNode("tid") != null)
			this.tid = e.getAttributeNode("tid").getValue();
	}

	void printMedTARSQITIMEX3() {
		System.out.println("TIMEX3:\n\t" + this.begin + "\t" + this.end + "\t" + this.value + "\t" + this.type + "\t" + this.origin + "\t" + this.tid);
	}
}
