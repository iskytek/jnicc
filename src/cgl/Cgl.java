/*
 * Cgl.java
 */

package cgl;

import static java.lang.System.out;

import cbc.CbcCutGenerator;
import cbc.CbcModel;

/**
 * Utility class for JNI-Coin Cgl Interface
 */
public class Cgl {

   public enum Cut { 
             ALL_DIFFERENT         , CLIQUE, 
             DUPLICATE_ROW         , FAKE_CLIQUE, 
             FLOW_COVER            , GMI, 
             GOMORY, IMPLICATION   , KNAPSACK_COVER, 
             LAND_P                , LIFT_AND_PROJECT, 
             MIXED_INTEGER_ROUNDING, MIXED_INTEGER_ROUNDING2, 
             ODD_HOLE              , PROBING,
             RED_SPLIT             , RED_SPLIT2,
             RESIDUAL_CAPACITY     , SIMPLE_ROUNDING,
             STORED                , TEMPORARY,
             TWOMIR                , ZERO_HALF
  } 
  
  private final int iHowOften;
  private final Cut cut;
  
  private int iLimit = 50;
  
  /*--------------------------------------------------------------------------*/  
  public Cgl( Cut cut, int iHowOften ) {
       
    this.iHowOften = iHowOften;
    this.cut       = cut;
  }

  /*--------------------------------------------------------------------------*/
  public int getHowOften() { return iHowOften; }
  
  public int getLimit()        { return iLimit; }
  public void setLimit( int n) { iLimit = n;    }
  
  public Cut getCut(){ return cut; }
  
  /*--------------------------------------------------------------------------*/
  public static void printCglStatistics( CbcModel cbcModel ) {
    out.println( "Cgl Statistics..." );
    
    int noGenerators = cbcModel.getNumberCutGenerators();
    if( noGenerators > 0 ) {
      out.println( noGenerators + " cut generators used:" );
      
      for( int i = 0; i < noGenerators; i++ ) {
        //out.print( "Cut generator " + i  + " wird erstellt..." );
        CbcCutGenerator curGenerator = 
                        new CbcCutGenerator( cbcModel.getCutGenerator( i ));
        //out.println( "  ...O.K." );
        curGenerator.refreshModel( cbcModel );
        out.println( "  > " + i + ". " + curGenerator.getCutGeneratorName()    +
                     " tried " + curGenerator.numberTimesEntered()             + 
                     " times and created "  + curGenerator.numberCutsInTotal() +
                     " cuts, " + curGenerator.numberCutsActive() + " active." ); 
        if( curGenerator.timing() ) 
          out.printf( "       Time in Generator: %5.2f sec.\n", 
                              curGenerator.timeInCutGenerator() );
      }      
    } else out.println( "No cut generators used." );
    out.flush();
  }
  
  /*--------------------------------------------------------------------------*/
  public static void printTuningInfo( CbcModel cbcModel ) {
    out.println( "\nTuning info..." );
    
    int noGenerators = cbcModel.getNumberCutGenerators();
    if( noGenerators > 0 ) {      
      for( int i = 0; i < noGenerators; i++ ) {
        CbcCutGenerator curGenerator = 
                        new CbcCutGenerator( cbcModel.getCutGenerator( i ));
        out.println( "  > " + i + ". " + curGenerator.getCutGeneratorName() );
        out.println( "   Tuning Info: " + curGenerator.generateTuning() );
      }      
    } else out.println( "No cut generators used." );
  }  
}
