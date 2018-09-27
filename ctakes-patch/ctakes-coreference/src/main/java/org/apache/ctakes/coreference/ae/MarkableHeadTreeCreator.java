package org.apache.ctakes.coreference.ae;

import java.util.Comparator;
import java.util.Map;

import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.core.util.DocumentIDAnnotationUtil;
import org.apache.ctakes.dependency.parser.util.DependencyUtility;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.textsem.Markable;
import org.apache.ctakes.utils.struct.MapFactory;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.util.ViewUriUtil;

@PipeBitInfo(
      name = "Markable Head Tree Creator",
      description = "Annotates Markables.",
      dependencies = { PipeBitInfo.TypeProduct.DOCUMENT_ID,
            PipeBitInfo.TypeProduct.MARKABLE, PipeBitInfo.TypeProduct.DEPENDENCY_NODE }
)
public class MarkableHeadTreeCreator extends JCasAnnotator_ImplBase {

  private static final String MAP_KEY = "MarkableHeadMap";
  
  private static final Logger logger = Logger.getLogger(MarkableHeadTreeCreator.class);
  
  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    
  }
  
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    Map<Markable,ConllDependencyNode> treeMap = MapFactory.createInstance(getKey(jcas));
    
    for(Markable m: JCasUtil.select(jcas, Markable.class)){
      ConllDependencyNode headNode = DependencyUtility.getNominalHeadNode(jcas, m);
      treeMap.put(m, headNode);
//      markable2head.put(m, headNode);
    }
  }
  
  public static String getKey(JCas jcas){
    String docId = null;
    try{
      docId = DocumentIDAnnotationUtil.getDocumentID(jcas);
    }catch(Exception e){
      //System.err.println(e.getMessage());
    }
    if(docId == null || docId == DocumentIDAnnotationUtil.NO_DOCUMENT_ID){
      try {
        docId = ViewUriUtil.getURI(jcas).toString();
      } catch (Exception e) {
        //System.err.println(e.getMessage());
        //logger.warn("No document ID found using traditional methods. Using ad hoc combination");
        String docText = jcas.getDocumentText();
        docId = docText.substring(0, Math.min(20, docText.length())) + "_hash=" + docText.hashCode(); 
      }
    }
    return docId + "-" + MAP_KEY;
  }

  public static class MarkableDepheadPairComparator implements Comparator<Markable> {

    @Override
    public int compare(Markable m1, Markable m2) {
      // look at the start first
      if(m1.getBegin() < m2.getBegin()){
        return -1;
      }else if(m2.getBegin() < m1.getBegin()){
        return 1;
      }else if(m1.getEnd() < m2.getEnd()){
        return -1;
      }else if(m2.getEnd() < m1.getEnd()){
        return 1;
      }else{
        // m1 and m2 have the exact same span
        return 0;
      }
    }
  }
}
