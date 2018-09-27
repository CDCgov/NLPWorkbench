
/* First created by JCasGen Mon May 15 14:25:40 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** This FeatureFeatureRelation potenitally connects multiple clinical features.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * @generated */
public class TimeTimeRelation_Type extends RelationFeature_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TimeTimeRelation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
 
  /** @generated */
  final Feature casFeat_TID1;
  /** @generated */
  final int     casFeatCode_TID1;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTID1(int addr) {
        if (featOkTst && casFeat_TID1 == null)
      jcas.throwFeatMissing("TID1", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TID1);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTID1(int addr, String v) {
        if (featOkTst && casFeat_TID1 == null)
      jcas.throwFeatMissing("TID1", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_TID1, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TID2;
  /** @generated */
  final int     casFeatCode_TID2;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTID2(int addr) {
        if (featOkTst && casFeat_TID2 == null)
      jcas.throwFeatMissing("TID2", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TID2);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTID2(int addr, String v) {
        if (featOkTst && casFeat_TID2 == null)
      jcas.throwFeatMissing("TID2", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_TID2, v);}
    
  
 
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
      jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Relation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRelation(int addr, String v) {
        if (featOkTst && casFeat_Relation == null)
      jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Relation, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TimeTimeRelation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TID1 = jcas.getRequiredFeatureDE(casType, "TID1", "uima.cas.String", featOkTst);
    casFeatCode_TID1  = (null == casFeat_TID1) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TID1).getCode();

 
    casFeat_TID2 = jcas.getRequiredFeatureDE(casType, "TID2", "uima.cas.String", featOkTst);
    casFeatCode_TID2  = (null == casFeat_TID2) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TID2).getCode();

 
    casFeat_Relation = jcas.getRequiredFeatureDE(casType, "Relation", "uima.cas.String", featOkTst);
    casFeatCode_Relation  = (null == casFeat_Relation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Relation).getCode();

  }
}



    