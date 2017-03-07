

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package org.cleartk.srl.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.cleartk.score.type.ScoredAnnotation;
import org.cleartk.token.type.Sentence;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class Predicate extends ScoredAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Predicate.class);
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
  protected Predicate() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Predicate(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Predicate(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Predicate(JCas jcas, int begin, int end) {
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
  //* Feature: annotation

  /** getter for annotation - gets 
   * @generated
   * @return value of the feature 
   */
  public Annotation getAnnotation() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_annotation == null)
      jcasType.jcas.throwFeatMissing("annotation", "org.cleartk.srl.type.Predicate");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_annotation)));}
    
  /** setter for annotation - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotation(Annotation v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_annotation == null)
      jcasType.jcas.throwFeatMissing("annotation", "org.cleartk.srl.type.Predicate");
    jcasType.ll_cas.ll_setRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_annotation, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: arguments

  /** getter for arguments - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getArguments() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "org.cleartk.srl.type.Predicate");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments)));}
    
  /** setter for arguments - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setArguments(FSArray v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "org.cleartk.srl.type.Predicate");
    jcasType.ll_cas.ll_setRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for arguments - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public Argument getArguments(int i) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "org.cleartk.srl.type.Predicate");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments), i);
    return (Argument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments), i)));}

  /** indexed setter for arguments - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setArguments(int i, Argument v) { 
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "org.cleartk.srl.type.Predicate");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: sentence

  /** getter for sentence - gets 
   * @generated
   * @return value of the feature 
   */
  public Sentence getSentence() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_sentence == null)
      jcasType.jcas.throwFeatMissing("sentence", "org.cleartk.srl.type.Predicate");
    return (Sentence)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_sentence)));}
    
  /** setter for sentence - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentence(Sentence v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_sentence == null)
      jcasType.jcas.throwFeatMissing("sentence", "org.cleartk.srl.type.Predicate");
    jcasType.ll_cas.ll_setRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_sentence, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: baseForm

  /** getter for baseForm - gets 
   * @generated
   * @return value of the feature 
   */
  public String getBaseForm() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_baseForm == null)
      jcasType.jcas.throwFeatMissing("baseForm", "org.cleartk.srl.type.Predicate");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Predicate_Type)jcasType).casFeatCode_baseForm);}
    
  /** setter for baseForm - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBaseForm(String v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_baseForm == null)
      jcasType.jcas.throwFeatMissing("baseForm", "org.cleartk.srl.type.Predicate");
    jcasType.ll_cas.ll_setStringValue(addr, ((Predicate_Type)jcasType).casFeatCode_baseForm, v);}    
   
    
  //*--------------*
  //* Feature: frameSet

  /** getter for frameSet - gets 
   * @generated
   * @return value of the feature 
   */
  public String getFrameSet() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_frameSet == null)
      jcasType.jcas.throwFeatMissing("frameSet", "org.cleartk.srl.type.Predicate");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Predicate_Type)jcasType).casFeatCode_frameSet);}
    
  /** setter for frameSet - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFrameSet(String v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_frameSet == null)
      jcasType.jcas.throwFeatMissing("frameSet", "org.cleartk.srl.type.Predicate");
    jcasType.ll_cas.ll_setStringValue(addr, ((Predicate_Type)jcasType).casFeatCode_frameSet, v);}    
   
    
  //*--------------*
  //* Feature: propTxt

  /** getter for propTxt - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPropTxt() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_propTxt == null)
      jcasType.jcas.throwFeatMissing("propTxt", "org.cleartk.srl.type.Predicate");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Predicate_Type)jcasType).casFeatCode_propTxt);}
    
  /** setter for propTxt - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPropTxt(String v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_propTxt == null)
      jcasType.jcas.throwFeatMissing("propTxt", "org.cleartk.srl.type.Predicate");
    jcasType.ll_cas.ll_setStringValue(addr, ((Predicate_Type)jcasType).casFeatCode_propTxt, v);}    
  }

    