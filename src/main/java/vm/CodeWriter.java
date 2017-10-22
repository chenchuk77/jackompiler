package vm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenchuk on 10/21/17.
 */
public class CodeWriter {
    private String outputAsmCode;
    private Command command;
    private Integer index;

//    0 SP
//1 LCL
//2 ARG
//3 THIS
//4 THAT

    // generate assembly code

    // local, argument, this, that:  mapped directly on the Hack RAM
    private Map<String, String> memorySegment;

    private void init(){
        memorySegment = new HashMap<String, String>();
        memorySegment.put("local",    "LCL");   // RAM [1]
        memorySegment.put("argument", "ARG");   // RAM [2]
        memorySegment.put("this",     "THIS");  // RAM [3]
        memorySegment.put("that",     "THAT");  // RAM [4]
    }

    public CodeWriter(String outputAsmCode){
        this.outputAsmCode = outputAsmCode;
        this.index = 0;
    }
    public void  writeArithmetic(){
        // add to output string
        outputAsmCode += "";
    }
    public String  writePushPop(String command){
        // add to output string
        outputAsmCode += "";
        return "";
    }
    public void close(){
        // close file
    }

    private String generatePush(String arg1, Integer arg2){
        StringBuilder pushCommand = new StringBuilder();

        // const is direct value
        if (arg1.equals("const")){
            pushCommand.append("// push const " +arg2 )      ; pushCommand.append("\n");
            pushCommand.append("    @" +arg2 )                   ; pushCommand.append("\n");
            pushCommand.append("    D=A")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
        } else {
            pushCommand.append("// push " +arg1+ " " +arg2 ) ; pushCommand.append("\n");
            pushCommand.append("    @" +arg2 )                   ; pushCommand.append("\n");
            pushCommand.append("    D=A")                        ; pushCommand.append("\n");
            pushCommand.append("    @" +memorySegment.get(arg1)) ; pushCommand.append("\n");
            pushCommand.append("    A=A+D")                      ; pushCommand.append("\n");
            pushCommand.append("    D=M")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
        }
        return pushCommand.toString();
    }
    private String generatePop(String arg1, Integer arg2){
        StringBuilder popCommand = new StringBuilder();
        popCommand.append("// pop " +arg1+ " " +arg2 )  ; popCommand.append("\n");
        popCommand.append("    @" +arg2 )                   ; popCommand.append("\n");
        popCommand.append("    D=A")                        ; popCommand.append("\n");
        popCommand.append("    @" +memorySegment.get(arg1)) ; popCommand.append("\n");
        popCommand.append("    D=A+D")                      ; popCommand.append("\n");
        popCommand.append("    @addr")                      ; popCommand.append("\n");
        popCommand.append("    M=D")                        ; popCommand.append("\n");
        popCommand.append("    @SP")                        ; popCommand.append("\n");
        popCommand.append("    M=M-1")                      ; popCommand.append("\n");
        popCommand.append("    A=M")                        ; popCommand.append("\n");
        popCommand.append("    D=M")                        ; popCommand.append("\n");
        popCommand.append("    @addr")                      ; popCommand.append("\n");
        popCommand.append("    A=M")                        ; popCommand.append("\n");
        popCommand.append("    M=D")                        ; popCommand.append("\n\n");
        return popCommand.toString();
    }
    private String generateAdd(){
        StringBuilder addCommand = new StringBuilder();
        addCommand.append("// add: pop (y,x), push (x+y)\n");
        addCommand.append("@SP")                        ; addCommand.append("\n");
        addCommand.append("M=M-1")                      ; addCommand.append("\n");
        addCommand.append("A=M")                        ; addCommand.append("\n");
        addCommand.append("D=M")                        ; addCommand.append("\n");
        addCommand.append("@SP")                        ; addCommand.append("\n");
        addCommand.append("M=M-1")                      ; addCommand.append("\n");
        addCommand.append("A=M")                        ; addCommand.append("\n");
        addCommand.append("M=M+D")                      ; addCommand.append("\n");
        addCommand.append("@SP")                        ; addCommand.append("\n");
        addCommand.append("M=M+1")                      ; addCommand.append("\n\n");
        return addCommand.toString();
    }
    private String generateSub(){
        StringBuilder subCommand = new StringBuilder();
        subCommand.append("// sub: pop (y,x), push (x-y)\n");
        subCommand.append("    @SP")                        ; subCommand.append("\n");
        subCommand.append("    M=M-1")                      ; subCommand.append("\n");
        subCommand.append("    A=M")                        ; subCommand.append("\n");
        subCommand.append("    D=M")                        ; subCommand.append("\n");
        subCommand.append("    @SP")                        ; subCommand.append("\n");
        subCommand.append("    M=M-1")                      ; subCommand.append("\n");
        subCommand.append("    A=M")                        ; subCommand.append("\n");
        subCommand.append("    M=M-D")                      ; subCommand.append("\n");
        subCommand.append("    @SP")                        ; subCommand.append("\n");
        subCommand.append("    M=M+1")                      ; subCommand.append("\n\n");
        return subCommand.toString();
    }

