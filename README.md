# jnicc
Java Native Interface for Covering Codes

This project implements a Java native interface for the COIN-OR Cbc MIP solver.
You must have Cbc installed on your system to use this program. 
The software was tested on Debian 9 but should run on most Linux systems.

The project comes with a model generator for covering code problems in the
Hamming space. This serves just as an example. You can also use a MPS file for 
data input, which allows you to solve almost any kind of MIP problem. 
Alternatively you can modify the model generator or substitute it with your own
model generator class.

Should it by accident happen that you are interested in solving covering code
problems, keep in mind, that covering code models are defined by four 
Parameters: 
 1. q: number of letters in the alphabet, q >= 2,
 2. n: length of codewords, n >= 1,
 3. R: covering radius (Volume), 1 <= R <= n,
 4. s: a decomposition factor, determining the number of generated variables 
       (= Math.pow( q, s ), 1 <= s <= n       
The solution of the calculation is a lower bound for the number of codewords
needed to cover the Hamming space.

You specify your parameters in the file "jnicc.ini", which is read at startup. 
If you specify a MPS input file, the four parameters are ignored and the 
MPS problem solved instead.

Installation:
Use your preferred Java IDE to export a jar file into the "run" directory and
use "make" for making the dynamic library libWrapCoin.so. Make sure, the lib is
in the search path by either adding it to LD_LIBRARY_PATH or by a symbolic link 
(i.e. ln -s libWrapCoin.so /usr/lib/jni/).

The project is still in an early stage and right now, only a small subset of the
Cbc functions are wrapped. 
  

Good luck!
