/*
 * Main.java (jnicc)
 */

package jni_coin;

import static java.lang.System.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import cgl.Cgl;

/**
 * jnicc main class 
 */
public class Main implements Globals {

  final String VERSION = getClass().getName() + ", Rel. 1.1.7, 2019-02-10";
  
  static final String FN_PROPERTIES     = "jnicc.ini";
  static final String FILE_EXTENSION    = ".sol";
  static final String OLDFILE_EXTENSION = ".old";

  static final int MAX_S = 23;
  
  private final ArrayList<String> alCoinLibs = new ArrayList<String>();
  private final ArrayList<Cgl>    alCuts     = new ArrayList<Cgl>();
  
  private FileWriter     fwSol    = null;
  private Properties     props    = null;
    
  private String sSolDir  = ".";
  private String sMpsFile = null;
  private String sBestSol = null; // best known solution so far 

  private boolean bContinue    = true;
  private int     nOfThreads   = 1;
  private int     nCbcLogLevel = 1;
  private double  dObjSense  = OsiClpSolverInterface.OBJ_SENSE_MIN;
  
  private int nq = 2, nn = 10, ns = 3, nr = 1, nRowType = 1, nBoundType = 1, 
              nLbz = -Integer.MAX_VALUE, nUbz = Integer.MAX_VALUE, 
              nCmpNodesDepth = 10, nMaxNodesOnTree = 100;

  private Worker worker = null;
  private Thread thr    = null;

  /*--------------------------------------------------------------------------*/  
  public static void main( String[] args ) {
    try{ new Main().go(); } catch( Exception ex ) { ex.printStackTrace(); }
  }

  /*--------------------------------------------------------------------------*/  
  public Main() {
    
    // Get process-ID:
    long pid = ManagementFactory.getRuntimeMXBean().getPid();       
    out.println( "Starting " + getVersion() + " with PID " + pid 
                                            + " at " + new Date() + "..." );    
    out.println( "Library Path is: " + 
                  ManagementFactory.getRuntimeMXBean().getLibraryPath() );
    out.println( "Classpath is   : " + 
                  ManagementFactory.getRuntimeMXBean().getClassPath() );

    Runtime.getRuntime().addShutdownHook( new Thread() {
      @Override
      public void run() {
        String msg = null;
        if( bContinue ) 
             msg = "Shutting down forcefully at " + new Date() + "...";
        else msg = "Shutting down normally at "   + new Date() + "...";
        
        out.println( msg );
        try{ if( fwSol != null ) {
          fwSol.write( "\n\n***  END LOG FILE. " + new Date() + "  ***\n" );
          fwSol.close();
        }} catch( IOException ex ) { ex.printStackTrace(); }      

        out.println( "Good-bye." );
      }}); 

    out.println( "Shutdown hook attached." );
  }

  /*--------------------------------------------------------------------------*/  
  private void go() {
    
    String sFilename = sSolDir + File.separator;
    
    int rc = getProps(); // ini file lesen
    if( rc == RC_SUCCESS ) rc = loadLibraries();
        
    if( rc == RC_SUCCESS ) {
      // Make filename
      if( sMpsFile != null ) {
        int pos = sMpsFile.lastIndexOf( File.separator );
        if( pos >= 0 ) sFilename += (sMpsFile.substring( pos + 1 ));
        else sFilename += sMpsFile;
      } else {
        if( (nq < 2) || (nq > 21) ) nq = 2;
        if( (nn < 1) || (nn > 33) ) nn = 3;
        if( (nr < 1) || (nr > nn) ) nr = 1;
        if( ns < 0 || ns > nn || ns > MAX_S ) ns = 1;
        if( nLbz > nUbz ) { int nTmp = nLbz; nLbz = nUbz; nUbz = nTmp; }
        sFilename += ("q" + nq + "_n" + nn + "_R" + nr +"_s" + ns);
      }  
      sFilename += FILE_EXTENSION;
      if( nCmpNodesDepth  <= 0 ) nCmpNodesDepth  =  10;
      if( nMaxNodesOnTree <= 0 ) nMaxNodesOnTree = 100;
      
      rc = openLogFile( sFilename, sFilename + OLDFILE_EXTENSION );      
      if( rc == RC_SUCCESS ) out.println( "Look for results in " + sFilename );      
    }
    
    if( rc == RC_SUCCESS ) { 
      if( nOfThreads <= 0 ) nOfThreads = 1;
      if( worker == null ) {
        if( sMpsFile != null ) worker = new Worker( this, 
            fwSol, nOfThreads, dObjSense, nCbcLogLevel, sMpsFile, 
            nCmpNodesDepth, nMaxNodesOnTree, sBestSol, alCuts );
        else                   worker = new Worker( this, 
            fwSol, nOfThreads, dObjSense, nCbcLogLevel, nq, nn, nr, ns, 
            nRowType, nBoundType, nLbz, nUbz, nCmpNodesDepth, nMaxNodesOnTree, 
            sBestSol, alCuts ); 
      }
      
      if( (thr == null) || thr.isInterrupted() || 
         (thr.getState() == Thread.State.TERMINATED) ) {
       
        // Neue Thread anlegen...
        thr = new Thread( worker );
             
        thr.start();
      } 
      else System.out.println( "Thread läuft bereits." );
    }
  }

