/*
 * Globals.java
 */

package jni_coin;

/**
 * Interface defining some constants.
 */
public interface Globals {
  
  public static final char BLANK_CHAR  = ' ';

  public static final int RC_SUCCESS             =   0;
  public static final int RC_FAIL                =  -1;
  public static final int RC_NOT_FOUND           = 100;
  public static final int RC_NOT_YET_IMPLEMENTED = 200;

  public static final String BLANK    = " ";
  public static final String CRNL     = "\r\n";
  public static final String NL       = "\n";
  public static final String TAB      = "\t";
  public static final String VERSION  = "Globals. Rel. 1.1.0, 2019-02-10";

  public String getVersion();
}
