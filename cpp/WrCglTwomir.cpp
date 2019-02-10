/* JNI/C++ wrapper class for CglProbing
 * Version ==> VERSION
 */

#include "cgl_CglTwomir.h"

#include "CglTwomir.hpp"

#define VERSION "WrCglTwomir. Rel. 1.0.0, 2019-02-05"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Add RedSplit cut.
 */
JNIEXPORT jlong JNICALL Java_cgl_CglTwomir_jni_1new( 
		JNIEnv *, jclass ) {

  CglTwomir *p = new CglTwomir();
  if( p == NULL ) fprintf( stderr, "CglTwomir is null!" );
  fflush( stderr );

  return (jlong) p;
}

/*---------------------------------------------------------------------------*/
/* Set limit
 */
JNIEXPORT void JNICALL Java_cgl_CglTwomir_jni_1setTwomirType( 
	       JNIEnv *, jclass, jlong jlCgl, jint jiVal ) {

  CglTwomir *p = (CglTwomir *) jlCgl;
  if( p != NULL ) p->setTwomirType( jiVal );
}

