package org.apache.ctakes.coreference.eval;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import org.apache.ctakes.assertion.medfacts.cleartk.*;
import org.apache.ctakes.core.config.ConfigParameterConstants;
import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.core.util.DocumentIDAnnotationUtil;
import org.apache.ctakes.core.util.ListFactory;
import org.apache.ctakes.coreference.ae.*;
import org.apache.ctakes.coreference.factory.CoreferenceAnnotatorFactory;
import org.apache.ctakes.dependency.parser.util.DependencyUtility;
import org.apache.ctakes.relationextractor.eval.RelationExtractorEvaluation.HashableArguments;
import org.apache.ctakes.temporal.ae.BackwardsTimeAnnotator;
import org.apache.ctakes.temporal.ae.DocTimeRelAnnotator;
import org.apache.ctakes.temporal.ae.EventAnnotator;
import org.apache.ctakes.temporal.eval.EvaluationOfEventTimeRelations.ParameterSettings;
import org.apache.ctakes.temporal.eval.EvaluationOfTemporalRelations_ImplBase;
import org.apache.ctakes.typesystem.type.constants.CONST;
import org.apache.ctakes.typesystem.type.relation.*;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.syntax.WordToken;
import org.apache.ctakes.typesystem.type.textsem.*;
import org.apache.ctakes.typesystem.type.textspan.Paragraph;
import org.apache.ctakes.utils.distsem.WordEmbeddings;
import org.apache.ctakes.utils.distsem.WordVector;
import org.apache.ctakes.utils.distsem.WordVectorReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.analysis_engine.metadata.FlowConstraints;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.component.ViewCreatorAnnotator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.FlowControllerFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.flow.*;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.FloatArray;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCopier;
import org.apache.uima.util.FileUtils;
import org.cleartk.eval.AnnotationStatistics;
import org.cleartk.ml.CleartkAnnotator;
import org.cleartk.ml.jar.DefaultDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.JarClassifierBuilder;
import org.cleartk.ml.liblinear.LibLinearStringOutcomeDataWriter;
import org.cleartk.ml.svmlight.rank.SvmLightRankDataWriter;
import org.cleartk.ml.tksvmlight.model.CompositeKernel.ComboOperator;
import org.cleartk.util.ViewUriUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvaluationOfEventCoreference extends EvaluationOfTemporalRelations_ImplBase {
 

  static interface CoreferenceOptions extends TempRelOptions{
    @Option
    public String getOutputDirectory();
    
    @Option
    public boolean getUseTmp();
    
    @Option
    public boolean getTestOnTrain();
    
    @Option(longName="external")
    public boolean getUseExternalScorer();
    
    @Option(shortName="t", defaultValue={"MENTION_CLUSTER"})
    public EVAL_SYSTEM getEvalSystem();
    
    @Option(shortName="c", defaultValue="default")
    public String getConfig();
    
    @Option(shortName="s")
    public String getScorerPath();
    
    @Option
    public boolean getGoldMarkables();
    
    @Option
    public boolean getSkipTest();
  }
  
  private static Logger logger = Logger.getLogger(EvaluationOfEventCoreference.class);
  public static float COREF_PAIRS_DOWNSAMPLE = 0.5f;
  public static float COREF_CLUSTER_DOWNSAMPLE=0.5f;
  private static final int NUM_SAMPLES = 0;
  private static final double DROPOUT_RATE = 0.1;
  
  protected static ParameterSettings pairwiseParams = new ParameterSettings(DEFAULT_BOTH_DIRECTIONS, COREF_PAIRS_DOWNSAMPLE, "tk",
      1.0, 1.0, "linear", ComboOperator.SUM, 0.1, 0.5);
  protected static ParameterSettings clusterParams = new ParameterSettings(DEFAULT_BOTH_DIRECTIONS, COREF_CLUSTER_DOWNSAMPLE, "tk",
      1.0, 1.0, "linear", ComboOperator.SUM, 0.1, 0.5);
  
  private static String goldOut = "";
  private static String systemOut = "";
  
  public static void main(String[] args) throws Exception {
    CoreferenceOptions options = CliFactory.parseArguments(CoreferenceOptions.class, args);

    List<Integer> patientSets = options.getPatients().getList();
    List<Integer> trainItems = getTrainItems(options);
    List<Integer> testItems = options.getTestOnTrain() ? getTrainItems(options) : getTestItems(options);

    ParameterSettings params = options.getEvalSystem() == EVAL_SYSTEM.MENTION_PAIR ? pairwiseParams : clusterParams;
    
    File workingDir = new File("target/eval/temporal-relations/coreference/" + options.getEvalSystem() + File.separator +  options.getConfig());
    if(!workingDir.exists()) workingDir.mkdirs();
    if(options.getUseTmp()){
      File tempModelDir = File.createTempFile("temporal", null, workingDir);
      tempModelDir.delete();
      tempModelDir.mkdir();
      workingDir = tempModelDir;
    }
    EvaluationOfEventCoreference eval = new EvaluationOfEventCoreference(
        workingDir,
        options.getRawTextDirectory(),
        options.getXMLDirectory(),
        options.getXMLFormat(),
        options.getSubcorpus(),
        options.getXMIDirectory(),
        options.getTreebankDirectory(),
        options.getPrintErrors(),
        options.getPrintFormattedRelations(),
        params,
        options.getKernelParams(),
        options.getOutputDirectory());

    eval.skipTrain = options.getSkipTrain();
    eval.skipWrite = options.getSkipDataWriting();
    eval.skipTest = options.getSkipTest();
    eval.goldMarkables = options.getGoldMarkables();
    eval.evalType = options.getEvalSystem();
    eval.config = options.getConfig();
    goldOut = "gold." + eval.config + ".conll";
    systemOut = "system." + eval.config + ".conll";
    
    eval.prepareXMIsFor(patientSets);
    
    params.stats = eval.trainAndTest(trainItems, testItems);//training);//

    if(options.getUseTmp()){
      FileUtils.deleteRecursive(workingDir);
    }
    
    if(options.getUseExternalScorer() && !options.getSkipTest()){
      Pattern patt = Pattern.compile("(?:Coreference|BLANC): Recall: \\([^\\)]*\\) (\\S+)%.*Precision: \\([^\\)]*\\) (\\S+)%.*F1: (\\S+)%");
      Runtime runtime = Runtime.getRuntime();
      Process p = runtime.exec(new String[]{
          "perl",
          options.getScorerPath(),
          "all",
          options.getOutputDirectory() + goldOut,
          options.getOutputDirectory() + systemOut,
          "none"});
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line, metric=null;
      System.out.println(String.format("%10s%7s%7s%7s", "Metric", "Rec", "Prec", "F1"));
      Map<String,Double> scores = new HashMap<>();
      while((line = reader.readLine()) != null){
        line = line.trim();
        if(line.startsWith("METRIC")){
          metric = line.substring(7);  // everything after "METRIC"
          metric = metric.substring(0, metric.length()-1);  // remove colon from the end
        }else if(line.startsWith("Coreference")){
          Matcher m = patt.matcher(line);
          if(m.matches()){
            System.out.println(String.format("%10s%7.2f%7.2f%7.2f", metric, Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)), Double.parseDouble(m.group(3))));
            scores.put(metric, Double.parseDouble(m.group(3)));
          }
        }
      }
      
      if(scores.containsKey("muc") && scores.containsKey("bcub") && scores.containsKey("ceafe")){
        double conll = (scores.get("muc") + scores.get("bcub") + scores.get("ceafe")) / 3.0;
        System.out.println(String.format("%10s              %7.2f", "Conll", conll));
      }
    }
  }
  
  boolean skipTrain=false; 
  boolean skipWrite=false;
  boolean skipTest=false;
  boolean goldMarkables=false;
  public enum EVAL_SYSTEM { BASELINE, MENTION_PAIR, MENTION_CLUSTER, CLUSTER_RANK, PERSON_ONLY };
  EVAL_SYSTEM evalType;
  String config=null;
  
  private String outputDirectory;
  
  public EvaluationOfEventCoreference(File baseDirectory,
      File rawTextDirectory, File xmlDirectory,
      org.apache.ctakes.temporal.eval.Evaluation_ImplBase.XMLFormat xmlFormat, Subcorpus subcorpus,
      File xmiDirectory, File treebankDirectory, boolean printErrors,
      boolean printRelations, ParameterSettings params, String cmdParams, String outputDirectory) {
    super(baseDirectory, rawTextDirectory, xmlDirectory, xmlFormat, subcorpus, xmiDirectory,
        treebankDirectory, printErrors, printRelations, params);
    this.outputDirectory = outputDirectory;
    this.kernelParams = cmdParams == null ? null : cmdParams.replace("\"", "").split(" ");
  }

  @Override
  protected void train(CollectionReader collectionReader, File directory)
      throws Exception {
    if(skipTrain) return;
    if(this.evalType == EVAL_SYSTEM.BASELINE || this.evalType == EVAL_SYSTEM.PERSON_ONLY) return;
    if(!skipWrite){
      AggregateBuilder aggregateBuilder = this.getPreprocessorAggregateBuilder();
      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(DocumentIDPrinter.class));
      aggregateBuilder.add(PolarityCleartkAnalysisEngine.createAnnotatorDescription());
      aggregateBuilder.add(UncertaintyCleartkAnalysisEngine.createAnnotatorDescription());
      aggregateBuilder.add(GenericCleartkAnalysisEngine.createAnnotatorDescription());
      aggregateBuilder.add(HistoryCleartkAnalysisEngine.createAnnotatorDescription());
      aggregateBuilder.add(SubjectCleartkAnalysisEngine.createAnnotatorDescription());

      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ViewCreatorAnnotator.class, ViewCreatorAnnotator.PARAM_VIEW_NAME, "Baseline"));
      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ParagraphAnnotator.class));
