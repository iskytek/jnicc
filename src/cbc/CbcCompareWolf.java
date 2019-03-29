/*
 * CbcCompareWolf.java
 */

package cbc;

import static java.lang.System.*;

import cgl.Cgl;
import jni_coin.MathUtils;
import jni_coin.Worker;

/**
 * Node comparison and call back handling
 */
public class CbcCompareWolf {

  private final int CMP_MODE_MIN_OBJ     = 0;
  private final int CMP_MODE_MAX_OBJ     = 1;
  private final int CMP_MODE_MIN_DEPTH   = 2;
  private final int CMP_MODE_MAX_DEPTH   = 3;
  private final int CMP_MODE_MIN_UNSATIS = 4;
  private final int CMP_MODE_MAX_UNSATIS = 5;

  private final long EKN_SEC_MILLI       = 1000 * 60 * 5;
  private final long GENERATOR_SEC_MILLI = 1000 * 60 * 5;    

  private transient long cPtr;
  
  private CbcModel cbcModel = null;
  private Worker   worker   = null;
  private int nBestZSoFar = Integer.MAX_VALUE;
  private int noColsInModel = 0;
  private int nCmpMode = CMP_MODE_MIN_OBJ;
  private long lastEKNPrint      = 0; //currentTimeMillis();
  private long lastTuningPrint   = 0; //currentTimeMillis();
  private double dnodesSve       = 0.0;
  private double dNodesOnTreeSve = 0;
  
  public native long jni_setNodeComparison( long cPtrCbcModel );
  
  /*--------------------------------------------------------------------------*/
  public CbcCompareWolf( Worker worker, CbcModel cbcModel ) {
   
    this.cbcModel = cbcModel; 
    this.worker   = worker;
    long cPtrCbcModel = cbcModel.getCPtr();
    if( cPtrCbcModel != 0 ) {
      cPtr = jni_setNodeComparison( cPtrCbcModel ); 
    } else err.println( "Null Pointer in CbcCompareWolf!" );
  }
  
  /*--------------------------------------------------------------------------*/
  public long getCPtr() { return cPtr; }
  
  /*--------------------------------------------------------------------------*/
  /**
   * Call back from CbcCompareWolf::every1000Node()
   * @param cPtrCbcModel
   * @param noNodes nodes solved
   * @return (new) compare mode
   */
  public int every1000Nodes( long cPtrCbcModel, int noNodes, 
                             int nodesOnTree  , int nMaxDepth ) {    
    
    if( cbcModel.getCPtr() != cPtrCbcModel ) 
                              out.println( "Pointers different! Why?" );
  
    // Growth rate:
    double dRate = 100.0 * (nodesOnTree - dNodesOnTreeSve) /
                           (noNodes     - dnodesSve      );
    dNodesOnTreeSve = nodesOnTree; dnodesSve = noNodes;
    
    // Define strategy:    
    if( noNodes < worker.CMP_NODES_DEPTH ) nCmpMode = CMP_MODE_MAX_DEPTH;
    else if( noNodes < (1.5 * worker.CMP_NODES_DEPTH) ) 
                                                nCmpMode = CMP_MODE_MIN_UNSATIS;
    else {
      if( nodesOnTree > worker.MAX_NODES_ON_TREE ) nCmpMode = CMP_MODE_MAX_DEPTH;
      else if( nodesOnTree < (0.9 * worker.MAX_NODES_ON_TREE ) ) 
                                                    nCmpMode = CMP_MODE_MIN_OBJ;              
    }
    
    if( noNodes > 0 ) {
      // Limit EKN output
      long lNow = currentTimeMillis();
      if( (lNow - lastEKNPrint) >= EKN_SEC_MILLI ) {
        lastEKNPrint = lNow;        
        out.printf( "EKN: nodes=%,d, ot=%,d/%d r=%.2f%% cmp=%d bp=%f, bs=", 
                          noNodes, nodesOnTree, nMaxDepth, dRate, nCmpMode,                       
                          cbcModel.getBestPossibleObjValue() );    
        if( nBestZSoFar == Integer.MAX_VALUE ) out.print( "-" ); 
        else                                   out.print( nBestZSoFar );     
        out.printf( " mem=%,d t=", Runtime.getRuntime().freeMemory() );
        out.println( MathUtils.formatTime( lNow- worker.startZeit));
        out.flush();
      } 
      
      if( noNodes == 1_000 ) Cgl.printCglStatistics( cbcModel );      
      
      /*lNow = currentTimeMillis();
      if( (lNow - lastTuningPrint)>= GENERATOR_SEC_MILLI ) {
        lastTuningPrint = lNow;
        Cgl.printCglStatistics( cbcModel );
        Cgl.printTuningInfo   ( cbcModel );
      }*/
    }
    
    return nCmpMode;
  }
  
  /*--------------------------------------------------------------------------*/
  /**
   * Call back from CbcCompareWolf::newSolution()
   * @param cPtrCbcModel
   * @param noSolutions: # solutions found
   * @return true: change strategy 
   */
  public boolean newSolution( long cPtrCbcModel, int noSolutions ) {

    if( cbcModel.getCPtr() != cPtrCbcModel ) 
                              out.println( "Pointers different! Why?" );
    // Save solution:
    double[] adSolution = cbcModel.bestSolution( noColsInModel );
    nBestZSoFar = 0;
    worker.util.writePrint( "\nSolution found." );
    int noNzInSol = 0; final int VALUES_PER_LINE = 20;
    for( int j = 0; j < noColsInModel; j++ ) {
      int nVal = (int) Math.round( adSolution[ j ] ); 
      if( nVal  != 0 ) {
        noNzInSol++;
        nBestZSoFar += nVal;
        worker.util.writeFile( "x" + j + "=" + nVal + " ", false );
        if( (noNzInSol % VALUES_PER_LINE) == 0 ) worker.util.writeFile( " \\" );
      }           
    }
    if( (noNzInSol % VALUES_PER_LINE) != 0 ) worker.util.writeFile( "" );
    worker.util.writePrint( "NSO: Solutions found so far: " + noSolutions +  
                            ". Best z so far: " + nBestZSoFar );
    worker.util.writePrint( "-----------------------------------------------" );    
    
    return false;
  }
  
  /*--------------------------------------------------------------------------*/
  public void setNoColsInModel( int n ) { noColsInModel = n; }  
}
