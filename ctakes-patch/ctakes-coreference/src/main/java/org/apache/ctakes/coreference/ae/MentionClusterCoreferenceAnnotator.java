package org.apache.ctakes.coreference.ae;

import static org.apache.ctakes.core.pipeline.PipeBitInfo.TypeProduct.*;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.core.util.ListFactory;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterAgreementFeaturesExtractor;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterAttributeFeaturesExtractor;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterDepHeadExtractor;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterSalienceFeaturesExtractor;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterSectionFeaturesExtractor;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterSemTypeDepPrefsFeatureExtractor;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterStackFeaturesExtractor;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterStringFeaturesExtractor;
import org.apache.ctakes.coreference.ae.features.cluster.MentionClusterUMLSFeatureExtractor;
import org.apache.ctakes.coreference.ae.pairing.cluster.ClusterMentionPairer_ImplBase;
import org.apache.ctakes.coreference.ae.pairing.cluster.ClusterPairer;
import org.apache.ctakes.coreference.ae.pairing.cluster.HeadwordPairer;
import org.apache.ctakes.coreference.ae.pairing.cluster.SectionHeaderPairer;
import org.apache.ctakes.coreference.ae.pairing.cluster.SentenceDistancePairer;
import org.apache.ctakes.coreference.util.MarkableUtilities;
import org.apache.ctakes.relationextractor.ae.features.RelationFeaturesExtractor;
import org.apache.ctakes.relationextractor.eval.RelationExtractorEvaluation.HashableArguments;
import org.apache.ctakes.typesystem.type.refsem.AnatomicalSite;
import org.apache.ctakes.typesystem.type.refsem.DiseaseDisorder;
import org.apache.ctakes.typesystem.type.refsem.Element;
import org.apache.ctakes.typesystem.type.refsem.Event;
import org.apache.ctakes.typesystem.type.refsem.Medication;
import org.apache.ctakes.typesystem.type.refsem.Procedure;
import org.apache.ctakes.typesystem.type.refsem.SignSymptom;
import org.apache.ctakes.typesystem.type.relation.CollectionTextRelation;
import org.apache.ctakes.typesystem.type.relation.CollectionTextRelationIdentifiedAnnotationRelation;
import org.apache.ctakes.typesystem.type.relation.CoreferenceRelation;
import org.apache.ctakes.typesystem.type.textsem.AnatomicalSiteMention;
import org.apache.ctakes.typesystem.type.textsem.DiseaseDisorderMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.Markable;
import org.apache.ctakes.typesystem.type.textsem.MedicationMention;
import org.apache.ctakes.typesystem.type.textsem.ProcedureMention;
import org.apache.ctakes.typesystem.type.textsem.SignSymptomMention;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.ctakes.utils.struct.CounterMap;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkAnnotator;
import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.DataWriter;
import org.cleartk.ml.Feature;
import org.cleartk.ml.Instance;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.jar.DefaultDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import org.cleartk.util.ViewUriUtil;
@PipeBitInfo(
	      name = "Coreference (Clusters)",
	      description = "Coreference annotator using mention-synchronous paradigm.",
   	      dependencies = { BASE_TOKEN, SENTENCE, SECTION, IDENTIFIED_ANNOTATION, MARKABLE },
   	      products = { COREFERENCE_RELATION }
	)
public class MentionClusterCoreferenceAnnotator extends CleartkAnnotator<String> {
  public static final String NO_RELATION_CATEGORY = "-NONE-";
  public static final String PARAM_PROBABILITY_OF_KEEPING_A_NEGATIVE_EXAMPLE =
      "ProbabilityOfKeepingANegativeExample";
  @ConfigurationParameter(
      name = PARAM_PROBABILITY_OF_KEEPING_A_NEGATIVE_EXAMPLE,
      mandatory = false,
      description = "probability that a negative example should be retained for training")
  protected double probabilityOfKeepingANegativeExample = 0.5;

