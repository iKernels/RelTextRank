

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
public class Nerc extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Nerc.class);
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
  protected Nerc() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Nerc(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Nerc(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Nerc(JCas jcas, int begin, int end) {
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
  //* Feature: tagset

  /** getter for tagset - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTagset() {
    if (Nerc_Type.featOkTst && ((Nerc_Type)jcasType).casFeat_tagset == null)
      jcasType.jcas.throwFeatMissing("tagset", "it.unitn.nlpir.types.Nerc");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Nerc_Type)jcasType).casFeatCode_tagset);}
    
  /** setter for tagset - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTagset(String v) {
    if (Nerc_Type.featOkTst && ((Nerc_Type)jcasType).casFeat_tagset == null)
      jcasType.jcas.throwFeatMissing("tagset", "it.unitn.nlpir.types.Nerc");
    jcasType.ll_cas.ll_setStringValue(addr, ((Nerc_Type)jcasType).casFeatCode_tagset, v);}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (Nerc_Type.featOkTst && ((Nerc_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "it.unitn.nlpir.types.Nerc");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Nerc_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (Nerc_Type.featOkTst && ((Nerc_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "it.unitn.nlpir.types.Nerc");
    jcasType.ll_cas.ll_setStringValue(addr, ((Nerc_Type)jcasType).casFeatCode_value, v);}    
  }

    