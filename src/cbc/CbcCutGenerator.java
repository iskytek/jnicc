/*
 * CbcCutGenerator.java
 */

package cbc;

/**
 * Interface between Cbc and Cut Generation Library. 
 */
public class CbcCutGenerator {

  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/
  public static native String  jni_generateTuning( long cPtr );
  public static native String  jni_getCutGeneratorName( long cPtr );
  public static native int     jni_numberCutsInTotal( long cPtr );
  public static native int     jni_numberCutsActive( long cPtr );
  public static native int     jni_numberTimesEntered( long cPtr );
  public static native void    jni_refreshModel( long cPtr, long cPtrCbcModel );
  
  //Set level of cut inaccuracy (0 means exact e.g. cliques)
  public static native void    jni_setInaccuracy( long cPtr, int iVal );
  
  public static native void    jni_setNeedsOptimalBasis( long cPtr,
                                                         boolean bVal );
  
  //Set whether the cut generator should be called in the normal place. 
  public static native void    jni_setNormal( long cPtr, boolean bVal );
  
  public static native void    jni_setSwitchOfIfLessThan( long cPtr, int iVal );
  
  //Set the depth criterion for calls to the Cgl object's generateCuts routine. 
  //Only active if > 0
  public static native void    jni_setWhatDepth( long cPtr, int iVal );
  
  public static native double  jni_timeInCutGenerator( long cPtr );
  public static native boolean jni_timing( long cPtr );
  /*--------------------------------------------------------------------------*/
  public CbcCutGenerator( long cPtr ) { this.cPtr = cPtr; }
  
  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }
  
  /*--------------------------------------------------------------------------*/
  public String generateTuning() {
    String s = "No tuning available.";
    if( cPtr != 0 ) { s = jni_generateTuning( cPtr ); }
    return s;
  }
  
  public String getCutGeneratorName() {
    String sName = "NoName";
    if( cPtr != 0 ) { sName = jni_getCutGeneratorName( cPtr ); }
    return sName;
  }

  public int numberCutsActive() {
    int n = -1;
    if( cPtr != 0 ) n = jni_numberCutsActive( cPtr );
    return n;
  }

  public int numberCutsInTotal() {
    int n = -1;
    if( cPtr != 0 ) n = jni_numberCutsInTotal( cPtr);
    return n;
  }

  public int numberTimesEntered() {
    int n = -1;
    if( cPtr != 0 ) n = jni_numberTimesEntered( cPtr );
    return n;
  }

  public void refreshModel( CbcModel cbcModel ) {
    jni_refreshModel( cPtr, cbcModel.getCPtr() );
  }

  public void setInaccuracy( int iVal ) {
    jni_setInaccuracy( cPtr, iVal );
  }

  public void setNeedsOptimalBasis( boolean bVal ) {
    jni_setNeedsOptimalBasis( cPtr, bVal );
  }

  public void setNormal( boolean bVal ) {
    jni_setNormal( cPtr, bVal );
  }

  public void setSwitchOffIfLessThan( int iVal ) {
    jni_setSwitchOfIfLessThan( cPtr, iVal );
  }

  public void setWhatDepth( int iVal ) {
    jni_setWhatDepth( cPtr, iVal );
  }

  public double timeInCutGenerator() {
    double d = Double.NaN;
    if( cPtr != 0 ) { d = jni_timeInCutGenerator( cPtr ); }
    return d;
  }
    
  public boolean timing() {
    boolean b = false;
    if( cPtr != 0 ) b = jni_timing( cPtr );
    return b;
  }
}
