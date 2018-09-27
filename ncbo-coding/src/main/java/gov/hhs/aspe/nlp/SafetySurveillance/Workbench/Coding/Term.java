package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding;

import java.io.Serializable;

import gov.hhs.aspe.nlp.SafetySurveillance.Corpus.TLink;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;

/**
 * @author Guangfan.Zhang; Wei.Chen1 modified and add comments
 *
 * Engility Corporation
 * Jun 22, 2017
 */
public class Term implements Serializable{
	public String word;		// the identified 'word' to be annotated.
	//	public String term;
	public String id;
	public int sofaID;
	public String strCls;	// 'Class', which should be changed into "Preferred Term" based on NCBO search
	public String ontology;	// 'Ontology'
	public String strType;	// 'MatchType', which sould be changed into 'Class'
	public String context;	// 'Context'
	public String PT;
	public int from;		// 'From' field in the table
	public int to;			// 'To'
	public int sentenceID;
	public String mentionType, mentionTypeGold;
	public String date	= "";
	public boolean accurateDate; 
	public int eventTemporalAssociation;
	public TLink tlink;
	public Term copy()
	{
		Term newTerm = new Term();
		newTerm.word = word;
		newTerm.strCls = strCls;
		newTerm.ontology = ontology;
		newTerm.strType = strType;
		newTerm.from = from;
		newTerm.to = to;
		newTerm.context = context;
		newTerm.mentionType = mentionType;
		newTerm.mentionTypeGold = mentionTypeGold;
		newTerm.tlink = tlink;
		newTerm.eventTemporalAssociation = eventTemporalAssociation;
		newTerm.date = date;
		newTerm.sentenceID = sentenceID;
		newTerm.id = id;
		newTerm.sofaID = sofaID;
		return newTerm;
	}
	public void fill(String txt, String strWord, String strTerm, String ontology, int lastPos){
		word = strWord.toUpperCase();
		//if word ends with "." after a digit, treat it as a period. 
		from = txt.toUpperCase().indexOf(word, lastPos)+1;
		if(word.endsWith(".")){
			if(word.length() > 1)
				if(Character.isDigit(word.charAt(word.length()-2)))
				{
					word = word.substring(0, word.length() -1);
				}
					
		}
//		term = strTerm;
		to = from + word.length()-1;
		this.ontology = ontology;
		context = GeneralUtility.getContext(txt, strWord, from, to, 10);
		strCls = word;
		strType = strTerm;
	}
	public void fill(String txt, String strWord, int from, String strPT, String strTerm, String ontology ){
		word = strWord.toUpperCase();
//		term = strTerm;
		this.from = from;
		to = from + word.length()-1;
		this.ontology = ontology;
		context = GeneralUtility.getContext(txt, strWord, from, to, 10);
		strCls = strPT;
		strType = strTerm;
	}
}
