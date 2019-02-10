/* JNI/C++ wrapper class for CglProbing
 */

#include "cgl_CglProbing.h"

#include "CglProbing.hpp"

#define VERSION "WrCglProbing. Rel. 1.0.2, 2019-02-03"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Add Probing.
 */
JNIEXPORT jlong JNICALL Java_cgl_CglProbing_jni_1new( 
		JNIEnv *, jclass ) {

  //printf( "WrCglProbing.new()...\n" ); fflush( stdout );
  
  CglProbing *p = new CglProbing();
  if( p == NULL ) fprintf( stderr, "CglProbing is null!" );
  fflush( stderr );

  return (jlong) p;
}

/*---------------------------------------------------------------------------*/
/* Set row cuts type
 */
JNIEXPORT void JNICALL Java_cgl_CglProbing_jni_1setRowCuts( 
	       JNIEnv *, jclass, jlong jlCglProbing, jint jiValue ) {

  CglProbing *p = (CglProbing *) jlCglProbing;
  if( p != NULL ) p->setRowCuts( jiValue );
}

/*---------------------------------------------------------------------------*/
/* Set max elements considered at each node
 */
JNIEXPORT void JNICALL Java_cgl_CglProbing_jni_1setMaxElements( 
	       JNIEnv *, jclass, jlong jlCglProbing, jint jiValue ) {

  CglProbing *p = (CglProbing *) jlCglProbing;
  if( p != NULL ) p->setMaxElements( jiValue );
}

/*---------------------------------------------------------------------------*/
/* Set max elements considered at root node.
 */
JNIEXPORT void JNICALL Java_cgl_CglProbing_jni_1setMaxElementsRoot( 
	       JNIEnv *, jclass, jlong jlCglProbing, jint jiValue ) {

  CglProbing *p = (CglProbing *) jlCglProbing;
  if( p != NULL ) p->setMaxElementsRoot( jiValue );
}

/*---------------------------------------------------------------------------*/
/* Set max elements to look at in each probe
 */
JNIEXPORT void JNICALL Java_cgl_CglProbing_jni_1setMaxLook( 
	       JNIEnv *, jclass, jlong jlCglProbing, jint jiValue ) {

  CglProbing *p = (CglProbing *) jlCglProbing;
  if( p != NULL ) p->setMaxLook( jiValue );
}

/*---------------------------------------------------------------------------*/
/* Set max elements to look at in each probe (root).
 */
JNIEXPORT void JNICALL Java_cgl_CglProbing_jni_1setMaxLookRoot( 
	       JNIEnv *, jclass, jlong jlCglProbing, jint jiValue ) {

  CglProbing *p = (CglProbing *) jlCglProbing;
  if( p != NULL ) p->setMaxLookRoot( jiValue );
}

/*---------------------------------------------------------------------------*/
/* Set max pass at each node.
 */
JNIEXPORT void JNICALL Java_cgl_CglProbing_jni_1setMaxPass( 
	       JNIEnv *, jclass, jlong jlCglProbing, jint jiValue ) {

  CglProbing *p = (CglProbing *) jlCglProbing;
  if( p != NULL ) p->setMaxPass( jiValue );
}

/*---------------------------------------------------------------------------*/
/* Set max pass at root node.
 */
JNIEXPORT void JNICALL Java_cgl_CglProbing_jni_1setMaxPassRoot( 
	       JNIEnv *, jclass, jlong jlCglProbing, jint jiValue ) {

  CglProbing *p = (CglProbing *) jlCglProbing;
  if( p != NULL ) p->setMaxPassRoot( jiValue );
}

/*---------------------------------------------------------------------------*/
/* Set using objective.
 */
JNIEXPORT void JNICALL Java_cgl_CglProbing_jni_1setUsingObjective(
	       JNIEnv *, jclass, jlong jlCglProbing, jint jiValue ) {

  CglProbing *p = (CglProbing *) jlCglProbing;
  if( p != NULL ) p->setUsingObjective( jiValue );
}

