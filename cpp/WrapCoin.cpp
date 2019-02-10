/* C++ wrapper Klasse für JNI-Coin
 * Version ==> VERSION
 */

#include "jni_coin_Worker.h"
#include "jni_coin_OsiClpSolverInterface.h"
#include "jni_coin_CoinModel.h"
#include "cbc_CbcModel.h"
#include "cbc_CbcCompareWolf.h"

#include <stdio.h>

#include "OsiClpSolverInterface.hpp"
#include "CoinModel.hpp"
#include "CbcModel.hpp"
#include "CbcCompareBase.hpp"

#include "CbcCompareWolf.hpp"

#define VERSION "WrapCoin. Rel. 1.1.3, 2019-02-03"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Objekt vom Typ CoinModel anlegen und Pointer als long zurück.
 */
JNIEXPORT jlong JNICALL Java_jni_1coin_CoinModel_jni_1new(
		JNIEnv *, jclass ) {

  //printf( "WrapCoin.Java_jni_1coin_CoinModel_jni_1new()...\n" );
  //fflush( stdout );
  
  CoinModel *p = new CoinModel();
  if( p == NULL ) fprintf( stderr, "CoinModel ist null!" );
  fflush( stderr );

  return (jlong) p;
}


/*---------------------------------------------------------------------------*/
/* Objekt wird nicht mehr benötigt und gelöscht.
 */
JNIEXPORT void JNICALL Java_jni_1coin_CoinModel_jni_1delete(
	       JNIEnv *, jclass, jlong nCoinModel ) {

  //printf( "WrapCoin.Java_jni_1coin_CoinModel_jni_1delete()...\n" );
  //fflush( stdout );
  
  CoinModel *p = (CoinModel *) nCoinModel;
  if( p != NULL ) delete p;
}

/*---------------------------------------------------------------------------*/
/* Add column.
 */
JNIEXPORT void JNICALL Java_jni_1coin_CoinModel_jni_1addColumn(
	       JNIEnv * env, jclass, jlong jlCoinModel, jint jiNoInCol,
	       jdouble jdLoBo, jdouble jdUpBo,
	       jdouble jdCost, jstring jsColName, jboolean jbIsInt ) {

  CoinModel *p = (CoinModel *) jlCoinModel;
  if( p != NULL ) {
    const char * s = env->GetStringUTFChars( jsColName, NULL );
    p->addColumn( jiNoInCol, NULL, NULL, jdLoBo, jdUpBo, jdCost, s, jbIsInt );
    env->ReleaseStringUTFChars( jsColName, s );
  }
}

/*---------------------------------------------------------------------------*/
/* Add row..
 */
JNIEXPORT void JNICALL Java_jni_1coin_CoinModel_jni_1addRow(
	       JNIEnv * env, jclass, jlong jlCoinModel, jint jiNoInRow, 
	       jintArray jiaIndex, jdoubleArray jdaValue, jdouble jdLoBo, 
	       jdouble jdUpBo, jstring jsRowName ) {

  CoinModel *p = (CoinModel *) jlCoinModel;
  if( p != NULL ) {
    const char   * s = env->GetStringUTFChars     ( jsRowName, NULL );
    int    * anIndex = env->GetIntArrayElements   ( jiaIndex , NULL );
    double * adValue = env->GetDoubleArrayElements( jdaValue , NULL );
    
    /*printf( "WrapCoin.addRow(). Value array length: %d\n",
    env->GetArrayLength( jdaValue ) ); fflush( stdout );*/

    p->addRow( jiNoInRow, anIndex, adValue, jdLoBo, jdUpBo, s );
    env->ReleaseDoubleArrayElements( jdaValue, adValue, 0 );
    env->ReleaseIntArrayElements   ( jiaIndex, anIndex, 0 );
    env->ReleaseStringUTFChars( jsRowName, s );
  }
}

/*---------------------------------------------------------------------------*/
/**
 * @return libWrapCoin Version
 */
JNIEXPORT jstring JNICALL Java_jni_1coin_Worker_jni_1getVersion(
		  JNIEnv * env, jclass ) {

  //printf( "Java_jni_1coin_Worker_jni_1getVersion()...\n" );
  //fflush( stdout );
  return env->NewStringUTF( VERSION );
}

/*******************************************************************************
JNIEXPORT jint JNICALL Java_jni_1horst_Main_jni_1print( 
	       JNIEnv *, jclass, jint nWidth, jint nPrecision, jdouble dx ) {

  char fmt[ 30 ];
  sprintf( fmt, "%%%d.%df", nWidth, nPrecision );
  jint rc = printf( fmt, dx );

  return rc;
}

JNIEXPORT jstring JNICALL Java_jni_1horst_Main_jni_1give_1take (
                  JNIEnv *env, jclass, jstring jsIn ) {

  if( jsIn == NULL ) jsIn = env->NewStringUTF( "Wrapper sagt: Input ist Nullstring." );

  const char * str = env->GetStringUTFChars( jsIn, NULL  );
  printf( "Das ist angekommen: %s\n", str );  
  fflush( stdout );
  env->ReleaseStringUTFChars( jsIn, str );

  return env->NewStringUTF( VERSION );
}

* Die Funktion überprüft einen String Pointer auf NULL und generiert ggf.
 * eine Exception.
 *
JNIEXPORT jint JNICALL Java_jni_1horst_Main_jni_1checkNP(
	       JNIEnv* env, jobject, jstring jsIn ) {

  int rc = RC_SUCCESS;

  if( jsIn == NULL ) {
    jsIn = env->NewStringUTF( "Wrapper sagt: Input ist Nullstring." );
    const char* sIn = env->GetStringUTFChars( jsIn, NULL  );
    fprintf( stderr, "\n%s\n", sIn ); fflush( stderr );
    rc = RC_FAIL;
    env->ThrowNew( env->FindClass( "java/lang/NullPointerException" ),
		   "Null Pointer geht gar nicht! rc = RC_FAIL." );
  } else {
    const char* sIn = env->GetStringUTFChars( jsIn, NULL  );
    printf( "Wrapper.checkNP(): %s\n", sIn ); 
    fflush( stdout );
  }	  
  
  return rc;
}
*/
