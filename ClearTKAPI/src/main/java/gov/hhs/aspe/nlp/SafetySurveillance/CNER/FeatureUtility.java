package gov.hhs.aspe.nlp.SafetySurveillance.CNER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cleartk.ml.Feature;
import org.cleartk.token.type.Token;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.Term;

public class FeatureUtility {
	  public static void addFeatureFromExternalSources( List<Feature> features, Token token, 
			  HashMap<String,ArrayList<Term>> codedResultsByWord, HashMap<String, ArrayList<String[]>> moreMapping, 
			  int featuresetID){
		  
		  String txtToken2 = token.getCoveredText();
		  int isETHER = 0, isMetaMap = 0, isNCBO = 0;

		  int from = token.getBegin();
		  boolean isMed = false;
		  ArrayList<String> featureNames = new ArrayList<String>();
		  
		  boolean contained = false;
		  if(txtToken2.equals("-"))
			  return;

		  if(txtToken2.contains("-"))
		  {
			  String[] txtTokens = txtToken2.split("-");
			  contained = true;
			  //need to have every substring being detected by any lexicon
			  for(String txtToken:txtTokens)
			  {
				  if(!codedResultsByWord.containsKey(txtToken.toUpperCase()))
				  {
					  contained = false;
					  break;
				  }
			  }
		  }
		  else
			  if(codedResultsByWord.containsKey(txtToken2.toUpperCase()))
			  {
				  contained = true;
			  }
		  
		  if(contained)
		  {
			  String[] txtTokens = null;
			  if(txtToken2.contains("-"))
			  {
				  txtTokens = txtToken2.split("-");
				  for(String txtToken:txtTokens)
				  {
					  
				  }
			  }
			  else{
				  txtTokens = new String[1];
				  txtTokens[0] = txtToken2;
			  }
			  ArrayList<Term> curTerms = null;
			  ArrayList<Term> trueTerms = new ArrayList<Term>();
			  ArrayList<String[]> curTempMapping = new ArrayList<String[]>();
			  HashMap<String, ArrayList<String[]>> tempMapAll = new HashMap<String, ArrayList<String[]>>();
			  for(String txtToken:txtTokens)
			  {
				  HashMap<String, ArrayList<String[]>> tempMap = null;
				  int curFrom = from + txtToken2.indexOf(txtToken); 
				  curTerms = codedResultsByWord.get(txtToken.toUpperCase());
				  for(Term t:curTerms )
				  {
					  int trueFrom = t.from;
					  
					  if(!t.ontology.equals("ETHER"))
					  {
						  trueFrom = trueFrom + t.word.indexOf(txtToken.toUpperCase());
					  }
					  if((trueFrom - curFrom) * (trueFrom - curFrom ) <= 4) 
//						  if((trueFrom - from) * (trueFrom - from ) <= (2 + t.word.length())* (2+t.word.length())) 
					  {
						  //not the one that is being examined
						  trueTerms.add(t);
					  }
				  }
				  tempMap = GeneralUtility.getTermClass(trueTerms);
				  tempMapAll.putAll(tempMap);
				  ArrayList<String[]> curTempMap = tempMap.get(txtToken.toUpperCase());
				  if(curTempMap!=null)
					  curTempMapping.addAll(curTempMap);
				  if(curTempMap == null)
				  {
					  String keyFound = "";//partial word
					  for(String key:tempMap.keySet()){
						  if(key.toUpperCase().contains(txtToken.toUpperCase()))
						  {
							  keyFound = key;
							  curTempMapping.addAll(tempMap.get(key));
							  break;
						  }
						  if(txtToken.toUpperCase().contains(key))
						  {
							  keyFound = key;
							  curTempMapping.addAll(tempMap.get(key));
							  break;
						  }
					  }
				  }
			  }

			  if(curTempMapping != null)
			  {
				  for(String[] t:curTempMapping){
					  switch (t[2]){
					  case "ETHER":
						  isETHER = 1;
//						  System.out.println(token.getCoveredText() + " is detected by ETHER");
						  break;

					  case "MetaMap":
						  isMetaMap = 1;
						  break;
					  default:
						  isNCBO = 1;
						  break;						  }
				  }
				  
				  if(featuresetID > 2 || 
						  (isETHER == 1 && featuresetID == 0) ||
						  (isNCBO== 1 && featuresetID == 1) ||
						  (isMetaMap == 1 && featuresetID == 2))
				  {
					  for(String[] curMapping:curTempMapping){
						  String ontology = curMapping[2];
						  if(featuresetID > 2)
							  addAFeatureFromExternalSources(curMapping, featureNames, features);
						  else{
							  if(ontology.equals("ETHER") && isETHER == 1 && featuresetID == 0) 
								  addAFeatureFromExternalSources(curMapping, featureNames, features);
							  else if( ontology.equals("MetaMap") && isMetaMap== 1 && featuresetID == 2 )
									  addAFeatureFromExternalSources(curMapping, featureNames, features);
							  else 
							  {
								  if(isNCBO== 1 && featuresetID == 1) 
									  addAFeatureFromExternalSources(curMapping, featureNames, features);
							  }
						  }
					  }
				  }

				  isMed = true;
				  if(curTempMapping.size() != 0)
					  curTempMapping.remove(0);
//				  curTempMapping.clear();
				  
				  if(curTempMapping.size() == 0)
				  {
					  tempMapAll.remove(txtToken2.toUpperCase());
				  }
				  moreMapping.putAll(tempMapAll);
				  //				  foundCount = foundCount + codedResults2.get(from+"").size();
				  //				  System.out.println(from + ": " + codedResults2.get(from+"").size() + "; a match is found! " + " Count: " + foundCount);
			  }
		  }
		  else{
			  if(moreMapping.containsKey(txtToken2.toUpperCase()))
			  {
				  isMed = true;
				  if(moreMapping.get(token.getCoveredText().toUpperCase()).size() == 0 )
					  System.out.println("No mapping is found!");
				  for(String[] t:moreMapping.get(txtToken2.toUpperCase())){
					  switch (t[2]){
					  case "ETHER":
						  isETHER = 1;
						  break;
					  case "MetaMap":
						  isMetaMap = 1;
						  break;
					  default:
						  isNCBO = 1;
						  break;						  
						  
					  }
				  }
				  addAFeatureFromExternalSources(moreMapping.get(txtToken2.toUpperCase()).get(0), featureNames, features);
			  }
		  }
		  features.add(new Feature("Med", isMed));
		  switch (featuresetID){
		  case 0:
			  features.add(new Feature("ETHER", isETHER));
			  break;
		  case 1:
			  features.add(new Feature("NCBO", isNCBO));
			  break;
		  case 2:
			  features.add(new Feature("MetaMap", isMetaMap));
			  break;
		  default:
			  features.add(new Feature("ETHER", isETHER));
			  features.add(new Feature("NCBO", isNCBO));
			  features.add(new Feature("MetaMap", isMetaMap));
		  }
	  }
	  public static void addAFeatureFromExternalSources(String[] curMapping, ArrayList<String> featureNames, List<Feature>features){
		  
		  for(int k = 0; k < 3; k++){
			  String curFeature = curMapping[k];
			  curFeature = curFeature.replaceAll(" ",  "-");
			  if(featureNames.contains(curFeature))
			  {
				  continue;
			  }
			  else
				  featureNames.add(curFeature);

			  features.add(new Feature(curFeature, 1.0));
		  }
	  }

}
