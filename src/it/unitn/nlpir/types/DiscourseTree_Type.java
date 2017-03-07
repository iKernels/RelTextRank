
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
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * @generated */
public class DiscourseTree_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DiscourseTree_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DiscourseTree_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DiscourseTree(addr, DiscourseTree_Type.this);
  			   DiscourseTree_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DiscourseTree(addr, DiscourseTree_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DiscourseTree.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.nlpir.types.DiscourseTree");
 
  /** @generated */
  final Feature casFeat_tree;
  /** @generated */
  final int     casFeatCode_tree;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTree(int addr) {
        if (featOkTst && casFeat_tree == null)
      jcas.throwFeatMissing("tree", "it.unitn.nlpir.types.DiscourseTree");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tree);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTree(int addr, String v) {
        if (featOkTst && casFeat_tree == null)
      jcas.throwFeatMissing("tree", "it.unitn.nlpir.types.DiscourseTree");
    ll_cas.ll_setStringValue(addr, casFeatCode_tree, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DiscourseTree_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_tree = jcas.getRequiredFeatureDE(casType, "tree", "uima.cas.String", featOkTst);
    casFeatCode_tree  = (null == casFeat_tree) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tree).getCode();

  }
}



    