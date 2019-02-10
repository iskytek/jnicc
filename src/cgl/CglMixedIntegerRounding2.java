/*
 * CglMixedIntegerRouding2.java
 */

package cgl;

/**
 * Wrapper class for Cgl MixedIntegerRounding2
 */
public class CglMixedIntegerRounding2 {

  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/  
  public static native long jni_new();
  
  /*--------------------------------------------------------------------------*/
  public CglMixedIntegerRounding2() { cPtr = jni_new(); }
  
  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }
}
