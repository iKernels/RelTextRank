

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
public class Mention extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Mention.class);
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
  protected Mention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Mention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Mention(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Mention(JCas jcas, int begin, int end) {
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
  //* Feature: id

  /** getter for id - gets 
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (Mention_Type.featOkTst && ((Mention_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "it.unitn.nlpir.types.Mention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Mention_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (Mention_Type.featOkTst && ((Mention_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "it.unitn.nlpir.types.Mention");
    jcasType.ll_cas.ll_setStringValue(addr, ((Mention_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: chain

  /** getter for chain - gets 
   * @generated
   * @return value of the feature 
   */
  public CoreferenceChain getChain() {
    if (Mention_Type.featOkTst && ((Mention_Type)jcasType).casFeat_chain == null)
      jcasType.jcas.throwFeatMissing("chain", "it.unitn.nlpir.types.Mention");
    return (CoreferenceChain)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Mention_Type)jcasType).casFeatCode_chain)));}
    
  /** setter for chain - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setChain(CoreferenceChain v) {
    if (Mention_Type.featOkTst && ((Mention_Type)jcasType).casFeat_chain == null)
      jcasType.jcas.throwFeatMissing("chain", "it.unitn.nlpir.types.Mention");
    jcasType.ll_cas.ll_setRefValue(addr, ((Mention_Type)jcasType).casFeatCode_chain, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    