// not tested !

// implementing 'pop local 2'
// psuedo : addr=LCL+2 SP--, *addr=*SP


@2      // local2 var is LCL+2
D=A
@LCL
D=A+D

@addr   // save address of local2 var
M=D

@SP     // SP--
M=M-1
A=M     // goto top of stack
D=M     // pop value

@addr   // store in the address of local2
A=M
M=D
