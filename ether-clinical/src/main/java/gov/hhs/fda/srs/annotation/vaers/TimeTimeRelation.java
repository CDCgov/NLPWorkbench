

/* First created by JCasGen Mon May 15 14:25:40 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** This FeatureFeatureRelation potenitally connects multiple clinical features.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * XML source: C:/Repositories/Code/VaersData/desc/VaersTypeSystem.xml
 * @generated */
public class TimeTimeRelation extends RelationFeature {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TimeTimeRelation.class);
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
  protected TimeTimeRelation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TimeTimeRelation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TimeTimeRelation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TimeTimeRelation(JCas jcas, int begin, int end) {
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
  //* Feature: TID1

  /** getter for TID1 - gets The first temporal featue ID that is related.
   * @generated
   * @return value of the feature 
   */
  public String getTID1() {
    if (TimeTimeRelation_Type.featOkTst && ((TimeTimeRelation_Type)jcasType).casFeat_TID1 == null)
      jcasType.jcas.throwFeatMissing("TID1", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TimeTimeRelation_Type)jcasType).casFeatCode_TID1);}
    
  /** setter for TID1 - sets The first temporal featue ID that is related. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTID1(String v) {
    if (TimeTimeRelation_Type.featOkTst && ((TimeTimeRelation_Type)jcasType).casFeat_TID1 == null)
      jcasType.jcas.throwFeatMissing("TID1", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TimeTimeRelation_Type)jcasType).casFeatCode_TID1, v);}    
   
    
  //*--------------*
  //* Feature: TID2

  /** getter for TID2 - gets The second temporal featue ID that is related.
   * @generated
   * @return value of the feature 
   */
  public String getTID2() {
    if (TimeTimeRelation_Type.featOkTst && ((TimeTimeRelation_Type)jcasType).casFeat_TID2 == null)
      jcasType.jcas.throwFeatMissing("TID2", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TimeTimeRelation_Type)jcasType).casFeatCode_TID2);}
    
  /** setter for TID2 - sets The second temporal featue ID that is related. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTID2(String v) {
    if (TimeTimeRelation_Type.featOkTst && ((TimeTimeRelation_Type)jcasType).casFeat_TID2 == null)
      jcasType.jcas.throwFeatMissing("TID2", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TimeTimeRelation_Type)jcasType).casFeatCode_TID2, v);}    
   
    
  //*--------------*
  //* Feature: Relation

  /** getter for Relation - gets The values may include 'before', 'after', and 'overlap', etc., and may be expanded in the future.
   * @generated
   * @return value of the feature 
   */
  public String getRelation() {
    if (TimeTimeRelation_Type.featOkTst && ((TimeTimeRelation_Type)jcasType).casFeat_Relation == null)
      jcasType.jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TimeTimeRelation_Type)jcasType).casFeatCode_Relation);}
    
  /** setter for Relation - sets The values may include 'before', 'after', and 'overlap', etc., and may be expanded in the future. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelation(String v) {
    if (TimeTimeRelation_Type.featOkTst && ((TimeTimeRelation_Type)jcasType).casFeat_Relation == null)
      jcasType.jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.TimeTimeRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TimeTimeRelation_Type)jcasType).casFeatCode_Relation, v);}    
  }

    