//      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ParagraphVectorAnnotator.class));
      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(RelationPropagator.class));
      aggregateBuilder.add(EventAnnotator.createAnnotatorDescription());
      aggregateBuilder.add(BackwardsTimeAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/timeannotator/model.jar"));
      aggregateBuilder.add(DocTimeRelAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/doctimerel/model.jar"));
      if(this.goldMarkables){
        aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CopyGoldMarkablesInChains.class));
      }else{
        aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(DeterministicMarkableAnnotator.class));
        //    aggregateBuilder.add(CopyFromGold.getDescription(/*Markable.class,*/ CoreferenceRelation.class, CollectionTextRelation.class));
        aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(RemovePersonMarkables.class));
      }
      // MarkableHeadTreeCreator creates a cache of mappings from Markables to dependency heads since so many feature extractors use that information
      // major speedup
      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(MarkableHeadTreeCreator.class));
      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CopyCoreferenceRelations.class, CopyCoreferenceRelations.PARAM_GOLD_VIEW, GOLD_VIEW_NAME));
      aggregateBuilder.add(MarkableSalienceAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/salience/model.jar"));
      if(this.evalType == EVAL_SYSTEM.MENTION_PAIR){
        aggregateBuilder.add(EventCoreferenceAnnotator.createDataWriterDescription(
            //        TKSVMlightStringOutcomeDataWriter.class,
                    LibLinearStringOutcomeDataWriter.class,
//            LibSvmStringOutcomeDataWriter.class,
//            TkLibSvmStringOutcomeDataWriter.class,
            directory,
            params.probabilityOfKeepingANegativeExample
            ));
      }else if(this.evalType == EVAL_SYSTEM.MENTION_CLUSTER){
        AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(
            MentionClusterCoreferenceAnnotator.class,
            CleartkAnnotator.PARAM_IS_TRAINING,
            true,
            MentionClusterCoreferenceAnnotator.PARAM_PROBABILITY_OF_KEEPING_A_NEGATIVE_EXAMPLE,
            params.probabilityOfKeepingANegativeExample,
            DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
            LibLinearStringOutcomeDataWriter.class,
            DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
            directory);        
        aggregateBuilder.add(aed);
        for(int i = 0; i < NUM_SAMPLES; i++){
          // after each iteration, remove the gold chains in the system view and re-copy over gold chains with some variation:
          aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(RemoveAllCoreferenceAnnotations.class));
          aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CopyCoreferenceRelations.class, CopyCoreferenceRelations.PARAM_GOLD_VIEW, GOLD_VIEW_NAME, CopyCoreferenceRelations.PARAM_DROP_ELEMENTS, true));          

          aed = AnalysisEngineFactory.createEngineDescription(
              MentionClusterCoreferenceAnnotator.class,
              CleartkAnnotator.PARAM_IS_TRAINING,
              true,
              MentionClusterCoreferenceAnnotator.PARAM_PROBABILITY_OF_KEEPING_A_NEGATIVE_EXAMPLE,
              params.probabilityOfKeepingANegativeExample,
              MentionClusterCoreferenceAnnotator.PARAM_USE_EXISTING_ENCODERS,
              true,
              DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
              LibLinearStringOutcomeDataWriter.class,
              DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
              directory);
              
          aggregateBuilder.add(aed);
          
        }
      }else if(this.evalType == EVAL_SYSTEM.CLUSTER_RANK){
        // TODO
        aggregateBuilder.add(MentionClusterRankingCoreferenceAnnotator.createDataWriterDescription(
            SvmLightRankDataWriter.class, 
            directory, 
            params.probabilityOfKeepingANegativeExample));
      }else{
        logger.warn("Encountered a training configuration taht does not add an annotator: " + this.evalType);
      }
      Logger.getLogger(EventCoreferenceAnnotator.class).setLevel(Level.WARN);
      // create gold chains for writing out which we can then use for our scoring tool
      //    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CoreferenceChainScoringOutput.class,
      //        CoreferenceChainScoringOutput.PARAM_OUTPUT_DIR,
      //        this.outputDirectory + "train"));
      FlowControllerDescription corefFlowControl = FlowControllerFactory.createFlowControllerDescription(CorefEvalFlowController.class);
      aggregateBuilder.setFlowControllerDescription(corefFlowControl);

      SimplePipeline.runPipeline(collectionReader, aggregateBuilder.createAggregate());
    }
    String[] optArray;

    if(this.kernelParams == null){
      ArrayList<String> svmOptions = new ArrayList<>();
      svmOptions.add("-c"); svmOptions.add(""+params.svmCost);        // svm cost
      svmOptions.add("-t"); svmOptions.add(""+params.svmKernelIndex); // kernel index 
      svmOptions.add("-d"); svmOptions.add("3");                      // degree parameter for polynomial
      svmOptions.add("-g"); svmOptions.add(""+params.svmGamma);
      if(params.svmKernelIndex==ParameterSettings.SVM_KERNELS.indexOf("tk")){
        svmOptions.add("-S"); svmOptions.add(""+params.secondKernelIndex);   // second kernel index (similar to -t) for composite kernel
        String comboFlag = (params.comboOperator == ComboOperator.SUM ? "+" : params.comboOperator == ComboOperator.PRODUCT ? "*" : params.comboOperator == ComboOperator.TREE_ONLY ? "T" : "V");
        svmOptions.add("-C"); svmOptions.add(comboFlag);
        svmOptions.add("-L"); svmOptions.add(""+params.lambda);
        svmOptions.add("-T"); svmOptions.add(""+params.tkWeight);
        svmOptions.add("-N"); svmOptions.add("3");   // normalize trees and features
      }
      optArray = svmOptions.toArray(new String[]{});
    }else{
      optArray = this.kernelParams;
    }
    JarClassifierBuilder.trainAndPackage(directory, optArray);
  }

  @Override
  protected AnnotationStatistics<String> test(
      CollectionReader collectionReader, File directory) throws Exception {
    AnnotationStatistics<String> corefStats = new AnnotationStatistics<>();
    if(this.skipTest){
      logger.info("Skipping test");
      return corefStats;
    }
    AggregateBuilder aggregateBuilder = this.getPreprocessorAggregateBuilder();
    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(DocumentIDPrinter.class));
    aggregateBuilder.add(PolarityCleartkAnalysisEngine.createAnnotatorDescription());
    aggregateBuilder.add(UncertaintyCleartkAnalysisEngine.createAnnotatorDescription());
    aggregateBuilder.add(GenericCleartkAnalysisEngine.createAnnotatorDescription());
    aggregateBuilder.add(HistoryCleartkAnalysisEngine.createAnnotatorDescription());
    aggregateBuilder.add(SubjectCleartkAnalysisEngine.createAnnotatorDescription());
    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ParagraphAnnotator.class));
