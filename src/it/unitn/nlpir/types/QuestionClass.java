

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package it.unitn.nlpir.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class QuestionClass extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(QuestionClass.class);
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
  protected QuestionClass() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public QuestionClass(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public QuestionClass(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public QuestionClass(JCas jcas, int begin, int end) {
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
  //* Feature: questionClass

  /** getter for questionClass - gets 
   * @generated
   * @return value of the feature 
   */
  public String getQuestionClass() {
    if (QuestionClass_Type.featOkTst && ((QuestionClass_Type)jcasType).casFeat_questionClass == null)
      jcasType.jcas.throwFeatMissing("questionClass", "it.unitn.nlpir.types.QuestionClass");
    return jcasType.ll_cas.ll_getStringValue(addr, ((QuestionClass_Type)jcasType).casFeatCode_questionClass);}
    
  /** setter for questionClass - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuestionClass(String v) {
    if (QuestionClass_Type.featOkTst && ((QuestionClass_Type)jcasType).casFeat_questionClass == null)
      jcasType.jcas.throwFeatMissing("questionClass", "it.unitn.nlpir.types.QuestionClass");
    jcasType.ll_cas.ll_setStringValue(addr, ((QuestionClass_Type)jcasType).casFeatCode_questionClass, v);}    
   
    
  //*--------------*
  //* Feature: confidence

  /** getter for confidence - gets 
   * @generated
   * @return value of the feature 
   */
  public double getConfidence() {
    if (QuestionClass_Type.featOkTst && ((QuestionClass_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "it.unitn.nlpir.types.QuestionClass");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((QuestionClass_Type)jcasType).casFeatCode_confidence);}
    
  /** setter for confidence - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setConfidence(double v) {
    if (QuestionClass_Type.featOkTst && ((QuestionClass_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "it.unitn.nlpir.types.QuestionClass");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((QuestionClass_Type)jcasType).casFeatCode_confidence, v);}    
  }

    