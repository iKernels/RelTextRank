

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
public class ArkTweetToken extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ArkTweetToken.class);
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
  protected ArkTweetToken() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ArkTweetToken(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ArkTweetToken(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ArkTweetToken(JCas jcas, int begin, int end) {
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
  //* Feature: PosTag

  /** getter for PosTag - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPosTag() {
    if (ArkTweetToken_Type.featOkTst && ((ArkTweetToken_Type)jcasType).casFeat_PosTag == null)
      jcasType.jcas.throwFeatMissing("PosTag", "it.unitn.nlpir.types.ArkTweetToken");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ArkTweetToken_Type)jcasType).casFeatCode_PosTag);}
    
  /** setter for PosTag - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPosTag(String v) {
    if (ArkTweetToken_Type.featOkTst && ((ArkTweetToken_Type)jcasType).casFeat_PosTag == null)
      jcasType.jcas.throwFeatMissing("PosTag", "it.unitn.nlpir.types.ArkTweetToken");
    jcasType.ll_cas.ll_setStringValue(addr, ((ArkTweetToken_Type)jcasType).casFeatCode_PosTag, v);}    
   
    
  //*--------------*
  //* Feature: Features

  /** getter for Features - gets 
   * @generated
   * @return value of the feature 
   */
  public String getFeatures() {
    if (ArkTweetToken_Type.featOkTst && ((ArkTweetToken_Type)jcasType).casFeat_Features == null)
      jcasType.jcas.throwFeatMissing("Features", "it.unitn.nlpir.types.ArkTweetToken");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ArkTweetToken_Type)jcasType).casFeatCode_Features);}
    
  /** setter for Features - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFeatures(String v) {
    if (ArkTweetToken_Type.featOkTst && ((ArkTweetToken_Type)jcasType).casFeat_Features == null)
      jcasType.jcas.throwFeatMissing("Features", "it.unitn.nlpir.types.ArkTweetToken");
    jcasType.ll_cas.ll_setStringValue(addr, ((ArkTweetToken_Type)jcasType).casFeatCode_Features, v);}    
  }

    