//    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(ParagraphVectorAnnotator.class));
    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(RelationPropagator.class));
    aggregateBuilder.add(BackwardsTimeAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/timeannotator/model.jar"));
    aggregateBuilder.add(EventAnnotator.createAnnotatorDescription());
    aggregateBuilder.add(DocTimeRelAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/doctimerel/model.jar"));
    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CoreferenceChainScoringOutput.class,
          ConfigParameterConstants.PARAM_OUTPUTDIR,
        this.outputDirectory + goldOut,
        CoreferenceChainScoringOutput.PARAM_GOLD_VIEW_NAME,
        GOLD_VIEW_NAME));
    if(this.goldMarkables){
      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CopyGoldMarkablesInChains.class)); //CopyFromGold.getDescription(Markable.class));
    }else{
      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(DeterministicMarkableAnnotator.class));
      //    aggregateBuilder.add(CopyFromGold.getDescription(/*Markable.class,*/ CoreferenceRelation.class, CollectionTextRelation.class));
      aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(RemovePersonMarkables.class));
    }
    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(MarkableHeadTreeCreator.class));
    aggregateBuilder.add(MarkableSalienceAnnotator.createAnnotatorDescription("/org/apache/ctakes/temporal/ae/salience/model.jar"));
    if(this.evalType == EVAL_SYSTEM.MENTION_PAIR){
      aggregateBuilder.add(EventCoreferenceAnnotator.createAnnotatorDescription(directory.getAbsolutePath() + File.separator + "model.jar"));
    }else if(this.evalType == EVAL_SYSTEM.MENTION_CLUSTER){
//      aggregateBuilder.add(EventCoreferenceAnnotator.createScoringAnnotatorDescription("/org/apache/ctakes/coreference/mention-pair" + File.separator + "model.jar"));
      aggregateBuilder.add(MentionClusterCoreferenceAnnotator.createAnnotatorDescription(directory.getAbsolutePath() + File.separator + "model.jar"));
    }else if(this.evalType == EVAL_SYSTEM.CLUSTER_RANK){
      aggregateBuilder.add(MentionClusterRankingCoreferenceAnnotator.createAnnotatorDescription(directory.getAbsolutePath() + File.separator + "model.jar"));
    }else if(this.evalType == EVAL_SYSTEM.BASELINE){
      aggregateBuilder.add(CoreferenceAnnotatorFactory.getLegacyCoreferencePipeline());
    }else{
      logger.info("Running an evaluation that does not add an annotator: " + this.evalType);
    }
