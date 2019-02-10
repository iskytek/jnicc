/* CbcCompareWolf.hpp
 */

#ifndef CbcCompareWolf_H
#define CbcCompareWolf_H

#include "CbcNode.hpp"
#include "CbcCompareBase.hpp"

#include <jni.h>

class CbcModel;

class CbcCompareWolf : public CbcCompareBase {

public:
  // Default Constructor 
  CbcCompareWolf() ;

  // Constructor with java method;
  CbcCompareWolf( JNIEnv *env, jobject jobThis );
  
  // Constructor with weight
  CbcCompareWolf( int );

  // Copy constructor 
  CbcCompareWolf( const CbcCompareWolf& cbcCompareWolf );
   
  // Assignment operator 
  CbcCompareWolf& operator=( const CbcCompareWolf& cbcCompareWolf );

  // Clone
  virtual CbcCompareBase * clone() const;

  ~CbcCompareWolf();

  /* This returns true if weighted value of node y is less than
     weighted value of node x */
  virtual bool test( CbcNode * x, CbcNode * y ) ;
  
  // This is alternate test function
  virtual bool alternateTest( CbcNode * x, CbcNode * y );
  
  // This allows method to change behavior as it is called after each solution
  virtual bool newSolution( CbcModel* model, double objectiveAtContinuous,
			                     int    numberInfeasibilitiesAtContinuous );

  // Returns true if wants code to do scan with alternate criterion
  virtual bool fullScan() const;
  
  // This allows method to change behavior. Return true if want tree re-sorted
  virtual bool every1000Nodes( CbcModel* model, int numberNodes );

protected:
  //mutable int count_;
  // Tree size (at last check)
  //int treeSize_;

private:
  int       nMaxDepth_; // max depth found so far in test() 
  int       nCmpMode_;
  long      lTestCounter_;
  jclass    jclThis_;
  jobject   jobThis_;
  JavaVM   *pJVM_;
};
#endif
