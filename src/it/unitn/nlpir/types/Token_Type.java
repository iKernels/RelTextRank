
/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package it.unitn.nlpir.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * @generated */
public class Token_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Token_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Token_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Token(addr, Token_Type.this);
  			   Token_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Token(addr, Token_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Token.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("it.unitn.nlpir.types.Token");
 
  /** @generated */
  final Feature casFeat_postag;
  /** @generated */
  final int     casFeatCode_postag;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPostag(int addr) {
        if (featOkTst && casFeat_postag == null)
      jcas.throwFeatMissing("postag", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_postag);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPostag(int addr, String v) {
        if (featOkTst && casFeat_postag == null)
      jcas.throwFeatMissing("postag", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_postag, v);}
    
  
 
  /** @generated */
  final Feature casFeat_lemma;
  /** @generated */
  final int     casFeatCode_lemma;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getLemma(int addr) {
        if (featOkTst && casFeat_lemma == null)
      jcas.throwFeatMissing("lemma", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_lemma);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLemma(int addr, String v) {
        if (featOkTst && casFeat_lemma == null)
      jcas.throwFeatMissing("lemma", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_lemma, v);}
    
  
 
  /** @generated */
  final Feature casFeat_stem;
  /** @generated */
  final int     casFeatCode_stem;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getStem(int addr) {
        if (featOkTst && casFeat_stem == null)
      jcas.throwFeatMissing("stem", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_stem);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStem(int addr, String v) {
        if (featOkTst && casFeat_stem == null)
      jcas.throwFeatMissing("stem", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_stem, v);}
    
  
 
  /** @generated */
  final Feature casFeat_topic;
  /** @generated */
  final int     casFeatCode_topic;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTopic(int addr) {
        if (featOkTst && casFeat_topic == null)
      jcas.throwFeatMissing("topic", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_topic);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTopic(int addr, String v) {
        if (featOkTst && casFeat_topic == null)
      jcas.throwFeatMissing("topic", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_topic, v);}
    
  
 
  /** @generated */
  final Feature casFeat_isFiltered;
  /** @generated */
  final int     casFeatCode_isFiltered;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsFiltered(int addr) {
        if (featOkTst && casFeat_isFiltered == null)
      jcas.throwFeatMissing("isFiltered", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isFiltered);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsFiltered(int addr, boolean v) {
        if (featOkTst && casFeat_isFiltered == null)
      jcas.throwFeatMissing("isFiltered", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isFiltered, v);}
    
  
 
  /** @generated */
  final Feature casFeat_synonyms;
  /** @generated */
  final int     casFeatCode_synonyms;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSynonyms(int addr) {
        if (featOkTst && casFeat_synonyms == null)
      jcas.throwFeatMissing("synonyms", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getRefValue(addr, casFeatCode_synonyms);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSynonyms(int addr, int v) {
        if (featOkTst && casFeat_synonyms == null)
      jcas.throwFeatMissing("synonyms", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setRefValue(addr, casFeatCode_synonyms, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getSynonyms(int addr, int i) {
        if (featOkTst && casFeat_synonyms == null)
      jcas.throwFeatMissing("synonyms", "it.unitn.nlpir.types.Token");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_synonyms), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_synonyms), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_synonyms), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setSynonyms(int addr, int i, String v) {
        if (featOkTst && casFeat_synonyms == null)
      jcas.throwFeatMissing("synonyms", "it.unitn.nlpir.types.Token");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_synonyms), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_synonyms), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_synonyms), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getIntValue(addr, casFeatCode_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setId(int addr, int v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setIntValue(addr, casFeatCode_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_substitution;
  /** @generated */
  final int     casFeatCode_substitution;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSubstitution(int addr) {
        if (featOkTst && casFeat_substitution == null)
      jcas.throwFeatMissing("substitution", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_substitution);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSubstitution(int addr, String v) {
        if (featOkTst && casFeat_substitution == null)
      jcas.throwFeatMissing("substitution", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_substitution, v);}
    
  
 
  /** @generated */
  final Feature casFeat_NERFeatures;
  /** @generated */
  final int     casFeatCode_NERFeatures;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNERFeatures(int addr) {
        if (featOkTst && casFeat_NERFeatures == null)
      jcas.throwFeatMissing("NERFeatures", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getRefValue(addr, casFeatCode_NERFeatures);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNERFeatures(int addr, int v) {
        if (featOkTst && casFeat_NERFeatures == null)
      jcas.throwFeatMissing("NERFeatures", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setRefValue(addr, casFeatCode_NERFeatures, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getNERFeatures(int addr, int i) {
        if (featOkTst && casFeat_NERFeatures == null)
      jcas.throwFeatMissing("NERFeatures", "it.unitn.nlpir.types.Token");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_NERFeatures), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_NERFeatures), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_NERFeatures), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setNERFeatures(int addr, int i, String v) {
        if (featOkTst && casFeat_NERFeatures == null)
      jcas.throwFeatMissing("NERFeatures", "it.unitn.nlpir.types.Token");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_NERFeatures), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_NERFeatures), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_NERFeatures), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_hypernyms;
  /** @generated */
  final int     casFeatCode_hypernyms;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getHypernyms(int addr) {
        if (featOkTst && casFeat_hypernyms == null)
      jcas.throwFeatMissing("hypernyms", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getRefValue(addr, casFeatCode_hypernyms);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setHypernyms(int addr, int v) {
        if (featOkTst && casFeat_hypernyms == null)
      jcas.throwFeatMissing("hypernyms", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setRefValue(addr, casFeatCode_hypernyms, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getHypernyms(int addr, int i) {
        if (featOkTst && casFeat_hypernyms == null)
      jcas.throwFeatMissing("hypernyms", "it.unitn.nlpir.types.Token");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_hypernyms), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_hypernyms), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_hypernyms), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setHypernyms(int addr, int i, String v) {
        if (featOkTst && casFeat_hypernyms == null)
      jcas.throwFeatMissing("hypernyms", "it.unitn.nlpir.types.Token");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_hypernyms), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_hypernyms), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_hypernyms), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_sentiment;
  /** @generated */
  final int     casFeatCode_sentiment;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSentiment(int addr) {
        if (featOkTst && casFeat_sentiment == null)
      jcas.throwFeatMissing("sentiment", "it.unitn.nlpir.types.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_sentiment);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSentiment(int addr, String v) {
        if (featOkTst && casFeat_sentiment == null)
      jcas.throwFeatMissing("sentiment", "it.unitn.nlpir.types.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_sentiment, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Token_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_postag = jcas.getRequiredFeatureDE(casType, "postag", "uima.cas.String", featOkTst);
    casFeatCode_postag  = (null == casFeat_postag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_postag).getCode();

 
    casFeat_lemma = jcas.getRequiredFeatureDE(casType, "lemma", "uima.cas.String", featOkTst);
    casFeatCode_lemma  = (null == casFeat_lemma) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lemma).getCode();

 
    casFeat_stem = jcas.getRequiredFeatureDE(casType, "stem", "uima.cas.String", featOkTst);
    casFeatCode_stem  = (null == casFeat_stem) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_stem).getCode();

 
    casFeat_topic = jcas.getRequiredFeatureDE(casType, "topic", "uima.cas.String", featOkTst);
    casFeatCode_topic  = (null == casFeat_topic) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_topic).getCode();

 
    casFeat_isFiltered = jcas.getRequiredFeatureDE(casType, "isFiltered", "uima.cas.Boolean", featOkTst);
    casFeatCode_isFiltered  = (null == casFeat_isFiltered) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isFiltered).getCode();

 
    casFeat_synonyms = jcas.getRequiredFeatureDE(casType, "synonyms", "uima.cas.StringArray", featOkTst);
    casFeatCode_synonyms  = (null == casFeat_synonyms) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_synonyms).getCode();

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.Integer", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_substitution = jcas.getRequiredFeatureDE(casType, "substitution", "uima.cas.String", featOkTst);
    casFeatCode_substitution  = (null == casFeat_substitution) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_substitution).getCode();

 
    casFeat_NERFeatures = jcas.getRequiredFeatureDE(casType, "NERFeatures", "uima.cas.StringArray", featOkTst);
    casFeatCode_NERFeatures  = (null == casFeat_NERFeatures) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_NERFeatures).getCode();

 
    casFeat_hypernyms = jcas.getRequiredFeatureDE(casType, "hypernyms", "uima.cas.StringArray", featOkTst);
    casFeatCode_hypernyms  = (null == casFeat_hypernyms) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_hypernyms).getCode();

 
    casFeat_sentiment = jcas.getRequiredFeatureDE(casType, "sentiment", "uima.cas.String", featOkTst);
    casFeatCode_sentiment  = (null == casFeat_sentiment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sentiment).getCode();

  }
}



    