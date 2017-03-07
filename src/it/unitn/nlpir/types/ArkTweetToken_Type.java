
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
public class ArkTweetToken_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ArkTweetToken_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ArkTweetToken_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ArkTweetToken(addr, ArkTweetToken_Type.this);
  			   ArkTweetToken_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ArkTweetToken(addr, ArkTweetToken_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ArkTweetToken.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.nlpir.types.ArkTweetToken");
 
  /** @generated */
  final Feature casFeat_PosTag;
  /** @generated */
  final int     casFeatCode_PosTag;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPosTag(int addr) {
        if (featOkTst && casFeat_PosTag == null)
      jcas.throwFeatMissing("PosTag", "it.unitn.nlpir.types.ArkTweetToken");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PosTag);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPosTag(int addr, String v) {
        if (featOkTst && casFeat_PosTag == null)
      jcas.throwFeatMissing("PosTag", "it.unitn.nlpir.types.ArkTweetToken");
    ll_cas.ll_setStringValue(addr, casFeatCode_PosTag, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Features;
  /** @generated */
  final int     casFeatCode_Features;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFeatures(int addr) {
        if (featOkTst && casFeat_Features == null)
      jcas.throwFeatMissing("Features", "it.unitn.nlpir.types.ArkTweetToken");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Features);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFeatures(int addr, String v) {
        if (featOkTst && casFeat_Features == null)
      jcas.throwFeatMissing("Features", "it.unitn.nlpir.types.ArkTweetToken");
    ll_cas.ll_setStringValue(addr, casFeatCode_Features, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ArkTweetToken_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PosTag = jcas.getRequiredFeatureDE(casType, "PosTag", "uima.cas.String", featOkTst);
    casFeatCode_PosTag  = (null == casFeat_PosTag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PosTag).getCode();

 
    casFeat_Features = jcas.getRequiredFeatureDE(casType, "Features", "uima.cas.String", featOkTst);
    casFeatCode_Features  = (null == casFeat_Features) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Features).getCode();

  }
}



    