

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package it.unitn.nlpir.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.cas.DoubleArray;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class LdaTopicDistribution extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(LdaTopicDistribution.class);
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
  protected LdaTopicDistribution() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public LdaTopicDistribution(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public LdaTopicDistribution(JCas jcas) {
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
  //* Feature: topicDistribution

  /** getter for topicDistribution - gets 
   * @generated
   * @return value of the feature 
   */
  public DoubleArray getTopicDistribution() {
    if (LdaTopicDistribution_Type.featOkTst && ((LdaTopicDistribution_Type)jcasType).casFeat_topicDistribution == null)
      jcasType.jcas.throwFeatMissing("topicDistribution", "it.unitn.nlpir.types.LdaTopicDistribution");
    return (DoubleArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((LdaTopicDistribution_Type)jcasType).casFeatCode_topicDistribution)));}
    
  /** setter for topicDistribution - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTopicDistribution(DoubleArray v) {
    if (LdaTopicDistribution_Type.featOkTst && ((LdaTopicDistribution_Type)jcasType).casFeat_topicDistribution == null)
      jcasType.jcas.throwFeatMissing("topicDistribution", "it.unitn.nlpir.types.LdaTopicDistribution");
    jcasType.ll_cas.ll_setRefValue(addr, ((LdaTopicDistribution_Type)jcasType).casFeatCode_topicDistribution, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for topicDistribution - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public double getTopicDistribution(int i) {
    if (LdaTopicDistribution_Type.featOkTst && ((LdaTopicDistribution_Type)jcasType).casFeat_topicDistribution == null)
      jcasType.jcas.throwFeatMissing("topicDistribution", "it.unitn.nlpir.types.LdaTopicDistribution");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((LdaTopicDistribution_Type)jcasType).casFeatCode_topicDistribution), i);
    return jcasType.ll_cas.ll_getDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((LdaTopicDistribution_Type)jcasType).casFeatCode_topicDistribution), i);}

  /** indexed setter for topicDistribution - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setTopicDistribution(int i, double v) { 
    if (LdaTopicDistribution_Type.featOkTst && ((LdaTopicDistribution_Type)jcasType).casFeat_topicDistribution == null)
      jcasType.jcas.throwFeatMissing("topicDistribution", "it.unitn.nlpir.types.LdaTopicDistribution");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((LdaTopicDistribution_Type)jcasType).casFeatCode_topicDistribution), i);
    jcasType.ll_cas.ll_setDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((LdaTopicDistribution_Type)jcasType).casFeatCode_topicDistribution), i, v);}
  }

    