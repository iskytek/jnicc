/*
 * Worker.java
 */

package jni_coin;

import static java.lang.System.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import cbc.CbcCompareWolf;
import cbc.CbcCutGenerator;
import cbc.CbcModel;
import cgl.*;

/**
 * Main worker class running in background thread.
 * Here is where all the interesting stuff happens.
 */
public class Worker implements Runnable, Globals { 
  
  final String VERSION = getClass().getName() + ", Rel. 1.2.4, 2019-02-10";

  final int    NO_THREADS;
  final int    CBC_LOG_LEVEL;
  public final int    CMP_NODES_DEPTH;
  public final int    MAX_NODES_ON_TREE;
  final double OBJ_SENSE_MIN = 1.0;
  final double OBJ_SENSE_MAX = -OBJ_SENSE_MIN;
  final double OBJ_SENSE; 
  
  final String PREFIX_COL    = "U";
  final String PREFIX_ROW    = "R";

  private OsiClpSolverInterface lpSolver       = null;
  private CoinModel             coinModel      = null;
  private CbcModel              cbcModel       = null;
  private CbcCompareWolf        cbcCompareWolf = null;
  
  public static native String jni_getVersion(); 
 
  private Main main           = null;
  private String sMpsFile     = null;
  private String sProblemName = "ProblemName";
  private ModGen modgen = null;
  
  private final ArrayList<Cgl> alCuts;
  
  public Util   util     = null;
  String sBestSol = null;
  
  int noColsInModel = 0;
  public long startZeit = 0l;
  
  /*--------------------------------------------------------------------------*/
  static { /* Moved to Main.loadLibraries() */ }

  /*--------------------------------------------------------------------------*/  
  /** Constructor for MPS file
   * 
   * @param main
   * @param fwSol
   * @param nOfThreads
   * @param dObjSense
   * @param nCbcLogLevel
   * @param sMpsFile
   */
  public Worker( Main main, FileWriter fwSol, int nOfThreads, double dObjSense, 
                 int nCbcLogLevel, String sMpsFile, int nCmpNodesDepth, 
                 int nMaxNodesOnTree, String sBestSol, ArrayList<Cgl> alCuts ) {    
    
    this( main, fwSol, dObjSense, nOfThreads, nCbcLogLevel, 0, 0, 0, 0, 0, 0, 0,
          0, nCmpNodesDepth, nMaxNodesOnTree, sBestSol, alCuts );    
    this.sMpsFile = sMpsFile;
  }
  
  /*--------------------------------------------------------------------------*/
  /** Constructor for internal model 
   */
  public Worker( Main main, FileWriter fwSol, int nOfThreads, double dObjSense, 
                 int nCbcLogLevel, int nq, int nn, int nr, int ns, int nRowType, 
                 int nBoundType, int nLbz, int nUbz, int nCmpNodesDepth, 
                 int nMaxNodesOnTree, String sBestSol, ArrayList<Cgl> alCuts ) {
    
    this( main, fwSol, dObjSense, nOfThreads, nCbcLogLevel, nq, nn, nr, ns, 
          nRowType, nBoundType, nLbz, nUbz, nCmpNodesDepth, nMaxNodesOnTree,
          sBestSol, alCuts );
    
    sProblemName = "q" + nq + "_n" + nn + "_R"+ nr +"_s" + ns;
  }
  
  /*--------------------------------------------------------------------------*/
  private Worker( Main main, FileWriter fwSol, double dObjSense, int nOfThreads, 
                 int nCbcLogLevel, int nq, int nn, int nr, int ns, int nRowType, 
                 int nBoundType, int nLbz, int nUbz, int nCmpNodesDepth, 
                 int nMaxNodesOnTree, String sBestSol, ArrayList<Cgl> alCuts ) {
    super();
    
    this.main         = main;
    this.sBestSol     = sBestSol;
    NO_THREADS        = nOfThreads;
    CBC_LOG_LEVEL     = nCbcLogLevel;
    OBJ_SENSE         = dObjSense; 
    CMP_NODES_DEPTH   = 1000 * nCmpNodesDepth;
    MAX_NODES_ON_TREE = 1000 * nMaxNodesOnTree;
    
    util   = new Util( fwSol );
    modgen = new ModGen( fwSol, nq, nn, nr, ns, nRowType, 
                         nBoundType, nLbz, nUbz ); 
    noColsInModel = modgen.getNoColumns();
    this.alCuts   = alCuts;
  }
    
