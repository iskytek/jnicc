/*
 * CglKnapsackCover.java
 */

package cgl;

/**
 * Wrapper class for Cgl KnapsackCover
 */
public class CglKnapsackCover {

  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/  
  public static native long jni_new();
  
  /*--------------------------------------------------------------------------*/
  public CglKnapsackCover() { cPtr = jni_new(); }
  
  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }
}
