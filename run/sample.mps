* Starting modgen.Main, Rel. 1.0.8, 2018-11-10 at Sat Jan 19 10:19:40 CET 2019...
* q = 2, R = 1, n = 3, s = 2, bound type: 0
* VR=4  Sphere-covering bound: ceil(q**n/VR)=2
* VR0=2 VR1=1 VR2=0
* RHS=q**(n-s)=2
* Upper bound Uj is RHS=2
*
NAME          q2_n3_R1_s2
ROWS
 N  COST
 G  R0
 G  R1
 G  R2
 G  R3
COLUMNS
    MARK0000  'MARKER'                 'INTORG'
    U0        COST                 1
    U0        R0                   2   R1                   1
    U0        R2                   1
    U1        COST                 1
    U1        R0                   1   R1                   2
    U1        R3                   1
    U2        COST                 1
    U2        R0                   1   R2                   2
    U2        R3                   1
    U3        COST                 1
    U3        R1                   1   R2                   1
    U3        R3                   2
    MARK0001  'MARKER'                 'INTEND'
RHS
    RHS1      R0                   2   R1                   2
    RHS1      R2                   2   R3                   2
BOUNDS
 UP B1        U0                   2
 UP B1        U1                   2
 UP B1        U2                   2
 UP B1        U3                   2
ENDATA
