
/* First created by JCasGen Thu Apr 20 16:11:59 EDT 2017 */
package gov.hhs.fda.srs.annotation.vaers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** 
 * Updated by JCasGen Fri Aug 10 17:21:03 EDT 2018
 * @generated */
public class SecondLevelDiagnosis_Type extends CategoryDiagnosticFeatures_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SecondLevelDiagnosis.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.hhs.fda.srs.annotation.vaers.SecondLevelDiagnosis");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SecondLevelDiagnosis_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    