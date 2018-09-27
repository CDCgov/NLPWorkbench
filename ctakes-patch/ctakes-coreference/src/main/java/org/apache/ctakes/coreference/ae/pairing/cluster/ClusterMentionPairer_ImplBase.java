package org.apache.ctakes.coreference.ae.pairing.cluster;

import org.apache.ctakes.coreference.ae.MentionClusterCoreferenceAnnotator.CollectionTextRelationIdentifiedAnnotationPair;
import org.apache.ctakes.coreference.ae.pairing.AnnotationPairer;
import org.apache.ctakes.typesystem.type.relation.CollectionTextRelation;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.Markable;
import org.apache.ctakes.utils.struct.MapFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.*;

import static org.apache.ctakes.coreference.ae.MarkableHeadTreeCreator.getKey;

//import org.apache.ctakes.dependency.parser.util.DependencyUtility;

public abstract class ClusterMentionPairer_ImplBase implements AnnotationPairer<Markable, CollectionTextRelationIdentifiedAnnotationPair> {
  public abstract List<CollectionTextRelationIdentifiedAnnotationPair> getPairs(JCas jcas, Markable m);
  private Map<ConllDependencyNode,Collection<IdentifiedAnnotation>> nodeEntMap = null;

  @Override
  public void reset(JCas jcas){
    nodeEntMap = JCasUtil.indexCovering(jcas, ConllDependencyNode.class, IdentifiedAnnotation.class);
  }
  
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
    ConllDependencyNode head = MapFactory.get(getKey(jcas), markable);
    if ( head == null ) {
      return Collections.emptySet();
    }

    Collection<IdentifiedAnnotation> coveringEnts = nodeEntMap.get(head);
    for(IdentifiedAnnotation ent : coveringEnts){
      if(ent.getOntologyConceptArr() == null) continue; // skip non-umls entities.
      ConllDependencyNode entHead = MapFactory.get(getKey(jcas), ent);
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

  protected static final boolean dominates(Annotation arg1, Annotation arg2) {
    return (arg1.getBegin() <= arg2.getBegin() && arg1.getEnd() >= arg2.getEnd());
  }
}
