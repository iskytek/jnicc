# Makefile for jnicc

PROJECTPATH=".."
OBJS = WrapCoin.o WrapClp.o WrCbcModel.o CbcCompareWolf.o WrCglProbing.o WrCglRedSplit.o WrCglGomory.o WrCbcCutGenerator.o \
       WrCglTwomir.o WrCglKnapsackCover.o WrCglMixedIntegerRounding.o WrCglMixedIntegerRounding2.o
CXX = g++

# C++ Compiler options
# PIC: Position Independent Code
CXXFLAGS = -c -O3 -fPIC -fomit-frame-pointer -pipe -DNDEBUG -Wpedantic -Wparentheses -Wreturn-type -Wcast-qual -Wpointer-arith -Wwrite-strings -Wconversion  

# additional C++ Compiler options for linking
CXXLINKFLAGS = -Wl,--rpath -Wl,/lib

# Directory COIN header files
#COININCDIR = /usr/include/coin
COININCDIR = /opt/Cbc/build/include/coin

# Directory jni.h 
JNIHDIR = /usr/lib/jvm/java-11-openjdk-amd64/include

# Directory jni_md.h 
JNILINUXDIR = ${JNIHDIR}/linux

# Libraries necessary to link with Cbc
# LIBS = -L/usr/lib -lCbc -lClp -lOsi -Cgl -OsiClp -OsiCbc -lCoinUtils -lbz2 -lz -llapack -lblas -lm
LIBS = -L/opt/Cbc/build/lib -lCbc -lClp -lOsi -lCgl -lOsiClp -lOsiCbc -lCoinUtils -lbz2 -lz -llapack -lblas -lm

CLEANFILES = ${OBJS} ${PROJECTPATH}/run/libWrapCoin.so

Wrapper: $(OBJS)
	 $(CXX) $(CXXLINKFLAGS) -shared -o libWrapCoin.so ${OBJS} $(LIBS)
	 mv libWrapCoin.so ${PROJECTPATH}/run/
	 ls -thlF --color  ${PROJECTPATH}/run/

WrapCoin.o: WrapCoin.cpp WrapClp.cpp WrCbcModel.cpp CbcCompareWolf.cpp WrCglProbing.cpp WrCglRedSplit.cpp WrCglGomory.cpp WrCbcCutGenerator.cpp \
	    WrCglTwomir.cpp WrCglKnapsackCover.cpp WrCglMixedIntegerRounding.cpp WrCglMixedIntegerRounding2.cpp
	    $(CXX) -I${JNIHDIR} -I${JNILINUXDIR} -I${COININCDIR} $(CXXFLAGS) \
		    WrapCoin.cpp WrapClp.cpp WrCbcModel.cpp CbcCompareWolf.cpp WrCglProbing.cpp WrCglRedSplit.cpp WrCglGomory.cpp WrCbcCutGenerator.cpp \
		    WrCglTwomir.cpp WrCglKnapsackCover.cpp WrCglMixedIntegerRounding.cpp WrCglMixedIntegerRounding2.cpp

.PHONY: header
header:
	javac -h . -cp "${PROJECTPATH}/bin/"                          \
               ${PROJECTPATH}/src/jni_coin/Worker.java                \
               ${PROJECTPATH}/src/jni_coin/OsiClpSolverInterface.java \
               ${PROJECTPATH}/src/jni_coin/CoinModel.java             \
               ${PROJECTPATH}/src/cbc/CbcCompareWolf.java             \
               ${PROJECTPATH}/src/cbc/CbcCutGenerator.java            \
               ${PROJECTPATH}/src/cbc/CbcModel.java                   \
               ${PROJECTPATH}/src/cgl/CglGomory.java                  \
               ${PROJECTPATH}/src/cgl/CglProbing.java                 \
               ${PROJECTPATH}/src/cgl/CglTwomir.java                  \
               ${PROJECTPATH}/src/cgl/CglKnapsackCover.java           \
               ${PROJECTPATH}/src/cgl/CglMixedIntegerRounding.java    \
               ${PROJECTPATH}/src/cgl/CglMixedIntegerRounding2.java   \
               ${PROJECTPATH}/src/cgl/CglRedSplit.java
	ls -thlF --color
	
.PHONY: clean
clean:
	rm -rf $(CLEANFILES)
	ls -thlF --color
