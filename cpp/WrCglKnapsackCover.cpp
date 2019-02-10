/* JNI/C++ wrapper class for CglKnapsackCover
 */

#include "cgl_CglKnapsackCover.h"

#include "CglKnapsackCover.hpp"

#define VERSION "WrCglKnapsackCover. Rel. 1.0.1, 2019-02-06"
#define RC_SUCCESS 0
#define RC_FAIL   -1

/*---------------------------------------------------------------------------*/
/* Add Knapsack cut.
 */
JNIEXPORT jlong JNICALL Java_cgl_CglKnapsackCover_jni_1new( 
		JNIEnv *, jclass ) {

  CglKnapsackCover *p = new CglKnapsackCover();
  if( p == NULL ) fprintf( stderr, "CglKnapsackCover is null!" );
  fflush( stderr );

  return (jlong) p;
}