  public static final String PARAM_USE_EXISTING_ENCODERS="UseExistingEncoders";
  @ConfigurationParameter(name = PARAM_USE_EXISTING_ENCODERS,
      mandatory=false,
      description = "Whether to use encoders in output directory during data writing; if we are making multiple calls")
  private boolean useExistingEncoders=false;
      
  protected Random coin = new Random(0);

  boolean greedyFirst = true;
  
  private static DataWriter<String> classDataWriter = null;
  
  public static AnalysisEngineDescription createDataWriterDescription(
      Class<? extends DataWriter<String>> dataWriterClass,
      File outputDirectory,
      float downsamplingRate) throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngineDescription(
        MentionClusterCoreferenceAnnotator.class,
        CleartkAnnotator.PARAM_IS_TRAINING,
        true,
        MentionClusterCoreferenceAnnotator.PARAM_PROBABILITY_OF_KEEPING_A_NEGATIVE_EXAMPLE,
        downsamplingRate,
        DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
        dataWriterClass,
        DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
        outputDirectory);
  }

  public static AnalysisEngineDescription createAnnotatorDescription(
      String modelPath) throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngineDescription(
        MentionClusterCoreferenceAnnotator.class,
        CleartkAnnotator.PARAM_IS_TRAINING,
        false,
        GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
        modelPath);
  }

  private List<RelationFeaturesExtractor<CollectionTextRelation,IdentifiedAnnotation>> relationExtractors = this.getFeatureExtractors();
  private List<FeatureExtractor1<Markable>> mentionExtractors = this.getMentionExtractors();
  private List<ClusterMentionPairer_ImplBase> pairExtractors = this.getPairExtractors();
  
