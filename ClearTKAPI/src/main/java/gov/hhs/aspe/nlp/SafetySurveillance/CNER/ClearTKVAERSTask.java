package gov.hhs.aspe.nlp.SafetySurveillance.CNER;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkAnnotator;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import org.cleartk.ml.jar.JarClassifierBuilder;
import org.cleartk.opennlp.tools.PosTaggerAnnotator;
import org.cleartk.opennlp.tools.SentenceAnnotator;
import org.cleartk.snowball.DefaultSnowballStemmer;
import org.cleartk.token.tokenizer.TokenAnnotator;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.cr.UriCollectionReader;

import gov.hhs.aspe.nlp.SafetySurveillance.IO.PrettyPrintWriter;
import gov.hhs.aspe.nlp.SafetySurveillance.IO.XmiWriter;
import gov.hhs.aspe.nlp.SafetySurveillance.VAERS.VAERSToDocumentTextAnnotator;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;

/**
 * <h1> ClearTK Wrapper Class for VAERS data semantic entity recognition </h1>   
 * The ClearTKVAERSTask class serves as the wrapper class for ClearTK (UIMA) based machine learning method. It incorporates three key components other than common POS, sentence splitter, <i>etc.</i>:
 * 
 * <p>
 * <li>
 * 	Raw data parser: VAERSToDocumentTextAnnotator
 * </li>
 * <li>
 * 	Feature extractor: ClinicalNamedEntityChunker
 * </li>
 * <li>
 * Model loader: loaded with GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH
 * </li>
 *   
 * 
 * @author Guangfan.Zhang
 *
 */
@SuppressWarnings("deprecation")
public class ClearTKVAERSTask extends SwingWorker {
	public ClearTKVAERSTask()
	{
	}
	
//	public String dataFile = "*.txt"; 
	public ArrayList<String> selectedFiles;
//	public String sourceLocation = "resources";
	public String outputDir ;
	public String report = "";
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	public String modelDirectory = "";
//	public boolean isSingleFileSelected;
	String serializedTermFile = "";
	public static String metaMapRootDir = "C:/Software/public_mm_lite/";
	
	
	@Override
	protected Void doInBackground() throws Exception {
		runClearTK();
		return null;
	}
	
