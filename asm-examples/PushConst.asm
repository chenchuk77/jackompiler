// not tested !

// implementing 'push local 2'
// push constant i (not address offset, real value)
// psuedo : *SP=i, SP++

// push constant 2 (this not like other segments, 2 is val, not addr !)

@2
D=A

@SP
A=M
M=D

@SP     // SP++
M=M+1



