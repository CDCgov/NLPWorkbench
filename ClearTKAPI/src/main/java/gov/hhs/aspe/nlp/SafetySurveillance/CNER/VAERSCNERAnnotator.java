/** 
 * Copyright (c) 2012, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */
package gov.hhs.aspe.nlp.SafetySurveillance.CNER;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkAnnotator;
import org.cleartk.ml.Feature;
import org.cleartk.ml.Instance;
import org.cleartk.ml.chunking.IoChunking;
import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Following;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.CombinedExtractor1;
import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.extractor.NamedFeatureExtractor1;
import org.cleartk.ml.feature.extractor.TypePathExtractor;
import org.cleartk.ml.feature.function.CharacterCategoryPatternFunction;
import org.cleartk.ml.feature.function.CharacterCategoryPatternFunction.PatternType;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import org.cleartk.ml.libsvm.LibSvmBooleanOutcomeClassifier;
import org.cleartk.ml.libsvm.LibSvmBooleanOutcomeClassifierBuilder;
import org.cleartk.ne.type.NamedEntityMention;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.cleartk.util.ViewUriUtil;

import gov.hhs.aspe.nlp.SafetySurveillance.FeatureExtractor.ContainASemanticType;
import gov.hhs.aspe.nlp.SafetySurveillance.FeatureExtractor.MedicalTermFeatures;
import gov.hhs.aspe.nlp.SafetySurveillance.FeatureExtractor.TriggerWord;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.TermPreProcessing;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.Term;

/**
 * 
 * Modified to generate features to deal with VAERS data.
 * 
 *  Guangfan Zhang
 *  Engility Corporation
 *  2018
 *  
 * <br>
 * Copyright (c) 2012, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * 
 * This class demonstrates how to write a new CleartkAnnotator. Like the
 * {@link BasicDocumentClassificationAnnotator}, this class is used for building and categorizing
 * documents according to their Usenet group. The feature extraction flow illustrates how to extract
 * more complex features that require aggregating statistics for transformation prior to training
 * and classification.
 * 
 * @author Lee Becker
 * 
 */

public class VAERSCNERAnnotator extends CleartkAnnotator<Object> {
	public static final String PARAM_METAMAPLITE_DIRECTORY = "metaMapRootDir";
	private String metaMapRootDir;
	 private IoChunking<Token, NamedEntityMention> chunking;

	public static final String PARAM_MEDMODEL_DIRECTORY = "medModelDir";
	private String medModelDir;

	public static final String PARAM_COMPUTELEXICONFEATURES = "computeLexiconFeatures";
	private boolean computeLexiconFeatures;

	public static final String PARAM_LEXICONFEATURE_DIRECTORY = "lexiconFeatureDir";
	private String lexiconFeatureDir;

	private FeatureExtractor1<Token> extractor, extractorPOS, extractorPattern, extractorStem;

  private CleartkExtractor<Token, Token> contextExtractor, contextExtractorPOS, contextExtractorPattern, 
  											contextExtractorStem;
  private TriggerWord triggerWordPDXExtractor, triggerWordSDXExtractor, triggerWordRuleOutExtractor, triggerWordFHXExtractor, triggerWordMHXExtractor;;
  private ContainASemanticType containASemanticTypeExact, containASemanticType; 
//  private SVMClassifier svmClassifier; 
  private TermPreProcessing TPP;

  private ArrayList<String> selectedOntologies = new ArrayList<String>(), selectedUMLS = new ArrayList<String>();

