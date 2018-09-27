
/* First created by JCasGen Wed Oct 18 11:06:50 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** A link that represents and connects one clinical feature and possibly zero or multiple temporal features.
Additionally, this link represents time-time relation as well. And thus, the name has been changed to the current one: TemporalRelation to indicate both feature-time and time-time relations
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * @generated */
public class TemporalRelation_Type extends RelationFeature_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TemporalRelation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
 
  /** @generated */
  final Feature casFeat_CoreID;
  /** @generated */
  final int     casFeatCode_CoreID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCoreID(int addr) {
        if (featOkTst && casFeat_CoreID == null)
      jcas.throwFeatMissing("CoreID", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CoreID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCoreID(int addr, String v) {
        if (featOkTst && casFeat_CoreID == null)
      jcas.throwFeatMissing("CoreID", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_CoreID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_RefID;
  /** @generated */
  final int     casFeatCode_RefID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRefID(int addr) {
        if (featOkTst && casFeat_RefID == null)
      jcas.throwFeatMissing("RefID", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_RefID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRefID(int addr, String v) {
        if (featOkTst && casFeat_RefID == null)
      jcas.throwFeatMissing("RefID", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_RefID, v);}
    
  
 
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
      jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_atype);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAtype(int addr, String v) {
        if (featOkTst && casFeat_atype == null)
      jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_atype, v);}
    
  
 
  /** @generated */
  final Feature casFeat_CoreText;
  /** @generated */
  final int     casFeatCode_CoreText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCoreText(int addr) {
        if (featOkTst && casFeat_CoreText == null)
      jcas.throwFeatMissing("CoreText", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CoreText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCoreText(int addr, String v) {
        if (featOkTst && casFeat_CoreText == null)
      jcas.throwFeatMissing("CoreText", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_CoreText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_RefText;
  /** @generated */
  final int     casFeatCode_RefText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRefText(int addr) {
        if (featOkTst && casFeat_RefText == null)
      jcas.throwFeatMissing("RefText", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_RefText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRefText(int addr, String v) {
        if (featOkTst && casFeat_RefText == null)
      jcas.throwFeatMissing("RefText", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_RefText, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TemporalRelation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_CoreID = jcas.getRequiredFeatureDE(casType, "CoreID", "uima.cas.String", featOkTst);
    casFeatCode_CoreID  = (null == casFeat_CoreID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CoreID).getCode();

 
    casFeat_RefID = jcas.getRequiredFeatureDE(casType, "RefID", "uima.cas.String", featOkTst);
    casFeatCode_RefID  = (null == casFeat_RefID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_RefID).getCode();

 
    casFeat_atype = jcas.getRequiredFeatureDE(casType, "atype", "uima.cas.String", featOkTst);
    casFeatCode_atype  = (null == casFeat_atype) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_atype).getCode();

 
    casFeat_CoreText = jcas.getRequiredFeatureDE(casType, "CoreText", "uima.cas.String", featOkTst);
    casFeatCode_CoreText  = (null == casFeat_CoreText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CoreText).getCode();

 
    casFeat_RefText = jcas.getRequiredFeatureDE(casType, "RefText", "uima.cas.String", featOkTst);
    casFeatCode_RefText  = (null == casFeat_RefText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_RefText).getCode();

  }
}



    