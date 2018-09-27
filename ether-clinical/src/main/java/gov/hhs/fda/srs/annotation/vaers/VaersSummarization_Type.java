
/* First created by JCasGen Wed May 31 13:17:54 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** The summarization of a VAERS report.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * @generated */
public class VaersSummarization_Type extends VaersFeature_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = VaersSummarization.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
 
  /** @generated */
  final Feature casFeat_Summary;
  /** @generated */
  final int     casFeatCode_Summary;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSummary(int addr) {
        if (featOkTst && casFeat_Summary == null)
      jcas.throwFeatMissing("Summary", "gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Summary);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSummary(int addr, String v) {
        if (featOkTst && casFeat_Summary == null)
      jcas.throwFeatMissing("Summary", "gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
    ll_cas.ll_setStringValue(addr, casFeatCode_Summary, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Narrative;
  /** @generated */
  final int     casFeatCode_Narrative;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNarrative(int addr) {
        if (featOkTst && casFeat_Narrative == null)
      jcas.throwFeatMissing("Narrative", "gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Narrative);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNarrative(int addr, String v) {
        if (featOkTst && casFeat_Narrative == null)
      jcas.throwFeatMissing("Narrative", "gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
    ll_cas.ll_setStringValue(addr, casFeatCode_Narrative, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public VaersSummarization_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Summary = jcas.getRequiredFeatureDE(casType, "Summary", "uima.cas.String", featOkTst);
    casFeatCode_Summary  = (null == casFeat_Summary) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Summary).getCode();

 
    casFeat_Narrative = jcas.getRequiredFeatureDE(casType, "Narrative", "uima.cas.String", featOkTst);
    casFeatCode_Narrative  = (null == casFeat_Narrative) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Narrative).getCode();

  }
}



    