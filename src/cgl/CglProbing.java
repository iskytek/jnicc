/*
 * CglProbing.java
 */

package cgl;

/**
 * Wrapper class for Cgl Probing
 */
public class CglProbing {

  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/  
  public static native long jni_new();
  public static native void jni_setMaxElements( long cPtr, int iValue );
  public static native void jni_setMaxElementsRoot( long cPtr, int iValue );
  public static native void jni_setMaxLook( long cPtr, int iValue );
  public static native void jni_setMaxLookRoot( long cPtr, int iValue );
  public static native void jni_setMaxPass( long cPtr, int iValue );
  public static native void jni_setMaxPassRoot( long cPtr, int iValue );
  public static native void jni_setRowCuts( long cPtr, int iValue );
  public static native void jni_setUsingObjective( long cPtr, int iValue );
  
  /*--------------------------------------------------------------------------*/
  public CglProbing() { cPtr = jni_new(); }
  
  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }

  public void setMaxElements( int iValue ) {
    if( cPtr != 0 ) jni_setMaxElements( cPtr, iValue );    
  }

  public void setMaxElementsRoot( int iValue ) {
    if( cPtr != 0 ) jni_setMaxElementsRoot( cPtr, iValue );    
  }

  public void setMaxLook( int iValue ) {
    if( cPtr != 0 ) jni_setMaxLook( cPtr, iValue );    
  }

  public void setMaxLookRoot( int iValue ) {
    if( cPtr != 0 ) jni_setMaxLookRoot( cPtr, iValue );    
  }

  public void setMaxPass( int iValue ) {
    if( cPtr != 0 ) jni_setMaxPass( cPtr, iValue );    
  }

  public void setMaxPassRoot( int iValue ) {
    if( cPtr != 0 ) jni_setMaxPassRoot( cPtr, iValue );    
  }

  public void setRowCuts( int iValue ) {
    if( cPtr != 0 ) jni_setRowCuts( cPtr, iValue );    
  }

  public void setUsingObjective( int iValue ) {
    if( cPtr != 0 ) jni_setUsingObjective( cPtr, iValue );    
  }
}
