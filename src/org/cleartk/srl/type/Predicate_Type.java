
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
public class Predicate_Type extends ScoredAnnotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Predicate_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Predicate_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Predicate(addr, Predicate_Type.this);
  			   Predicate_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Predicate(addr, Predicate_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Predicate.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.cleartk.srl.type.Predicate");
 
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
      jcas.throwFeatMissing("annotation", "org.cleartk.srl.type.Predicate");
    return ll_cas.ll_getRefValue(addr, casFeatCode_annotation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnnotation(int addr, int v) {
        if (featOkTst && casFeat_annotation == null)
      jcas.throwFeatMissing("annotation", "org.cleartk.srl.type.Predicate");
    ll_cas.ll_setRefValue(addr, casFeatCode_annotation, v);}
    
  
 
  /** @generated */
  final Feature casFeat_arguments;
  /** @generated */
  final int     casFeatCode_arguments;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getArguments(int addr) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "org.cleartk.srl.type.Predicate");
    return ll_cas.ll_getRefValue(addr, casFeatCode_arguments);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setArguments(int addr, int v) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "org.cleartk.srl.type.Predicate");
    ll_cas.ll_setRefValue(addr, casFeatCode_arguments, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getArguments(int addr, int i) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "org.cleartk.srl.type.Predicate");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setArguments(int addr, int i, int v) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "org.cleartk.srl.type.Predicate");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_sentence;
  /** @generated */
  final int     casFeatCode_sentence;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSentence(int addr) {
        if (featOkTst && casFeat_sentence == null)
      jcas.throwFeatMissing("sentence", "org.cleartk.srl.type.Predicate");
    return ll_cas.ll_getRefValue(addr, casFeatCode_sentence);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSentence(int addr, int v) {
        if (featOkTst && casFeat_sentence == null)
      jcas.throwFeatMissing("sentence", "org.cleartk.srl.type.Predicate");
    ll_cas.ll_setRefValue(addr, casFeatCode_sentence, v);}
    
  
 
  /** @generated */
  final Feature casFeat_baseForm;
  /** @generated */
  final int     casFeatCode_baseForm;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getBaseForm(int addr) {
        if (featOkTst && casFeat_baseForm == null)
      jcas.throwFeatMissing("baseForm", "org.cleartk.srl.type.Predicate");
    return ll_cas.ll_getStringValue(addr, casFeatCode_baseForm);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBaseForm(int addr, String v) {
        if (featOkTst && casFeat_baseForm == null)
      jcas.throwFeatMissing("baseForm", "org.cleartk.srl.type.Predicate");
    ll_cas.ll_setStringValue(addr, casFeatCode_baseForm, v);}
    
  
 
  /** @generated */
  final Feature casFeat_frameSet;
  /** @generated */
  final int     casFeatCode_frameSet;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFrameSet(int addr) {
        if (featOkTst && casFeat_frameSet == null)
      jcas.throwFeatMissing("frameSet", "org.cleartk.srl.type.Predicate");
    return ll_cas.ll_getStringValue(addr, casFeatCode_frameSet);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFrameSet(int addr, String v) {
        if (featOkTst && casFeat_frameSet == null)
      jcas.throwFeatMissing("frameSet", "org.cleartk.srl.type.Predicate");
    ll_cas.ll_setStringValue(addr, casFeatCode_frameSet, v);}
    
  
 
  /** @generated */
  final Feature casFeat_propTxt;
  /** @generated */
  final int     casFeatCode_propTxt;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPropTxt(int addr) {
        if (featOkTst && casFeat_propTxt == null)
      jcas.throwFeatMissing("propTxt", "org.cleartk.srl.type.Predicate");
    return ll_cas.ll_getStringValue(addr, casFeatCode_propTxt);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPropTxt(int addr, String v) {
        if (featOkTst && casFeat_propTxt == null)
      jcas.throwFeatMissing("propTxt", "org.cleartk.srl.type.Predicate");
    ll_cas.ll_setStringValue(addr, casFeatCode_propTxt, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Predicate_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_annotation = jcas.getRequiredFeatureDE(casType, "annotation", "uima.tcas.Annotation", featOkTst);
    casFeatCode_annotation  = (null == casFeat_annotation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_annotation).getCode();

 
    casFeat_arguments = jcas.getRequiredFeatureDE(casType, "arguments", "uima.cas.FSArray", featOkTst);
    casFeatCode_arguments  = (null == casFeat_arguments) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_arguments).getCode();

 
    casFeat_sentence = jcas.getRequiredFeatureDE(casType, "sentence", "org.cleartk.token.type.Sentence", featOkTst);
    casFeatCode_sentence  = (null == casFeat_sentence) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sentence).getCode();

 
    casFeat_baseForm = jcas.getRequiredFeatureDE(casType, "baseForm", "uima.cas.String", featOkTst);
    casFeatCode_baseForm  = (null == casFeat_baseForm) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_baseForm).getCode();

 
    casFeat_frameSet = jcas.getRequiredFeatureDE(casType, "frameSet", "uima.cas.String", featOkTst);
    casFeatCode_frameSet  = (null == casFeat_frameSet) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_frameSet).getCode();

 
    casFeat_propTxt = jcas.getRequiredFeatureDE(casType, "propTxt", "uima.cas.String", featOkTst);
    casFeatCode_propTxt  = (null == casFeat_propTxt) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_propTxt).getCode();

  }
}



    