

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
public class WikipediaPage extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WikipediaPage.class);
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
  protected WikipediaPage() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public WikipediaPage(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public WikipediaPage(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public WikipediaPage(JCas jcas, int begin, int end) {
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
  //* Feature: URI

  /** getter for URI - gets 
   * @generated
   * @return value of the feature 
   */
  public String getURI() {
    if (WikipediaPage_Type.featOkTst && ((WikipediaPage_Type)jcasType).casFeat_URI == null)
      jcasType.jcas.throwFeatMissing("URI", "it.unitn.nlpir.types.WikipediaPage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaPage_Type)jcasType).casFeatCode_URI);}
    
  /** setter for URI - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setURI(String v) {
    if (WikipediaPage_Type.featOkTst && ((WikipediaPage_Type)jcasType).casFeat_URI == null)
      jcasType.jcas.throwFeatMissing("URI", "it.unitn.nlpir.types.WikipediaPage");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaPage_Type)jcasType).casFeatCode_URI, v);}    
   
    
  //*--------------*
  //* Feature: disambiguatorConfidence

  /** getter for disambiguatorConfidence - gets 
   * @generated
   * @return value of the feature 
   */
  public double getDisambiguatorConfidence() {
    if (WikipediaPage_Type.featOkTst && ((WikipediaPage_Type)jcasType).casFeat_disambiguatorConfidence == null)
      jcasType.jcas.throwFeatMissing("disambiguatorConfidence", "it.unitn.nlpir.types.WikipediaPage");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((WikipediaPage_Type)jcasType).casFeatCode_disambiguatorConfidence);}
    
  /** setter for disambiguatorConfidence - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDisambiguatorConfidence(double v) {
    if (WikipediaPage_Type.featOkTst && ((WikipediaPage_Type)jcasType).casFeat_disambiguatorConfidence == null)
      jcasType.jcas.throwFeatMissing("disambiguatorConfidence", "it.unitn.nlpir.types.WikipediaPage");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((WikipediaPage_Type)jcasType).casFeatCode_disambiguatorConfidence, v);}    
  }

    