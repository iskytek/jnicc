/* JNI/C++ wrapper Klasse für CbcModel
 */

#include "jni_coin_OsiClpSolverInterface.h"
#include "cbc_CbcCompareWolf.h"
#include "cbc_CbcModel.h"

#include <stdio.h>

#include "OsiClpSolverInterface.hpp"
#include "CbcNode.hpp"
#include "CglCutGenerator.hpp"
#include "CbcCutGenerator.hpp"
#include "CbcModel.hpp"
#include "CbcCompareBase.hpp"
#include "CglGomory.hpp"

#include "CbcCompareWolf.hpp"

#define VERSION "WrCbcModel. Rel. 1.0.6, 2019-02-03"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Add cut gererator.
 */
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1addCutGenerator( 
	       JNIEnv *pEnv, jclass, jlong jlCbcModel, jlong jlGenerator, 
	       jint jiHowOften, jstring jsName ) {

  CbcModel        *pCbcModel  = (CbcModel        *) jlCbcModel;
  CglCutGenerator *pGenerator = (CglCutGenerator *) jlGenerator;
  if( pCbcModel != NULL && pGenerator != NULL ) {
    const char * s = pEnv->GetStringUTFChars( jsName, NULL );
    pCbcModel->addCutGenerator( pGenerator, jiHowOften, s );
    pCbcModel->cutGenerator( pCbcModel->numberCutGenerators() - 1 )->setTiming( true );
    pEnv->ReleaseStringUTFChars( jsName, s );
  }
}

/*---------------------------------------------------------------------------*/
/* Add Gomory cut.
 *
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1addCutGomory(
	       JNIEnv *, jclass, jlong jlCbcModel, jint jiHowOften, jint jiLimit ) {

  CbcModel *p = (CbcModel *) jlCbcModel;
  if( p != NULL ) {
    CglGomory cglG;
    cglG.setLimit( jiLimit );
    p->addCutGenerator( &cglG, jiHowOften, "Gomory" );
    
    // Set timing:
    int noGenerators = p->numberCutGenerators();
    //p->cutGenerator( p->numberCutGenerators() - 1 )->setTiming( true );
    for( int i = 0; i < noGenerators; i++ ) p->cutGenerator( i )->setTiming( true );
  }
}*/

/*---------------------------------------------------------------------------*/
/* Set call back method.
 */
JNIEXPORT jlong JNICALL Java_cbc_CbcCompareWolf_jni_1setNodeComparison(
	        JNIEnv * env, jobject jobCbcCompareWolf, jlong jlCbcModel ) {

  CbcModel *p = (CbcModel *) jlCbcModel;
  CbcCompareWolf *pWolf = new CbcCompareWolf( env, jobCbcCompareWolf );
  if( (p != NULL) && (pWolf != NULL) ) p->setNodeComparison( pWolf  );

  return (jlong) pWolf; 
}

/*---------------------------------------------------------------------------*/
/* Get cut generator.
 */
JNIEXPORT jlong JNICALL Java_cbc_CbcModel_jni_1getCutGenerator(
		JNIEnv *, jclass, jlong jlCbcModel, jint jindex ) {
 
  jlong jl = 0;

  CbcModel *p = (CbcModel *) jlCbcModel;
  if( p != NULL ) jl = (jlong) p->cutGenerator( jindex );  

  return jl;
}

/*---------------------------------------------------------------------------*/
/* Get number of cut generators.
 */
JNIEXPORT jint JNICALL Java_cbc_CbcModel_jni_1getNumberCutGenerators(
	       JNIEnv *, jclass, jlong jlCbcModel ) {
	
  jint noGenerators = -1;

  CbcModel *p = (CbcModel *) jlCbcModel;
  if( p == NULL ) {
    fprintf( stderr, "CbcModel ist null!" ); fflush( stderr );
  } else noGenerators = p->numberCutGenerators();

  return noGenerators;
}
  
/*---------------------------------------------------------------------------*/
/* Get number of threads in CbcModel.
 */
JNIEXPORT jint JNICALL Java_cbc_CbcModel_jni_1getNumberThreads(
	       JNIEnv *, jclass, jlong jlCbcModel ) {
	
  jint noThreads = 0;

  CbcModel *p = (CbcModel *) jlCbcModel;
  if( p == NULL ) {
    fprintf( stderr, "CbcModel ist null!" ); fflush( stderr );
  } else noThreads = p->getNumberThreads();

  return noThreads;
}

/*---------------------------------------------------------------------------*/
/* Set log level.
 */
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1setLogLevel( 
	       JNIEnv *, jclass, jlong jlCbcModel, jint jiLogLevel ) {
	
  CbcModel *pCbcModel = (CbcModel *) jlCbcModel;
  pCbcModel->setLogLevel( (int) jiLogLevel );
}

/*---------------------------------------------------------------------------*/
/* Branch and bound.
 */
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1branchAndBound( 
	       JNIEnv *, jclass, jlong jlCbcModel, jint jiStatistics ) {

  CbcModel *pCbcModel = (CbcModel *) jlCbcModel;
  if( jiStatistics > 0 ) pCbcModel->branchAndBound( (int) jiStatistics );
  else                   pCbcModel->branchAndBound();

}

/*---------------------------------------------------------------------------*/
/* Inital solve of LP.
 */
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1initialSolve( 
	       JNIEnv *, jclass, jlong nCbcModel ) {

  //printf( "WrapCbc.CbcModel.initialSolve()...\n" ); fflush( stdout );

  CbcModel *pCbcModel = (CbcModel *) nCbcModel;
  pCbcModel->initialSolve();
}

