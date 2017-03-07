

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package it.unitn.nlpir.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class Token extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Token.class);
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
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Token(JCas jcas, int begin, int end) {
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
  //* Feature: postag

  /** getter for postag - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPostag() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_postag == null)
      jcasType.jcas.throwFeatMissing("postag", "it.unitn.nlpir.types.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_postag);}
    
  /** setter for postag - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPostag(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_postag == null)
      jcasType.jcas.throwFeatMissing("postag", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_postag, v);}    
   
    
  //*--------------*
  //* Feature: lemma

  /** getter for lemma - gets 
   * @generated
   * @return value of the feature 
   */
  public String getLemma() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "it.unitn.nlpir.types.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_lemma);}
    
  /** setter for lemma - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLemma(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_lemma, v);}    
   
    
  //*--------------*
  //* Feature: stem

  /** getter for stem - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStem() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_stem == null)
      jcasType.jcas.throwFeatMissing("stem", "it.unitn.nlpir.types.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_stem);}
    
  /** setter for stem - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStem(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_stem == null)
      jcasType.jcas.throwFeatMissing("stem", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_stem, v);}    
   
    
  //*--------------*
  //* Feature: topic

  /** getter for topic - gets LDA Topic
   * @generated
   * @return value of the feature 
   */
  public String getTopic() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_topic == null)
      jcasType.jcas.throwFeatMissing("topic", "it.unitn.nlpir.types.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_topic);}
    
  /** setter for topic - sets LDA Topic 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTopic(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_topic == null)
      jcasType.jcas.throwFeatMissing("topic", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_topic, v);}    
   
    
  //*--------------*
  //* Feature: isFiltered

  /** getter for isFiltered - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsFiltered() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_isFiltered == null)
      jcasType.jcas.throwFeatMissing("isFiltered", "it.unitn.nlpir.types.Token");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Token_Type)jcasType).casFeatCode_isFiltered);}
    
  /** setter for isFiltered - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsFiltered(boolean v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_isFiltered == null)
      jcasType.jcas.throwFeatMissing("isFiltered", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Token_Type)jcasType).casFeatCode_isFiltered, v);}    
   
    
  //*--------------*
  //* Feature: synonyms

  /** getter for synonyms - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getSynonyms() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_synonyms == null)
      jcasType.jcas.throwFeatMissing("synonyms", "it.unitn.nlpir.types.Token");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_synonyms)));}
    
  /** setter for synonyms - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSynonyms(StringArray v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_synonyms == null)
      jcasType.jcas.throwFeatMissing("synonyms", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setRefValue(addr, ((Token_Type)jcasType).casFeatCode_synonyms, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for synonyms - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getSynonyms(int i) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_synonyms == null)
      jcasType.jcas.throwFeatMissing("synonyms", "it.unitn.nlpir.types.Token");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_synonyms), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_synonyms), i);}

  /** indexed setter for synonyms - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSynonyms(int i, String v) { 
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_synonyms == null)
      jcasType.jcas.throwFeatMissing("synonyms", "it.unitn.nlpir.types.Token");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_synonyms), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_synonyms), i, v);}
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated
   * @return value of the feature 
   */
  public int getId() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "it.unitn.nlpir.types.Token");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Token_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(int v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setIntValue(addr, ((Token_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: substitution

  /** getter for substitution - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSubstitution() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_substitution == null)
      jcasType.jcas.throwFeatMissing("substitution", "it.unitn.nlpir.types.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_substitution);}
    
  /** setter for substitution - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSubstitution(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_substitution == null)
      jcasType.jcas.throwFeatMissing("substitution", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_substitution, v);}    
   
    
  //*--------------*
  //* Feature: NERFeatures

  /** getter for NERFeatures - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getNERFeatures() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_NERFeatures == null)
      jcasType.jcas.throwFeatMissing("NERFeatures", "it.unitn.nlpir.types.Token");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_NERFeatures)));}
    
  /** setter for NERFeatures - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNERFeatures(StringArray v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_NERFeatures == null)
      jcasType.jcas.throwFeatMissing("NERFeatures", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setRefValue(addr, ((Token_Type)jcasType).casFeatCode_NERFeatures, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for NERFeatures - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getNERFeatures(int i) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_NERFeatures == null)
      jcasType.jcas.throwFeatMissing("NERFeatures", "it.unitn.nlpir.types.Token");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_NERFeatures), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_NERFeatures), i);}

  /** indexed setter for NERFeatures - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setNERFeatures(int i, String v) { 
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_NERFeatures == null)
      jcasType.jcas.throwFeatMissing("NERFeatures", "it.unitn.nlpir.types.Token");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_NERFeatures), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_NERFeatures), i, v);}
   
    
  //*--------------*
  //* Feature: hypernyms

  /** getter for hypernyms - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getHypernyms() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_hypernyms == null)
      jcasType.jcas.throwFeatMissing("hypernyms", "it.unitn.nlpir.types.Token");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_hypernyms)));}
    
  /** setter for hypernyms - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setHypernyms(StringArray v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_hypernyms == null)
      jcasType.jcas.throwFeatMissing("hypernyms", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setRefValue(addr, ((Token_Type)jcasType).casFeatCode_hypernyms, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for hypernyms - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getHypernyms(int i) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_hypernyms == null)
      jcasType.jcas.throwFeatMissing("hypernyms", "it.unitn.nlpir.types.Token");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_hypernyms), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_hypernyms), i);}

  /** indexed setter for hypernyms - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setHypernyms(int i, String v) { 
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_hypernyms == null)
      jcasType.jcas.throwFeatMissing("hypernyms", "it.unitn.nlpir.types.Token");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_hypernyms), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_hypernyms), i, v);}
   
    
  //*--------------*
  //* Feature: sentiment

  /** getter for sentiment - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSentiment() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_sentiment == null)
      jcasType.jcas.throwFeatMissing("sentiment", "it.unitn.nlpir.types.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_sentiment);}
    
  /** setter for sentiment - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentiment(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_sentiment == null)
      jcasType.jcas.throwFeatMissing("sentiment", "it.unitn.nlpir.types.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_sentiment, v);}    
  }

    