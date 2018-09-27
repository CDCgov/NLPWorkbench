package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Temporal;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class load EVENTs tags from a MedTARSQI output file.
 * @author Wei.Chen
 *
 */
public class MedTARSQIEvent {
	int begin;
	int end;
	String origin = null;
	String pos = null;
	String tense = null;
	String form = null;
	String epos = null;
	String eiid = null;
	String eid = null;
	String classTag = null; //the name 'class' is the tagname, but string 'class' string is reserved. 
	String aspect = null;
	
	public MedTARSQIEvent (){
	}

	public MedTARSQIEvent (Node e1){
		Element e = (Element)e1;

		if (e.getAttributeNode("begin") != null)
			this.begin = Integer.parseInt(e.getAttributeNode("begin").getValue());
		if (e.getAttributeNode("end") != null)
			this.end = Integer.parseInt(e.getAttributeNode("end").getValue());
		if (e.getAttributeNode("origin") != null)
			this.origin = e.getAttributeNode("origin").getValue();
		if (e.getAttributeNode("pos") != null)
			this.pos = e.getAttributeNode("pos").getValue();
		if (e.getAttributeNode("tense") != null)
			this.tense = e.getAttributeNode("tense").getValue();
		if (e.getAttributeNode("form") != null)
			this.form = e.getAttributeNode("form").getValue();
		if (e.getAttributeNode("epos") != null)
			this.epos = e.getAttributeNode("epos").getValue();
		if (e.getAttributeNode("eiid") != null)
			this.eiid = e.getAttributeNode("eiid").getValue();
		if (e.getAttributeNode("eid") != null)
			this.eid = e.getAttributeNode("eid").getValue();
		if (e.getAttributeNode("class") != null)
			this.classTag = e.getAttributeNode("class").getValue();
		if (e.getAttributeNode("aspect") != null)
			this.aspect = e.getAttributeNode("aspect").getValue();

	}

	void printMedTARSQIEvent() {
		System.out.println("EVENT:\n\t" + this.begin + "\t" + this.end + "\t" + this.origin + "\t" + this.pos + "\t" + this.tense + "\t" + this.form 
				+ "\t" + this.epos + "\t" + this.eiid + "\t" + this.eid + "\t" + this.classTag + "\t" + this.aspect);
	}
}
