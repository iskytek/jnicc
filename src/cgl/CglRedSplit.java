/*
 * CglRedSplit.java
 */

package cgl;

/**
 * Wrapper class for Cgl RedSplit
 */
public class CglRedSplit {

  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/  
  public static native long jni_new();
  public static native void jni_setLimit( long cPtr, int iValue );

  /*--------------------------------------------------------------------------*/
  public CglRedSplit() { cPtr = jni_new(); }
  
  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }

  public void setLimit( int iValue ) {
    if( cPtr != 0 ) jni_setLimit( cPtr, iValue );    
  }
}
