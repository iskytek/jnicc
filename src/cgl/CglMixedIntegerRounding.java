/*
 * CglMixedIntegerRouding.java
 */

package cgl;

/**
 * Wrapper class for Cgl MixedIntegerRounding
 */
public class CglMixedIntegerRounding {

  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/  
  public static native long jni_new();
  
  /*--------------------------------------------------------------------------*/
  public CglMixedIntegerRounding() { cPtr = jni_new(); }
  
  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }
}
