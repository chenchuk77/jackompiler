    // push constant 3
    @3
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // pop pointer 0 (THIS)
    @SP
    M=M-1
    A=M
    D=M
    @THIS
    M=D
    // push constant 4
    @4
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // pop pointer 1 (THAT)
    @SP
    M=M-1
    A=M
    D=M
    @THAT
    M=D
    // add: pop (y,x), push (x+y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    M=M+D
    @SP
    M=M+1
