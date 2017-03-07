

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
public class ContextTrees extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ContextTrees.class);
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
  protected ContextTrees() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ContextTrees(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ContextTrees(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ContextTrees(JCas jcas, int begin, int end) {
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
  //* Feature: trees

  /** getter for trees - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getTrees() {
    if (ContextTrees_Type.featOkTst && ((ContextTrees_Type)jcasType).casFeat_trees == null)
      jcasType.jcas.throwFeatMissing("trees", "it.unitn.nlpir.types.ContextTrees");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ContextTrees_Type)jcasType).casFeatCode_trees)));}
    
  /** setter for trees - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTrees(StringArray v) {
    if (ContextTrees_Type.featOkTst && ((ContextTrees_Type)jcasType).casFeat_trees == null)
      jcasType.jcas.throwFeatMissing("trees", "it.unitn.nlpir.types.ContextTrees");
    jcasType.ll_cas.ll_setRefValue(addr, ((ContextTrees_Type)jcasType).casFeatCode_trees, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for trees - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getTrees(int i) {
    if (ContextTrees_Type.featOkTst && ((ContextTrees_Type)jcasType).casFeat_trees == null)
      jcasType.jcas.throwFeatMissing("trees", "it.unitn.nlpir.types.ContextTrees");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ContextTrees_Type)jcasType).casFeatCode_trees), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ContextTrees_Type)jcasType).casFeatCode_trees), i);}

  /** indexed setter for trees - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setTrees(int i, String v) { 
    if (ContextTrees_Type.featOkTst && ((ContextTrees_Type)jcasType).casFeat_trees == null)
      jcasType.jcas.throwFeatMissing("trees", "it.unitn.nlpir.types.ContextTrees");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ContextTrees_Type)jcasType).casFeatCode_trees), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ContextTrees_Type)jcasType).casFeatCode_trees), i, v);}
  }

    