	public JCas runClearTK() throws ResourceInitializationException, UIMAException, IOException, ClassNotFoundException {
		
		List<File> files = null;
		files = new ArrayList<File>();
		boolean isVAERSFile = true;

		File prettyprintDir = new File(outputDir + "/prettyprint");
		if (!prettyprintDir.exists()) {
			boolean result = false;
			try{
				prettyprintDir.mkdir();
				result = true;
			} 
			catch(SecurityException se){
				//handle it
			}        
		}

		File xmiDir = new File(outputDir + "/xmi");
		if (!xmiDir.exists()) {
			boolean result = false;
			try{
				xmiDir.mkdir();
				result = true;
			} 
			catch(SecurityException se){
				//handle it
			}        
		}
		AggregateBuilder builder = new AggregateBuilder();
		
		if(selectedFiles != null)
		{
			if(selectedFiles.size() > 0)
			{
				GeneralUtility.CheckFileProcessed(selectedFiles, xmiDir.getAbsolutePath());
				for(String f:selectedFiles){
					files.add(new File(f));
				}
				
				if(selectedFiles.get(0).endsWith("txt")) //need to have a better logic to find out whether it is a VAERS format 
				{
					isVAERSFile = false;
				}
			}
			else 
				isVAERSFile = false;
		}
		else 
			isVAERSFile = false;
		
		
		if(files.size() == 0)
		{
			String ppName = "test.xml";
			files.add(new File(ppName));
		}
		CollectionReader reader = UriCollectionReader.getCollectionReaderFromFiles(files);


		if(!report.equals(""))
		{
			builder.add(AnalysisEngineFactory.createEngineDescription(
					VAERSToDocumentTextAnnotator.class, 
					VAERSToDocumentTextAnnotator.PARAM_DATA, 
					report));
		}
		else {
			if(isVAERSFile)
				builder.add(AnalysisEngineFactory.createEngineDescription(
						VAERSToDocumentTextAnnotator.class));
			else
				builder.add(UriToDocumentTextAnnotator.getDescription());
		}
		

		// NLP pre-processing components
		builder.add(SentenceAnnotator.getDescription());
		builder.add(TokenAnnotator.getDescription());

		builder.add(DefaultSnowballStemmer.getDescription("English"));
		builder.add(PosTaggerAnnotator.getDescription());
		
		if(modelDirectory.equals("")){
			modelDirectory= "data/models";
		}
		
		MLModelParameters propertyFile = null;
		
		boolean medOnly = false, computeLexiconFeatures = true;
		String medModelDir = "";
		String lexiconFeatureDir = "data/lexiconfeatures";
		int contextWindow = 3;
		int featuresetID = 8;
		boolean sequenceModel = true;

		String modelFileName = modelDirectory + "/MLModel.properties";
		String featureExtractor = "ClinicalNamedEntityChunker";
		Class featureExtractorClass = null;
		if(new File(modelFileName).exists())
		{
			propertyFile = new MLModelParameters();
			propertyFile.loadParameters(modelDirectory + "/MLModel.properties");
			contextWindow=propertyFile.contextWindow;
			featuresetID = propertyFile.featuresetID;
			if (propertyFile.sequenceModel >= 1 ){
				sequenceModel = true;
			}
			else
				sequenceModel = false;
			
			featureExtractor = propertyFile.featureExtractor;
			featureExtractorClass = Class.forName(featureExtractor);
		}
		else{
			if(sequenceModel)
			{
				featureExtractorClass = ClinicalNamedEntityChunker.class;
			}
			else
				featureExtractorClass = VAERSCNERAnnotator.class;
		}
		
		AnalysisEngineDescription vaersCNERAnnotator = null;
		System.out.println("Current feature ID = " + featuresetID);
		System.out.println("Model: " + modelDirectory);
		if(sequenceModel)
		{
			vaersCNERAnnotator  = AnalysisEngineFactory.createEngineDescription(
					ClinicalNamedEntityChunker.class,
					ClinicalNamedEntityChunker.PARAM_METAMAPLITE_DIRECTORY, 
					metaMapRootDir,
					ClinicalNamedEntityChunker.PARAM_MEDONLY,
					medOnly,
					ClinicalNamedEntityChunker.PARAM_COMPUTELEXICONFEATURES,
					computeLexiconFeatures,
					ClinicalNamedEntityChunker.PARAM_MEDMODEL_DIRECTORY,
					medModelDir,
					ClinicalNamedEntityChunker.PARAM_LEXICONFEATURE_DIRECTORY, 
					lexiconFeatureDir,
					ClinicalNamedEntityChunker.PARAM_SEQUENCE, 
					true,
					ClinicalNamedEntityChunker.PARAM_CONTEXTWINDOW,
					contextWindow,
					ClinicalNamedEntityChunker.PARAM_FEATURESETID,
					featuresetID,
					CleartkSequenceAnnotator.PARAM_IS_TRAINING,
					false,
					GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
					JarClassifierBuilder.getModelJarFile(modelDirectory));
		}else
		{
			vaersCNERAnnotator  = AnalysisEngineFactory.createEngineDescription(
					VAERSCNERAnnotator.class,
					VAERSCNERAnnotator.PARAM_METAMAPLITE_DIRECTORY, 
					metaMapRootDir,
					VAERSCNERAnnotator.PARAM_MEDONLY,
					medOnly,
					VAERSCNERAnnotator.PARAM_COMPUTELEXICONFEATURES,
					computeLexiconFeatures,
					VAERSCNERAnnotator.PARAM_MEDMODEL_DIRECTORY,
					medModelDir,
					VAERSCNERAnnotator.PARAM_LEXICONFEATURE_DIRECTORY, 
					lexiconFeatureDir,
					VAERSCNERAnnotator.PARAM_CONTEXTWINDOW,
					contextWindow,
					VAERSCNERAnnotator.PARAM_FEATURESETID,
					featuresetID,
					CleartkAnnotator.PARAM_IS_TRAINING,
					false,
					GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
					JarClassifierBuilder.getModelJarFile(modelDirectory));
		}
		System.out.println("Start to run a clearTK task: model = " + modelDirectory + "; sequence modeling: " + sequenceModel);
		builder.add(vaersCNERAnnotator);

		GeneralUtility.CreateADir(outputDir + "/xmi");
		AnalysisEngineDescription writerXMI = AnalysisEngineFactory.createEngineDescription(
				XmiWriter.class,
				XmiWriter.PARAM_OUTPUT_DIRECTORY,
				outputDir + "/xmi"
				);

		builder.add(writerXMI);
		GeneralUtility.CreateADir(outputDir + "/prettyprint/");
		
		AnalysisEngineDescription writerPP = createEngineDescription(
				PrettyPrintWriter.class,
				PrettyPrintWriter.PARAM_TARGET_LOCATION, 
				outputDir + "/prettyprint/",
				PrettyPrintWriter.PARAM_FILE, 
//				selectedFiles.get(0) + ".txt"
				"test.txt"
				); 
		
		builder.add(writerPP);

		// //////////////////////////////////////////////////////////////////////////////
		// Run pipeline for SER
		// //////////////////////////////////////////////////////////////////////////////
		AnalysisEngine ae = builder.createAggregate();
		
		JCasIterator iter = new JCasIterator(reader, ae);
		
//		SimplePipeline.runPipeline(
//				reader,
//				ae);
		JCas jCas = null;
		while (iter.hasNext()) {
			jCas = iter.next();
		}
		
		return jCas;
	}

}