/* CbcCompareWolf.cpp
 * Schnittstellenklasse zwischen JNI und Coin
 * Last update: 2019-02-09, wl
 */

#include <thread>
#include "CbcModel.hpp"
#include "CbcTree.hpp"
#include "CoinError.hpp"
#include "CoinHelperFunctions.hpp"
 
#include "CbcCompareWolf.hpp"

const int CMP_MODE_MIN_OBJ     = 0;
const int CMP_MODE_MAX_OBJ     = 1;
const int CMP_MODE_MIN_DEPTH   = 2;
const int CMP_MODE_MAX_DEPTH   = 3;
const int CMP_MODE_MIN_UNSATIS = 4;
const int CMP_MODE_MAX_UNSATIS = 5;

//const long MODULO_TESTCOUNTER  =10 * 1000 * 1000;

// Constructor with java method;
CbcCompareWolf::CbcCompareWolf( JNIEnv *env, jobject jobThis ) : 
	        CbcCompareWolf( CMP_MODE_MIN_OBJ ) { //UNSATISFIED ) { //DEPTH ) { //CMP_MODE_OBJECTIVE ) {

  /*printf( "CbcCompareWolf::CbcCompareWolf( JNIEnv, jmethodID )\n" );		
  std::cout << "Thread ID: " << std::this_thread::get_id() << "\n";
  std::cout << "env: " << env << "\n"; fflush( stdout );*/

  env->GetJavaVM( &pJVM_ );
  
  // Anhand von jobThis kann ein Pointer auf die Klasse ermittelt werden:
  jclass jcCompareWolf = env->GetObjectClass( jobThis );
  jclThis_  = (jclass) env->NewGlobalRef( jcCompareWolf );
  jobThis_  =          env->NewGlobalRef( jobThis       );
  //std::cout << "jobThis_: " << jobThis_ << "\n";
}

// Constructor with weight
CbcCompareWolf::CbcCompareWolf( int nCmpMode ) : CbcCompareBase() {
 
  //printf( "CbcCompareWolf::CbcCompareWolf( double )\n" ); fflush( stdout );
  nMaxDepth_    = 0;
  lTestCounter_ = -1;
  nCmpMode_     = nCmpMode;
}

// Copy constructor 
CbcCompareWolf::CbcCompareWolf( const CbcCompareWolf& cbcCompareWolf ) : CbcCompareBase( cbcCompareWolf ) {

  //printf( "CbcCompareWolf::CbcCompareWolf( cbcCompareWolf )\n" ); fflush( stdout );
  nMaxDepth_       = cbcCompareWolf.nMaxDepth_;
  lTestCounter_    = cbcCompareWolf.lTestCounter_;
  nCmpMode_        = cbcCompareWolf.nCmpMode_;
  jclThis_         = cbcCompareWolf.jclThis_;
  jobThis_         = cbcCompareWolf.jobThis_;
  pJVM_            = cbcCompareWolf.pJVM_;
}

// Clone
CbcCompareBase* CbcCompareWolf::clone() const {
  //printf( "CbcCompareWolf::clone()\n" ); fflush( stdout );
  return new CbcCompareWolf( *this );
}

// Assignment operator 
CbcCompareWolf& CbcCompareWolf::operator=( const CbcCompareWolf& cbcCompareWolf ) {

  printf( "CbcCompareWolf::operator=()\n" ); fflush( stdout );

  if( this != &cbcCompareWolf ) {
    
    CbcCompareBase::operator=( cbcCompareWolf );

    nMaxDepth_       = cbcCompareWolf.nMaxDepth_;
    lTestCounter_    = cbcCompareWolf.lTestCounter_;
    jclThis_         = cbcCompareWolf.jclThis_;
    jobThis_         = cbcCompareWolf.jobThis_;
    pJVM_            = cbcCompareWolf.pJVM_;
  }

  return *this;
}

// Destructor 
CbcCompareWolf::~CbcCompareWolf() {

  printf( "CbcCompareWolf::~CbcCompareWolf()\n" ); fflush( stdout );

  /*int rc = -1;
  JNIEnv *pEnv; // Achtung: Bei neuer Thread: pJVM_>AttachCurrentThread(...);
  if( pJVM_ != NULL ) rc = pJVM_->GetEnv( (void**)& pEnv, JNI_VERSION_10 );
  else fprintf( stderr, "Fehler bei GetEnv. pJVM_ ist null!\n" ); fflush( stderr ); 

  if( rc == JNI_OK ) {
    std::cout << "JNI_OK" << "\n"; fflush( stdout );
    pEnv->DeleteGlobalRef( jclThis_ );
    pEnv->DeleteGlobalRef( jobThis_ );
  }
  else if( rc == JNI_EDETACHED ) {
     fprintf( stderr, "Fehler bei GetEnv. JNI_EDETACHED!!\n" ); fflush( stderr ); 
  }
  else if( rc == JNI_EVERSION ) {
     fprintf( stderr, "Fehler bei GetEnv. JNI_EVERSION!!\n" ); fflush( stderr ); 
  }*/
}