  /*--------------------------------------------------------------------------*/  
  @Override
  public void run() {
        
    startZeit = currentTimeMillis();        
    printMemoryInfo();
    
    int rc = setUpCoin();
    if( rc == RC_SUCCESS && sMpsFile != null ) {
      noColsInModel = readMpsFile( sMpsFile );
      if( noColsInModel <= 0 ) rc = RC_FAIL;
    } else {
      
      rc = makeModel(); 
      
      if( rc == RC_SUCCESS && lpSolver != null ) {
        if( sProblemName == null ) sProblemName = "MyLpProblem";
        if( ! lpSolver.setModelName( sProblemName )) 
                       err.println( "Error in setModelName()." );
        else {
          String s = lpSolver.getModelName();
          if( s != null ) out.println( "ModelName set to " + s );
          else err.println( "Error in getModelName()." );
        }
      }
        
      if( rc == RC_SUCCESS && lpSolver != null ) {
        out.print( "Load from Coin Model..." );
        boolean bKeepSolution = true;
        int noErrors = lpSolver.loadFromCoinModel( coinModel, bKeepSolution );
        if( noErrors != 0 ) 
          err.println( "Load returned with " + noErrors + " Errors!" );
        else out.println( "  ...O.K." );
      }
      
      out.println( "Columns in lpSolver: " + lpSolver.getNumCols() +
                   ", Rows in lpSolver: "  + lpSolver.getNumRows() );
    }
    
    
    /******  From here on Mps file and internal model together ******/
    cbcCompareWolf.setNoColsInModel( noColsInModel );
    
    if( rc == RC_SUCCESS && cbcModel != null ) {
      //out.println( "Assigning lpSolver..." );
      cbcModel.assignSolver( lpSolver, false );      

      out.println( "Initial solve..." );
      cbcModel.initialSolve();      

      if( sBestSol != null ) {
        util.writePrint( "\nSetting best solution..." );
        cbcModel.setBestSolution( makeBestSol( noColsInModel ));
      }
      
      final int NUM_STRONG = noColsInModel;
      util.writePrint( "\nSetting number strong to " + NUM_STRONG + "..." );
      //cbcModel.setNumberStrong( CbcModel.DISABLE_STRONG_BRANCHING );
      cbcModel.setNumberStrong( NUM_STRONG );

      if( alCuts.size() > 0 ) {
        out.println( "Adding and configuring cut generators..." );      
        addCutGenerators();      
        configCutGenerators();
      } else out.println( "No cuts to set." );
      
      out.println( "Branch and bound..." );
      cbcModel.branchAndBound( CbcModel.STATISTICS_NODES );

      if( cbcModel.isProvenOptimal() ) {
        util.writePrint( "\nSolution is optimal. Objective value: " + 
                     cbcModel.getObjValue() ); 
        double[] adBestSolution = cbcModel.bestSolution( noColsInModel );
        if( adBestSolution != null ) {
          if( adBestSolution.length == noColsInModel ) {
            util.writeFile( "" );
            for( int i = 0; i < noColsInModel; i++ )
              if( adBestSolution[ i ] != 0.0 )
                util.writeFile( "x" + i + "=" + adBestSolution[ i ] );
          } 
          else {
            err.println( "Something is fishy. noCols != sol.length!" );
            rc = RC_FAIL;
          }         
        }
      } else util.writePrint( "Optimality NOT proven!!!" );      
    }
    
    if( rc == RC_SUCCESS ) {
      Cgl.printCglStatistics( cbcModel );
      Cgl.printTuningInfo   ( cbcModel );
      cleanUpCoin();
    }
      
    String msg = "\nRuntime: " + MathUtils.formatTime(
                 (currentTimeMillis() - startZeit));    
    out.println( msg );
    main.workerStop();
  }

