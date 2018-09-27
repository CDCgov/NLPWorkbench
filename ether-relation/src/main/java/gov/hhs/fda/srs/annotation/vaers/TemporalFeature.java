

/* First created by JCasGen Thu Apr 20 16:11:59 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** The parent type of the VAERS temporal features.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * XML source: C:/Repositories/Code/VaersData/desc/VaersTypeSystem.xml
 * @generated */
public class TemporalFeature extends VaersFeature {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TemporalFeature.class);
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
  protected TemporalFeature() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TemporalFeature(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TemporalFeature(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TemporalFeature(JCas jcas, int begin, int end) {
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
  //* Feature: Date

  /** getter for Date - gets The actual date in a standard form as in “yyyy-mm-dd”.
   * @generated
   * @return value of the feature 
   */
  public String getDate() {
    if (TemporalFeature_Type.featOkTst && ((TemporalFeature_Type)jcasType).casFeat_Date == null)
      jcasType.jcas.throwFeatMissing("Date", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemporalFeature_Type)jcasType).casFeatCode_Date);}
    
  /** setter for Date - sets The actual date in a standard form as in “yyyy-mm-dd”. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDate(String v) {
    if (TemporalFeature_Type.featOkTst && ((TemporalFeature_Type)jcasType).casFeat_Date == null)
      jcasType.jcas.throwFeatMissing("Date", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemporalFeature_Type)jcasType).casFeatCode_Date, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets The raw feature text or term(s) identified/extracted in the original clinical note.
   * @generated
   * @return value of the feature 
   */
  public String getText() {
    if (TemporalFeature_Type.featOkTst && ((TemporalFeature_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemporalFeature_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets The raw feature text or term(s) identified/extracted in the original clinical note. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setText(String v) {
    if (TemporalFeature_Type.featOkTst && ((TemporalFeature_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemporalFeature_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: atype

  /** getter for atype - gets It specifies the type of the temporal feature, such as 'Data'.
   * @generated
   * @return value of the feature 
   */
  public String getAtype() {
    if (TemporalFeature_Type.featOkTst && ((TemporalFeature_Type)jcasType).casFeat_atype == null)
      jcasType.jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemporalFeature_Type)jcasType).casFeatCode_atype);}
    
  /** setter for atype - sets It specifies the type of the temporal feature, such as 'Data'. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAtype(String v) {
    if (TemporalFeature_Type.featOkTst && ((TemporalFeature_Type)jcasType).casFeat_atype == null)
      jcasType.jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.TemporalFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemporalFeature_Type)jcasType).casFeatCode_atype, v);}    
  }

    