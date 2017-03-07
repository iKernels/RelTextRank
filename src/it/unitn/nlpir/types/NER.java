

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
public class NER extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NER.class);
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
  protected NER() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NER(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NER(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NER(JCas jcas, int begin, int end) {
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
  //* Feature: NERtype

  /** getter for NERtype - gets 
   * @generated
   * @return value of the feature 
   */
  public String getNERtype() {
    if (NER_Type.featOkTst && ((NER_Type)jcasType).casFeat_NERtype == null)
      jcasType.jcas.throwFeatMissing("NERtype", "it.unitn.nlpir.types.NER");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NER_Type)jcasType).casFeatCode_NERtype);}
    
  /** setter for NERtype - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNERtype(String v) {
    if (NER_Type.featOkTst && ((NER_Type)jcasType).casFeat_NERtype == null)
      jcasType.jcas.throwFeatMissing("NERtype", "it.unitn.nlpir.types.NER");
    jcasType.ll_cas.ll_setStringValue(addr, ((NER_Type)jcasType).casFeatCode_NERtype, v);}    
   
    
  //*--------------*
  //* Feature: lodbased

  /** getter for lodbased - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getLodbased() {
    if (NER_Type.featOkTst && ((NER_Type)jcasType).casFeat_lodbased == null)
      jcasType.jcas.throwFeatMissing("lodbased", "it.unitn.nlpir.types.NER");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((NER_Type)jcasType).casFeatCode_lodbased);}
    
  /** setter for lodbased - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLodbased(boolean v) {
    if (NER_Type.featOkTst && ((NER_Type)jcasType).casFeat_lodbased == null)
      jcasType.jcas.throwFeatMissing("lodbased", "it.unitn.nlpir.types.NER");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((NER_Type)jcasType).casFeatCode_lodbased, v);}    
  }

    