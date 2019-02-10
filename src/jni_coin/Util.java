/*
 * Util.Java
 */

package jni_coin;

import static java.lang.System.out;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Print utilities for jnicc 
 */
public class Util implements Globals {

  private FileWriter  fwSol   = null;
  
  /*--------------------------------------------------------------------------*/
  public Util() {;}
  
  /*--------------------------------------------------------------------------*/
  public Util( FileWriter fwSol ) {
    
    this.fwSol = fwSol;
  }
  
  /*--------------------------------------------------------------------------*/
  public FileWriter getFw() { return fwSol; }
  
  /*--------------------------------------------------------------------------*/
  public void writePrint( String msg ) {
    writeFile( msg ); out.println( msg );
  }
  public void writeFile( String msg ) { writeFile( msg, true ); }
  public void writeFile( String msg, boolean bAddNewline ) {
  
    try{   
      if( fwSol != null ) {
        fwSol.write( msg ); if( bAddNewline ) fwSol.write( NL );
        fwSol.flush();
      }     
    } catch( IOException ex ) { ex.printStackTrace(); }
  }

  
  /*--------------------------------------------------------------------------*/
  @Override
  public String getVersion() { return VERSION; } 
}
