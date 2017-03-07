
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
import org.cleartk.score.type.ScoredAnnotation_Type;

/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * @generated */
public class DependencyNode_Type extends ScoredAnnotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DependencyNode_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DependencyNode_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DependencyNode(addr, DependencyNode_Type.this);
  			   DependencyNode_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DependencyNode(addr, DependencyNode_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DependencyNode.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.cleartk.syntax.dependency.type.DependencyNode");
 
  /** @generated */
  final Feature casFeat_HeadRelations;
  /** @generated */
  final int     casFeatCode_HeadRelations;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getHeadRelations(int addr) {
        if (featOkTst && casFeat_HeadRelations == null)
      jcas.throwFeatMissing("HeadRelations", "org.cleartk.syntax.dependency.type.DependencyNode");
    return ll_cas.ll_getRefValue(addr, casFeatCode_HeadRelations);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setHeadRelations(int addr, int v) {
        if (featOkTst && casFeat_HeadRelations == null)
      jcas.throwFeatMissing("HeadRelations", "org.cleartk.syntax.dependency.type.DependencyNode");
    ll_cas.ll_setRefValue(addr, casFeatCode_HeadRelations, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getHeadRelations(int addr, int i) {
        if (featOkTst && casFeat_HeadRelations == null)
      jcas.throwFeatMissing("HeadRelations", "org.cleartk.syntax.dependency.type.DependencyNode");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_HeadRelations), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_HeadRelations), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_HeadRelations), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setHeadRelations(int addr, int i, int v) {
        if (featOkTst && casFeat_HeadRelations == null)
      jcas.throwFeatMissing("HeadRelations", "org.cleartk.syntax.dependency.type.DependencyNode");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_HeadRelations), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_HeadRelations), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_HeadRelations), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_ChildRelations;
  /** @generated */
  final int     casFeatCode_ChildRelations;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getChildRelations(int addr) {
        if (featOkTst && casFeat_ChildRelations == null)
      jcas.throwFeatMissing("ChildRelations", "org.cleartk.syntax.dependency.type.DependencyNode");
    return ll_cas.ll_getRefValue(addr, casFeatCode_ChildRelations);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setChildRelations(int addr, int v) {
        if (featOkTst && casFeat_ChildRelations == null)
      jcas.throwFeatMissing("ChildRelations", "org.cleartk.syntax.dependency.type.DependencyNode");
    ll_cas.ll_setRefValue(addr, casFeatCode_ChildRelations, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getChildRelations(int addr, int i) {
        if (featOkTst && casFeat_ChildRelations == null)
      jcas.throwFeatMissing("ChildRelations", "org.cleartk.syntax.dependency.type.DependencyNode");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_ChildRelations), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_ChildRelations), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_ChildRelations), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setChildRelations(int addr, int i, int v) {
        if (featOkTst && casFeat_ChildRelations == null)
      jcas.throwFeatMissing("ChildRelations", "org.cleartk.syntax.dependency.type.DependencyNode");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_ChildRelations), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_ChildRelations), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_ChildRelations), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DependencyNode_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_HeadRelations = jcas.getRequiredFeatureDE(casType, "HeadRelations", "uima.cas.FSArray", featOkTst);
    casFeatCode_HeadRelations  = (null == casFeat_HeadRelations) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_HeadRelations).getCode();

 
    casFeat_ChildRelations = jcas.getRequiredFeatureDE(casType, "ChildRelations", "uima.cas.FSArray", featOkTst);
    casFeatCode_ChildRelations  = (null == casFeat_ChildRelations) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ChildRelations).getCode();

  }
}



    