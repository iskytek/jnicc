/*
 * CbcModel.java
 */

package cbc;

import jni_coin.OsiClpSolverInterface;

/**
 * Calls to WrCbcModel.cpp
 */
public class CbcModel {

  public final static int STATISTICS_NO            = 0;
  public final static int STATISTICS_SUMMARY       = 1;
  public final static int STATISTICS_SOLUTION      = 2;
  public final static int STATISTICS_NODES         = 3;
  public final static int DISABLE_STRONG_BRANCHING = 0;
  
  private transient long cPtr;
  
  /*--------------------------------------------------------------------------*/
  public static native void    jni_addCutGenerator( long cPtr, 
                                   long cPtrGenerator, int nHowOften, 
                                   String sName );
  /*public static native void    jni_addCutGomory( long cPtr, int nHowOften, 
                                                            int nLimit );*/
  public static native void    jni_assignSolver( long cPtr, long sPtrLpSolver, 
                                                 boolean deleteSolver );
  public static native boolean jni_bestSolution( long cPtr, 
                                                 double[] adSolution );
  public static native void    jni_branchAndBound( long cPtr, int nStatistics );
  public static native void    jni_delete( long cPtr );
  public static native double  jni_getBestPossibleObjValue( long cPtr );
  public static native long    jni_getCutGenerator( long cPtr, int index );
  public static native int     jni_getNumberCutGenerators( long cPtr );
  public static native int     jni_getNumberThreads( long cPtr );
  public static native double  jni_getObjValue( long cPtr );
  public static native void    jni_initialSolve( long cPtr );
  public static native boolean jni_isProvenOptimal( long cPtr );
  public static native long    jni_new();
  public static native void    jni_setBestSolution( long cPtr, 
                                   double[] adBestSol, int noCols, double dObj,
                                   boolean bCheck );
  public static native void    jni_setLogLevel( long cPtr, int nLogLevel );
  public static native void    jni_setNumberStrong( long cPtr, int nMaxCand );
  public static native void    jni_setNumberThreads( long cPtr, int noThreads );
  
  /*--------------------------------------------------------------------------*/
  public CbcModel() { cPtr = jni_new(); }

  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }
  
  /*--------------------------------------------------------------------------*/
  public void addCutGenerator( long cPtrGenerator, int nHowOften, 
                                                   String sName ) {
    if( cPtr != 0 && cPtrGenerator != 0 ) 
                   jni_addCutGenerator( cPtr, cPtrGenerator, nHowOften, sName );
  }

  /*public void addCutGomory( int nHowOften, int nLimit ) {
    if( cPtr != 0 ) jni_addCutGomory( cPtr, nHowOften, nLimit );
  }*/
  
  public void assignSolver( OsiClpSolverInterface lpSolver, 
                                                  boolean deleteSolver ) {
    long cPtrSolver = lpSolver.getCPtr();
    if( cPtr != 0 && cPtrSolver != 0 ) 
      jni_assignSolver( cPtr, cPtrSolver, deleteSolver );    
  }

  /**
   * Get best solution from Cbc
   * @param NO_COLS
   * @return
   */
  public double[] bestSolution( final int NO_COLS ) {
    
    double[] adSolution = null;
    if( cPtr != 0 ) {
      adSolution = new double[ NO_COLS ];
      boolean isOk = jni_bestSolution( cPtr, adSolution );
      if( ! isOk ) adSolution = null;
    }
    
    return adSolution;
  }

  /**
   * Define an integer solution in run/jnicc.ini (if known)
   * @param adBestSol
   */
  public void setBestSolution( double[] adBestSol ) {
    
    final int NO_COLS = adBestSol.length;
    double dObj = 0.0; 
    for( double d: adBestSol ) dObj += d;
    jni_setBestSolution( cPtr, adBestSol, NO_COLS, dObj, true );
  }
  
  public void branchAndBound( int nStatistics ) {
    if( cPtr != 0 ) jni_branchAndBound( cPtr, nStatistics );    
  }

  public void delete() { 
    if( cPtr != 0 ) { jni_delete( cPtr ); cPtr = 0L; }
  }

  public long getCutGenerator( int index ) {
    long cPtrCbcCutGenerator = 0;
    if( cPtr != 0 ) { cPtrCbcCutGenerator = jni_getCutGenerator( cPtr, index );}
    return cPtrCbcCutGenerator;
  }

  public int getNumberCutGenerators() {
    int noGenerators = 0;
    if( cPtr != 0 ) { noGenerators = jni_getNumberCutGenerators( cPtr ); }
    return noGenerators;
  }

  public int getNumberThreads() {
    int noThreads = 0;
    if( cPtr != 0 ) { noThreads = jni_getNumberThreads( cPtr ); }
    return noThreads;
  }
  
  public Double getBestPossibleObjValue() {
    Double dBestPossible = null;
    if( cPtr != 0 ) dBestPossible = jni_getBestPossibleObjValue( cPtr );
    return dBestPossible;
  }
  
  public double getObjValue() {
    double dObjValue = Double.NaN;
    if( cPtr != 0 ) { dObjValue = jni_getObjValue( cPtr ); }
    return dObjValue;
  }

  public void initialSolve() { if( cPtr != 0 ) jni_initialSolve( cPtr ); } 

  public boolean isProvenOptimal() {
    boolean bIsOptimal = false;
    if( cPtr != 0 ) bIsOptimal = jni_isProvenOptimal( cPtr );
    return bIsOptimal;
  }

  public void setLogLevel( int nLogLevel ) {
    if( nLogLevel < 0 ) nLogLevel = 0;
    if( cPtr != 0 ) { jni_setLogLevel( cPtr, nLogLevel ); }
  }

  /*public void setNodeComparison() {
   * Methode befindet sich in CbcCompareWolf.java
    if( cPtr != 0 ) { jni_setNodeComparison( cPtr ); }
  }*/

  public void setNumberStrong( int nMaxCand ) {
    if( cPtr != 0 ) { jni_setNumberStrong( cPtr, nMaxCand ); }
  }

  public void setNumberThreads( int noThreads ) {
    if( cPtr != 0 ) { jni_setNumberThreads( cPtr, noThreads ); }
  }
}