//    aggregateBuilder.add(CoreferenceChainAnnotator.createAnnotatorDescription());
    if(!this.goldMarkables){
      aggregateBuilder.add(PersonChainAnnotator.createAnnotatorDescription());
    }
    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(CoreferenceChainScoringOutput.class,
        ConfigParameterConstants.PARAM_OUTPUTDIR,
        this.outputDirectory + systemOut));

    FlowControllerDescription corefFlowControl = FlowControllerFactory.createFlowControllerDescription(CorefEvalFlowController.class);
    aggregateBuilder.setFlowControllerDescription(corefFlowControl);
//    aggregateBuilder.add(AnalysisEngineFactory.createEngineDescription(XMIWriter.class));
    Function<CoreferenceRelation, ?> getSpan = new Function<CoreferenceRelation, HashableArguments>() {
      public HashableArguments apply(CoreferenceRelation relation) {
        return new HashableArguments(relation);
      }
    };
    Function<CoreferenceRelation, String> getOutcome = new Function<CoreferenceRelation,String>() {
      public String apply(CoreferenceRelation relation){
        return "Coreference";
      }
    };
     

    for(Iterator<JCas> casIter =new JCasIterator(collectionReader, aggregateBuilder.createAggregate()); casIter.hasNext();){
      JCas jCas = casIter.next();
      JCas goldView = jCas.getView(GOLD_VIEW_NAME);
      JCas systemView = jCas.getView(CAS.NAME_DEFAULT_SOFA);
      Collection<CoreferenceRelation> goldRelations = JCasUtil.select(
          goldView,
          CoreferenceRelation.class);
      Collection<CoreferenceRelation> systemRelations = JCasUtil.select(
          systemView,
          CoreferenceRelation.class);
      corefStats.add(goldRelations, systemRelations, getSpan, getOutcome);
      if(this.printErrors){
        Map<HashableArguments, BinaryTextRelation> goldMap = Maps.newHashMap();
        for (BinaryTextRelation relation : goldRelations) {
          goldMap.put(new HashableArguments(relation), relation);
        }
        Map<HashableArguments, BinaryTextRelation> systemMap = Maps.newHashMap();
        for (BinaryTextRelation relation : systemRelations) {
          systemMap.put(new HashableArguments(relation), relation);
        }
        Set<HashableArguments> all = Sets.union(goldMap.keySet(), systemMap.keySet());
        List<HashableArguments> sorted = Lists.newArrayList(all);
        Collections.sort(sorted);
        for (HashableArguments key : sorted) {
          BinaryTextRelation goldRelation = goldMap.get(key);
          BinaryTextRelation systemRelation = systemMap.get(key);
          if (goldRelation == null) {
            System.out.println("System added: " + formatRelation(systemRelation));
          } else if (systemRelation == null) {
            System.out.println("System dropped: " + formatRelation(goldRelation));
          } else if (!systemRelation.getCategory().equals(goldRelation.getCategory())) {
            String label = systemRelation.getCategory();
            System.out.printf("System labeled %s for %s\n", label, formatRelation(goldRelation));
          } else{
            System.out.println("Nailed it! " + formatRelation(systemRelation));
          }
        }
      }
    }

    return corefStats;
  }
  
  public static class AnnotationComparator implements Comparator<Annotation> {

    @Override
    public int compare(Annotation o1, Annotation o2) {
      if(o1.getBegin() < o2.getBegin()){
        return -1;
      }else if(o1.getBegin() == o2.getBegin() && o1.getEnd() < o2.getEnd()){
        return -1;
      }else if(o1.getBegin() == o2.getBegin() && o1.getEnd() > o2.getEnd()){
        return 1;
      }else if(o2.getBegin() < o1.getBegin()){
        return 1;
      }else{
        return 0;
      }
    }
  }
  public static class DocumentIDPrinter extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    static Logger logger = Logger.getLogger(DocumentIDPrinter.class);
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
      String docId = DocumentIDAnnotationUtil.getDocumentID(jCas);
      if(docId == DocumentIDAnnotationUtil.NO_DOCUMENT_ID){
        docId = new File(ViewUriUtil.getURI(jCas)).getName();
      }
      logger.info(String.format("Processing %s\n", docId));
    }
    
  }

  @PipeBitInfo(
        name = "Gold Markables Copier",
        description = "Copies Markables from the Gold view to the System view.",
        role = PipeBitInfo.Role.SPECIAL,
        dependencies = { PipeBitInfo.TypeProduct.MARKABLE }
  )
  public static class CopyGoldMarkablesInChains extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
      JCas goldView, systemView;
      try {
        goldView = jCas.getView( GOLD_VIEW_NAME );
        systemView = jCas.getView( CAS.NAME_DEFAULT_SOFA );
      } catch ( CASException e ) {
        throw new AnalysisEngineProcessException( e );
      }
      // first remove any system markables that snuck in
      for ( Markable annotation : Lists.newArrayList( JCasUtil.select( systemView, Markable.class ) ) ) {
        annotation.removeFromIndexes();
      }

      CasCopier copier = new CasCopier( goldView.getCas(), systemView.getCas() );
      Feature sofaFeature = jCas.getTypeSystem().getFeatureByFullName( CAS.FEATURE_FULL_NAME_SOFA );
      HashSet<String> existingSpans = new HashSet<>();
      for ( CollectionTextRelation chain : JCasUtil.select(goldView, CollectionTextRelation.class)){
        for ( Markable markable : JCasUtil.select(chain.getMembers(), Markable.class)){
          // some spans are annotated twice erroneously in gold -- if we can't fix make sure we don't add twice
          // or else the evaluation script will explode.
          String key = markable.getBegin() + "-" + (markable.getEnd() - markable.getBegin());
          if(existingSpans.contains(key)) continue;
          
          Markable copy = (Markable)copier.copyFs( markable );
          copy.setFeatureValue( sofaFeature, systemView.getSofa() );
          copy.addToIndexes( systemView );
          existingSpans.add(key);
        }
      }
    }
      
    
  }
  /*
   * The Relation extractors all create relation objects but don't populate the objects inside of them
   * with pointers to the relation.
   */
  public static class RelationPropagator extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
      for(LocationOfTextRelation locRel : JCasUtil.select(jcas, LocationOfTextRelation.class)){
        IdentifiedAnnotation arg1 = (IdentifiedAnnotation) locRel.getArg1().getArgument();
        IdentifiedAnnotation arg2 = (IdentifiedAnnotation) locRel.getArg2().getArgument();
        // have to do this 3 different times because there is no intermediate class between EventMention and
        // the three types that can have locations that has that location attribute.
        // for the case where there are 2 locations, we take the one whose anatomical site argument
        // has the the longer span assuming it is more specific
        if(arg1 instanceof ProcedureMention){
          ProcedureMention p = ((ProcedureMention)arg1);
          if(p.getBodyLocation() == null){
            p.setBodyLocation(locRel);
          }else{
            Annotation a = p.getBodyLocation().getArg2().getArgument();
            int oldSize = a.getEnd() - a.getBegin();
            int newSize = arg2.getEnd() - arg2.getEnd();
            if(newSize > oldSize){
              p.setBodyLocation(locRel);
            }
          }
        }else if(arg1 instanceof DiseaseDisorderMention){
          DiseaseDisorderMention d = (DiseaseDisorderMention)arg1;
          if(d.getBodyLocation() == null){
            d.setBodyLocation(locRel);
          }else{
            Annotation a = d.getBodyLocation().getArg2().getArgument();
            int oldSize = a.getEnd() - a.getBegin();
            int newSize = arg2.getEnd() - arg2.getEnd();
            if(newSize > oldSize){
              d.setBodyLocation(locRel);
            }
          }
        }else if(arg1 instanceof SignSymptomMention){
          SignSymptomMention s = (SignSymptomMention)arg1;
          if(s.getBodyLocation() == null){
            s.setBodyLocation(locRel);
          }else{
            Annotation a = s.getBodyLocation().getArg2().getArgument();
            int oldSize = a.getEnd() - a.getBegin();
            int newSize = arg2.getEnd() - arg2.getEnd();
            if(newSize > oldSize){
              s.setBodyLocation(locRel);
            }
          }          
        }
      }
    }
    
  }
  
  public static class ParagraphAnnotator extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {

    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
      List<BaseToken> tokens = new ArrayList<>(JCasUtil.select(jcas, BaseToken.class));
      BaseToken lastToken = null;
      int parStart = 0;
      
      for(int i = 0; i < tokens.size(); i++){
        BaseToken token = tokens.get(i);
        if(parStart == i && token instanceof NewlineToken){
          // we've just created a pargraph ending but there were multiple newlines -- don't want to start the
          // new paragraph until we are past the newlines -- increment the parStart index and move forward
          parStart++;
        }else if(lastToken != null && token instanceof NewlineToken){
          Paragraph par = new Paragraph(jcas, tokens.get(parStart).getBegin(), lastToken.getEnd());
          par.addToIndexes();
          parStart = i+1;
        }
        lastToken = token;
      }
      
    }
    
  }
  
  
  public static class ParagraphVectorAnnotator extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    WordEmbeddings words = null;

    @Override
    public void initialize(final UimaContext context) throws ResourceInitializationException{
      try {
        words = WordVectorReader.getEmbeddings(FileLocator.getAsStream("org/apache/ctakes/coreference/distsem/mimic_vectors.txt"));
      } catch (IOException e) {
        e.printStackTrace();
        throw new ResourceInitializationException(e);
      }
    }
    
    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
      List<Paragraph> pars = new ArrayList<>(JCasUtil.select(jcas, Paragraph.class));
      FSArray parVecs = new FSArray(jcas, pars.size());
      for(int parNum = 0; parNum < pars.size(); parNum++){
        Paragraph par = pars.get(parNum);
        float[] parVec = new float[words.getDimensionality()];

        List<BaseToken> tokens = JCasUtil.selectCovered(BaseToken.class, par);
        for(int i = 0; i < tokens.size(); i++){
          BaseToken token = tokens.get(i);
          if(token instanceof WordToken){
            String word = token.getCoveredText().toLowerCase();
            if(words.containsKey(word)){
              WordVector wv = words.getVector(word);
              for(int j = 0; j < parVec.length; j++){
                parVec[j] += wv.getValue(j);
              }
            }          
          }
        }
        normalize(parVec);
        FloatArray vec = new FloatArray(jcas, words.getDimensionality());
        vec.copyFromArray(parVec, 0, 0, parVec.length);
        vec.addToIndexes();
        parVecs.set(parNum, vec);
      }
      parVecs.addToIndexes();
    }

    private static final void normalize(float[] vec) {
      double sum = 0.0;
      for(int i = 0; i < vec.length; i++){
        sum += (vec[i]*vec[i]);
      }
      sum = Math.sqrt(sum);
      for(int i = 0; i < vec.length; i++){
        vec[i] /= sum;
      }
    }
  }

  @PipeBitInfo(
        name = "Coreference Copier",
        description = "Sets Modality based upon context.",
        role = PipeBitInfo.Role.SPECIAL,
        dependencies = { PipeBitInfo.TypeProduct.MARKABLE, PipeBitInfo.TypeProduct.COREFERENCE_RELATION }
  )
  public static class CopyCoreferenceRelations extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {

    public static final String PARAM_GOLD_VIEW = "GoldViewName";
    @ConfigurationParameter(name=PARAM_GOLD_VIEW, mandatory=true, description="View containing gold standard annotations")
    private String goldViewName;
    
    public static final String PARAM_DROP_ELEMENTS = "Dropout";
    @ConfigurationParameter(name = PARAM_DROP_ELEMENTS, mandatory=false)
    private boolean dropout = false;

    @SuppressWarnings("synthetic-access")
    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
      JCas goldView = null;
      try {
        goldView = jcas.getView(goldViewName);
      } catch (CASException e) {
        e.printStackTrace();
        throw new AnalysisEngineProcessException(e);
      }
      
      HashMap<Markable,Markable> gold2sys = new HashMap<>();
      Map<ConllDependencyNode,Collection<Markable>> depIndex = JCasUtil.indexCovering(jcas, ConllDependencyNode.class, Markable.class);
      // remove those with removed markables (person mentions)
      List<CollectionTextRelation> toRemove = new ArrayList<>();
      
      for(CollectionTextRelation goldChain : JCasUtil.select(goldView, CollectionTextRelation.class)){
        FSList head = goldChain.getMembers();
//        NonEmptyFSList sysList = new NonEmptyFSList(jcas);
//        NonEmptyFSList listEnd = sysList;
        List<List<Markable>> systemLists = new ArrayList<>(); // the gold list can be split up into many lists if we allow dropout.
        boolean removeChain = false;
        List<Markable> prevList = null;
        
        // first one is guaranteed to be nonempty otherwise it would not be in cas
        do{
          NonEmptyFSList element = (NonEmptyFSList) head;
          Markable goldMarkable = (Markable) element.getHead();
          if(goldMarkable == null){
            logger.error(String.format("Found an unexpected null gold markable"));
          }
          boolean mapped = mapGoldMarkable(jcas, goldMarkable, gold2sys, depIndex);
          
          // if we can't align the gold markable with one in the system cas then don't add it:
          if(!mapped){
            String text = "<Out of bounds>";
            if(!(goldMarkable.getBegin() < 0 || goldMarkable.getEnd() >= jcas.getDocumentText().length())){
              text = goldMarkable.getCoveredText();
            }
            logger.warn(String.format("There is a gold markable %s [%d, %d] which could not map to a system markable.", 
                text, goldMarkable.getBegin(), goldMarkable.getEnd()));
            removeChain = true;
            break;
          }
          
          Markable sysMarkable = gold2sys.get(goldMarkable);
          if(!dropout || systemLists.size() == 0){
            if(systemLists.size() == 0) systemLists.add(new ArrayList<>());
            systemLists.get(0).add(sysMarkable);
//            prevList = systemLists.get(0);
//            // if this is not first time through move listEnd to end.
//            if(listEnd.getHead() != null){
//              listEnd.setTail(new NonEmptyFSList(jcas));
//              listEnd.addToIndexes();
//              listEnd = (NonEmptyFSList) listEnd.getTail();
//            }
//
//            // add markable to end of list:
//            listEnd.setHead(gold2sys.get(goldMarkable));
          }else{
            // 3 options: Do correctly (append to same list as last element), ii) Start its own list, iii) Randomly join another list
            if(Math.random() > DROPOUT_RATE){
              // most of the time do the right thing:
              systemLists.get(0).add(sysMarkable);
            }else{
              int listIndex = (int) Math.ceil(Math.random() * systemLists.size());
              if(listIndex == systemLists.size()){
                systemLists.add(new ArrayList<>());
              }
              systemLists.get(listIndex).add(sysMarkable);
            }
          }
          head = element.getTail();
        }while(head instanceof NonEmptyFSList);
        
        // don't bother copying over -- the gold chain was of person mentions
        if(!removeChain){
//          listEnd.setTail(new EmptyFSList(jcas));
//          listEnd.addToIndexes();
//          listEnd.getTail().addToIndexes();
//          sysList.addToIndexes();
          for(List<Markable> chain : systemLists){
            if(chain.size() > 1){
              CollectionTextRelation sysRel = new CollectionTextRelation(jcas);
              sysRel.setMembers(ListFactory.buildList(jcas, chain));
              sysRel.addToIndexes();
            }
          }
        }
      }
      
      for(CoreferenceRelation goldRel : JCasUtil.select(goldView, CoreferenceRelation.class)){
        if((gold2sys.containsKey(goldRel.getArg1().getArgument()) && gold2sys.containsKey(goldRel.getArg2().getArgument()))){
          CoreferenceRelation sysRel = new CoreferenceRelation(jcas);
          sysRel.setCategory(goldRel.getCategory());
          sysRel.setDiscoveryTechnique(CONST.REL_DISCOVERY_TECH_GOLD_ANNOTATION);

          RelationArgument arg1 = new RelationArgument(jcas);
          arg1.setArgument(gold2sys.get(goldRel.getArg1().getArgument()));
          sysRel.setArg1(arg1);
          arg1.addToIndexes();

          RelationArgument arg2 = new RelationArgument(jcas);
          arg2.setArgument(gold2sys.get(goldRel.getArg2().getArgument()));
          sysRel.setArg2(arg2);
          arg2.addToIndexes();         
          
          sysRel.addToIndexes();        
        }
      }
    }
    
    private static boolean mapGoldMarkable(JCas jcas, Markable goldMarkable, Map<Markable,Markable> gold2sys, Map<ConllDependencyNode, Collection<Markable>> depIndex){
      if(!(goldMarkable.getBegin() < 0 || goldMarkable.getEnd() >= jcas.getDocumentText().length())){
        
        
        ConllDependencyNode headNode = DependencyUtility.getNominalHeadNode(jcas, goldMarkable);

        for(Markable sysMarkable : depIndex.get(headNode)){
          ConllDependencyNode markNode = DependencyUtility.getNominalHeadNode(jcas, sysMarkable);
          if(markNode == headNode){
            gold2sys.put(goldMarkable, sysMarkable);
            return true;
          }
        }
      }else{
        // Have seen some instances where anafora writes a span that is not possible, log them
        // so they can be found and fixed:
        logger.warn(String.format("There is a markable with span [%d, %d] in a document with length %d\n", 
            goldMarkable.getBegin(), goldMarkable.getEnd(), jcas.getDocumentText().length()));
        return false;
      }
      return false;
    }
  }
  
  public static class RemoveAllCoreferenceAnnotations extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
      List<CollectionTextRelation> chains = new ArrayList<>(JCasUtil.select(jcas, CollectionTextRelation.class));
      for(CollectionTextRelation chain : chains){
        NonEmptyFSList head = null;
        FSList nextHead = chain.getMembers();
        do{
          head = (NonEmptyFSList) nextHead;
          head.removeFromIndexes();
          nextHead = head.getTail();
        }while(nextHead instanceof NonEmptyFSList);
        chain.removeFromIndexes();
      }
      List<CoreferenceRelation> rels = new ArrayList<>(JCasUtil.select(jcas, CoreferenceRelation.class));
      for(CoreferenceRelation rel : rels){
        rel.getArg1().removeFromIndexes();
        rel.getArg2().removeFromIndexes();
        rel.removeFromIndexes();
      }
    }    
  }
  
  public static class RemovePersonMarkables extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {

    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
//      JCas systemView=null, goldView=null;
//      try{
//        systemView = jcas.getView(CAS.NAME_DEFAULT_SOFA);
//        goldView = jcas.getView(GOLD_VIEW_NAME);
//      }catch(Exception e){
//        throw new AnalysisEngineProcessException(e);
//      }
      List<Markable> toRemove = new ArrayList<>();
      for(Markable markable : JCasUtil.select(jcas, Markable.class)){
        if(markable.getCoveredText().equals("I")){
          System.err.println("Unauthorized markable 'I'");
        }
        List<BaseToken> coveredTokens = JCasUtil.selectCovered(jcas, BaseToken.class, markable);
        if(coveredTokens.size() == 1 && coveredTokens.get(0).getPartOfSpeech() != null &&
            coveredTokens.get(0).getPartOfSpeech().startsWith("PRP") &&
            !markable.getCoveredText().toLowerCase().equals("it")){
          toRemove.add(markable);
        }else if(coveredTokens.size() > 0 && (coveredTokens.get(0).getCoveredText().startsWith("Mr.") || coveredTokens.get(0).getCoveredText().startsWith("Dr.") ||
                coveredTokens.get(0).getCoveredText().startsWith("Mrs.") || coveredTokens.get(0).getCoveredText().startsWith("Ms."))){
          toRemove.add(markable);
        }else if(markable.getCoveredText().toLowerCase().endsWith("patient") || markable.getCoveredText().toLowerCase().equals("pt")){
          toRemove.add(markable);
        }
      }
      
      for(Markable markable : toRemove){
        markable.removeFromIndexes();
      }
    } 
  }
  
  /* This flow control section borrows from the UIMA implementation of FixedFlowController
   * and its internal Flow object. Simple change to check if there are any gold
   * coref annotations inside the cas, and if not skip out so we don't waste
   * time running coref code on those (since we're not going to print out the answers
   * anyways)
   */
  public static class CorefEvalFlowController extends org.apache.uima.flow.JCasFlowController_ImplBase {
    List<String> mSequence;

    
    @Override
    public void initialize(FlowControllerContext context)
        throws ResourceInitializationException {
      super.initialize(context);
      
      FlowConstraints flowConstraints = context.getAggregateMetadata().getFlowConstraints();
      mSequence = new ArrayList<>();
      if (flowConstraints instanceof FixedFlow) {
        String[] sequence = ((FixedFlow) flowConstraints).getFixedFlow();
        mSequence.addAll(Arrays.asList(sequence));
      } else {
        throw new ResourceInitializationException(ResourceInitializationException.FLOW_CONTROLLER_REQUIRES_FLOW_CONSTRAINTS,
                new Object[]{this.getClass().getName(), "fixedFlow", context.getAggregateMetadata().getSourceUrlString()});
      }
    }

    @Override
    public Flow computeFlow(JCas jcas) throws AnalysisEngineProcessException {
      return new CorefEvalFlow(jcas, 0);
    }
    
    class CorefEvalFlow extends JCasFlow_ImplBase {

      private JCas jcas;
      private int currentStep;

      public CorefEvalFlow(JCas jcas, int step){
        this.jcas = jcas;
        this.currentStep = step;
      }

      @Override
      public Step next() {
        // if we are past the last annotator finish
        if (currentStep >= mSequence.size()) {
          return new FinalStep();
        }

        // if we have gold standard relations, continue
        if(currentStep > 0 && mSequence.get(currentStep-1).equals(DocumentIDPrinter.class.getName())){
          JCas goldView;
          try {
            goldView = jcas.getView(GOLD_VIEW_NAME);
            if(JCasUtil.select(goldView, CoreferenceRelation.class).size() == 0){
              System.out.println("Skipping this document with no coreference relations.");
              return new FinalStep();
            }
          } catch (CASException e) {
            // no need to stop flow -- just go ahead to default simple step.
            e.printStackTrace();
          }
        }
        
        // otherwise finish
        return new SimpleStep(mSequence.get(currentStep++));
      }
    }
  }
}