/*---------------------------------------------------------------------------*/
/* Assign lpSolver to cbcModel.
 */
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1assignSolver(
	       JNIEnv *, jclass, jlong nCbcModel, jlong nLpSolver, 
	       jboolean jbDeleteSolver ) {

  /*printf( "WrapCbc.CbcModel.assignSolver()...\n" ); fflush( stdout );
  printf( "nCbcModel: %ld, nLpSolver: %ld\n", nCbcModel, nLpSolver );
  fflush( stdout );*/
  
  CbcModel           *pCbcModel = (CbcModel *) nCbcModel;
  OsiSolverInterface *pLpSolver = (OsiClpSolverInterface *) nLpSolver;

  if( jbDeleteSolver ) pCbcModel->assignSolver( pLpSolver ); // true
  else                 pCbcModel->assignSolver( pLpSolver, false );
  
}

/*---------------------------------------------------------------------------*/
/* Maximum number or candidates for strong branching.
 */
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1setNumberStrong(
	       JNIEnv *, jclass, jlong jlCbcModel, jint jiMaxCand ) {

  CbcModel *pCbcModel = (CbcModel *) jlCbcModel;
  if( pCbcModel != NULL ) pCbcModel->setNumberStrong( jiMaxCand );
}

/*---------------------------------------------------------------------------*/
/* Set number of threads in CbcModel.
 */
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1setNumberThreads( 
	       JNIEnv *, jclass, jlong nCbcModel, jint noThreads ) {

  CbcModel *p = (CbcModel *) nCbcModel;
  if( p == NULL ) {
    fprintf( stderr, "CbcModel ist null!" );
    fflush( stderr );
  } else p->setNumberThreads( noThreads );
}

/*---------------------------------------------------------------------------*/
/* Create CoinModel and return Pointer as long.
 */
JNIEXPORT jlong JNICALL Java_cbc_CbcModel_jni_1new(
		JNIEnv *, jclass ) {

  CbcModel *p = new CbcModel();
  if( p == NULL ) fprintf( stderr, "CbcModel ist null!" );
  fflush( stderr );

  return (jlong) p;
}

/*---------------------------------------------------------------------------*/
/* Delete CbcModel object.
 */
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1delete(
	       JNIEnv *, jclass, jlong nCbcModel ) {

  //printf( "WrapCbc.Java_cbc_CbcModel_jni_1delete()...\n" );
  //fflush( stdout );
  
  CbcModel *p = (CbcModel *) nCbcModel;
  if( p != NULL ) delete p;
}

/*---------------------------------------------------------------------------*/
/* Get optimal z value.
 */ 
JNIEXPORT jdouble JNICALL Java_cbc_CbcModel_jni_1getObjValue( 
			  JNIEnv *, jclass, jlong jlCbcModel ) {
  
  jdouble jdObjValue = 0.0;		
  
  CbcModel *p = (CbcModel *) jlCbcModel;
  if( p != NULL ) jdObjValue = p->getObjValue();

  return jdObjValue;
}

/*---------------------------------------------------------------------------*/
/* Best possible.
 */ 
JNIEXPORT jdouble JNICALL Java_cbc_CbcModel_jni_1getBestPossibleObjValue(
		  JNIEnv *, jclass, jlong jlCbcModel ) {

  jdouble jdObjValue = 0.0;		
  
  CbcModel *p = (CbcModel *) jlCbcModel;
  if( p != NULL ) jdObjValue = p->getBestPossibleObjValue();

  return jdObjValue;

}

/*---------------------------------------------------------------------------*/
/* Check if solution is optimal.
 */ 
JNIEXPORT jboolean JNICALL Java_cbc_CbcModel_jni_1isProvenOptimal(
		   JNIEnv *, jclass, jlong jlCbcModel ) {

  jboolean jbIsProven = JNI_FALSE;		
  
  CbcModel *p = (CbcModel *) jlCbcModel;
  if( p != NULL ) if( p->isProvenOptimal() ) jbIsProven = JNI_TRUE;

  return jbIsProven;
}

/*---------------------------------------------------------------------------*/
/* Get solution vector.
 */ 
JNIEXPORT jboolean JNICALL Java_cbc_CbcModel_jni_1bestSolution(
		   JNIEnv * env, jclass, jlong jlCbcModel, 
		   jdoubleArray jdaSolution ) {

  jboolean jbIsOk = JNI_FALSE;
  const double *pSolution;

  CbcModel *p = (CbcModel *) jlCbcModel;
  if( p != NULL ) pSolution = p->bestSolution();

  if( pSolution != NULL ) {
    jsize jsLength = env->GetArrayLength( jdaSolution );
    double * pDouble = env->GetDoubleArrayElements( jdaSolution, NULL );
    for( int i = 0; i < jsLength; i++ ) pDouble[ i ] = pSolution[ i ];
    env->ReleaseDoubleArrayElements( jdaSolution, pDouble, 0 );
    jbIsOk = JNI_TRUE;
  }

  return jbIsOk;
}

/*---------------------------------------------------------------------------*/
/* Set best solution.
 */ 
JNIEXPORT void JNICALL Java_cbc_CbcModel_jni_1setBestSolution(
	       JNIEnv * pEnv, jclass, jlong jlCbcModel, jdoubleArray jdaBestSol,
	       jint jiNoCols, jdouble jdObj, jboolean jbCheck ) {

  CbcModel *pCbcModel = (CbcModel *) jlCbcModel;
  if( pCbcModel != NULL ) {
    //jsize jsLength  = pEnv->GetArrayLength( jdaBestSol );
    double *pDouble = pEnv->GetDoubleArrayElements( jdaBestSol, NULL );
    pCbcModel->setBestSolution( pDouble, jiNoCols, jdObj, jbCheck );
    pEnv->ReleaseDoubleArrayElements( jdaBestSol, pDouble, 0 );
  }
}

