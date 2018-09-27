

/* First created by JCasGen Thu Apr 20 16:11:59 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** The parent type of the VAERS clinical features.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * XML source: C:/Repositories/Code/VaersData/desc/VaersTypeSystem.xml
 * @generated */
public class ClinicalFeature extends VaersFeature {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ClinicalFeature.class);
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
  protected ClinicalFeature() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ClinicalFeature(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ClinicalFeature(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ClinicalFeature(JCas jcas, int begin, int end) {
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
  //* Feature: TimeID

  /** getter for TimeID - gets The ID of a temporal feature. This is reserved for the specification of clinical-temporal feature association relationship.

However, the latest decision is to NOT associate TimeID here, but to a TLink or similar structure.
   * @generated
   * @return value of the feature 
   */
  public String getTimeID() {
    if (ClinicalFeature_Type.featOkTst && ((ClinicalFeature_Type)jcasType).casFeat_TimeID == null)
      jcasType.jcas.throwFeatMissing("TimeID", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClinicalFeature_Type)jcasType).casFeatCode_TimeID);}
    
  /** setter for TimeID - sets The ID of a temporal feature. This is reserved for the specification of clinical-temporal feature association relationship.

However, the latest decision is to NOT associate TimeID here, but to a TLink or similar structure. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTimeID(String v) {
    if (ClinicalFeature_Type.featOkTst && ((ClinicalFeature_Type)jcasType).casFeat_TimeID == null)
      jcasType.jcas.throwFeatMissing("TimeID", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClinicalFeature_Type)jcasType).casFeatCode_TimeID, v);}    
   
    
  //*--------------*
  //* Feature: Relation

  /** getter for Relation - gets This field is reserved for the specification of clinical-temporal feature association relationship. This field may include values including BEFORE, AFTER and OVERLAP.

However, the latest decision is to not associate any 'relation' here, but to a TLink or similar structure.
   * @generated
   * @return value of the feature 
   */
  public String getRelation() {
    if (ClinicalFeature_Type.featOkTst && ((ClinicalFeature_Type)jcasType).casFeat_Relation == null)
      jcasType.jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClinicalFeature_Type)jcasType).casFeatCode_Relation);}
    
  /** setter for Relation - sets This field is reserved for the specification of clinical-temporal feature association relationship. This field may include values including BEFORE, AFTER and OVERLAP.

However, the latest decision is to not associate any 'relation' here, but to a TLink or similar structure. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelation(String v) {
    if (ClinicalFeature_Type.featOkTst && ((ClinicalFeature_Type)jcasType).casFeat_Relation == null)
      jcasType.jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClinicalFeature_Type)jcasType).casFeatCode_Relation, v);}    
   
    
  //*--------------*
  //* Feature: atype

  /** getter for atype - gets This may contain a feature type specification to indicate which particular feature this span of text has been annotated. The word 'type' is avoided since it is a reserved word in UIMA.
   * @generated
   * @return value of the feature 
   */
  public String getAtype() {
    if (ClinicalFeature_Type.featOkTst && ((ClinicalFeature_Type)jcasType).casFeat_atype == null)
      jcasType.jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClinicalFeature_Type)jcasType).casFeatCode_atype);}
    
  /** setter for atype - sets This may contain a feature type specification to indicate which particular feature this span of text has been annotated. The word 'type' is avoided since it is a reserved word in UIMA. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAtype(String v) {
    if (ClinicalFeature_Type.featOkTst && ((ClinicalFeature_Type)jcasType).casFeat_atype == null)
      jcasType.jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClinicalFeature_Type)jcasType).casFeatCode_atype, v);}    
   
    
  //*--------------*
  //* Feature: preferred_term

  /** getter for preferred_term - gets The preferred term from coding service for the extracted text span.
   * @generated
   * @return value of the feature 
   */
  public String getPreferred_term() {
    if (ClinicalFeature_Type.featOkTst && ((ClinicalFeature_Type)jcasType).casFeat_preferred_term == null)
      jcasType.jcas.throwFeatMissing("preferred_term", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClinicalFeature_Type)jcasType).casFeatCode_preferred_term);}
    
  /** setter for preferred_term - sets The preferred term from coding service for the extracted text span. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPreferred_term(String v) {
    if (ClinicalFeature_Type.featOkTst && ((ClinicalFeature_Type)jcasType).casFeat_preferred_term == null)
      jcasType.jcas.throwFeatMissing("preferred_term", "gov.hhs.fda.srs.annotation.vaers.ClinicalFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClinicalFeature_Type)jcasType).casFeatCode_preferred_term, v);}    
  }

    