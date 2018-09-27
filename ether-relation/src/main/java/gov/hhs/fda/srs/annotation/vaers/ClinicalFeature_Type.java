
/* First created by JCasGen Thu Apr 20 16:11:59 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** The parent type of the VAERS clinical features.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * @generated */
public class ClinicalFeature_Type extends VaersFeature_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ClinicalFeature.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
 
  /** @generated */
  final Feature casFeat_TimeID;
  /** @generated */
  final int     casFeatCode_TimeID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTimeID(int addr) {
        if (featOkTst && casFeat_TimeID == null)
      jcas.throwFeatMissing("TimeID", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TimeID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTimeID(int addr, String v) {
        if (featOkTst && casFeat_TimeID == null)
      jcas.throwFeatMissing("TimeID", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_TimeID, v);}
    
  
 
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
      jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Relation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRelation(int addr, String v) {
        if (featOkTst && casFeat_Relation == null)
      jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_Relation, v);}
    
  
 
  /** @generated */
  final Feature casFeat_atype;
  /** @generated */
  final int     casFeatCode_atype;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAtype(int addr) {
        if (featOkTst && casFeat_atype == null)
      jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_atype);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAtype(int addr, String v) {
        if (featOkTst && casFeat_atype == null)
      jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_atype, v);}
    
  
 
  /** @generated */
  final Feature casFeat_preferred_term;
  /** @generated */
  final int     casFeatCode_preferred_term;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPreferred_term(int addr) {
        if (featOkTst && casFeat_preferred_term == null)
      jcas.throwFeatMissing("preferred_term", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_preferred_term);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPreferred_term(int addr, String v) {
        if (featOkTst && casFeat_preferred_term == null)
      jcas.throwFeatMissing("preferred_term", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_preferred_term, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ClinicalFeature_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TimeID = jcas.getRequiredFeatureDE(casType, "TimeID", "uima.cas.String", featOkTst);
    casFeatCode_TimeID  = (null == casFeat_TimeID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TimeID).getCode();

 
    casFeat_Relation = jcas.getRequiredFeatureDE(casType, "Relation", "uima.cas.String", featOkTst);
    casFeatCode_Relation  = (null == casFeat_Relation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Relation).getCode();

 
    casFeat_atype = jcas.getRequiredFeatureDE(casType, "atype", "uima.cas.String", featOkTst);
    casFeatCode_atype  = (null == casFeat_atype) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_atype).getCode();

 
    casFeat_preferred_term = jcas.getRequiredFeatureDE(casType, "preferred_term", "uima.cas.String", featOkTst);
    casFeatCode_preferred_term  = (null == casFeat_preferred_term) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_preferred_term).getCode();

  }
}



    