//  private Set<String> markableStrings = null;
  
  protected List<RelationFeaturesExtractor<CollectionTextRelation,IdentifiedAnnotation>> getFeatureExtractors() {
    List<RelationFeaturesExtractor<CollectionTextRelation,IdentifiedAnnotation>> extractors = new ArrayList<>();
    extractors.add(new MentionClusterAgreementFeaturesExtractor());
    extractors.add(new MentionClusterStringFeaturesExtractor());
    extractors.add(new MentionClusterSectionFeaturesExtractor());
    extractors.add(new MentionClusterUMLSFeatureExtractor());
    extractors.add(new MentionClusterDepHeadExtractor());
    extractors.add(new MentionClusterStackFeaturesExtractor());
    extractors.add(new MentionClusterSalienceFeaturesExtractor());
    extractors.add(new MentionClusterAttributeFeaturesExtractor());
//    extractors.add(new MentionClusterAttributeVectorExtractor()); // does nothing yet
    
//    extractors.add(new MentionClusterDistanceFeaturesExtractor());
    
    try {
//      extractors.add(new MentionClusterDistSemExtractor("org/apache/ctakes/coreference/distsem/mimic_vectors.txt"));
//      extractors.add(new MentionClusterDistSemExtractor("org/apache/ctakes/coreference/distsem/deps.words"));
      extractors.add(new MentionClusterSemTypeDepPrefsFeatureExtractor());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return extractors;
  }
  
  protected List<FeatureExtractor1<Markable>> getMentionExtractors(){
    List<FeatureExtractor1<Markable>> extractors = new ArrayList<>();
    // mention features from pairwise system:
    extractors.add(new MentionClusterAgreementFeaturesExtractor());
    extractors.add(new MentionClusterSectionFeaturesExtractor());
    extractors.add(new MentionClusterUMLSFeatureExtractor());
    extractors.add(new MentionClusterDepHeadExtractor());
    extractors.add(new MentionClusterSalienceFeaturesExtractor());

//    try{
//      extractors.add(new MentionClusterMentionFeaturesExtractor("org/apache/ctakes/coreference/distsem/ties1mil.lowercase.txt"));
//    }catch(CleartkExtractorException e){
//      e.printStackTrace();
//    }
    extractors.add(new MentionClusterAttributeFeaturesExtractor());

    return extractors;
  }
  
  protected List<ClusterMentionPairer_ImplBase> getPairExtractors(){
    List<ClusterMentionPairer_ImplBase> pairers = new ArrayList<>();
    int sentDist = 5;
    pairers.add(new SentenceDistancePairer(sentDist));
    pairers.add(new SectionHeaderPairer(sentDist));
    pairers.add(new ClusterPairer(Integer.MAX_VALUE));
    pairers.add(new HeadwordPairer());
    return pairers;
  }
  
  protected Iterable<CollectionTextRelationIdentifiedAnnotationPair> getCandidateRelationArgumentPairs(
      JCas jcas,
      Markable mention){
    LinkedHashSet<CollectionTextRelationIdentifiedAnnotationPair> pairs = new LinkedHashSet<>();   
    for(ClusterMentionPairer_ImplBase pairer : this.pairExtractors){
      pairs.addAll(pairer.getPairs(jcas, mention));
    }
   
    return pairs;
  }
  
  private void resetPairers(JCas jcas){
    for(ClusterMentionPairer_ImplBase pairer : this.pairExtractors){
      pairer.reset(jcas);
    }
  }
   
  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    
    if(this.useExistingEncoders && classDataWriter != null){
      this.dataWriter = classDataWriter;
    }else if(this.isTraining()){
      classDataWriter = this.dataWriter;
    }
  }
  
  @Override
  public void process(JCas jCas) throws AnalysisEngineProcessException {
    // lookup from pair of annotations to binary text relation
    // note: assumes that there will be at most one relation per pair
    this.resetPairers(jCas);
    
    Map<CollectionTextRelationIdentifiedAnnotationPair, CollectionTextRelationIdentifiedAnnotationRelation> relationLookup;
    relationLookup = new HashMap<>();
    if (this.isTraining()) {
      for (CollectionTextRelation cluster : JCasUtil.select(jCas, CollectionTextRelation.class)) {
        for(IdentifiedAnnotation mention : JCasUtil.select(cluster.getMembers(), Markable.class)){
          CollectionTextRelationIdentifiedAnnotationRelation relation = 
              new CollectionTextRelationIdentifiedAnnotationRelation(jCas);
          relation.setCluster(cluster);
          relation.setMention(mention);
          relation.setCategory("CoreferenceClusterMember");
          relation.addToIndexes();
          // The key is a list of args so we can do bi-directional lookup
          CollectionTextRelationIdentifiedAnnotationPair key = new CollectionTextRelationIdentifiedAnnotationPair(cluster, mention);
          if(relationLookup.containsKey(key)){
            String cat = relationLookup.get(key).getCategory();
            System.err.println("Error in: "+ ViewUriUtil.getURI(jCas).toString());
            System.err.println("Error! This attempted relation " + relation.getCategory() + " already has a relation " + cat + " at this span: " + mention.getCoveredText());
          }
          relationLookup.put(key, relation);
        }
      }
    }

    
    for(Segment segment : JCasUtil.select(jCas, Segment.class)){
      for(Markable mention : JCasUtil.selectCovered(jCas, Markable.class, segment)){
//        ConllDependencyNode headNode = DependencyUtility.getNominalHeadNode(jCas, mention);
        boolean singleton = true;
        double maxScore = 0.0;
        CollectionTextRelation maxCluster = null;
        
        for(CollectionTextRelationIdentifiedAnnotationPair pair : this.getCandidateRelationArgumentPairs(jCas, mention)){
          CollectionTextRelation cluster = pair.getCluster();
          // apply all the feature extractors to extract the list of features
          List<Feature> features = new ArrayList<>();
          for (RelationFeaturesExtractor<CollectionTextRelation,IdentifiedAnnotation> extractor : this.relationExtractors) {
            List<Feature> feats = extractor.extract(jCas, cluster, mention);
            if (feats != null){
//              Logger.getRootLogger().info(String.format("For cluster with %d mentions, %d %s features", JCasUtil.select(cluster.getMembers(), Markable.class).size(), feats.size(), extractor.getClass().getSimpleName()));
              features.addAll(feats);
            }
          }
                 
          for(FeatureExtractor1<Markable> extractor : this.mentionExtractors){
            features.addAll(extractor.extract(jCas, mention));
          }
          
          // here is where feature conjunctions can go (dupFeatures)
          List<Feature> dupFeatures = new ArrayList<>();
          // sanity check on feature values
          for (Feature feature : features) {
            if (feature.getValue() == null) {
              feature.setValue("NULL");
              String message = String.format("Null value found in %s from %s", feature, features);
              System.err.println(message);
            }            
          }
          
          features.addAll(dupFeatures);
                   
          // during training, feed the features to the data writer
          if (this.isTraining()) {
            String category = this.getRelationCategory(relationLookup, cluster, mention);
            if (category == null) {
              continue;
            }

            // create a classification instance and write it to the training data
            this.dataWriter.write(new Instance<>(category, features));
            if(!category.equals(NO_RELATION_CATEGORY)){
              singleton = false;
              break;
            }
          }

          // during classification feed the features to the classifier and create
          // annotations
          else {
            String predictedCategory = this.classify(features);
            // TODO look at scores in classifier and try best-pair rather than first-pair?
            Map<String,Double> scores = this.classifier.score(features);
            
            // add a relation annotation if a true relation was predicted
            if (!predictedCategory.equals(NO_RELATION_CATEGORY)) {
//              Logger.getLogger("MCAnnotator").info(String.format("Making a pair with score %f", scores.get(predictedCategory)));
              if(greedyFirst){
                createRelation(jCas, cluster, mention, predictedCategory, scores.get(predictedCategory));
                singleton = false;
                // break here for "closest-first" greedy decoding strategy (Soon et al., 2001), terminology from Lasalle and Denis (2013),
                // for "best first" need to keep track of all relations with scores and only keep the highest
                break;
              }
              if(scores.get(predictedCategory) > maxScore){
            	  maxScore = scores.get(predictedCategory);
            	  maxCluster = cluster;
              }
            }
          }
        }
        if(!this.isTraining() && !greedyFirst && maxCluster != null){
          // make a link with the max cluster
          createRelation(jCas, maxCluster, mention, "CoreferenceClusterMember", maxScore);
        }
                       
        // if we got this far and never matched up the markable then add it to list.
        // do this even during training -- adds non-chain markables to antecedent list which will be seen during testing.
        if(singleton){
          // make the markable it's own cluster:
          CollectionTextRelation chain = new CollectionTextRelation(jCas);
          chain.setCategory("Identity");
          NonEmptyFSList list = new NonEmptyFSList(jCas);
          list.setHead(mention);
          list.setTail(new EmptyFSList(jCas));
          chain.setMembers(list);
          chain.addToIndexes();
          list.addToIndexes();
          list.getTail().addToIndexes();
        }
      }
    }
    
    removeSingletonClusters(jCas);
    
    createEventClusters(jCas);
  }
  
 
  /**
   * Looks up the arguments in the specified lookup table and converts the
   * relation into a label for classification
   * 
   * @return If this category should not be processed for training return
   *         <i>null</i> otherwise it returns the label sent to the datawriter
   */
  protected String getRelationCategory(
      Map<CollectionTextRelationIdentifiedAnnotationPair, CollectionTextRelationIdentifiedAnnotationRelation> relationLookup,
      CollectionTextRelation cluster,
      IdentifiedAnnotation mention) {
    CollectionTextRelationIdentifiedAnnotationRelation relation = 
        relationLookup.get(new CollectionTextRelationIdentifiedAnnotationPair(cluster, mention));
    String category;
    if (relation != null) {
      category = relation.getCategory();
    } else if (coin.nextDouble() <= this.probabilityOfKeepingANegativeExample) {
      category = NO_RELATION_CATEGORY;
    } else {
      category = null;
    }
    return category;
  }

  /**
   * Predict an outcome given a set of features. By default, this simply
   * delegates to the object's <code>classifier</code>. Subclasses may override
   * this method to implement more complex classification procedures.
   * 
   * @param features
   *          The features to be classified.
   * @return The predicted outcome (label) for the features.
   */
  protected String classify(List<Feature> features) throws CleartkProcessingException {
    return this.classifier.classify(features);
  }

  /**
   * Create a UIMA relation type based on arguments and the relation label. This
   * allows subclasses to create/define their own types: e.g. coreference can
   * create CoreferenceRelation instead of BinaryTextRelation
   * 
   * @param jCas
   *          - JCas object, needed to create new UIMA types
   * @param arg1
   *          - First argument to relation
   * @param arg2
   *          - Second argument to relation
   * @param predictedCategory
   *          - Name of relation
   */
  protected void createRelation(
      JCas jCas,
      CollectionTextRelation cluster,
      IdentifiedAnnotation mention,
      String predictedCategory,
      Double confidence) {
    // add the relation to the CAS
    CollectionTextRelationIdentifiedAnnotationRelation relation = new CollectionTextRelationIdentifiedAnnotationRelation(jCas);
    relation.setCluster(cluster);
    relation.setMention(mention);
    relation.setCategory(predictedCategory);
    relation.setConfidence(confidence);
    relation.addToIndexes();
    
//    RelationArgument arg = new RelationArgument(jCas);
//    arg.setArgument(mention);
    ListFactory.append(jCas, cluster.getMembers(), mention);    
  }

  /**
   * Create the set of Event types for every chain we found in the document.
   * Event is a non-Annotation type (i.e., no span) that has its own attributes
   * but points to an FSArray of mentions which each have their own attributes.
   * 
   * @param jCas
   *        - JCas object, needed to create UIMA types
   * @throws AnalysisEngineProcessException 
   */
  private static void createEventClusters(JCas jCas) throws AnalysisEngineProcessException{
    // First, find the largest span identified annotation that shares a headword with the markable
    // do that by finding the head of the markable, then finding the identifiedannotations that cover it:
    
    Map<Markable, List<IdentifiedAnnotation>> markable2annotations = MarkableUtilities.indexCoveringUmlsAnnotations(jCas);

    for(CollectionTextRelation cluster : JCasUtil.select(jCas, CollectionTextRelation.class)){
      CounterMap<Class<? extends IdentifiedAnnotation>> headCounts = new CounterMap<>();
      List<Markable> memberList = new ArrayList<>(JCasUtil.select(cluster.getMembers(), Markable.class));
      for(Markable member : memberList){
        // Now find the largest covering annotation:
        IdentifiedAnnotation largest = null;
        for(IdentifiedAnnotation covering : markable2annotations.get(member)){
          if(largest == null || (covering.getEnd()-covering.getBegin() > (largest.getEnd()-largest.getBegin()))){
            largest = covering;
          }
        }
        if(largest != null){
          headCounts.add(largest.getClass());
        }
      }
      FSArray mentions = new FSArray(jCas, memberList.size());
      IntStream.range(0, memberList.size()).forEach(i -> mentions.set(i, memberList.get(i)));

      Element element = null;
      if(headCounts.size() == 0){
        element = new Event(jCas);
      }else{
        Class<? extends IdentifiedAnnotation> mostCommon = headCounts.entrySet().stream()
            .sorted(Map.Entry.<Class<? extends IdentifiedAnnotation>,Integer>comparingByValue().reversed())
            .limit(1)
            .map(f -> f.getKey())
            .collect(Collectors.toList()).get(0);
        if(mostCommon.equals(DiseaseDisorderMention.class)){
          element = new DiseaseDisorder(jCas);
        }else if(mostCommon.equals(ProcedureMention.class)){
          element = new Procedure(jCas);
        }else if(mostCommon.equals(SignSymptomMention.class)){
          element = new SignSymptom(jCas);
        }else if(mostCommon.equals(MedicationMention.class)){
          element = new Medication(jCas);
        }else if(mostCommon.equals(AnatomicalSiteMention.class)){
          element = new AnatomicalSite(jCas);
        }else{
          System.err.println("This coreference chain has an unknown type: " + mostCommon.getSimpleName());
          throw new AnalysisEngineProcessException();
        }
      }
      element.setMentions(mentions);
      element.addToIndexes();
    }
  }

  private static void removeSingletonClusters(JCas jcas){
    List<CollectionTextRelation> toRemove = new ArrayList<>();
    for(CollectionTextRelation rel : JCasUtil.select(jcas, CollectionTextRelation.class)){     
      NonEmptyFSList head = (NonEmptyFSList) rel.getMembers();
      if(head.getTail() instanceof EmptyFSList){
        toRemove.add(rel);
      }
    }
    
    for(CollectionTextRelation rel : toRemove){
      rel.removeFromIndexes();
    }
  }
  
 
