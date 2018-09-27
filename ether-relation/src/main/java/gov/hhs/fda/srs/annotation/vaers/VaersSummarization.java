

/* First created by JCasGen Wed May 31 13:17:54 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** The summarization of a VAERS report.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * XML source: C:/Repositories/Code/VaersData/desc/VaersTypeSystem.xml
 * @generated */
public class VaersSummarization extends VaersFeature {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(VaersSummarization.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected VaersSummarization() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public VaersSummarization(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public VaersSummarization(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public VaersSummarization(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Summary

  /** getter for Summary - gets The textual representation of the report summary.
   * @generated
   * @return value of the feature 
   */
  public String getSummary() {
    if (VaersSummarization_Type.featOkTst && ((VaersSummarization_Type)jcasType).casFeat_Summary == null)
      jcasType.jcas.throwFeatMissing("Summary", "gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VaersSummarization_Type)jcasType).casFeatCode_Summary);}
    
  /** setter for Summary - sets The textual representation of the report summary. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSummary(String v) {
    if (VaersSummarization_Type.featOkTst && ((VaersSummarization_Type)jcasType).casFeat_Summary == null)
      jcasType.jcas.throwFeatMissing("Summary", "gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
    jcasType.ll_cas.ll_setStringValue(addr, ((VaersSummarization_Type)jcasType).casFeatCode_Summary, v);}    
   
    
  //*--------------*
  //* Feature: Narrative

  /** getter for Narrative - gets the narrative as input to summarize
   * @generated
   * @return value of the feature 
   */
  public String getNarrative() {
    if (VaersSummarization_Type.featOkTst && ((VaersSummarization_Type)jcasType).casFeat_Narrative == null)
      jcasType.jcas.throwFeatMissing("Narrative", "gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VaersSummarization_Type)jcasType).casFeatCode_Narrative);}
    
  /** setter for Narrative - sets the narrative as input to summarize 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNarrative(String v) {
    if (VaersSummarization_Type.featOkTst && ((VaersSummarization_Type)jcasType).casFeat_Narrative == null)
      jcasType.jcas.throwFeatMissing("Narrative", "gov.hhs.fda.srs.annotation.vaers.VaersSummarization");
    jcasType.ll_cas.ll_setStringValue(addr, ((VaersSummarization_Type)jcasType).casFeatCode_Narrative, v);}    
  }

    