

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package it.unitn.nlpir.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class CoreferenceChain extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CoreferenceChain.class);
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
  protected CoreferenceChain() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CoreferenceChain(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CoreferenceChain(JCas jcas) {
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
  //* Feature: id

  /** getter for id - gets 
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "it.unitn.nlpir.types.CoreferenceChain");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "it.unitn.nlpir.types.CoreferenceChain");
    jcasType.ll_cas.ll_setStringValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: mentions

  /** getter for mentions - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getMentions() {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "it.unitn.nlpir.types.CoreferenceChain");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_mentions)));}
    
  /** setter for mentions - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMentions(FSArray v) {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "it.unitn.nlpir.types.CoreferenceChain");
    jcasType.ll_cas.ll_setRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_mentions, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for mentions - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public Mention getMentions(int i) {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "it.unitn.nlpir.types.CoreferenceChain");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_mentions), i);
    return (Mention)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_mentions), i)));}

  /** indexed setter for mentions - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setMentions(int i, Mention v) { 
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "it.unitn.nlpir.types.CoreferenceChain");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_mentions), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_mentions), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    