

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package org.cleartk.score.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class ScoredTOP extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ScoredTOP.class);
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
  protected ScoredTOP() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ScoredTOP(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ScoredTOP(JCas jcas) {
    super(jcas);
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
  //* Feature: score

  /** getter for score - gets 
   * @generated
   * @return value of the feature 
   */
  public double getScore() {
    if (ScoredTOP_Type.featOkTst && ((ScoredTOP_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "org.cleartk.score.type.ScoredTOP");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((ScoredTOP_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setScore(double v) {
    if (ScoredTOP_Type.featOkTst && ((ScoredTOP_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "org.cleartk.score.type.ScoredTOP");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((ScoredTOP_Type)jcasType).casFeatCode_score, v);}    
  }

    