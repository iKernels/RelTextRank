
/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package org.cleartk.syntax.dependency.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.cleartk.score.type.ScoredTOP_Type;

/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * @generated */
public class DependencyRelation_Type extends ScoredTOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DependencyRelation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DependencyRelation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DependencyRelation(addr, DependencyRelation_Type.this);
  			   DependencyRelation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DependencyRelation(addr, DependencyRelation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DependencyRelation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.cleartk.syntax.dependency.type.DependencyRelation");
 
  /** @generated */
  final Feature casFeat_Head;
  /** @generated */
  final int     casFeatCode_Head;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getHead(int addr) {
        if (featOkTst && casFeat_Head == null)
      jcas.throwFeatMissing("Head", "org.cleartk.syntax.dependency.type.DependencyRelation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Head);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setHead(int addr, int v) {
        if (featOkTst && casFeat_Head == null)
      jcas.throwFeatMissing("Head", "org.cleartk.syntax.dependency.type.DependencyRelation");
    ll_cas.ll_setRefValue(addr, casFeatCode_Head, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Relation;
  /** @generated */
  final int     casFeatCode_Relation;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRelation(int addr) {
        if (featOkTst && casFeat_Relation == null)
      jcas.throwFeatMissing("Relation", "org.cleartk.syntax.dependency.type.DependencyRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Relation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRelation(int addr, String v) {
        if (featOkTst && casFeat_Relation == null)
      jcas.throwFeatMissing("Relation", "org.cleartk.syntax.dependency.type.DependencyRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Relation, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Child;
  /** @generated */
  final int     casFeatCode_Child;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getChild(int addr) {
        if (featOkTst && casFeat_Child == null)
      jcas.throwFeatMissing("Child", "org.cleartk.syntax.dependency.type.DependencyRelation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Child);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setChild(int addr, int v) {
        if (featOkTst && casFeat_Child == null)
      jcas.throwFeatMissing("Child", "org.cleartk.syntax.dependency.type.DependencyRelation");
    ll_cas.ll_setRefValue(addr, casFeatCode_Child, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DependencyRelation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Head = jcas.getRequiredFeatureDE(casType, "Head", "org.cleartk.syntax.dependency.type.DependencyNode", featOkTst);
    casFeatCode_Head  = (null == casFeat_Head) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Head).getCode();

 
    casFeat_Relation = jcas.getRequiredFeatureDE(casType, "Relation", "uima.cas.String", featOkTst);
    casFeatCode_Relation  = (null == casFeat_Relation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Relation).getCode();

 
    casFeat_Child = jcas.getRequiredFeatureDE(casType, "Child", "org.cleartk.syntax.dependency.type.DependencyNode", featOkTst);
    casFeatCode_Child  = (null == casFeat_Child) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Child).getCode();

  }
}



    