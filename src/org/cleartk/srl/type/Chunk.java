

/* First created by JCasGen Wed Nov 23 17:36:58 CET 2016 */
package org.cleartk.srl.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.cleartk.score.type.ScoredAnnotation;


/** 
 * Updated by JCasGen Wed Nov 23 17:36:58 CET 2016
 * XML source: /Users/kateryna/Documents/workspace/RelationalTextRanking/desc/PipelineTypeSystem.xml
 * @generated */
public class Chunk extends ScoredAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Chunk.class);
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
  protected Chunk() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Chunk(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Chunk(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Chunk(JCas jcas, int begin, int end) {
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
  //* Feature: chunkType

  /** getter for chunkType - gets 
   * @generated
   * @return value of the feature 
   */
  public String getChunkType() {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_chunkType == null)
      jcasType.jcas.throwFeatMissing("chunkType", "org.cleartk.srl.type.Chunk");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Chunk_Type)jcasType).casFeatCode_chunkType);}
    
  /** setter for chunkType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setChunkType(String v) {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_chunkType == null)
      jcasType.jcas.throwFeatMissing("chunkType", "org.cleartk.srl.type.Chunk");
    jcasType.ll_cas.ll_setStringValue(addr, ((Chunk_Type)jcasType).casFeatCode_chunkType, v);}    
  }

    