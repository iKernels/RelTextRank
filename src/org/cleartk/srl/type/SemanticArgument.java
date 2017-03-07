

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package org.cleartk.srl.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class SemanticArgument extends Argument {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SemanticArgument.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected SemanticArgument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SemanticArgument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SemanticArgument(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public SemanticArgument(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: label

  /** getter for label - gets 
   * @generated
   * @return value of the feature 
   */
  public String getLabel() {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "org.cleartk.srl.type.SemanticArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_label);}
    
  /** setter for label - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLabel(String v) {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "org.cleartk.srl.type.SemanticArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_label, v);}    
   
    
  //*--------------*
  //* Feature: feature

  /** getter for feature - gets 
   * @generated
   * @return value of the feature 
   */
  public String getFeature() {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_feature == null)
      jcasType.jcas.throwFeatMissing("feature", "org.cleartk.srl.type.SemanticArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_feature);}
    
  /** setter for feature - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFeature(String v) {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_feature == null)
      jcasType.jcas.throwFeatMissing("feature", "org.cleartk.srl.type.SemanticArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_feature, v);}    
   
    
  //*--------------*
  //* Feature: coreferenceAnnotations

  /** getter for coreferenceAnnotations - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getCoreferenceAnnotations() {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_coreferenceAnnotations == null)
      jcasType.jcas.throwFeatMissing("coreferenceAnnotations", "org.cleartk.srl.type.SemanticArgument");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_coreferenceAnnotations)));}
    
  /** setter for coreferenceAnnotations - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCoreferenceAnnotations(FSArray v) {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_coreferenceAnnotations == null)
      jcasType.jcas.throwFeatMissing("coreferenceAnnotations", "org.cleartk.srl.type.SemanticArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_coreferenceAnnotations, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for coreferenceAnnotations - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public Annotation getCoreferenceAnnotations(int i) {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_coreferenceAnnotations == null)
      jcasType.jcas.throwFeatMissing("coreferenceAnnotations", "org.cleartk.srl.type.SemanticArgument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_coreferenceAnnotations), i);
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_coreferenceAnnotations), i)));}

  /** indexed setter for coreferenceAnnotations - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setCoreferenceAnnotations(int i, Annotation v) { 
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_coreferenceAnnotations == null)
      jcasType.jcas.throwFeatMissing("coreferenceAnnotations", "org.cleartk.srl.type.SemanticArgument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_coreferenceAnnotations), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_coreferenceAnnotations), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: preposition

  /** getter for preposition - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPreposition() {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_preposition == null)
      jcasType.jcas.throwFeatMissing("preposition", "org.cleartk.srl.type.SemanticArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_preposition);}
    
  /** setter for preposition - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPreposition(String v) {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_preposition == null)
      jcasType.jcas.throwFeatMissing("preposition", "org.cleartk.srl.type.SemanticArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_preposition, v);}    
   
    
  //*--------------*
  //* Feature: hyphenTag

  /** getter for hyphenTag - gets 
   * @generated
   * @return value of the feature 
   */
  public String getHyphenTag() {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_hyphenTag == null)
      jcasType.jcas.throwFeatMissing("hyphenTag", "org.cleartk.srl.type.SemanticArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_hyphenTag);}
    
  /** setter for hyphenTag - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setHyphenTag(String v) {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_hyphenTag == null)
      jcasType.jcas.throwFeatMissing("hyphenTag", "org.cleartk.srl.type.SemanticArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_hyphenTag, v);}    
   
    
  //*--------------*
  //* Feature: propTxt

  /** getter for propTxt - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPropTxt() {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_propTxt == null)
      jcasType.jcas.throwFeatMissing("propTxt", "org.cleartk.srl.type.SemanticArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_propTxt);}
    
  /** setter for propTxt - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPropTxt(String v) {
    if (SemanticArgument_Type.featOkTst && ((SemanticArgument_Type)jcasType).casFeat_propTxt == null)
      jcasType.jcas.throwFeatMissing("propTxt", "org.cleartk.srl.type.SemanticArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticArgument_Type)jcasType).casFeatCode_propTxt, v);}    
  }

    