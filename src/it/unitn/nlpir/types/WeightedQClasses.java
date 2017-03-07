

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package it.unitn.nlpir.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class WeightedQClasses extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WeightedQClasses.class);
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
  protected WeightedQClasses() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public WeightedQClasses(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public WeightedQClasses(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public WeightedQClasses(JCas jcas, int begin, int end) {
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
  //* Feature: classesNames

  /** getter for classesNames - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getClassesNames() {
    if (WeightedQClasses_Type.featOkTst && ((WeightedQClasses_Type)jcasType).casFeat_classesNames == null)
      jcasType.jcas.throwFeatMissing("classesNames", "it.unitn.nlpir.types.WeightedQClasses");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesNames)));}
    
  /** setter for classesNames - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassesNames(StringArray v) {
    if (WeightedQClasses_Type.featOkTst && ((WeightedQClasses_Type)jcasType).casFeat_classesNames == null)
      jcasType.jcas.throwFeatMissing("classesNames", "it.unitn.nlpir.types.WeightedQClasses");
    jcasType.ll_cas.ll_setRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesNames, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for classesNames - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getClassesNames(int i) {
    if (WeightedQClasses_Type.featOkTst && ((WeightedQClasses_Type)jcasType).casFeat_classesNames == null)
      jcasType.jcas.throwFeatMissing("classesNames", "it.unitn.nlpir.types.WeightedQClasses");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesNames), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesNames), i);}

  /** indexed setter for classesNames - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setClassesNames(int i, String v) { 
    if (WeightedQClasses_Type.featOkTst && ((WeightedQClasses_Type)jcasType).casFeat_classesNames == null)
      jcasType.jcas.throwFeatMissing("classesNames", "it.unitn.nlpir.types.WeightedQClasses");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesNames), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesNames), i, v);}
   
    
  //*--------------*
  //* Feature: classesWeights

  /** getter for classesWeights - gets 
   * @generated
   * @return value of the feature 
   */
  public DoubleArray getClassesWeights() {
    if (WeightedQClasses_Type.featOkTst && ((WeightedQClasses_Type)jcasType).casFeat_classesWeights == null)
      jcasType.jcas.throwFeatMissing("classesWeights", "it.unitn.nlpir.types.WeightedQClasses");
    return (DoubleArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesWeights)));}
    
  /** setter for classesWeights - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassesWeights(DoubleArray v) {
    if (WeightedQClasses_Type.featOkTst && ((WeightedQClasses_Type)jcasType).casFeat_classesWeights == null)
      jcasType.jcas.throwFeatMissing("classesWeights", "it.unitn.nlpir.types.WeightedQClasses");
    jcasType.ll_cas.ll_setRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesWeights, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for classesWeights - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public double getClassesWeights(int i) {
    if (WeightedQClasses_Type.featOkTst && ((WeightedQClasses_Type)jcasType).casFeat_classesWeights == null)
      jcasType.jcas.throwFeatMissing("classesWeights", "it.unitn.nlpir.types.WeightedQClasses");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesWeights), i);
    return jcasType.ll_cas.ll_getDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesWeights), i);}

  /** indexed setter for classesWeights - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setClassesWeights(int i, double v) { 
    if (WeightedQClasses_Type.featOkTst && ((WeightedQClasses_Type)jcasType).casFeat_classesWeights == null)
      jcasType.jcas.throwFeatMissing("classesWeights", "it.unitn.nlpir.types.WeightedQClasses");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesWeights), i);
    jcasType.ll_cas.ll_setDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WeightedQClasses_Type)jcasType).casFeatCode_classesWeights), i, v);}
  }

    