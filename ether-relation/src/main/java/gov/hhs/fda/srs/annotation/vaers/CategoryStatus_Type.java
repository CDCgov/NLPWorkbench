
/* First created by JCasGen Wed Apr 26 11:47:56 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** CategoryStatus itself is both a category and a specific feature.
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * @generated */
public class CategoryStatus_Type extends ClinicalFeature_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CategoryStatus.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.hhs.fda.srs.annotation.vaers.CategoryStatus");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CategoryStatus_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    