  public static final String PREDICTION_VIEW_NAME = "VAERSCNERPredictionView";
  public static final String PARAM_MEDONLY= "medOnly";
  private boolean medOnly;
  public static final String PARAM_FEATURESETID = "featuresetID";
  private int featuresetID = 0;
  public static final String PARAM_CONTEXTWINDOW = "contextWindow";
  private int contextWindow = 0;
  private String filePath; 
  private static Logger log = LogManager.getLogger(VAERSCNERAnnotator.class);

/**
 * featuresetID:
 *  0: ETHER
 *  1: NCBO
 *  2: MetamapLite
 *  3: ETHER + NCBO + MetamapLite 
 *  4: 3 with trigger words
 *  5: 4 + POS + type feature 
 *  6: 5 + POS + type feature + stem
 *  7: 6 + relative location in the sentence   
 *  8: 4 with context (POS, type & stem)
// *  9: no stem or word in any form
 */
  public void initialize(UimaContext context) throws ResourceInitializationException {
	    super.initialize(context);

	    log.debug("Start to initialize VAERSCNERAnnotator...");
	    featuresetID = (Integer) context.getConfigParameterValue(PARAM_FEATURESETID);
	    contextWindow = (Integer) context.getConfigParameterValue(PARAM_CONTEXTWINDOW);
	    computeLexiconFeatures = (Boolean) context.getConfigParameterValue(PARAM_COMPUTELEXICONFEATURES);
	    medModelDir = (String) context.getConfigParameterValue(PARAM_MEDMODEL_DIRECTORY);
	    metaMapRootDir = (String) context.getConfigParameterValue(
	    		PARAM_METAMAPLITE_DIRECTORY);
		medOnly =  (Boolean) context.getConfigParameterValue(
				PARAM_MEDONLY);	    
		lexiconFeatureDir = (String) context.getConfigParameterValue(PARAM_LEXICONFEATURE_DIRECTORY);
		
	    // the token feature extractor: text, char pattern (uppercase, digits, etc.), and part-of-speech
	    this.extractor = new CombinedExtractor1<Token>(
	        new FeatureFunctionExtractor<Token>(
	            new CoveredTextExtractor<Token>(),
	            new CharacterCategoryPatternFunction<Token>(PatternType.REPEATS_MERGED)),
	        new TypePathExtractor<Token>(Token.class, "pos"));

	    this.extractorPattern=  new FeatureFunctionExtractor<Token>(
	            new CoveredTextExtractor<Token>(),
	            new CharacterCategoryPatternFunction<Token>(PatternType.REPEATS_MERGED));
	    
	    this.extractorPOS = new TypePathExtractor<Token>(Token.class, "pos");
	    this.extractorStem= new TypePathExtractor<Token>(Token.class, "stem");
//	    this.extractor = new FeatureFunctionExtractor<Token>(
//	                new CoveredTextExtractor<Token>(),
//	                new CharacterCategoryPatternFunction<Token>(PatternType.REPEATS_MERGED));
//	    this.extractor = new TypePathExtractor<Token>(Token.class, "pos");
//	    // the context feature extractor: the features above for the 3 preceding and 3 following tokens
	    this.contextExtractor = new CleartkExtractor<Token, Token>(
	        Token.class,
	        this.extractor,
	        new Preceding(contextWindow),
	        new Following(contextWindow));
	    
	    this.contextExtractorPOS = new CleartkExtractor<Token, Token>(
		        Token.class,
		        this.extractorPOS,
		        new Preceding(contextWindow),
		        new Following(contextWindow));
	    
	    this.contextExtractorPattern = new CleartkExtractor<Token, Token>(
		        Token.class,
		        this.extractorPattern,
		        new Preceding(contextWindow),
		        new Following(contextWindow));
	    this.contextExtractorStem= new CleartkExtractor<Token, Token>(
		        Token.class,
		        this.extractorPOS,
		        new Preceding(contextWindow),
		        new Following(contextWindow));

//	    this.triggerWorfPDXExtractor= new TriggerWord<Token, Token>("pDx", "Diagnosis", false);
	    ArrayList<String> mHxWords = new ArrayList<String>();
	    mHxWords.add("mhx");
	    mHxWords.add("medical history");
	    mHxWords.add("medicalhistory");
	    
	    
	    this.triggerWordMHXExtractor = new TriggerWord<Token, Token>("mHx", mHxWords, 0);

	    ArrayList<String> fHxWords = new ArrayList<String>();
	    fHxWords.add("fhx");
	    fHxWords.add("family history");
	    fHxWords.add("familyhistory");
	    
	    this.triggerWordFHXExtractor = new TriggerWord<Token, Token>("fHx", fHxWords, 0);

	    ArrayList<String> roWords = new ArrayList<String>();
	    roWords.add("r/o");
	    roWords.add("ro");
	    roWords.add("rule out");
	    roWords.add("rule/out");
	    roWords.add("ruled out");
	    roWords.add("ruled/out");
	    roWords.add("no evidence of");
	    
	    this.triggerWordRuleOutExtractor= new TriggerWord<Token, Token>("ruleout", roWords, 0);

//	    this.triggerWorfPDXExtractor= new TriggerWord<Token, Token>("pDx", "Diagnosis", false);
	    ArrayList<String> sDXWords = new ArrayList<String>();
	    sDXWords.add("develop");
	    sDXWords.add("experience");
	    sDXWords.add("suffer");
	    sDXWords.add("state");
	    sDXWords.add("suggest");
	    sDXWords.add("Impression");
	    sDXWords.add("Assessment");
	    sDXWords.add("Possible");
	    sDXWords.add("ModifierCertainty");

	    this.triggerWordSDXExtractor= new TriggerWord<Token, Token>("sDx", sDXWords, 1);
//	    this.svmClassifier = new SVMClassifier<Token, Token>("MedTerm", medModelDir);
	    ArrayList<String> pDXWords = new ArrayList<String>();
	    pDXWords.add("diagnos");
	    pDXWords.add("diagnosi");
	    pDXWords.add("dx");
	    
	    this.triggerWordPDXExtractor= new TriggerWord<Token, Token>("pDx", pDXWords, 0);
//	    this.triggerWorfPDXExtractor= new TriggerWord<Token, Token>("pDx", "dx\'d", false);
	    ArrayList<String> diagnosisKWList = new ArrayList<String>();
//	    diagnosisKWList.add("Death");
	    diagnosisKWList.add("generalTerm");
	    diagnosisKWList.add("Anatomy");
	    diagnosisKWList.add("death");
	    this.containASemanticType = new ContainASemanticType(diagnosisKWList, "ETHER", 3);
//	    this.containASemanticTypeExact = new ContainASemanticType("Death", "ETHER", 3);
	    
	    // the chunking definition: Tokens will be combined to form NamedEntityMentions, with labels
	    // from the "mentionType" attribute so that we get B-location, I-person, etc.
	    this.chunking = new IoChunking<Token, NamedEntityMention>(
	        Token.class,
	        NamedEntityMention.class,
	    		"mentionType");

	    TPP = new TermPreProcessing(metaMapRootDir);
	    
	    selectedOntologies.add("SNOMEDCT");
	    selectedOntologies.add("ETHER");
	    selectedOntologies.add("MetaMapLite");
	    log.debug("VAERSCNERAnnotator has been initialized.");
  }


