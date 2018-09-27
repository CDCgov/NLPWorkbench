package org.apache.ctakes.coreference.ae.features.cluster;

import static org.apache.ctakes.coreference.ae.MarkableHeadTreeCreator.getKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ctakes.core.util.ListIterable;
import org.apache.ctakes.coreference.ae.features.StringMatchingFeatureExtractor;
import org.apache.ctakes.relationextractor.ae.features.RelationFeaturesExtractor;
import org.apache.ctakes.typesystem.type.relation.CollectionTextRelation;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.Markable;
import org.apache.ctakes.utils.struct.MapFactory;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

public class MentionClusterDepHeadExtractor implements
    RelationFeaturesExtractor<CollectionTextRelation, IdentifiedAnnotation>, FeatureExtractor1<Markable> {

  @Override
  public List<Feature> extract(JCas jCas, CollectionTextRelation cluster,
      IdentifiedAnnotation mention) throws AnalysisEngineProcessException {
    List<Feature> feats = new ArrayList<>();
    
    ConllDependencyNode mentionHead = MapFactory.get(getKey(jCas), mention);
    Set<String> memberHeads = new HashSet<>();
    Set<String> memberPaths = new HashSet<>();
    
    for(Markable member : new ListIterable<Markable>(cluster.getMembers())){
      if(member.getBegin() > mention.getEnd()) break;
      ConllDependencyNode memberHead = MapFactory.get(getKey(jCas), member);
      if(memberHead != null){
        String headWord = memberHead.getCoveredText().toLowerCase();
        memberHeads.add(headWord);
        memberPaths.add(memberHead.getDeprel());
      }
//      DependencyPath path = DependencyUtility.getPathToTop(jCas, memberHead);
    }
//    for(String headWord : memberHeads){
//      feats.add(new Feature("MemberHead", headWord));
//    }
//    for(String path : memberPaths){
//      feats.add(new Feature("MemberRel", path));
//    }
    
    if(mentionHead != null){
      String headWord = mentionHead.getCoveredText().toLowerCase();
//      feats.add(new Feature("MentionRel", mentionHead.getDeprel()));
//      feats.add(new Feature("MentionHead", headWord));
      if(memberHeads.contains(headWord) && !StringMatchingFeatureExtractor.isPronoun(mention)){
        feats.add(new Feature("ClusterHeadMatchesMentionHead", true));
      }
    }
    
    return feats;
  }

  @Override
  public List<Feature> extract(JCas jCas, Markable mention) throws CleartkExtractorException {
    List<Feature> feats = new ArrayList<>();
    ConllDependencyNode mentionHead = MapFactory.get(getKey(jCas), mention);

    if(mentionHead != null){
      feats.add(new Feature("MentionRel", mentionHead.getDeprel()));
    }
    
    return feats;
  }

}
