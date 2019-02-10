/*
 * CglTwomir.java
 */

package cgl;

/**
 * Wrapper class for Cgl Twomir (Twostep MIR)
 */
public class CglTwomir {

  public final static int TYPE_NORMAL = 0;
  
  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/  
  public static native long jni_new();
  
  // 0 normal, 1 add original matrix one, 2 replace. 
  public static native void jni_setTwomirType( long cPtr, int iValue );

  /*--------------------------------------------------------------------------*/
  public CglTwomir() { cPtr = jni_new(); }
  
  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }

  public void setTwomirType( int iValue ) {
    if( cPtr != 0 ) jni_setTwomirType( cPtr, iValue );    
  }
}
