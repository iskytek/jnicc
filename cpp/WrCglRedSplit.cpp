/* JNI/C++ wrapper class for CglProbing
 * Version ==> VERSION
 */

#include "cgl_CglRedSplit.h"

#include "CglRedSplit.hpp"

#define VERSION "WrCglRedSplit. Rel. 1.0.1, 2019-02-03"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Add RedSplit cut.
 */
JNIEXPORT jlong JNICALL Java_cgl_CglRedSplit_jni_1new( 
		JNIEnv *, jclass ) {

  //printf( "WrCglRedSplit.new()...\n" ); fflush( stdout );
  
  CglRedSplit *p = new CglRedSplit();
  if( p == NULL ) fprintf( stderr, "CglRedSplit is null!" );
  fflush( stderr );

  return (jlong) p;
}

/*---------------------------------------------------------------------------*/
/* Set limit
 */
JNIEXPORT void JNICALL Java_cgl_CglRedSplit_jni_1setLimit( 
	       JNIEnv *, jclass, jlong jlCglRedSplit, jint jiValue ) {

  CglRedSplit *p = (CglRedSplit *) jlCglRedSplit;
  if( p != NULL ) p->setLimit( jiValue );
}