  /*--------------------------------------------------------------------------*/
  /** nHowoften affects how generator is used. 
   *   0 or 1 means always
   *   .gt. 1 means every that number of nodes.
   *   Negative values have same meaning as positive but they may be switched 
   *   off (e.g -100) by code if not many cuts generated at continuous.
   *   -99 is just done at root. */
  void addCutGenerators() {
    
    for( Cgl cgl: alCuts ) {
      
      if( cgl.getCut() == Cgl.Cut.GOMORY ) {        
        var cglGomory = new CglGomory();        
        // Change limit on how many variables in cut (default 50)       
        out.println( "   > Add Gomory( HOW_OFTEN=" + cgl.getHowOften() + 
                                    ", LIMIT="     + cgl.getLimit() + " )..." );
        cglGomory.setLimit( cgl.getLimit() );
        cbcModel.addCutGenerator( cglGomory.getCPtr(), 
                                  cgl.getHowOften(), "Gomory" ); 
      }
      else if( cgl.getCut() == Cgl.Cut.PROBING ) {        
        
        var cglProbing = new CglProbing();
        
        //Set 0 don't 1 do -1 don't even think about it.
        final int USE_OBJ           =    1;
                                     // max elements to be considered
        final int MAX_ELEMENTS_ROOT = noColsInModel; //  10;  
        final int MAX_ELEMENTS      = noColsInModel;
                                     // max variables to look at in one probe 
        final int MAX_LOOK_ROOT     = noColsInModel; //  10;  
        final int MAX_LOOK          =    5;
        final int MAX_PASS          =    1; // max passes per node
        final int MAX_PASS_ROOT     = 1000; // max passes per root node 
        // 0: no cuts, 1: just disaggregation type, 2: coefficient, 3: both
        final int ROW_CUTS          =    3; //0;  
        cglProbing.setUsingObjective ( USE_OBJ );
        cglProbing.setMaxElements    ( MAX_ELEMENTS );
        cglProbing.setMaxElementsRoot( MAX_ELEMENTS_ROOT );
        cglProbing.setMaxLook        ( MAX_LOOK );
        cglProbing.setMaxLookRoot    ( MAX_LOOK_ROOT );
        cglProbing.setMaxPass        ( MAX_PASS );
        cglProbing.setMaxPassRoot    ( MAX_PASS_ROOT );
        cglProbing.setRowCuts        ( ROW_CUTS );
        out.println( "   > Add Probing("                              +
                           " HOW_OFTEN="          + cgl.getHowOften() + 
                           ", USE_OBJ="           + USE_OBJ           +
                           ", ROW_CUTS="          + ROW_CUTS          +
                           ", MAX_ELEMENTS="      + MAX_ELEMENTS      +
                           ", MAX_ELEMENTS_ROOT=" + MAX_ELEMENTS_ROOT +
                           ", MAX_LOOK="          + MAX_LOOK          +
                           ", MAX_LOOK_ROOT="     + MAX_LOOK_ROOT     +                           
                           ", MAX_PASS="          + MAX_PASS          +
                           ", MAX_PASS_ROOT="     + MAX_PASS_ROOT     +
                           " )..." );
        cbcModel.addCutGenerator( cglProbing.getCPtr(), 
                                  cgl.getHowOften(), "Probing" );        
      } else if( cgl.getCut() == Cgl.Cut.RED_SPLIT ) {
        
        var cglRedSplit = new CglRedSplit();
        
        // max number of non zero coefficients in generated cut (default 50)
        final int LIMIT = noColsInModel;       
        out.println( "   > Add RedSplit( HOW_OFTEN=" + cgl.getHowOften() + 
                                      ", LIMIT="     + LIMIT + " )..." );
        cglRedSplit.setLimit( LIMIT );
        cbcModel.addCutGenerator( cglRedSplit.getCPtr(), 
                                  cgl.getHowOften(), "RedSplit" ); 
      } else if( cgl.getCut() == Cgl.Cut.TWOMIR ) {        
        
        var cglTwomir = new CglTwomir();
        
        // 0 normal, 1 add original matrix one, 2 replace.        
        out.println( "   > Add Twomir(" +
                             " HOW_OFTEN=" + cgl.getHowOften() + 
                             ", TYPE="     + CglTwomir.TYPE_NORMAL + " )..." );
        cglTwomir.setTwomirType( CglTwomir.TYPE_NORMAL );
        cbcModel.addCutGenerator( cglTwomir.getCPtr(), 
                                  cgl.getHowOften(), "cglTwomir" ); 
      } else if( cgl.getCut() == Cgl.Cut.KNAPSACK_COVER ) {              
        var cglKnapsackCover = new CglKnapsackCover();               
        out.println( "   > Add Knapsack(" +
                             " HOW_OFTEN=" + cgl.getHowOften() + " )..." );
        cbcModel.addCutGenerator( cglKnapsackCover.getCPtr(), 
                                  cgl.getHowOften(), "cglKnapsackCover" ); 
      } else if( cgl.getCut() == Cgl.Cut.MIXED_INTEGER_ROUNDING ) {              
        var cglMxIntRnd = new CglMixedIntegerRounding();               
        out.println( "   > Add MixedIntegerRounding(" +
                             " HOW_OFTEN=" + cgl.getHowOften() + " )..." );
        cbcModel.addCutGenerator( cglMxIntRnd.getCPtr(), 
                                  cgl.getHowOften(),"cglMixedIntegerRounding" ); 
      } else if( cgl.getCut() == Cgl.Cut.MIXED_INTEGER_ROUNDING2 ) {              
        var cglMxIntRnd2 = new CglMixedIntegerRounding2();               
        out.println( "   > Add MixedIntegerRounding2(" +
                             " HOW_OFTEN=" + cgl.getHowOften() + " )..." );
        cbcModel.addCutGenerator( cglMxIntRnd2.getCPtr(), 
                                  cgl.getHowOften(),"cglMixedIntegerRounding2"); 
      }
    } 
  }
 
