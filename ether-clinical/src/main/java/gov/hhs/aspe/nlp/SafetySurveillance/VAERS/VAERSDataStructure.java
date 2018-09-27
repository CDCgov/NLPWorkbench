package gov.hhs.aspe.nlp.SafetySurveillance.VAERS;

import java.util.ArrayList;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;
/**
 * Define VAERS data structure
 * @author Guangfan.Zhang
 *
 */
public class VAERSDataStructure {
	private ArrayList<Term> vaersElements;
	
	public ArrayList<Term> getVaersElements() {
		return vaersElements;
	}

	public ArrayList<Term> getVaersElementsByType(int eventTemporalAssociation) {
		ArrayList<Term> selectedElements = new ArrayList<Term>();
		
		for(Term element: vaersElements)
		{
			if(element.eventTemporalAssociation == eventTemporalAssociation){
				selectedElements.add(element);
			}
		}
		return selectedElements;
	}

	public void setVaersElements(ArrayList<Term> vaersElements) {
		this.vaersElements = vaersElements;
	}

	private String rawText;

	public String getRawText() {
		return rawText;
	}

	public void setRawText(String rawText) {
		this.rawText = rawText;
	}
	
	private int length = 0;

	public int getLength() {
		if(rawText!=null)
		{
			length = rawText.length();
		}
		
		return length;
	}
	
	private  String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
