
/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package it.unitn.it.nlpir.types;

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
public class UserMention_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (UserMention_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = UserMention_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new UserMention(addr, UserMention_Type.this);
  			   UserMention_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new UserMention(addr, UserMention_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = UserMention.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.it.nlpir.types.UserMention");
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "it.unitn.it.nlpir.types.UserMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "it.unitn.it.nlpir.types.UserMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_isAuthor;
  /** @generated */
  final int     casFeatCode_isAuthor;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsAuthor(int addr) {
        if (featOkTst && casFeat_isAuthor == null)
      jcas.throwFeatMissing("isAuthor", "it.unitn.it.nlpir.types.UserMention");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isAuthor);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsAuthor(int addr, boolean v) {
        if (featOkTst && casFeat_isAuthor == null)
      jcas.throwFeatMissing("isAuthor", "it.unitn.it.nlpir.types.UserMention");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isAuthor, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public UserMention_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

 
    casFeat_isAuthor = jcas.getRequiredFeatureDE(casType, "isAuthor", "uima.cas.Boolean", featOkTst);
    casFeatCode_isAuthor  = (null == casFeat_isAuthor) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isAuthor).getCode();

  }
}



    