  /*--------------------------------------------------------------------------*/
  void configCutGenerators() {
    
    int noGenerators = cbcModel.getNumberCutGenerators();
    if( noGenerators > 0 ) {      
      for( int i = 0; i < noGenerators; i++ ) {
        CbcCutGenerator curGenerator = 
                        new CbcCutGenerator( cbcModel.getCutGenerator( i ));
        
        String sGeneratorName = curGenerator.getCutGeneratorName();         
        if( sGeneratorName.equals( "Gomory"   ) || 
            sGeneratorName.equals( "Twomir"   ) || 
            sGeneratorName.equals( "RedSplit" )) {        
          curGenerator.setNeedsOptimalBasis( true );
        }
        
        curGenerator.setSwitchOffIfLessThan( 0 );
        curGenerator.setWhatDepth( 5 );
        curGenerator.setInaccuracy( 0 );
        curGenerator.setNormal( true );
      }      
    } else out.println( "No generators to configure." );    
  }
  
  /*--------------------------------------------------------------------------*/
  /** If MPS file is missing, we create an internal model.
   * 
   * @return
   */
  int makeModel() {
   
    int rc = RC_SUCCESS;
    
    util.writePrint( "\nCreating internal model using " + modgen.getVersion() );
    util.writePrint( "Problem name is " + sProblemName );
    
    final int NO_COLS = modgen.getNoColumns();
    
    out.print( "\tCalculating coeffs (vol)..." );
    rc = modgen.calcVolumen();
    
    if( rc == RC_SUCCESS ) {        
      out.println ( "\t...O.K." );
      final double  UPPER_BOUND = (double) modgen.getUpperBoundCol();
      final double  LOWER_BOUND = 0.0;
      final double  COST        = 1.0;
      final boolean IS_INTEGER  = true;
      
      out.print( "\tGenerating " + NO_COLS + " cols..." );
      for( int col = 0; col < NO_COLS; col++ ) 
          coinModel.addColumn( LOWER_BOUND, UPPER_BOUND, COST, 
                                    PREFIX_COL + col, IS_INTEGER );
      noColsInModel = NO_COLS;
      out.println ( "\t\t...O.K." );
    }

    if( rc == RC_SUCCESS ) {   
      
      final double NZ = 1.0;
            
      var anRowIndex = new int   [ NO_COLS ];
      var adRowValue = new double[ NO_COLS ];
      
      // Ggf. Lower und upper bounds Z setzen:
      if( modgen.isSetLBZ() || modgen.isSetUBZ() ) {
        if( modgen.isSetLBZ() && modgen.isSetUBZ()  ) 
          out.print( "\tSetting LBZ=" + modgen.getLBZ() + 
                             ", UBZ=" + modgen.getUBZ() + "..." );      
        else if( modgen.isSetLBZ() )
          out.print( "\tSetting LBZ = " + modgen.getLBZ() + "...\t" );
        else out.print( "\tSetting UBZ = " + modgen.getUBZ() + "...\t" );
        
        for( int j = 0; j < NO_COLS; j++ ) {
          anRowIndex[ j ] =  j; adRowValue[ j ] = NZ;
        }  
        coinModel.addRow( NO_COLS, anRowIndex, adRowValue, 
               (double) modgen.getLBZ(), (double) modgen.getUBZ(), "Z_Bounds" ); 
        out.println ( "\t...O.K." );
      }
    
      final int NO_ROWS = NO_COLS;
      final double LHS  = modgen.getLhs(); // Left hand side
      final double RHS  = (double) modgen.getRhs();
      out.print( "\tGenerating " + NO_ROWS + " rows..." );
      for( int row = 0; row < NO_ROWS; row++ ) {
        int noNzInRow = 0;
        
        for( int col = 0; col < NO_COLS; col++ ) {
          long nz = modgen.getVRi( 0  );
          if( row != col ) // Hammingabstand ermitteln:
            nz = modgen.getVRi( modgen.getHamDist( col, row, 
                                               modgen.getN(), modgen.getQ() ) ); 
          if( nz != 0 ) {
            anRowIndex[ noNzInRow ] = col;
            adRowValue[ noNzInRow ] =  nz;
            noNzInRow++;
          }
        }
        coinModel.addRow( noNzInRow, anRowIndex, adRowValue, RHS, LHS, 
                          PREFIX_ROW + row );                
      }
      out.println ( "\t\t...O.K." );
    }
        
    return rc;
  }
  