  public void process(JCas jCas) throws AnalysisEngineProcessException {
	  String doc = jCas.getDocumentText();
	  URI uri = ViewUriUtil.getURI(jCas);
//	  System.out.println(uri);
//	  System.out.println("Before: " + selectedOntologies.size());
//	  selectedOntologies.clear();
//	  if(featuresetID == 0 || featuresetID == 3 || featuresetID >7 )
//		  selectedOntologies.add("ETHER");
//	  if(featuresetID == 1 || featuresetID == 3 || featuresetID >7 )
//		  selectedOntologies.add("SNOMEDCT");
//	  if(featuresetID == 2 || featuresetID == 3 || featuresetID >7 )
//		  selectedOntologies.add("MetaMapLite");
	  filePath = uri.getPath();
	  if(filePath.contains("/"))
		  filePath = filePath.substring(filePath.lastIndexOf("/")+1);
	  if(filePath.contains("\\"))
		  filePath = filePath.substring(filePath.lastIndexOf("\\")+1);
	  
	  System.out.println(filePath);
	  HashMap<String, ArrayList<Term>> codedResults = null;
	  HashMap<String,ArrayList<Term>> codedResultsByWord = null;
	  
	  /**
	   * Write lexicon features to a directory
	   */
//	  if(!lexiconFeatureDir.equals("")){
//		  String featureDir = lexiconFeatureDir + "/" + filePath + ".json";
//		  SaveLexiconFeatures saveLFeatures = new SaveLexiconFeatures(featureDir);
//		  
//		  if(new File(featureDir).exists() && !computeLexiconFeatures){
//			  //features have been extracted, load them into the memory
//			  codedResultsByWord = saveLFeatures.load();
//		  }
//		  else
//		  {
			  codedResults = TPP.process(null, null, doc, selectedOntologies, selectedUMLS);
			  codedResultsByWord = GeneralUtility.sortTermsByWord(codedResults);
//			  //save them
//			  saveLFeatures.save(codedResultsByWord);
//			  
//		  }
//	  }
	  this.containASemanticType.setCodedResults(codedResultsByWord);
	  triggerWordPDXExtractor.setCodedResults(codedResultsByWord);
	  triggerWordSDXExtractor.setCodedResults(codedResultsByWord);
	  triggerWordFHXExtractor.setCodedResults(codedResultsByWord);
	  triggerWordMHXExtractor.setCodedResults(codedResultsByWord);
	  triggerWordRuleOutExtractor.setCodedResults(codedResultsByWord);
	  
//	  LinkedHashMap<String,ArrayList<Term>> codedResults2 = GeneralUtility.sortTerms(codedResults);
//	  System.out.println("After: " + selectedOntologies.size());
//	  HashMap<String,ArrayList<Term>> codedResultsByWord = GeneralUtility.sortTermsByWord(codedResults);
	  if(medOnly)
		  processMedOnly(jCas, codedResultsByWord);
	  else{
		  processSER(jCas, codedResultsByWord);
	  }
  }
  
