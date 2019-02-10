/*
 * CoinModel.java
 */

package jni_coin;

/**
 * Wrapper class for CoinModel
 */
public class CoinModel {

  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/
  public static native void jni_addColumn( long cPtr, int noInCol, double dLoBo, 
              double dUpBo, double dCost, String sColName, boolean bIsInteger );
  public static native void jni_addRow( long cPtr, int noInRow, int[] anIndex,
              double[] adValue, double dLoBo, double dUpBo, String sRowName );
  public static native long jni_new();
  public static native void jni_delete( long cPtr );
  
  /*--------------------------------------------------------------------------*/
  CoinModel() { cPtr = jni_new(); }

  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }
  
  /*--------------------------------------------------------------------------*/  
  public int addColumn( double dLowerBound, double dUpperBound, double dCost, 
                        String sColName, boolean bIsInteger ) {    
    final int NO_IN_COUMN = 0;
    jni_addColumn( cPtr, NO_IN_COUMN, dLowerBound, dUpperBound, 
                   dCost, sColName, bIsInteger );    
    return 0; // rc for future use
  }
  
  public int addRow( int noInRow, int[] anIndex, double[] adValue, 
                     double dLoBo, double dUpBo, String sRowName ) {
    jni_addRow( cPtr, noInRow, anIndex, adValue, dLoBo, dUpBo, sRowName );        
    return 0; // rc for future use
  }
      
  public void delete() { if( cPtr != 0 ) { jni_delete( cPtr ); cPtr = 0L; } }
}
