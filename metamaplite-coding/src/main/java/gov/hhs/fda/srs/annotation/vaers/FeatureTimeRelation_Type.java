
/* First created by JCasGen Mon May 15 14:04:39 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A link that represents and connects one clinical feature and possibly zero or multiple temporal features.
 * Updated by JCasGen Tue Aug 22 14:55:59 EDT 2017
 * @generated */
public class FeatureTimeRelation_Type extends RelationFeature_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = FeatureTimeRelation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
 
  /** @generated */
  final Feature casFeat_CID;
  /** @generated */
  final int     casFeatCode_CID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCID(int addr) {
        if (featOkTst && casFeat_CID == null)
      jcas.throwFeatMissing("CID", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCID(int addr, String v) {
        if (featOkTst && casFeat_CID == null)
      jcas.throwFeatMissing("CID", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_CID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TID;
  /** @generated */
  final int     casFeatCode_TID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTID(int addr) {
        if (featOkTst && casFeat_TID == null)
      jcas.throwFeatMissing("TID", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTID(int addr, String v) {
        if (featOkTst && casFeat_TID == null)
      jcas.throwFeatMissing("TID", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_TID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Relation;
  /** @generated */
  final int     casFeatCode_Relation;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRelation(int addr) {
        if (featOkTst && casFeat_Relation == null)
      jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Relation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRelation(int addr, String v) {
        if (featOkTst && casFeat_Relation == null)
      jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Relation, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public FeatureTimeRelation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_CID = jcas.getRequiredFeatureDE(casType, "CID", "uima.cas.String", featOkTst);
    casFeatCode_CID  = (null == casFeat_CID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CID).getCode();

 
    casFeat_TID = jcas.getRequiredFeatureDE(casType, "TID", "uima.cas.String", featOkTst);
    casFeatCode_TID  = (null == casFeat_TID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TID).getCode();

 
    casFeat_Relation = jcas.getRequiredFeatureDE(casType, "Relation", "uima.cas.String", featOkTst);
    casFeatCode_Relation  = (null == casFeat_Relation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Relation).getCode();

  }
}



    