  /*--------------------------------------------------------------------------*/
  
  /**
   * Open Log file. If file already exists, rename
   * @param sFilename    Log file name
   * @param sFilenameOld Old file name
   */
  int openLogFile( String sFilename, String sFilenameOld ) {

    int rc = RC_SUCCESS;
    
    File fileOld = new File( sFilenameOld );
    File file    = new File( sFilename );

    try {
      
      // make directory if not already existing:
      File directory = new File( sSolDir );
      if (! directory.exists() ) directory.mkdirs();
        
      // File umbenennen:
      if( file.exists() ) {
        Path pathOld = fileOld.toPath();
        Path path    = file.toPath();
        Files.copy( path, pathOld, StandardCopyOption.REPLACE_EXISTING );
      }
      
      // Create file and add some comments:                                                                                   
      fwSol = new FileWriter( file );  
      fwSol.write( "Starting " + getVersion() + " at " +
                                 new Date() + "...\n" );
      fwSol.flush();     
    } catch( IOException ex ) { rc = RC_FAIL; ex.printStackTrace(); }
    
    return rc;
  }

  /*--------------------------------------------------------------------------*/  
  /**
   * Load ini file
   */
  int getProps() {
    
    String s = null;
    props = new Properties();
    int rc = RC_SUCCESS;
    
    try {
      // Ini-File laden:
      props.load( new FileInputStream( FN_PROPERTIES ));
 
      s = props.getProperty( "LogDir" );
      if( s != null ) sSolDir = s.trim();
      else out.println( "Log directory not found in " + FN_PROPERTIES + 
                        ". Using current directory." );  

      s = props.getProperty( "MpsFile" );
      if( s != null ) sMpsFile = s.trim();
      else out.println( "Mps File not found in " + FN_PROPERTIES + 
                        ". Creating internal model." );  

      s = props.getProperty( "CbcLogLevel" );
      if( s != null ) nCbcLogLevel = Integer.parseInt( s.trim() );
      else out.println( "CbcLogLevel not found in " + FN_PROPERTIES +  
                        ". Using default " + nCbcLogLevel ); 
      
      s = props.getProperty( "Threads" );
      if( s != null ) nOfThreads = Integer.parseInt( s.trim() );
      else out.println( "Threads not found in " + FN_PROPERTIES +  
                        ". Using default 1." ); 
      
      s = props.getProperty( "MinOrMax" );
      if( s != null ) {
        s = s.trim().toUpperCase();
        if( s.equals( "MAX" )) dObjSense = OsiClpSolverInterface.OBJ_SENSE_MAX;
      } else out.println( "MinOrMax not found. Using MIN." );
      
      /********  Parameter for internal modell  ********/
      s = (props.getProperty( "Q" )).trim();
      if( s != null ) nq = Integer.parseInt( s.trim() );
      else out.println( "Q not found in " + FN_PROPERTIES + "!" );      

      s = props.getProperty( "R" );
      if( s != null ) nr = Integer.parseInt( s.trim() );
      else out.println( "R not found in " + FN_PROPERTIES + "!" );      

      s = props.getProperty( "N" );
      if( s != null ) nn = Integer.parseInt( s.trim() );
      else out.println( "N not found in " + FN_PROPERTIES + "!" );      

      s = props.getProperty( "S" );
      if( s != null ) ns = Integer.parseInt( s.trim() );
      else out.println( "S not found in " + FN_PROPERTIES + "!" ); 

      s = props.getProperty( "RTyp" );
      if( s != null ) nRowType = Integer.parseInt( s.trim() );
      else out.println( "RTyp not found in " + FN_PROPERTIES + "!" ); 

      s = props.getProperty( "BTyp" );
      if( s != null ) nBoundType = Integer.parseInt( s.trim() );
      else out.println( "BTyp not found in " + FN_PROPERTIES + "!" ); 

      s = props.getProperty( "LBZ" );
      if( s != null ) nLbz = Integer.parseInt( s.trim() );
      else out.println( "LBZ not found in " + FN_PROPERTIES + 
                        ". Setting LBZ = -INF" ); 

      s = props.getProperty( "UBZ" );
      if( s != null ) nUbz = Integer.parseInt( s.trim() );
      else out.println( "UBZ not found in " + FN_PROPERTIES +  
                        ". Setting UBZ = +INF" ); 

      s = props.getProperty( "SOL" );
      if( s != null ) sBestSol = s.trim();
      else out.println( "SOL not found in " + FN_PROPERTIES +  
                        ". Using Default." );       

      /********  Parameter for node selection strategy  ********/
      s = props.getProperty( "CmpNodesDepth" );
      if( s != null ) nCmpNodesDepth = Integer.parseInt( s.trim() );
      else out.println( "CmpNodesDepth not found in " + FN_PROPERTIES +  
                        ". Setting CmpNodesDepth = " + nCmpNodesDepth ); 
      
      s = props.getProperty( "MaxNodesOnTree" );
      if( s != null ) nMaxNodesOnTree = Integer.parseInt( s.trim() );
      else out.println( "MaxNodesOnTree not found in " + FN_PROPERTIES +  
                        ". Setting MaxNodesOnTree = " + nMaxNodesOnTree ); 
      
      /********  Parameter for Coin Library(/ies) ********/
      s = props.getProperty( "CoinLibs" );
      if( s != null ) {
        s = s.trim();
        StringTokenizer tokenizer = new StringTokenizer( s );
        if( tokenizer != null ) { while( tokenizer.hasMoreTokens() ) 
                                        alCoinLibs.add( tokenizer.nextToken() );          
        }
      } else out.println( "CoinLibs not found in " + FN_PROPERTIES ); 
      
      /*************  Parameter for Cuts *************/
      s = props.getProperty( "CutGomory" );
      if( s != null ) {
        s = s.trim(); String[] as = s.split( " " );
        if( as.length > 1 && as[ 0 ]!= "" ) { 
          Cgl cgl = new Cgl( Cgl.Cut.GOMORY, Integer.parseInt( as[ 0 ] )) ;
          alCuts.add( cgl );
          if( as.length > 2 && as[ 1 ]!= "" ) 
                                cgl.setLimit ( Integer.parseInt(as[ 1 ] ));
        }
      } 

      s = props.getProperty( "CutProbing" );
      if( s != null ) {
        s = s.trim();
        if( s!= "" ) alCuts.add( new Cgl(Cgl.Cut.PROBING, Integer.parseInt(s)));
      } 

      s = props.getProperty( "CutRedSplit" );
      if( s != null ) {
        s = s.trim();
        if( s!= "" ) alCuts.add(new Cgl(Cgl.Cut.RED_SPLIT,Integer.parseInt(s)));
      } 

      s = props.getProperty( "CutTwomir" );
      if( s != null ) {
        s = s.trim();
        if( s!= "" ) alCuts.add( new Cgl( Cgl.Cut.TWOMIR, Integer.parseInt(s)));
      } 

      s = props.getProperty( "CutKnapsack" );
      if( s != null ) {
        s = s.trim();
        if( s!= "" ) alCuts.add( new Cgl(Cgl.Cut.KNAPSACK_COVER, 
                                                Integer.parseInt(s)) );
      } 

      s = props.getProperty( "CutMixIntRound" );
      if( s != null ) {
        s = s.trim();
        if( s!= "" ) alCuts.add( new Cgl(Cgl.Cut.MIXED_INTEGER_ROUNDING, 
                                                Integer.parseInt(s)) );
      } 

      s = props.getProperty( "CutMixIntRound2" );
      if( s != null ) {
        s = s.trim();
        if( s!= "" ) alCuts.add( new Cgl(Cgl.Cut.MIXED_INTEGER_ROUNDING2, 
                                                Integer.parseInt(s)) );
      } 

      if( alCuts.size() == 0 ) 
                            out.println( "Not Cuts found in " + FN_PROPERTIES );
      
    } catch( IOException e ) {
      String sErrMsg = "IOException reading ini-File " + 
                              FN_PROPERTIES + ". " + e.getMessage();
      err.println( sErrMsg );
      rc = RC_FAIL;
    }       
    
    return rc;
  }

  /*--------------------------------------------------------------------------*/
  /* i.e.: ln -s <path-to-lib>/libWrapCoin.so /usr/lib/jni
   */
  private int loadLibraries() {
    
    int rc = RC_SUCCESS;
    
    out.println( "\nLoading libraries..." );
    
    try { 
      for( String sLib: alCoinLibs ) {
        out.print( "   " + sLib + "..." );        
        loadLibrary( sLib ); out.println( "\t...success" );   
      }
      out.println( "-------------------------------\n" );
    } catch( UnsatisfiedLinkError ex ) {      
      err.println( "Mist! Did not load library." );
      err.println( ex.getMessage() );
      err.println( ex.getStackTrace() );
      rc = RC_FAIL;
      ex.printStackTrace();    
    }
  
    return rc;
  }
  
  /*--------------------------------------------------------------------------*/  
  @Override
  public String getVersion() { return VERSION; }
  
  /*--------------------------------------------------------------------------*/  
  public void workerStop() {
    out.println( "Main.workerStop()..." );
    bContinue = false;
  }
}
