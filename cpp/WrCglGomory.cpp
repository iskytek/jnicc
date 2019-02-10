/* JNI/C++ wrapper class for CglProbing
 */

#include "cgl_CglGomory.h"

#include "CglGomory.hpp"

#define VERSION "WrCglGomory. Rel. 1.0.0, 2019-02-03"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Add Gomory cut.
 */
JNIEXPORT jlong JNICALL Java_cgl_CglGomory_jni_1new( 
		JNIEnv *, jclass ) {

  CglGomory *p = new CglGomory();
  if( p == NULL ) fprintf( stderr, "CglGomory is null!" );
  fflush( stderr );

  return (jlong) p;
}

/*---------------------------------------------------------------------------*/
/* Set limit
 */
JNIEXPORT void JNICALL Java_cgl_CglGomory_jni_1setLimit( 
	       JNIEnv *, jclass, jlong jlCglGomory, jint jiValue ) {

  CglGomory *p = (CglGomory *) jlCglGomory;
  if( p != NULL ) p->setLimit( jiValue );
}

