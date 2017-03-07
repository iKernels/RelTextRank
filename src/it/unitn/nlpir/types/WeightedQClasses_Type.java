
/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package it.unitn.nlpir.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * @generated */
public class WeightedQClasses_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (WeightedQClasses_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = WeightedQClasses_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new WeightedQClasses(addr, WeightedQClasses_Type.this);
  			   WeightedQClasses_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new WeightedQClasses(addr, WeightedQClasses_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WeightedQClasses.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.nlpir.types.WeightedQClasses");
 
  /** @generated */
  final Feature casFeat_classesNames;
  /** @generated */
  final int     casFeatCode_classesNames;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getClassesNames(int addr) {
        if (featOkTst && casFeat_classesNames == null)
      jcas.throwFeatMissing("classesNames", "it.unitn.nlpir.types.WeightedQClasses");
    return ll_cas.ll_getRefValue(addr, casFeatCode_classesNames);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setClassesNames(int addr, int v) {
        if (featOkTst && casFeat_classesNames == null)
      jcas.throwFeatMissing("classesNames", "it.unitn.nlpir.types.WeightedQClasses");
    ll_cas.ll_setRefValue(addr, casFeatCode_classesNames, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getClassesNames(int addr, int i) {
        if (featOkTst && casFeat_classesNames == null)
      jcas.throwFeatMissing("classesNames", "it.unitn.nlpir.types.WeightedQClasses");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classesNames), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_classesNames), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classesNames), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setClassesNames(int addr, int i, String v) {
        if (featOkTst && casFeat_classesNames == null)
      jcas.throwFeatMissing("classesNames", "it.unitn.nlpir.types.WeightedQClasses");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classesNames), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_classesNames), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classesNames), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_classesWeights;
  /** @generated */
  final int     casFeatCode_classesWeights;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getClassesWeights(int addr) {
        if (featOkTst && casFeat_classesWeights == null)
      jcas.throwFeatMissing("classesWeights", "it.unitn.nlpir.types.WeightedQClasses");
    return ll_cas.ll_getRefValue(addr, casFeatCode_classesWeights);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setClassesWeights(int addr, int v) {
        if (featOkTst && casFeat_classesWeights == null)
      jcas.throwFeatMissing("classesWeights", "it.unitn.nlpir.types.WeightedQClasses");
    ll_cas.ll_setRefValue(addr, casFeatCode_classesWeights, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public double getClassesWeights(int addr, int i) {
        if (featOkTst && casFeat_classesWeights == null)
      jcas.throwFeatMissing("classesWeights", "it.unitn.nlpir.types.WeightedQClasses");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getDoubleArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classesWeights), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_classesWeights), i);
	return ll_cas.ll_getDoubleArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classesWeights), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setClassesWeights(int addr, int i, double v) {
        if (featOkTst && casFeat_classesWeights == null)
      jcas.throwFeatMissing("classesWeights", "it.unitn.nlpir.types.WeightedQClasses");
    if (lowLevelTypeChecks)
      ll_cas.ll_setDoubleArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classesWeights), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_classesWeights), i);
    ll_cas.ll_setDoubleArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classesWeights), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public WeightedQClasses_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_classesNames = jcas.getRequiredFeatureDE(casType, "classesNames", "uima.cas.StringArray", featOkTst);
    casFeatCode_classesNames  = (null == casFeat_classesNames) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_classesNames).getCode();

 
    casFeat_classesWeights = jcas.getRequiredFeatureDE(casType, "classesWeights", "uima.cas.DoubleArray", featOkTst);
    casFeatCode_classesWeights  = (null == casFeat_classesWeights) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_classesWeights).getCode();

  }
}



    