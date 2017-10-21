// not tested !

// implementing 'push local 2'
// push segment i (local,arguments,this,that)
// psuedo : addr=LCL+2 SP++, *addr=*SP


@2      // local2 var is LCL+2
D=A
@LCL
A=A+D   // go into local2
D=M     // save value in D

@SP
A=M     // A points to top of stack
M=D     // push

@SP     // SP++
M=M+1

0 SP
1 LCL
2 ARG
3 THIS
4 THAT



