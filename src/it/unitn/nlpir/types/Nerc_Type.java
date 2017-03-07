
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
public class Nerc_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Nerc_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Nerc_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Nerc(addr, Nerc_Type.this);
  			   Nerc_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Nerc(addr, Nerc_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Nerc.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.nlpir.types.Nerc");
 
  /** @generated */
  final Feature casFeat_tagset;
  /** @generated */
  final int     casFeatCode_tagset;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTagset(int addr) {
        if (featOkTst && casFeat_tagset == null)
      jcas.throwFeatMissing("tagset", "it.unitn.nlpir.types.Nerc");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tagset);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTagset(int addr, String v) {
        if (featOkTst && casFeat_tagset == null)
      jcas.throwFeatMissing("tagset", "it.unitn.nlpir.types.Nerc");
    ll_cas.ll_setStringValue(addr, casFeatCode_tagset, v);}
    
  
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "it.unitn.nlpir.types.Nerc");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "it.unitn.nlpir.types.Nerc");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Nerc_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_tagset = jcas.getRequiredFeatureDE(casType, "tagset", "uima.cas.String", featOkTst);
    casFeatCode_tagset  = (null == casFeat_tagset) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tagset).getCode();

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

  }
}



    