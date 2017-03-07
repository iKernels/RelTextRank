
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
public class AnswerPattern_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (AnswerPattern_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = AnswerPattern_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new AnswerPattern(addr, AnswerPattern_Type.this);
  			   AnswerPattern_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new AnswerPattern(addr, AnswerPattern_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AnswerPattern.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.nlpir.types.AnswerPattern");
 
  /** @generated */
  final Feature casFeat_pattern;
  /** @generated */
  final int     casFeatCode_pattern;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPattern(int addr) {
        if (featOkTst && casFeat_pattern == null)
      jcas.throwFeatMissing("pattern", "it.unitn.nlpir.types.AnswerPattern");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pattern);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPattern(int addr, String v) {
        if (featOkTst && casFeat_pattern == null)
      jcas.throwFeatMissing("pattern", "it.unitn.nlpir.types.AnswerPattern");
    ll_cas.ll_setStringValue(addr, casFeatCode_pattern, v);}
    
  
 
  /** @generated */
  final Feature casFeat_isMatched;
  /** @generated */
  final int     casFeatCode_isMatched;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsMatched(int addr) {
        if (featOkTst && casFeat_isMatched == null)
      jcas.throwFeatMissing("isMatched", "it.unitn.nlpir.types.AnswerPattern");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isMatched);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsMatched(int addr, boolean v) {
        if (featOkTst && casFeat_isMatched == null)
      jcas.throwFeatMissing("isMatched", "it.unitn.nlpir.types.AnswerPattern");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isMatched, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public AnswerPattern_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_pattern = jcas.getRequiredFeatureDE(casType, "pattern", "uima.cas.String", featOkTst);
    casFeatCode_pattern  = (null == casFeat_pattern) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pattern).getCode();

 
    casFeat_isMatched = jcas.getRequiredFeatureDE(casType, "isMatched", "uima.cas.Boolean", featOkTst);
    casFeatCode_isMatched  = (null == casFeat_isMatched) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isMatched).getCode();

  }
}



    