  /*--------------------------------------------------------------------------*/
  /**
   * Convert String to double[] for best known solution
   * @param NO_COLS
   * @return
   */
  double[] makeBestSol( final int NO_COLS ) {
    
    // Set best solution:
    double[] adBestSol = new double[ NO_COLS ];
    StringTokenizer tokenizer = new StringTokenizer( sBestSol );
    if( tokenizer != null ) {
      while( tokenizer.hasMoreTokens() ) {
        String sToken = tokenizer.nextToken().trim();
        String s      = sToken.substring( 1, sToken.indexOf( "=" ) );
        int nIndex = Integer.parseInt( s );
        s = sToken.substring( sToken.indexOf( "=" ) + 1 );
        double dValue = Double.parseDouble( s );
        adBestSol[ nIndex ] = dValue;
      }
    }
    
    return adBestSol;
  }
  
  /*--------------------------------------------------------------------------*/
  /** 
   * Read MPS file and return number of Columns in model.
   * 
   * @param sMpsFileName
   * @return
   */
  int readMpsFile( String sMpsFileName ) {
    
    int noCols = 0;
    
    util.writeFile( NL + "MPS file name is " + sMpsFileName );   
        
    if( lpSolver != null ) {
      out.println( "Reading MpsFile " + sMpsFileName + "...");
      int noErrors = lpSolver.readMps( sMpsFileName );
      if( noErrors > 0 ) {
        err.println( noErrors + " Errors in "+ sMpsFileName );
        noCols = -noErrors;
      } else { 
        out.println( sMpsFileName + " O.K" );
        noCols = lpSolver.getNumCols();
        util.writePrint( "Columns in lpSolver: " + noCols  +
                         ", Rows in lpSolver: "  + lpSolver.getNumRows() );
      }
    }

    return noCols;
  }
  
