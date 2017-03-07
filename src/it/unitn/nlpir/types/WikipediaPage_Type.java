
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
public class WikipediaPage_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (WikipediaPage_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = WikipediaPage_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new WikipediaPage(addr, WikipediaPage_Type.this);
  			   WikipediaPage_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new WikipediaPage(addr, WikipediaPage_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WikipediaPage.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.nlpir.types.WikipediaPage");
 
  /** @generated */
  final Feature casFeat_URI;
  /** @generated */
  final int     casFeatCode_URI;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getURI(int addr) {
        if (featOkTst && casFeat_URI == null)
      jcas.throwFeatMissing("URI", "it.unitn.nlpir.types.WikipediaPage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_URI);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setURI(int addr, String v) {
        if (featOkTst && casFeat_URI == null)
      jcas.throwFeatMissing("URI", "it.unitn.nlpir.types.WikipediaPage");
    ll_cas.ll_setStringValue(addr, casFeatCode_URI, v);}
    
  
 
  /** @generated */
  final Feature casFeat_disambiguatorConfidence;
  /** @generated */
  final int     casFeatCode_disambiguatorConfidence;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getDisambiguatorConfidence(int addr) {
        if (featOkTst && casFeat_disambiguatorConfidence == null)
      jcas.throwFeatMissing("disambiguatorConfidence", "it.unitn.nlpir.types.WikipediaPage");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_disambiguatorConfidence);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDisambiguatorConfidence(int addr, double v) {
        if (featOkTst && casFeat_disambiguatorConfidence == null)
      jcas.throwFeatMissing("disambiguatorConfidence", "it.unitn.nlpir.types.WikipediaPage");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_disambiguatorConfidence, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public WikipediaPage_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_URI = jcas.getRequiredFeatureDE(casType, "URI", "uima.cas.String", featOkTst);
    casFeatCode_URI  = (null == casFeat_URI) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_URI).getCode();

 
    casFeat_disambiguatorConfidence = jcas.getRequiredFeatureDE(casType, "disambiguatorConfidence", "uima.cas.Double", featOkTst);
    casFeatCode_disambiguatorConfidence  = (null == casFeat_disambiguatorConfidence) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_disambiguatorConfidence).getCode();

  }
}



    