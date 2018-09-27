package org.apache.ctakes.coreference.ae.pairing.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ctakes.coreference.ae.MentionClusterCoreferenceAnnotator;
import org.apache.ctakes.coreference.ae.MentionClusterCoreferenceAnnotator.CollectionTextRelationIdentifiedAnnotationPair;
import org.apache.ctakes.coreference.util.ClusterUtils;
import org.apache.ctakes.dependency.parser.util.DependencyUtility;
import org.apache.ctakes.typesystem.type.relation.CollectionTextRelation;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.textsem.Markable;
import org.apache.log4j.Logger;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.tcas.Annotation;

public class HeadwordPairer extends ClusterMentionPairer_ImplBase {
  private Map<String, Set<Markable>> headWordMarkables = null;
  
  @Override
  public void reset(JCas jcas){
    super.reset(jcas);
    headWordMarkables = new HashMap<>();
  }
  
  @Override
  public List<CollectionTextRelationIdentifiedAnnotationPair> getPairs(JCas jcas, Markable mention) {
    List<CollectionTextRelationIdentifiedAnnotationPair> pairs = new ArrayList<>();

    ConllDependencyNode headNode = DependencyUtility.getNominalHeadNode(jcas, mention);
    if(headNode == null){
      Logger.getLogger(MentionClusterCoreferenceAnnotator.class).warn("There is a markable with no dependency node covering it.");
      return pairs;
    }
    String head = headNode.getCoveredText().toLowerCase();
    if(headWordMarkables.containsKey(head)){
      Set<Markable> headSet = headWordMarkables.get(head);
      for(CollectionTextRelation cluster : JCasUtil.select(jcas, CollectionTextRelation.class)){
        Annotation mostRecent = ClusterUtils.getMostRecent((NonEmptyFSList)cluster.getMembers(), mention);
        if(mostRecent == null) continue;
        for(Markable m : JCasUtil.select(cluster.getMembers(), Markable.class)){
          if(headSet.contains(mostRecent)){
            pairs.add(new CollectionTextRelationIdentifiedAnnotationPair(cluster, mention));
            break;
          }
          if(m == mostRecent) break;
        }
      }      
    }else{    
      headWordMarkables.put(head, new HashSet<Markable>());
    }
    headWordMarkables.get(head).add(mention);
    
    return pairs;  
  }
}
