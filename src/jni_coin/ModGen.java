/*
 * ModGen.Java
 */

package jni_coin;

import static java.lang.System.err;

import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Model generator class for jnicc. Used if no MPS file specified
 */
public class ModGen implements Globals {

  final String VERSION = getClass().getName() + ", Rel. 1.0.5, 2019-02-10";
  
  static final char[] ZIFFERN = 
    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
      'U', 'V', 'W', 'X', 'Y', 'Z' 
    };
  
  static final int MIN_BASIS = 2;
  static final int MAX_BASIS = ZIFFERN.length;
  
  final int Q;
  final int N;
  final int R;
  final int S;
  final int ROW_TYPE;
  final int ROW_TYPE_E = 0;
  final int ROW_TYPE_G = 1;
  final int BOUND_TYPE;
  final int BOUND_TYPE_RH = 0; // RHS (q**(n-s)) 
  final int BOUND_TYPE_1  = 1; // 1 
  final int BOUND_TYPE_PL = 2; // 0 <= Uj < +INF)
  final int BOUND_TYPE_BV = 3; // variable is binary (equal 0 or 1)
  final int DEFAULT_COST_COEFF  = 1;
  final int LBZ_DEFAULT         = -Integer.MAX_VALUE;
  final int UBZ_DEFAULT         =  Integer.MAX_VALUE;
  final int LBZ;
  final int UBZ;

  final String SOLUTION_COMMENT_RHS = "\nRHS=q**(n-s)=";
  
  final long[] anVRi;
  long lVR = 0, lKugelschranke = 0, lRhs = 0, lUpperBound = Long.MAX_VALUE;
  
  private Util util = null;
  
  /*--------------------------------------------------------------------------*/
  public ModGen( FileWriter fwSol, int nq, int nn, int nr, int ns, int nRowType, 
                 int nBoundType, int nLbz, int nUbz ) {     
    super();
    
    this.Q = nq; this.N = nn; this.R = nr; this.S = ns;
    this.ROW_TYPE   = nRowType;
    this.BOUND_TYPE = nBoundType;
    this.LBZ = nLbz;
    this.UBZ = nUbz;
    
    anVRi = new long[ S + 1 ];
    util  = new Util( fwSol ); 
  }
  
  /*--------------------------------------------------------------------------*/
  public int    getNoColumns()     { return (int) Math.pow( Q, S ); }
  public int    getN()             { return N;                      }
  public int    getQ()             { return Q;                      }
  public long   getRhs()           { return lRhs;                   }
  public long   getUpperBoundCol() { return lUpperBound;            }
  public double getLhs() {
    double dLhs = Double.MAX_VALUE;
    if( ROW_TYPE == ROW_TYPE_E ) dLhs = (double) lRhs;
    return dLhs;
  }
  public boolean isSetLBZ() { return ( LBZ != LBZ_DEFAULT ? true : false ); }
  public boolean isSetUBZ() { return ( UBZ != UBZ_DEFAULT ? true : false ); }
  public int     getLBZ()   { return LBZ; }
  public int     getUBZ()   { return UBZ; }
  public long    getVRi( int index )   { return anVRi[ index ]; }
  
  /*--------------------------------------------------------------------------*/  
  /**
   * Calc some values for covering radius 
   * @return RC_SUCCESS oder RC_FAIL
   */
  public int calcVolumen() {
           
    int rc = RC_SUCCESS;    
  
    try {
      lVR = 0;            
      for( int k = 0; k <= R; k++ )
         lVR = lVR + (long) ( MathUtils.nUeberk( N, k ) * 
                             (long) Math.pow( Q - 1, k ) ); 
                           
      lKugelschranke = (long) 
                       Math.ceil( Math.pow( Q, N ) / (double) lVR );

      util.writeFile( "VR=" + lVR + 
            "  Sphere-covering bound: ceil(q**n/VR)=" + lKugelschranke );
      
      for( int i = 0; i <= S; i++ ) {
        anVRi[ i ] = 0;
        
        for( int k = 0; k <= (R - i); k++ ) anVRi[ i ] += ((long) 
                 (MathUtils.nUeberk( N - S, k ) * (long) Math.pow( Q - 1, k )));        
      }
      
      for( int i = 0; i <= S; i++ ) 
        util.writeFile( "VR" + i + "=" + anVRi[ i ] + " ", false ); 
      
      lRhs = (long) Math.pow( Q, N - S );
      util.writeFile( SOLUTION_COMMENT_RHS + lRhs, false );
            
      if     ( BOUND_TYPE == BOUND_TYPE_RH ) {
        lUpperBound = lRhs;
        util.writeFile(  "\nUpper bound Uj is RHS=" + lUpperBound, false );
      }
      else if( BOUND_TYPE == BOUND_TYPE_1  ) {
        lUpperBound = 1;
        util.writeFile(  "\nUpper bound is " + lUpperBound, false );
      }
      else if( BOUND_TYPE == BOUND_TYPE_BV  ) {
        lUpperBound = 1;
        util.writeFile(  "\nUj is of type boolean.", false );
      }
      else {
        lUpperBound = Long.MAX_VALUE;
        util.writeFile(  "\nUpper bound Uj is +INF (Type PL)", false );
      }   
      
      if( LBZ != LBZ_DEFAULT ) 
        util.writeFile( "\nLower bound z is " + LBZ, false );
      if( UBZ != UBZ_DEFAULT ) 
        util.writeFile( "\nUpper bound z is " + UBZ, false );
    }
    catch( ArithmeticException ex ) {
      err.println( "calcVolumen(): Error calculation volume!" );
      ex.printStackTrace();
      rc = RC_FAIL;
    }
    
    return rc;
  }

  /*--------------------------------------------------------------------------*/  
  /**
   * Get Hamming-Distance between X and Y.
   * 
   * @param X Codeword 1
   * @param Y Codeword 2
   * @param N Length 
   * @param Q number of sybmols
   * @return Distance or -1
   */
  public int getHamDist( final int X, final int Y, final int N, final int Q ) {
    
    int nDist = 0;

    if( X != Y ) {
      if( (Q < MIN_BASIS) || (Q > MAX_BASIS) ) {
        err.println( "Error in getHamDist(). Wrong Q: " + Q );
        nDist = -1;
      }
      else if( Q == 2 ) nDist = getHamDistQ2( X, Y, N );
      else {
        ArrayList<Character> alX = new ArrayList<Character>(); 
        ArrayList<Character> alY = new ArrayList<Character>();
        
        if( X != 0 ) {          
          int n = X; // Change base for X:       
          while( n > 0 ) { alX.add( 0, ZIFFERN[ n % Q] ); n /= Q; }
        }
        
        if( Y != 0 ) {
          int n = Y; // Change base for Y:       
          while( n > 0 ) { alY.add( 0, ZIFFERN[ n % Q] ); n /= Q; }
        }
        
        final int DIFF;
        final int MAX;
        if( X > Y ) {
          MAX = alX.size();
          DIFF = MAX - alY.size();
          for( int i = 0; i < DIFF; i++ ) if( alX.get( i ) != '0' ) nDist++;          
          for( int i = DIFF; i < MAX; i++ ) 
               if( alX.get( i ) != alY.get( i - DIFF) ) nDist++;
        } else {
          MAX = alY.size();
          DIFF = MAX - alX.size();
          for( int i = 0; i < DIFF; i++ ) if( alY.get( i ) != '0' ) nDist++;
          for( int i = DIFF; i < MAX; i++ ) 
               if( alX.get( i - DIFF ) != alY.get( i ) ) nDist++;
        }       
      }
    }
    
    return nDist; 
  }

  /*--------------------------------------------------------------------------*/  
  /**
   * Get Hamming-Distance between X and Y. Attention: Only good for Q=2
   * 
   * @param X
   * @param Y
   * @param N
   * @return Distance
   */
  public int getHamDistQ2( final long X, final long Y, final int N ) {
    
    final long ONE = 1; 
    int nDist = 0;
    
    if( X != Y ) {
      // Alternative: XOR and count bits; cf. HamDist (Constructor).
      for( int bit = 0; bit < N; bit++ ) {            
        if( (X & (ONE << bit)) != (Y & (ONE << bit)) ) nDist++;
      }   
    }
    
    return nDist; 
  }

  /*--------------------------------------------------------------------------*/
  @Override
  public String getVersion() { return VERSION; } 
}
