

/* First created by JCasGen Wed Oct 18 11:06:50 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** A link that represents and connects one clinical feature and possibly zero or multiple temporal features.
Additionally, this link represents time-time relation as well. And thus, the name has been changed to the current one: TemporalRelation to indicate both feature-time and time-time relations
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * XML source: C:/Repositories/Code/VaersData/desc/VaersTypeSystem.xml
 * @generated */
public class TemporalRelation extends RelationFeature {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TemporalRelation.class);
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
  protected TemporalRelation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TemporalRelation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TemporalRelation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TemporalRelation(JCas jcas, int begin, int end) {
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
  //* Feature: CoreID

  /** getter for CoreID - gets Clinical Feature ID, as Core information ID.
   * @generated
   * @return value of the feature 
   */
  public String getCoreID() {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_CoreID == null)
      jcasType.jcas.throwFeatMissing("CoreID", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_CoreID);}
    
  /** setter for CoreID - sets Clinical Feature ID, as Core information ID. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCoreID(String v) {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_CoreID == null)
      jcasType.jcas.throwFeatMissing("CoreID", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_CoreID, v);}    
   
    
  //*--------------*
  //* Feature: RefID

  /** getter for RefID - gets Multiple (i.e., zero to more-than-one) temporal features can be associated with a clinical feature ID, as a reference. And thus, the name has been changed into RefID to indicate that it is a reference of the link (i.e., relation).
   * @generated
   * @return value of the feature 
   */
  public String getRefID() {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_RefID == null)
      jcasType.jcas.throwFeatMissing("RefID", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_RefID);}
    
  /** setter for RefID - sets Multiple (i.e., zero to more-than-one) temporal features can be associated with a clinical feature ID, as a reference. And thus, the name has been changed into RefID to indicate that it is a reference of the link (i.e., relation). 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRefID(String v) {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_RefID == null)
      jcasType.jcas.throwFeatMissing("RefID", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_RefID, v);}    
   
    
  //*--------------*
  //* Feature: atype

  /** getter for atype - gets The values may include 'BEFORE', 'AFTER', and 'OVERLAP', etc., and may be expanded in the future.
   * @generated
   * @return value of the feature 
   */
  public String getAtype() {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_atype == null)
      jcasType.jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_atype);}
    
  /** setter for atype - sets The values may include 'BEFORE', 'AFTER', and 'OVERLAP', etc., and may be expanded in the future. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAtype(String v) {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_atype == null)
      jcasType.jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_atype, v);}    
   
    
  //*--------------*
  //* Feature: CoreText

  /** getter for CoreText - gets clinical text of the CID, e.g., "vaccination chickenpox" of a particular CID.
   * @generated
   * @return value of the feature 
   */
  public String getCoreText() {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_CoreText == null)
      jcasType.jcas.throwFeatMissing("CoreText", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_CoreText);}
    
  /** setter for CoreText - sets clinical text of the CID, e.g., "vaccination chickenpox" of a particular CID. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCoreText(String v) {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_CoreText == null)
      jcasType.jcas.throwFeatMissing("CoreText", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_CoreText, v);}    
   
    
  //*--------------*
  //* Feature: RefText

  /** getter for RefText - gets the text of the TID, e.g., "3/20/2012"
   * @generated
   * @return value of the feature 
   */
  public String getRefText() {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_RefText == null)
      jcasType.jcas.throwFeatMissing("RefText", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_RefText);}
    
  /** setter for RefText - sets the text of the TID, e.g., "3/20/2012" 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRefText(String v) {
    if (TemporalRelation_Type.featOkTst && ((TemporalRelation_Type)jcasType).casFeat_RefText == null)
      jcasType.jcas.throwFeatMissing("RefText", "gov.hhs.fda.srs.annotation.vaers.TemporalRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemporalRelation_Type)jcasType).casFeatCode_RefText, v);}    
  }

    