  public void processSER(JCas jCas,HashMap<String, ArrayList<Term>> codedResultsByWord )throws AnalysisEngineProcessException{
	  MedicalTermFeatures medicalTermExtractor = new MedicalTermFeatures();
	  
	  HashMap<String, Object> outcomes = new HashMap<String, Object>();
	  HashMap<String, List<Feature>>  allFeatures = new HashMap<String, List<Feature>>();
	  
	  allFeatures = medicalTermExtractor.extractFeatures(jCas, codedResultsByWord, outcomes);
	  
	  HashMap<String, Boolean> medTermFeatures= null;
	  
	  if(medModelDir!=null )
		  if(!medModelDir.equals(""))
			  medTermFeatures = isMedFeature(allFeatures, medModelDir);
	  
	  for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
		  // extract features for each token in the sentence
		  HashMap<String, ArrayList<String[]>> moreMapping = new HashMap<String, ArrayList<String[]>>();
		  List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sentence);
		  //      System.out.println(sentence.getCoveredText());
		  List<List<Feature>> featureLists = new ArrayList<List<Feature>>();
		  ArrayList<String> listStr = new  ArrayList<String>();
		  //		  System.out.println(sentence.getCoveredText());
		  int jj = 0; 
		  for (Token token : tokens) {
			  String txtToken = token.getCoveredText();
//			  if(txtToken.toLowerCase().contains("die"))
//				  System.out.println(txtToken);
			  if(txtToken.equals(".") || txtToken.equals(",") || txtToken.equals(";"))
			  {
				  continue;
			  }
//			  if(txtToken.contains(":"))
//			  {
//				  System.out.println("Not a valid token");
//			  }
			  if(txtToken.contains("."))
			  {
				  txtToken = txtToken.replaceAll("\\.", "");
			  }
			  
			  List<Feature> features = new ArrayList<Feature>();
			  features.add(new Feature("FileName", filePath) );
			  List<Feature> patternFeatures =this.extractorPattern.extract(jCas, token);
			  int from = token.getBegin();
			  int end = token.getEnd();
			  String index = from + "_" + end;
			  boolean medOutcome = (Boolean)outcomes.get(index);
//			  features.addAll(Collections.singletonList(new Feature("Med", medOutcome)));
			  if(featuresetID > 4 )
//				  if(patternFeatures.size() > 1)
//					  features.add(patternFeatures.get(1));
			  {
				  features.addAll(patternFeatures);
				  features.addAll(this.extractorPOS.extract(jCas, token));
			  }
			  if(featuresetID > 5 )
				  features.addAll(this.extractorStem.extract(jCas,  token));
			  
//			  if(featuresetID > 1)
			  FeatureUtility.addFeatureFromExternalSources(features, token, codedResultsByWord, moreMapping, featuresetID);
//			  if(txtToken.contains("breast"))
//			  {
//				  System.out.println("---------------"  + txtToken + ": " + features.get(6) + "--------");
////				  Log.log(0,  txtToken + ": " + features.get(3));
////				  Log.getInstance();
//			  }

			  if(featuresetID > 6)
				  features.add(new Feature("Loc", jj*1.0/tokens.size()));
  			  
//				  features.add(new Feature("Stem", token.getStem()));
			  
//			  features.add(new Feature("LocInReport", token.getBegin() *1.0/jCas.getDocumentText().length()));
//			  features.addAll(this.extractor.extract(jCas, token));
			  
			  if(featuresetID > 7 ){
				  features.addAll(this.contextExtractorPOS.extract(jCas, token));
				  List<Feature> cpFeatures = this.contextExtractorPattern.extract(jCas, token);
				  for(Feature f:cpFeatures){
					  String name = f.getName();
					  if(name.endsWith("CharPatternRepeatsMerged"))
					  {
						  features.add(f);
					  }
				  }
			  }
			  jj++;
			  if(featuresetID > 3 ){
//			  features.addAll(this.contextExtractor.extract(jCas, token));
				  features.addAll(this.triggerWordPDXExtractor.extract(jCas,  token,  sentence));
				  features.addAll(this.triggerWordSDXExtractor.extract(jCas,  token,  sentence));
				  features.addAll(this.triggerWordMHXExtractor.extract(jCas,  token,  sentence));
				  features.addAll(this.triggerWordFHXExtractor.extract(jCas,  token,  sentence));
				  features.addAll(this.triggerWordRuleOutExtractor.extract(jCas,  token,  sentence));
//				  features.addAll(this.containASemanticTypeExact.extract(jCas, token, sentence));
				  features.addAll(this.containASemanticType.extract(jCas, token, sentence));
			  }
			  
//  		  if(features.size() > 1)
//  				  System.out.println(features);
			  Instance<Object> instance = new Instance<Object>();
			  instance.addAll(features);
//			  System.out.println(features);

			  // during training, convert NamedEntityMentions in the CAS into expected classifier outcomes
			  if (this.isTraining()) {
				  if(medTermFeatures!=null)
				  {
					  boolean curMedTerm = medTermFeatures.get(index);
					  if(curMedTerm == false)
					  {
						  continue;
					  }
				  }
				  // extract the gold (human annotated) NamedEntityMention annotations
				  List<NamedEntityMention> namedEntityMentions = JCasUtil.selectCovered(
						  jCas,
						  NamedEntityMention.class,
						  token);

				  if(namedEntityMentions.size() == 0)
				  {
					  instance.setOutcome("Other");
				  }
				  else
				  {
//					  System.out.println(namedEntityMentions.get(0));
					  instance.setOutcome(namedEntityMentions.get(0).getMentionType());
//					  System.out.println(token.getCoveredText());
				  }
				  this.dataWriter.write(instance);
			  }
			  // during classification, convert classifier outcomes into NamedEntityMentions in the CAS
			  else {
				  String result = (String)this.classifier.classify(features);
//				  System.out.println(result);
				  NamedEntityMention mention = new NamedEntityMention(jCas, token.getBegin(), token.getEnd());
				  if(medTermFeatures!=null)
				  {
					  boolean curMedTerm = medTermFeatures.get(index);
					  if(curMedTerm == false)
					  {
						  mention.setMentionType("Other");
					  }
					  else
						  if(result!=null)
							  mention.setMentionType(result);
						  else
							  mention.setMentionType("Other");
				  }
				  else{
					  if(result!=null)
						  mention.setMentionType(result);
					  else
						  mention.setMentionType("Other");
				  }
				  
				  mention.addToIndexes();
			  }
		  }
	  }
  }
  

  public void processMedOnly(JCas jCas, HashMap<String, ArrayList<Term>> codedResultsByWord) throws AnalysisEngineProcessException {
	  MedicalTermFeatures medicalTermExtractor = new MedicalTermFeatures();
	  
	  HashMap<String, Object> outcomes = new HashMap<String, Object>();
	  HashMap<String, List<Feature>>  allFeatures = new HashMap<String, List<Feature>>();
	  
	  allFeatures = medicalTermExtractor.extractFeatures(jCas, codedResultsByWord, outcomes);
	  for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
		  // extract features for each token in the sentence
		  List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sentence);
		  for (Token token : tokens) {
			  int from = token.getBegin();
			  int end = token.getEnd();
			  
			  String index = from + "_" + end;
			  
			  String txtToken = token.getCoveredText();
			  if(txtToken.contains("."))
			  {
				  txtToken = txtToken.replaceAll("\\.", "");
			  }
			  
			  List<Feature> features = allFeatures.get(index);
			  
			  Instance<Object> instance = new Instance<Object>();
			  instance.addAll(features);

			  // during training, convert NamedEntityMentions in the CAS into expected classifier outcomes
			  if (this.isTraining()) {
				  // extract the gold (human annotated) NamedEntityMention annotations
				  List<NamedEntityMention> namedEntityMentions = JCasUtil.selectCovered(
						  jCas,
						  NamedEntityMention.class,
						  token);

				  if(namedEntityMentions.size() == 0)
				  {
					  instance.setOutcome(false);
				  }
				  else
				  {
					  instance.setOutcome(true);
				  }
				  this.dataWriter.write(instance);
			  }
			  // during classification, convert classifier outcomes into NamedEntityMentions in the CAS
			  else {
				  boolean result = (Boolean)this.classifier.classify(features);
				  NamedEntityMention mention = new NamedEntityMention(jCas, token.getBegin(), token.getEnd());
				  if(result)
					  mention.setMentionType("True");

				  mention.addToIndexes();
			  }
		  }
	  }
  }

  public static AnalysisEngineDescription getClassifierDescription(File classifierJarFile)
      throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngineDescription(
        VAERSCNERAnnotator.class,
        GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
        classifierJarFile.toString());
  }

  public static class CountAnnotationExtractor<T extends Annotation> implements
      NamedFeatureExtractor1<T> {

    private Class<? extends Annotation> annotationType;

    private String name;

    public CountAnnotationExtractor(Class<? extends Annotation> annotationType) {
      this.annotationType = annotationType;
      this.name = "Count_" + this.annotationType.getName();
    }

    public String getFeatureName() {
      return this.name;
    }

    public List<Feature> extract(JCas view, Annotation focusAnnotation)
        throws CleartkExtractorException {
      List<?> annotations = JCasUtil.selectCovered(this.annotationType, focusAnnotation);
      return Arrays.asList(new Feature(this.name, annotations.size()));
    }
  }
  
  private HashMap<String, Boolean> isMedFeature(HashMap<String, List<Feature>> allFeatures, String modelDirName){
	  
	  HashMap<String, Boolean> medFeatures = new HashMap<String, Boolean>();

	  String featureName = "MedTerm";
	  boolean isMed = false;
	  try {
		  featureName = Feature.createName(featureName, "boolean");	    
		  LibSvmBooleanOutcomeClassifierBuilder builder = new LibSvmBooleanOutcomeClassifierBuilder();
		  LibSvmBooleanOutcomeClassifier classifier;
		  classifier = builder.loadClassifierFromTrainingDirectory(new File(modelDirName));

		  for(Entry<String, List<Feature>> entry:allFeatures.entrySet())
		  {
			  String index = entry.getKey();
			  List<Feature> features = entry.getValue();
			  isMed= (boolean)classifier.classify(features);
			  Collections.singletonList(new Feature(featureName, isMed));
			  medFeatures.put(index,  isMed);
		  }
	  } catch (AnalysisEngineProcessException | IOException e1) {
		  // TODO Auto-generated catch block
		  e1.printStackTrace();
	  }
	  return medFeatures;
  }
}
