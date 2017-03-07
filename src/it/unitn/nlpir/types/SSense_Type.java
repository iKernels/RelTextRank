
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

/** SuperSense tag
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * @generated */
public class SSense_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SSense_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SSense_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SSense(addr, SSense_Type.this);
  			   SSense_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SSense(addr, SSense_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SSense.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.nlpir.types.SSense");
 
  /** @generated */
  final Feature casFeat_tag;
  /** @generated */
  final int     casFeatCode_tag;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTag(int addr) {
        if (featOkTst && casFeat_tag == null)
      jcas.throwFeatMissing("tag", "it.unitn.nlpir.types.SSense");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tag);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTag(int addr, String v) {
        if (featOkTst && casFeat_tag == null)
      jcas.throwFeatMissing("tag", "it.unitn.nlpir.types.SSense");
    ll_cas.ll_setStringValue(addr, casFeatCode_tag, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SSense_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_tag = jcas.getRequiredFeatureDE(casType, "tag", "uima.cas.String", featOkTst);
    casFeatCode_tag  = (null == casFeat_tag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tag).getCode();

  }
}



    