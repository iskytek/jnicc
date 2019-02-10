/*
 * MathUtils.java
 */

package jni_coin;

import java.math.BigInteger;

/**
 * Some static utilities
 */
public class MathUtils implements Globals {
	
	public static final String VERSION = "MathUtils - Rel. 1.1.4, 2019-02-10";
  
  public static final int MILLIS_PRO_SEKUNDE = 1_000; 
  public static final int MILLIS_PRO_MINUTE  =    60 * MILLIS_PRO_SEKUNDE; 
  public static final int MILLIS_PRO_STUNDE  =    60 * MILLIS_PRO_MINUTE; 
  public static final int MILLIS_PRO_TAG     =    24 * MILLIS_PRO_STUNDE; 
  
  /*--------------------------------------------------------------------------*/ 
  /**
   * Format BigInteger.
   * 
   * @param big number
   * @return formatted number
   */
  static public String bigFormat( BigInteger big ){
        
    StringBuilder sb = new StringBuilder();    
    if( big.signum() < 0 ) sb.append( "-" );
    
    String sBig = big.abs().toString();
    
    if( sBig.length() <= 3 ) sb.append( sBig );
    else {  
      int pos = sBig.length() % 3;
      if( pos == 0 ) pos = 3; ;
      sb.append( sBig.substring( 0, pos ) );
      
      final int MAX_INDEX = sBig.length() - 1;
      for( ; pos < MAX_INDEX; pos += 3 ) 
                               sb.append( "." + sBig.substring( pos, pos + 3) );
    }
  
    return sb.toString();
  }
  
  /*--------------------------------------------------------------------------*/  
  /**
   * BigInteger!
   * 
   * @param n number
   * @return n! 
   */
  static public BigInteger faculty( int n ){
    
    BigInteger ergebnis = BigInteger.valueOf( 1 );        
    
    for( int i = 2; i <= n; i++ ) 
      ergebnis = ergebnis.multiply( BigInteger.valueOf( i ) );        
     
    return ergebnis;
  }
	
  /*--------------------------------------------------------------------------*/  
  /**
   * Format time 
   * 
   * @param time in milli-sec
   * @return time as string  
   */
  static public String formatTime( long time ){

    StringBuilder sb = new StringBuilder();
    if( time < 0 ) { sb.append( "-" ); time *= (-1); }
    
    if( time < MILLIS_PRO_SEKUNDE ) 
      sb.append( time + " ms" );
    else if( time < MILLIS_PRO_MINUTE  ) 
      sb.append( (time / MILLIS_PRO_SEKUNDE) + " sec" );
    else if( time < MILLIS_PRO_STUNDE ) {
      sb.append( (String.format( "%02d:%02d (mm:ss)", 
                              time / MILLIS_PRO_MINUTE, 
                              time % MILLIS_PRO_MINUTE / MILLIS_PRO_SEKUNDE )));
    }
    else if( time < MILLIS_PRO_TAG ) {
      sb.append( (String.format( "%02d:%02d:%02d (hh:mm:ss)", 
                              time / MILLIS_PRO_STUNDE,
                              time % MILLIS_PRO_STUNDE / MILLIS_PRO_MINUTE,
                              time % MILLIS_PRO_MINUTE / MILLIS_PRO_SEKUNDE )));
    }
    else{ // mehrere Tage
      sb.append( (String.format( "%d:%02d:%02d:%02d (ddd:hh:mm:ss)", 
                              time / MILLIS_PRO_TAG,
                              time % MILLIS_PRO_TAG / MILLIS_PRO_STUNDE,
                              time % MILLIS_PRO_STUNDE / MILLIS_PRO_MINUTE,
                              time % MILLIS_PRO_MINUTE / MILLIS_PRO_SEKUNDE )));
    }    
    
    return sb.toString();
  }

	/*--------------------------------------------------------------------------*/	
  /**
   * n over k (BigInteger).
   * Both factors &gt; 0
   *     
   * @param n &gt;= 0
   * @param k &gt;= 0
   * @return n ueber k
   * @throws ArithmeticException Error in bignUeberk
   */
  static public BigInteger bignUeberk( int n, int k ) 
  throws ArithmeticException{
    BigInteger bigErg = new BigInteger( n + "" );
    
    if( (n >= 0) && (k >= 0) ){     
       if( k != 1 ){
         if( n < k ) bigErg = new BigInteger( 0 + "" );
         else if( (n == k) || (k == 0) ) bigErg = new BigInteger( 1 + "" );
         else{      
            for( int i = 2; i <= k; i ++ ){
               n--; bigErg = bigErg.multiply( BigInteger.valueOf( n ) );
               bigErg = bigErg.divide( BigInteger.valueOf( i ) );
            }
         }
       }
    }
    else {
       System.err.println( "Error in bignUeberk. n = " + n + 
                           ", k = " + k + ", erg = " + bigErg.toString() );
       throw( new ArithmeticException() ); // nicht definiert
    }
    
    return bigErg;
  }
  
  /*--------------------------------------------------------------------------*/  
  /**
	 * n over k
	 * Both factors &gt; 0
   * 
   * @param n &gt;= 0
   * @param k &gt;= 0
   * @return n ueber k
   * @throws ArithmeticException Overflow
	 */
	static public long nUeberk( int n, int k ) throws ArithmeticException {
		 long erg = n;
		 
		 if( (n >= 0) && (k >= 0) ){     
				if( k != 1 ){
					if( n < k ) erg = 0;
					else if( (n == k) || (k == 0) ) erg = 1;
					else{			 
						 for( int i = 2; i <= k; i ++ ){
              n--; erg *= n;
              // check overflow :
              if( erg <= 0 ) throw( 
                              new ArithmeticException( "Overflow in nUeberk") );
							erg /= i;
            }
					}
				}
		 }
		 else {
				System.err.println( "Fehler in nUeberk. n = " + n + 
														", k = " + k + ", erg = " + erg );
				throw( new ArithmeticException() ); // nicht definiert
		 }
		 
		 return erg;
	}
	
	/*--------------------------------------------------------------------------*/	
	public String getVersion(){ return VERSION; }  
}
