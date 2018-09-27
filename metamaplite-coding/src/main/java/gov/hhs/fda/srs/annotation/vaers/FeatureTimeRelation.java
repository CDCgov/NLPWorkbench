

/* First created by JCasGen Mon May 15 14:04:39 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.tcas.Annotation;


/** A link that represents and connects one clinical feature and possibly zero or multiple temporal features.
 * Updated by JCasGen Tue Aug 22 14:55:59 EDT 2017
 * XML source: C:/Repositories/Code/VaersData/desc/VaersTypeSystem.xml
 * @generated */
public class FeatureTimeRelation extends RelationFeature {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(FeatureTimeRelation.class);
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
  protected FeatureTimeRelation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public FeatureTimeRelation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public FeatureTimeRelation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public FeatureTimeRelation(JCas jcas, int begin, int end) {
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
  //* Feature: CID

  /** getter for CID - gets Clinical Feature ID.
   * @generated
   * @return value of the feature 
   */
  public String getCID() {
    if (FeatureTimeRelation_Type.featOkTst && ((FeatureTimeRelation_Type)jcasType).casFeat_CID == null)
      jcasType.jcas.throwFeatMissing("CID", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((FeatureTimeRelation_Type)jcasType).casFeatCode_CID);}
    
  /** setter for CID - sets Clinical Feature ID. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCID(String v) {
    if (FeatureTimeRelation_Type.featOkTst && ((FeatureTimeRelation_Type)jcasType).casFeat_CID == null)
      jcasType.jcas.throwFeatMissing("CID", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((FeatureTimeRelation_Type)jcasType).casFeatCode_CID, v);}    
   
    
  //*--------------*
  //* Feature: TID

  /** getter for TID - gets Multiple (i.e., zero to more-than-one) temporal features can be associated with a clinical feature ID. However, each 'FeatureTimeRelation' will only represent one such relation.
   * @generated
   * @return value of the feature 
   */
  public String getTID() {
    if (FeatureTimeRelation_Type.featOkTst && ((FeatureTimeRelation_Type)jcasType).casFeat_TID == null)
      jcasType.jcas.throwFeatMissing("TID", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((FeatureTimeRelation_Type)jcasType).casFeatCode_TID);}
    
  /** setter for TID - sets Multiple (i.e., zero to more-than-one) temporal features can be associated with a clinical feature ID. However, each 'FeatureTimeRelation' will only represent one such relation. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTID(String v) {
    if (FeatureTimeRelation_Type.featOkTst && ((FeatureTimeRelation_Type)jcasType).casFeat_TID == null)
      jcasType.jcas.throwFeatMissing("TID", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((FeatureTimeRelation_Type)jcasType).casFeatCode_TID, v);}    
   
    
  //*--------------*
  //* Feature: Relation

  /** getter for Relation - gets The values may include 'before', 'after', and 'overlap', etc., and may be expanded in the future.
   * @generated
   * @return value of the feature 
   */
  public String getRelation() {
    if (FeatureTimeRelation_Type.featOkTst && ((FeatureTimeRelation_Type)jcasType).casFeat_Relation == null)
      jcasType.jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((FeatureTimeRelation_Type)jcasType).casFeatCode_Relation);}
    
  /** setter for Relation - sets The values may include 'before', 'after', and 'overlap', etc., and may be expanded in the future. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelation(String v) {
    if (FeatureTimeRelation_Type.featOkTst && ((FeatureTimeRelation_Type)jcasType).casFeat_Relation == null)
      jcasType.jcas.throwFeatMissing("Relation", "gov.hhs.fda.srs.annotation.vaers.FeatureTimeRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((FeatureTimeRelation_Type)jcasType).casFeatCode_Relation, v);}    
  }

    