/* JNI/C++ wrapper class for CglMixedIntegerRounding2
 * Version ==> VERSION
 */

#include "cgl_CglMixedIntegerRounding2.h"

#include "CglMixedIntegerRounding2.hpp"

#define VERSION "CglMixedIntegerRounding2. Rel. 1.0.0, 2019-02-06"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Add cut.
 */
JNIEXPORT jlong JNICALL Java_cgl_CglMixedIntegerRounding2_jni_1new( 
		JNIEnv *, jclass ) {

  CglMixedIntegerRounding2 *p = new CglMixedIntegerRounding2();
  if( p == NULL ) fprintf( stderr, "CglMixedIntegerRounding2 is null!" );
  fflush( stderr );

  return (jlong) p;
}