//------------------------------------------------------------------------------
// Returns true if y better than x 
bool CbcCompareWolf::test( CbcNode* pX, CbcNode* pY ) {

  bool b = true;

  if( (pX != NULL) && (pY != NULL) ) {

    if( pX->depth() > nMaxDepth_ ) nMaxDepth_ = pX->depth();
    if( pY->depth() > nMaxDepth_ ) nMaxDepth_ = pY->depth();

    if( nCmpMode_ == CMP_MODE_MIN_OBJ ) {        
      if( pX->objectiveValue() < pY->objectiveValue() ) b = false;
    }
    else if( nCmpMode_ == CMP_MODE_MAX_DEPTH ) {        
      if( pX->depth() > pY->depth() ) b = false;
    }
    else if( nCmpMode_ == CMP_MODE_MIN_DEPTH ) {        
      if( pX->depth() < pY->depth() ) b = false;
    }
    else if( nCmpMode_ == CMP_MODE_MIN_UNSATIS ) {
      if( pX->numberUnsatisfied() < pY->numberUnsatisfied() ) b = false;
    } else if( pX->objectiveValue() > pY->objectiveValue() ) b = false;

    /*if( (++lTestCounter_ % MODULO_TESTCOUNTER) == 0) {
      //printf( "CbcCompareWolf::test(): #=%'ld b=%s unsat=%d_%d\tdepth=%d_%d\tobj=%.7f_%.7f\n",
      printf( "TST: #=%'ld unsat=%d_%d depth=%d_%d obj=%.5f_%.5f\n",
               lTestCounter_          , //b ? "true " : "false", 
               pX->numberUnsatisfied(), pY->numberUnsatisfied(),
               pX->depth()            , pY->depth(),
               pX->objectiveValue()   , pY->objectiveValue() );
      fflush( stdout );
      //lTestCounter_ = 0;
    }*/

  } else fprintf( stderr, "CmpWolf::test(): Node is NULL\n" );

  return b;
}

//------------------------------------------------------------------------------
// This allows method to change behavior as it is called after each solution 
bool CbcCompareWolf::newSolution( CbcModel* pCbcModel, double objectiveAtContinuous,
		                  int noInfeasibilitiesAtContinuous ) {
  bool bChangeStrategy = false;

  if( pCbcModel != NULL ) {
    int noSolutions = pCbcModel->getSolutionCount(); 
    //printf( "CbcCompareWolf::newSolution( %d )...\n", noSolutions ); 
    JNIEnv *pEnv; // Achtung: Bei neuer Thread: pJVM_>AttachCurrentThread(...);
    pJVM_->GetEnv( (void**)& pEnv, JNI_VERSION_10 );
    if( (pJVM_->GetEnv( (void**)& pEnv, JNI_VERSION_10 )) == JNI_OK ) {
      jmethodID jmIDnewSolution = pEnv->GetMethodID( jclThis_, "newSolution"   , "(JI)Z" );
      bChangeStrategy  = pEnv->CallBooleanMethod( 
		         jobThis_, jmIDnewSolution, pCbcModel, noSolutions );
    } else { fprintf( stderr, "Fehler bei GetEnv!!\n" ); fflush( stderr ); }
  }

  return bChangeStrategy;
}

/*---------------------------------------------------------------------------*/
// This allows method to change behavior.
// Return true: resort tree
bool CbcCompareWolf::every1000Nodes( CbcModel* pCbcModel, int noNodes ) {
   
  bool bResort = false;

  /* printf( "CbcCompareWolf::every1000Nodes()...\n" );
  std::cout << "Thread ID: " << std::this_thread::get_id() << "\n";
  fflush( stdout );*/

  JNIEnv *pEnv; // Achtung: Bei neuer Thread: pJVM_>AttachCurrentThread(...);
  //std::cout << "pEnv: " << pEnv << "\n"; fflush( stdout );
  if( (pJVM_->GetEnv( (void**)& pEnv, JNI_VERSION_10 )) == JNI_OK ) {
    //std::cout << "JNI_OK" << "\n";
    jmethodID jmID1000 = pEnv->GetMethodID( jclThis_, "every1000Nodes", "(JIII)I" );
    int nNewCmpMode = pEnv->CallIntMethod( jobThis_, jmID1000, pCbcModel,
		            noNodes, pCbcModel->tree()->size(), nMaxDepth_ ); 
    // pCbcModel->currentDepth() liefert unsinnige Werte und pCbcModel->tree()->lastDepth
    // ist erst ab Version 2.9 verfügbar.

    if( nNewCmpMode != nCmpMode_ ) { nCmpMode_ = nNewCmpMode; bResort = true; }
  }
  else { fprintf( stderr, "every1000Nodes(). Fehler bei GetEnv!!\n" ); fflush( stderr ); } 
  /*else if( rc == JNI_EDETACHED ) {
     fprintf( stderr, "Fehler bei GetEnv. JNI_EDETACHED!!\n" ); fflush( stderr ); 
  } else if( rc == JNI_EVERSION ) {
     fprintf( stderr, "Fehler bei GetEnv. JNI_EVERSION!!\n" ); fflush( stderr ); 
  }*/

  return bResort;
}

/*---------------------------------------------------------------------------*/
// Returns true if wants code to do scan with alternate criterion 
bool CbcCompareWolf::fullScan() const {

  printf( "CbcCompareWolf::fullScan()...\n" );
  return false;
}

/*---------------------------------------------------------------------------*/
// This is the alternate test function                           */
bool CbcCompareWolf::alternateTest( CbcNode * x, CbcNode * y ) {
  
  printf( "CbcCompareWolf::alternateTest()...\n" );
  return false;
}
