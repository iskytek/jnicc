/* JNI/C++ wrapper Klasse für CbcModel
 */

#include "cbc_CbcCutGenerator.h"

#include <stdio.h>

#include "CbcNode.hpp"
#include "CbcCutGenerator.hpp"

#define VERSION "WrCbcCutGenerator. Rel. 1.0.1, 2019-02-04"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Get tuning info.
 */
JNIEXPORT jstring JNICALL Java_cbc_CbcCutGenerator_jni_1generateTuning(
		  JNIEnv *pEnv, jclass, jlong jlGenerator ) {
  
  jstring jsTuning = pEnv->NewStringUTF( "Tuning info not available." );

  CbcCutGenerator *p = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) {
    const int BUF_LEN = 4096;
    char *pS = (char *) malloc( BUF_LEN );
    FILE *pFile = fmemopen( pS, BUF_LEN, "w" );
    p->generateTuning( pFile );
    fclose( pFile ); 
    //printf( "*****  Tuning: %s\n", pS ); 
    jsTuning = pEnv->NewStringUTF( (char *) pS );
    free( pS );  
  }
  
  return jsTuning;
}

/*---------------------------------------------------------------------------*/
/* Get generator name.
 */
JNIEXPORT jstring JNICALL Java_cbc_CbcCutGenerator_jni_1getCutGeneratorName( 
		  JNIEnv * pEnv, jclass, jlong jlGenerator ) {
  
  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) return pEnv->NewStringUTF( p->cutGeneratorName() );
  else            return pEnv->NewStringUTF( "Name unknown" );
}

/*---------------------------------------------------------------------------*/
/* Number cuts active.
 */
JNIEXPORT jint JNICALL Java_cbc_CbcCutGenerator_jni_1numberCutsActive( 
	       JNIEnv * pEnv, jclass, jlong jlGenerator ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  return p->numberCutsActive();
}

/*---------------------------------------------------------------------------*/
/* Number cuts in total.
 */
JNIEXPORT jint JNICALL Java_cbc_CbcCutGenerator_jni_1numberCutsInTotal(
	       JNIEnv * pEnv, jclass, jlong jlGenerator ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  return p->numberCutsInTotal();
}

/*---------------------------------------------------------------------------*/
/* Number times entered.
 */
JNIEXPORT jint JNICALL Java_cbc_CbcCutGenerator_jni_1numberTimesEntered( 
	       JNIEnv * pEnv, jclass, jlong jlGenerator ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  return p->numberTimesEntered();
}

/*---------------------------------------------------------------------------*/
/* Refresh model and solver.
 */ 
JNIEXPORT void JNICALL Java_cbc_CbcCutGenerator_jni_1refreshModel(
	       JNIEnv *, jclass, jlong jlGenerator, jlong jlModel ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) p->refreshModel( (CbcModel *) jlModel );
}

/*---------------------------------------------------------------------------*/
/* Set inaccuracy.
 */ 
JNIEXPORT void JNICALL Java_cbc_CbcCutGenerator_jni_1setInaccuracy(
	       JNIEnv *, jclass, jlong jlGenerator, jint jiVal ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) p->setInaccuracy( jiVal ); 
}

/*---------------------------------------------------------------------------*/
/* Set needs optimal basis.
 */ 
JNIEXPORT void JNICALL Java_cbc_CbcCutGenerator_jni_1setNeedsOptimalBasis(
	       JNIEnv *, jclass, jlong jlGenerator, jboolean jbVal ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) p->setNeedsOptimalBasis( jbVal ); 
  else { fprintf( stderr, "Null Pointer in setNeedsOptimalBasis()!" ); 
	 fflush( stderr );
  }
}
/*---------------------------------------------------------------------------*/
/* Set normal.
 */ 
JNIEXPORT void JNICALL Java_cbc_CbcCutGenerator_jni_1setNormal(
	       JNIEnv *, jclass, jlong jlGenerator, jboolean jbVal ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) p->setNormal( jbVal ); 
  else { fprintf( stderr, "Null Pointer in setNormal()!" ); fflush( stderr ); }
}

/*---------------------------------------------------------------------------*/
/* Set Parameter.
 */ 
JNIEXPORT void JNICALL Java_cbc_CbcCutGenerator_jni_1setSwitchOfIfLessThan(
	       JNIEnv *, jclass, jlong jlGenerator, jint jiVal ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) p->setSwitchOffIfLessThan( jiVal ); 
}

/*---------------------------------------------------------------------------*/
/* Set depth.
 */ 
JNIEXPORT void JNICALL Java_cbc_CbcCutGenerator_jni_1setWhatDepth(
	       JNIEnv *, jclass, jlong jlGenerator, jint jiVal ) {

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) p->setWhatDepth( jiVal ); 
}

/*---------------------------------------------------------------------------*/
/* Time in generator 
 */ 
JNIEXPORT jdouble JNICALL Java_cbc_CbcCutGenerator_jni_1timeInCutGenerator(
	       JNIEnv * pEnv, jclass, jlong jlGenerator ) {

  jdouble jd = -1.0;

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) jd = p->timeInCutGenerator(); 

  return jd;
}

/*---------------------------------------------------------------------------*/
/* Is generator timed?
 */ 
JNIEXPORT jboolean JNICALL Java_cbc_CbcCutGenerator_jni_1timing(
	           JNIEnv * pEnv, jclass, jlong jlGenerator ) {

  //printf( "WrCbcCutGenerator.timing()...\n" ); fflush( stdout );
  jboolean jb = JNI_FALSE;

  CbcCutGenerator *p  = (CbcCutGenerator *) jlGenerator;
  if( p != NULL ) jb = p->timing(); 

  return jb;
}

