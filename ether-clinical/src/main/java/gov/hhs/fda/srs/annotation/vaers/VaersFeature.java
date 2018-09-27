

/* First created by JCasGen Thu Apr 20 16:26:17 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** This is the parent type of VAERS annotation. Most of the features (or 'attributes') are defined inside this type. 

Some ETHER features (or the data shown in the ETHER clinical/temporal annotation tables) have already been stored in this data structure. For example, the 'Feature Text' in the EHTER table can be obtained using "annotation.getCoveredText()" to show the actual text being annotation.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * XML source: C:/Repositories/Code/VaersData/desc/VaersTypeSystem.xml
 * @generated */
public class VaersFeature extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(VaersFeature.class);
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
  protected VaersFeature() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public VaersFeature(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public VaersFeature(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public VaersFeature(JCas jcas, int begin, int end) {
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
  //* Feature: ID

  /** getter for ID - gets The ID of a VAERS feature for both clinical and temporal types in a String form. 

Behind the scene, the ID is an integer of SQL DB ID; but it shows 'fDD' for 'feature ID' and similarly 'tDD' for 'time ID'.
   * @generated
   * @return value of the feature 
   */
  public String getID() {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets The ID of a VAERS feature for both clinical and temporal types in a String form. 

Behind the scene, the ID is an integer of SQL DB ID; but it shows 'fDD' for 'feature ID' and similarly 'tDD' for 'time ID'. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: atype

  /** getter for atype - gets This field store the abbreviated feature type name, such as "MHx" for "Medical History", or "CoD" for "Cause of Death".
   * @generated
   * @return value of the feature 
   */
  public String getAtype() {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_atype == null)
      jcasType.jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_atype);}
    
  /** setter for atype - sets This field store the abbreviated feature type name, such as "MHx" for "Medical History", or "CoD" for "Cause of Death". 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAtype(String v) {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_atype == null)
      jcasType.jcas.throwFeatMissing("atype", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_atype, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets This is the original feature text or term(s) found from the original clinical notes (e.g., vaers/pathology report)
   * @generated
   * @return value of the feature 
   */
  public String getText() {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets This is the original feature text or term(s) found from the original clinical notes (e.g., vaers/pathology report) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setText(String v) {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: comment

  /** getter for comment - gets 
   * @generated
   * @return value of the feature 
   */
  public String getComment() {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_comment == null)
      jcasType.jcas.throwFeatMissing("comment", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_comment);}
    
  /** setter for comment - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setComment(String v) {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_comment == null)
      jcasType.jcas.throwFeatMissing("comment", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_comment, v);}    
   
    
  //*--------------*
  //* Feature: begin

  /** getter for begin - gets the beginning position of identified feature text in the original text.
   * @generated
   * @return value of the feature 
   */
  public int getBegin() {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_begin == null)
      jcasType.jcas.throwFeatMissing("begin", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    return jcasType.ll_cas.ll_getIntValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_begin);}
    
  /** setter for begin - sets the beginning position of identified feature text in the original text. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setBegin(int v) {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_begin == null)
      jcasType.jcas.throwFeatMissing("begin", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    jcasType.ll_cas.ll_setIntValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_begin, v);}    
   
    
  //*--------------*
  //* Feature: end

  /** getter for end - gets the ending position of identified feature text in the original text.
   * @generated
   * @return value of the feature 
   */
  public int getEnd() {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_end == null)
      jcasType.jcas.throwFeatMissing("end", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    return jcasType.ll_cas.ll_getIntValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_end);}
    
  /** setter for end - sets the ending position of identified feature text in the original text. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setEnd(int v) {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_end == null)
      jcasType.jcas.throwFeatMissing("end", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    jcasType.ll_cas.ll_setIntValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_end, v);}    
   
    
  //*--------------*
  //* Feature: link

  /** getter for link - gets It contains a Link to another temporal expression, usually the TID.
However, latest decision is to associate links with an independent TLINK tag or similar tag, not here.
   * @generated
   * @return value of the feature 
   */
  public String getLink() {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_link == null)
      jcasType.jcas.throwFeatMissing("link", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_link);}
    
  /** setter for link - sets It contains a Link to another temporal expression, usually the TID.
However, latest decision is to associate links with an independent TLINK tag or similar tag, not here. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLink(String v) {
    if (VaersFeature_Type.featOkTst && ((VaersFeature_Type)jcasType).casFeat_link == null)
      jcasType.jcas.throwFeatMissing("link", "gov.hhs.fda.srs.annotation.vaers.VaersFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((VaersFeature_Type)jcasType).casFeatCode_link, v);}    
  }

    