  /*--------------------------------------------------------------------------*/
  private int cleanUpCoin() {
    
    int rc = RC_SUCCESS;
  
    out.println( "Cleaning up..." );
    
    /*if( rc == RC_SUCCESS  && lpSolver != null) {
      lpSolver.delete();
    }*/
    
    if( rc == RC_SUCCESS && coinModel != null ) {
      coinModel.delete();
    }

    /*if( rc == RC_SUCCESS && cbcModel != null ) {
      cbcModel.delete();
    }*/

    return rc;
  }
    
  /*--------------------------------------------------------------------------*/
  private int setUpCoin() {
    
    int rc = RC_SUCCESS;

    out.println( "\nSetting up Coin..." );
     
    coinModel = new CoinModel(); 
    long cPtr = coinModel.getCPtr(); 
    if(  cPtr == 0L ) { 
      err.println( "Error creating CoinModel!" );
      rc = RC_FAIL;      
    }

    if( rc == RC_SUCCESS ) {     
      lpSolver = new OsiClpSolverInterface();
      cPtr = lpSolver.getCPtr(); 
      if( cPtr == 0L ) { 
        err.println( "Error creating OsiClpSolverInterface!" );
        rc = RC_FAIL;      
      }
    }    

    if( rc == RC_SUCCESS ) {
      cbcModel = new CbcModel();
      cPtr = cbcModel.getCPtr(); 
      if( cPtr == 0L ) { 
        err.println( "Error creating CbcModel!" );
        rc = RC_FAIL;      
      } 
    }  
    
    if( rc == RC_SUCCESS && cbcModel != null ) {
      out.println( "Setting Cbc log level to " + CBC_LOG_LEVEL + "..." );
      cbcModel.setLogLevel( CBC_LOG_LEVEL );
    }
            
    if( rc == RC_SUCCESS && cbcModel != null ) {
      cbcModel.setNumberThreads( NO_THREADS );
      out.println( "Number of threads set to " + cbcModel.getNumberThreads() );
    }
    
    if( rc == RC_SUCCESS && lpSolver != null ) {
      lpSolver.setObjSense( OBJ_SENSE );
      String s = "MINimize";
      if( OBJ_SENSE == OBJ_SENSE_MAX ) s = "MAXimize!";
      out.println( "Direction of optimization is " + s );
    }

    if( rc == RC_SUCCESS ) {
      out.print( "Setting Node Comparison..." );
      cbcCompareWolf = new CbcCompareWolf( this, cbcModel );
      if( cbcCompareWolf != null && cbcCompareWolf.getCPtr() != 0 ) 
        out.println( "\t...O.K" );
      else err.println( "cbcCompareWolf.getCPtr() is 0!" );
    }
    
    return rc;
  }
  
  /*--------------------------------------------------------------------------*/
  private void printMemoryInfo() {
      
    out.println( "Runtime Version: " + Runtime.version() );
    out.println( "Memory info: " );
    out.printf( "Total: %,d, Max: %,d, Free: %,d\n", 
                 Runtime.getRuntime().totalMemory(),
                 Runtime.getRuntime().maxMemory()  ,
                 Runtime.getRuntime().freeMemory() );
  }
  
  /*--------------------------------------------------------------------------*/
  @Override
  public String getVersion() { return VERSION; }  
}
