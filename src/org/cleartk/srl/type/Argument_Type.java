
/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package org.cleartk.srl.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.cleartk.score.type.ScoredAnnotation_Type;

/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * @generated */
public class Argument_Type extends ScoredAnnotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Argument_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Argument_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Argument(addr, Argument_Type.this);
  			   Argument_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Argument(addr, Argument_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Argument.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.cleartk.srl.type.Argument");
 
  /** @generated */
  final Feature casFeat_annotation;
  /** @generated */
  final int     casFeatCode_annotation;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getAnnotation(int addr) {
        if (featOkTst && casFeat_annotation == null)
      jcas.throwFeatMissing("annotation", "org.cleartk.srl.type.Argument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_annotation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnnotation(int addr, int v) {
        if (featOkTst && casFeat_annotation == null)
      jcas.throwFeatMissing("annotation", "org.cleartk.srl.type.Argument");
    ll_cas.ll_setRefValue(addr, casFeatCode_annotation, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Argument_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_annotation = jcas.getRequiredFeatureDE(casType, "annotation", "uima.tcas.Annotation", featOkTst);
    casFeatCode_annotation  = (null == casFeat_annotation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_annotation).getCode();

  }
}



    