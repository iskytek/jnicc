/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class jni_coin_CoinModel */

#ifndef _Included_jni_coin_CoinModel
#define _Included_jni_coin_CoinModel
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     jni_coin_CoinModel
 * Method:    jni_addColumn
 * Signature: (JIDDDLjava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_jni_1coin_CoinModel_jni_1addColumn
  (JNIEnv *, jclass, jlong, jint, jdouble, jdouble, jdouble, jstring, jboolean);

/*
 * Class:     jni_coin_CoinModel
 * Method:    jni_addRow
 * Signature: (JI[I[DDDLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_jni_1coin_CoinModel_jni_1addRow
  (JNIEnv *, jclass, jlong, jint, jintArray, jdoubleArray, jdouble, jdouble, jstring);

/*
 * Class:     jni_coin_CoinModel
 * Method:    jni_new
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_jni_1coin_CoinModel_jni_1new
  (JNIEnv *, jclass);

/*
 * Class:     jni_coin_CoinModel
 * Method:    jni_delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_jni_1coin_CoinModel_jni_1delete
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