    private String generateNeg(){
        StringBuilder negCommand = new StringBuilder();
        negCommand.append("// neg: pop (y), push (-y)\n");
        negCommand.append("    @SP")                        ; negCommand.append("\n");
        negCommand.append("    M=M-1")                      ; negCommand.append("\n");
        negCommand.append("    A=M")                        ; negCommand.append("\n");
        // not sure if M=-M is allow
        //sb.append("M=-M")                             ; sb.append("\n");
        negCommand.append("    D=M")                        ; negCommand.append("\n");
        negCommand.append("    M=-D")                       ; negCommand.append("\n");
        negCommand.append("    @SP")                        ; negCommand.append("\n");
        negCommand.append("    M=M+1")                      ; negCommand.append("\n\n");
        return negCommand.toString();
    }

    private String generateEq(){
        // each call generate unique labels
        int id = index++;

        StringBuilder eqCommand = new StringBuilder();
        eqCommand.append("// eq: pop (y,x), push (x==y)\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    M=M-1")                      ; eqCommand.append("\n");
        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
        eqCommand.append("    D=M")                        ; eqCommand.append("\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    M=M-1")                      ; eqCommand.append("\n");
        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
        eqCommand.append("    D=M-D")                      ; eqCommand.append("\n");
        eqCommand.append("    @EQUAL_" +id)                ; eqCommand.append("\n");
        eqCommand.append("    D;JE")                       ; eqCommand.append("\n");
        eqCommand.append("(NOT_EQUAL_" +id+ ")")           ; eqCommand.append("\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
        eqCommand.append("    M=0 // false")               ; eqCommand.append("\n");
        eqCommand.append("    @END_COND_" +id)             ; eqCommand.append("\n");
        eqCommand.append("    0;JMP")                      ; eqCommand.append("\n");

        eqCommand.append("(EQUAL_" +id+ ")")               ; eqCommand.append("\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
        eqCommand.append("    M=-1 // true")               ; eqCommand.append("\n");
        eqCommand.append("(END_COND_" +id+ ")")            ; eqCommand.append("\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    M=M+1")                      ; eqCommand.append("\n\n");
        return eqCommand.toString();
    }







    public void addAsm(Command command) {
        StringBuilder sb = new StringBuilder();
        if (command.getCommandType() == CommandType.C_PUSH) {
            outputAsmCode += generatePush(command.getArg1(), command.getArg2());
        }
        if  (command.getCommandType() == CommandType.C_POP) {
            outputAsmCode += generatePop(command.getArg1(), command.getArg2());
        }
        if  (command.getCommandType() == CommandType.C_ARITHMETIC) {

            if (command.getOperation().equals("add")) outputAsmCode += generateAdd();
            if (command.getOperation().equals("sub")) outputAsmCode += generateSub();
            if (command.getOperation().equals("neg")) outputAsmCode += generateNeg();

        }
            //System.out.println(command.getArg1());

                //outputAsmCode += sb.toString();
            System.out.println(outputAsmCode);
    }
}


