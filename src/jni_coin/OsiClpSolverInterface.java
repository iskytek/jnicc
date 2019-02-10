/*
 * OsiClpSolverInterface.java
 */

package jni_coin;

/**
 * Calls to OsiClpSolverInterface
 */
public class OsiClpSolverInterface {

  public static final double OBJ_SENSE_MIN =  1.0;
  public static final double OBJ_SENSE_MAX = -1.0;
  
  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/
  public static native long    jni_new();
  public static native void    jni_delete( long cPtr );
  public static native String  jni_getModelName( long cPtr );
  public static native int     jni_getNumCols( long cPtr );
  public static native int     jni_getNumRows( long cPtr );
  public static native int     jni_loadFromCoinModel( long cPtr, 
                                    long cPtrCoinModel, boolean bKeepSolution );
  public static native void    jni_releaseClp( long cPtr );
  public static native boolean jni_setModelName( long cPtr, String sModelName );
  public static native void    jni_setObjSense( long cPtr, double dObjSense );
  public static native int     jni_readMps( long cPtr, String sMpsFile );
  
  /*--------------------------------------------------------------------------*/
  OsiClpSolverInterface() { cPtr = jni_new(); }

  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }

  /*--------------------------------------------------------------------------*/
  public void delete() {   
    releaseClp();
    if( cPtr != 0 ) { jni_delete( cPtr ); cPtr = 0L; }
  }

  public String getModelName() {
    String sModelName = null;
    if( cPtr != 0 ) { sModelName = jni_getModelName( cPtr ); }
    return sModelName;
  }

  public int getNumCols() {
    int numCols = 0;
    if( cPtr != 0 ) { numCols= jni_getNumCols( cPtr ); }
    return numCols;
  }

  public int getNumRows() {
    int numRows = 0;
    if( cPtr != 0 ) { numRows= jni_getNumRows( cPtr ); }
    return numRows;
  }

  public int loadFromCoinModel( CoinModel coinModel, boolean bKeepSolution ) {
    return jni_loadFromCoinModel( cPtr, coinModel.getCPtr(), bKeepSolution );
  }
  
  public int readMps( String sMpsFile ) {
    int noErrors = -1;
    if( cPtr != 0 ) { noErrors = jni_readMps( cPtr, sMpsFile ); }
    return noErrors;
  }
  
  public void releaseClp() { 
    if( cPtr != 0 ) { jni_releaseClp( cPtr ); } 
  }

  public boolean setModelName( String sModelName ) {
    boolean bRc = false;
    if( sModelName == null ) sModelName = "ModelName";
    if( cPtr != 0 ) { bRc = jni_setModelName( cPtr, sModelName ); }
    return bRc;
  }

  public void setObjSense( double dObjSense ) { 
    if( cPtr != 0 ) { jni_setObjSense( cPtr, dObjSense ); } 
  }
}
