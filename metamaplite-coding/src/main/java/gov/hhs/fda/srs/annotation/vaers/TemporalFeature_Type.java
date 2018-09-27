
/* First created by JCasGen Thu Apr 20 16:11:59 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** The parent type of the VAERS temporal features.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * @generated */
public class TemporalFeature_Type extends VaersFeature_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TemporalFeature.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
 
  /** @generated */
  final Feature casFeat_Date;
  /** @generated */
  final int     casFeatCode_Date;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDate(int addr) {
        if (featOkTst && casFeat_Date == null)
      jcas.throwFeatMissing("Date", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Date);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDate(int addr, String v) {
        if (featOkTst && casFeat_Date == null)
      jcas.throwFeatMissing("Date", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_Date, v);}
    
  
 
  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  
 
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
      jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_atype);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAtype(int addr, String v) {
        if (featOkTst && casFeat_atype == null)
      jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_atype, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TemporalFeature_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Date = jcas.getRequiredFeatureDE(casType, "Date", "uima.cas.String", featOkTst);
    casFeatCode_Date  = (null == casFeat_Date) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Date).getCode();

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_atype = jcas.getRequiredFeatureDE(casType, "atype", "uima.cas.String", featOkTst);
    casFeatCode_atype  = (null == casFeat_atype) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_atype).getCode();

  }
}



    