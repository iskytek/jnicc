/* JNI/C++ wrapper class for CglMixedIntegerRounding
 */

#include "cgl_CglMixedIntegerRounding.h"

#include "CglMixedIntegerRounding.hpp"

#define VERSION "CglMixedIntegerRounding. Rel. 1.0.0, 2019-02-06"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Add cut.
 */
JNIEXPORT jlong JNICALL Java_cgl_CglMixedIntegerRounding_jni_1new( 
		JNIEnv *, jclass ) {

  CglMixedIntegerRounding *p = new CglMixedIntegerRounding();
  if( p == NULL ) fprintf( stderr, "CglMixedIntegerRounding is null!" );
  fflush( stderr );

  return (jlong) p;
}

