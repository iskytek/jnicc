/* C++ wrapper Klasse für OsiClpSolverInterface
 * Version ==> VERSION
 */

#include "jni_coin_OsiClpSolverInterface.h"
#include "jni_coin_CoinModel.h"

#include <stdio.h>

#include "OsiClpSolverInterface.hpp"
#include "CoinModel.hpp"

#define VERSION "WrapClp. Rel. 1.0.0, 2019-01-26"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Set optimization direction.
 */
JNIEXPORT void JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1setObjSense(
	       JNIEnv *, jclass, jlong nSolver, jdouble dSense ) {

  //printf( "WrapCoin. setObjSense( %.1lf )...\n", dSense ); fflush( stdout );
  
  OsiClpSolverInterface *p = (OsiClpSolverInterface *) nSolver;
  if( p != NULL ) p->setObjSense( dSense );
}

/*---------------------------------------------------------------------------*/
/* Gesamtes Modell von Coin in LPSolver laden.
 */
JNIEXPORT jint JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1loadFromCoinModel(
	       JNIEnv *, jclass, jlong jlSolver, jlong jlCoinModel, 
	       jboolean jbKeepSolution ) {

  jint jiNoErrors = -1;

  OsiClpSolverInterface *pSolver = (OsiClpSolverInterface *) jlSolver;
  CoinModel             *pModel  = (CoinModel             *) jlCoinModel;

  if( (pSolver != NULL) && (pModel != NULL) ) jiNoErrors =
	         (jint) pSolver->loadFromCoinModel( *pModel, jbKeepSolution );

  return jiNoErrors;
}

/*---------------------------------------------------------------------------*/
/* Es wird ein Solver Objekt angelegt und der Pointer auf das Objekt als
 * long zurück gegeben. 
 */
JNIEXPORT jlong JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1new(
           	JNIEnv *, jclass ) {

  //printf( "WrapCoin.Java_jni_1coin_OsiClpSolverInterface_jni_1new()...\n" );
  //fflush( stdout );
  
  OsiClpSolverInterface *pSolver = new OsiClpSolverInterface();
  if( pSolver == NULL ) fprintf( stderr, "OsiClpSolverInterface ist null!" );
  fflush( stderr );

  return (jlong) pSolver;
}

/*---------------------------------------------------------------------------*/
/* Get number of columns. 
 */
JNIEXPORT jint JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1getNumCols(
	       JNIEnv *, jclass, jlong jlSolver ) {

  jint jiCols = 0;

  OsiClpSolverInterface *p = (OsiClpSolverInterface *) jlSolver;
  if( p != NULL ) jiCols = p->getNumCols();

  return jiCols;
}

/*---------------------------------------------------------------------------*/
/* Get number of rows. 
 */
JNIEXPORT jint JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1getNumRows(
	       JNIEnv *, jclass, jlong jlSolver ) {

  jint jiRows = 0;

  OsiClpSolverInterface *p = (OsiClpSolverInterface *) jlSolver;
  if( p != NULL ) jiRows = p->getNumRows();

  return jiRows;
}

/*---------------------------------------------------------------------------*/
/* Set problem name. 
 */
JNIEXPORT jboolean JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1setModelName(
		   JNIEnv * env, jclass, jlong jlSolver, jstring jsName ) {

  jboolean jbRc = JNI_FALSE;

  OsiClpSolverInterface *p = (OsiClpSolverInterface *) jlSolver;
  if( p != NULL ) {
    const char * sName = env->GetStringUTFChars( jsName, NULL );
    jbRc = p->setStrParam( OsiProbName, sName );
    env->ReleaseStringUTFChars( jsName, sName );
  }

  return jbRc;
}

/*---------------------------------------------------------------------------*/
/* Get problem name. 
 */
JNIEXPORT jstring JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1getModelName(
		  JNIEnv * env, jclass, jlong jlSolver ) {

  jstring jsModelName = NULL;

  OsiClpSolverInterface *p = (OsiClpSolverInterface *) jlSolver;
  if( p != NULL ) {
    std::string sName;
    if( p->getStrParam( OsiProbName, sName ) ) {
      const char* pName = strcpy( (char*) malloc( sName.size() + 1 ), sName.c_str() );
      jsModelName = env->NewStringUTF( pName );
      env->ReleaseStringUTFChars( jsModelName, pName );
    }
  }

  return jsModelName;
}

/*---------------------------------------------------------------------------*/
/* Delete solver object.
 */
JNIEXPORT void JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1delete(
	       JNIEnv *, jclass, jlong nSolver ) {

  //printf( "WrapCoin.Java_jni_1coin_OsiClpSolverInterface_jni_1delete()...\n" );
  //fflush( stdout );
  
  OsiClpSolverInterface *p = (OsiClpSolverInterface *) nSolver;
  if( p != NULL ) delete p;
}

/*---------------------------------------------------------------------------*/
/* Solver Objekt wird nicht mehr benötigt und gelöscht.
 */
JNIEXPORT void JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1releaseClp(
               JNIEnv *, jclass, jlong nSolver ) {

  //printf( "WrapCoin.Java_jni_1coin_OsiClpSolverInterface_jni_1releaseClp()...\n" );
  //fflush( stdout );
  
  OsiClpSolverInterface *pSolver = (OsiClpSolverInterface *) nSolver;
  if( pSolver != NULL ) pSolver->releaseClp();
}

/*---------------------------------------------------------------------------*/
/* Read MPS file.
 */
JNIEXPORT jint JNICALL Java_jni_1coin_OsiClpSolverInterface_jni_1readMps(
	       JNIEnv * env, jclass, jlong nSolver, jstring jsMpsFile ) {

  int noErrors = -1;
  //printf( "WrapCoin.readMps()...\n" ); fflush( stdout );
  
  OsiClpSolverInterface *p = (OsiClpSolverInterface *) nSolver;
  if( p != NULL ) {  
    const char * sMpsFile = env->GetStringUTFChars( jsMpsFile, NULL  );
    noErrors = p->readMps( sMpsFile, "" );
    env->ReleaseStringUTFChars( jsMpsFile, sMpsFile );
  }

  return (jint) noErrors;
}