//  private static final boolean dominates(Annotation arg1, Annotation arg2) {
//    return (arg1.getBegin() <= arg2.getBegin() && arg1.getEnd() >= arg2.getEnd());
//  }

  /*
  public Set<String> getBestEnt(JCas jcas, CollectionTextRelation cluster){
    Set<String> semTypes = new HashSet<>();
    for(Markable member : JCasUtil.select(cluster.getMembers(), Markable.class)){
      semTypes.addAll(getBestEnt(jcas, member));
    }
    return semTypes;
  }
  
  public Set<String> getBestEnt(JCas jcas, Markable markable){
    Set<String> bestEnts = new HashSet<>();
    IdentifiedAnnotation bestEnt = null;
    Set<IdentifiedAnnotation> otherBestEnts = new HashSet<>();
    ConllDependencyNode head = DependencyUtility.getNominalHeadNode(jcas, markable);
    Collection<IdentifiedAnnotation> coveringEnts = nodeEntMap.get(head);
    for(IdentifiedAnnotation ent : coveringEnts){
      if(ent.getOntologyConceptArr() == null) continue; // skip non-umls entities.
      ConllDependencyNode entHead = DependencyUtility.getNominalHeadNode(jcas, ent);
      if(entHead == head){
        if(bestEnt == null){
          bestEnt = ent;
        }else if((ent.getEnd()-ent.getBegin()) > (bestEnt.getEnd() - bestEnt.getBegin())){
          // if the span of this entity is bigger than the biggest existing one:
          bestEnt = ent;
          otherBestEnts = new HashSet<>();
        }else if((ent.getEnd()-ent.getBegin()) == (bestEnt.getEnd() - bestEnt.getBegin())){
          // there is another one with the exact same span and possibly different type!
          otherBestEnts.add(ent);
        }
      }
    }

    if(bestEnt!=null){
      bestEnts.add(bestEnt.getClass().getSimpleName());
      for(IdentifiedAnnotation other : otherBestEnts){
        bestEnts.add(other.getClass().getSimpleName());
      }
    }
    return bestEnts;
  }
  */
  
  public Map<HashableArguments, Double> getMarkablePairScores(JCas jCas){
    Map<HashableArguments, Double> scoreMap = new HashMap<>();
    for(CoreferenceRelation reln : JCasUtil.select(jCas, CoreferenceRelation.class)){
      HashableArguments pair = new HashableArguments(reln.getArg1().getArgument(), reln.getArg2().getArgument());
      scoreMap.put(pair, reln.getConfidence());
    }
    return scoreMap;
  }
  
  public static class CollectionTextRelationIdentifiedAnnotationPair {
    private final CollectionTextRelation cluster;
    private final IdentifiedAnnotation mention;
    
    public CollectionTextRelationIdentifiedAnnotationPair(CollectionTextRelation cluster, IdentifiedAnnotation mention){
      this.cluster = cluster;
      this.mention = mention;
    }
    
    public final CollectionTextRelation getCluster(){
      return this.cluster;
    }
    
    public final IdentifiedAnnotation getMention(){
      return this.mention;
    }
    
    @Override
    public boolean equals(Object obj) {
      CollectionTextRelationIdentifiedAnnotationPair other = (CollectionTextRelationIdentifiedAnnotationPair) obj;
      return (this.cluster == other.cluster &&
          this.mention == other.mention);
    }
    
    @Override
    public int hashCode() {
      return 31*cluster.hashCode() + (mention==null ? 0 : mention.hashCode());
    }
  }

}
