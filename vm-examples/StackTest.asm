    // push constant 17
    @17
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 17
    @17
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // eq: pop (y,x), push (x==y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @EQUAL_0
    D;JEQ
(NOT_EQUAL_0)
    @SP
    A=M
    M=0
    @END_COND_0
    0;JMP
(EQUAL_0)
    @SP
    A=M
    M=-1
(END_COND_0)
    @SP
    M=M+1

    // push constant 17
    @17
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 16
    @16
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // eq: pop (y,x), push (x==y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @EQUAL_1
    D;JEQ
(NOT_EQUAL_1)
    @SP
    A=M
    M=0
    @END_COND_1
    0;JMP
(EQUAL_1)
    @SP
    A=M
    M=-1
(END_COND_1)
    @SP
    M=M+1

    // push constant 16
    @16
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 17
    @17
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // eq: pop (y,x), push (x==y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @EQUAL_2
    D;JEQ
(NOT_EQUAL_2)
    @SP
    A=M
    M=0
    @END_COND_2
    0;JMP
(EQUAL_2)
    @SP
    A=M
    M=-1
(END_COND_2)
    @SP
    M=M+1

    // push constant 892
    @892
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 891
    @891
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // lt: pop (y,x), push (x<y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @LT_3
    D;JLT
(NOT_LT_3)
    @SP
    A=M
    M=0
    @END_COND_3
    0;JMP
(LT_3)
    @SP
    A=M
    M=-1
(END_COND_3)
    @SP
    M=M+1

    // push constant 891
    @891
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 892
    @892
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // lt: pop (y,x), push (x<y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @LT_4
    D;JLT
(NOT_LT_4)
    @SP
    A=M
    M=0
    @END_COND_4
    0;JMP
(LT_4)
    @SP
    A=M
    M=-1
(END_COND_4)
    @SP
    M=M+1

    // push constant 891
    @891
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 891
    @891
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // lt: pop (y,x), push (x<y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @LT_5
    D;JLT
(NOT_LT_5)
    @SP
    A=M
    M=0
    @END_COND_5
    0;JMP
(LT_5)
    @SP
    A=M
    M=-1
(END_COND_5)
    @SP
    M=M+1

    // push constant 32767
    @32767
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 32766
    @32766
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // gt: pop (y,x), push (x>y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @GT_6
    D;JGT
(NOT_GT_6)
    @SP
    A=M
    M=0
    @END_COND_6
    0;JMP
(GT_6)
    @SP
    A=M
    M=-1
(END_COND_6)
    @SP
    M=M+1

    // push constant 32766
    @32766
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 32767
    @32767
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // gt: pop (y,x), push (x>y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @GT_7
    D;JGT
(NOT_GT_7)
    @SP
    A=M
    M=0
    @END_COND_7
    0;JMP
(GT_7)
    @SP
    A=M
    M=-1
(END_COND_7)
    @SP
    M=M+1

    // push constant 32766
    @32766
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 32766
    @32766
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // gt: pop (y,x), push (x>y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    D=M-D
    @GT_8
    D;JGT
(NOT_GT_8)
    @SP
    A=M
    M=0
    @END_COND_8
    0;JMP
(GT_8)
    @SP
    A=M
    M=-1
(END_COND_8)
    @SP
    M=M+1

    // push constant 57
    @57
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 31
    @31
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // push constant 53
    @53
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

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

    // push constant 112
    @112
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // sub: pop (y,x), push (x-y)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    M=M-D
    @SP
    M=M+1

    // neg: pop (y), push (-y)
    @SP
    M=M-1
    A=M
    M=-M
    @SP
    M=M+1

    // and: pop (y,x), push (xANDy)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    M=M&D
    @SP
    M=M+1

    // push constant 82
    @82
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1

    // or: pop (y,x), push (xORy)
    @SP
    M=M-1
    A=M
    D=M
    @SP
    M=M-1
    A=M
    M=M|D
    @SP
    M=M+1

    // not: pop (y), push (!y)
    @SP
    M=M-1
    A=M
    M=!M
    @SP
    M=M+1
