package vm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenchuk on 10/21/17.
 */
public class CodeWriter {

    private String prefix;
    private String outputAsmCode;
    private Command command;
    private Integer index;


    // generate assembly code

    // local, argument, this, that:  mapped directly on the Hack RAM
    private Map<String, String> memorySegment;

    public CodeWriter(String filename){

        // only filename for 'static' handling
        this.prefix = filename.substring(0,filename.indexOf('.'));
        if (prefix.contains("/")){
            this.prefix = prefix.substring(filename.indexOf('/')+1);
        }

        outputAsmCode = "" ;
        this.index = 0;
        memorySegment = new HashMap<String, String>();
        memorySegment.put("local",    "LCL");   // RAM [1]
        memorySegment.put("argument", "ARG");   // RAM [2]
        memorySegment.put("this",     "THIS");  // RAM [3]
        memorySegment.put("that",     "THAT");  // RAM [4]
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
        if (arg1.equals("constant")) {
            pushCommand.append("    // push constant " + arg2); pushCommand.append("\n");
            pushCommand.append("    @" + arg2);                 pushCommand.append("\n");
            pushCommand.append("    D=A");                      pushCommand.append("\n");
            pushCommand.append("    @SP");                      pushCommand.append("\n");
            pushCommand.append("    A=M");                      pushCommand.append("\n");
            pushCommand.append("    M=D");                      pushCommand.append("\n");
            pushCommand.append("    @SP");                      pushCommand.append("\n");
            pushCommand.append("    M=M+1");                    pushCommand.append("\n\n");
        } else if (arg1.equals("temp")) {
            // temp segment is 8 Fixed addresses
            Integer equivRegister = arg2 + 5; // temp 0 mapped to R5,.... temp 7 mapped to R12
            String RX = "R" + equivRegister.toString();
            pushCommand.append("    // push temp " +arg2+ " (" +RX+ ")") ; pushCommand.append("\n");
            pushCommand.append("    @" +RX )                     ; pushCommand.append("\n");
            pushCommand.append("    D=M")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
        } else if (arg1.equals("pointer")) {
            // pointer 0 -> THIS , pointer 1 -> THAT
            String PTR = (arg2 == 0) ? "THIS" : "THAT";
            pushCommand.append("    // push pointer " +arg2)     ; pushCommand.append("\n");
            pushCommand.append("    @" +PTR )                    ; pushCommand.append("\n");
            pushCommand.append("    D=M")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
        } else if (arg1.equals("static")) {
            // static used the [filename.x] convention
            pushCommand.append("    // push static " +arg2)      ; pushCommand.append("\n");
            pushCommand.append("    @" +prefix+ "." +arg2)     ; pushCommand.append("\n");
            pushCommand.append("    D=M")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
        } else {
            // pointers of base addresses
            pushCommand.append("    // push " +arg1+ " " +arg2 ) ; pushCommand.append("\n");
            pushCommand.append("    @" +arg2 )                   ; pushCommand.append("\n");
            pushCommand.append("    D=A")                        ; pushCommand.append("\n");
            pushCommand.append("    @" +memorySegment.get(arg1)) ; pushCommand.append("\n");
            pushCommand.append("    A=M+D")                      ; pushCommand.append("\n");
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
        if (arg1.equals("temp")) {
            // temp segment is 8 Fixed addresses
            Integer equivRegister = arg2 + 5; // temp 0 mapped to R5,.... temp 7 mapped to R12
            String RX = "R" + equivRegister.toString();
            popCommand.append("    // pop temp " +arg2+ " (" +RX+ ")") ; popCommand.append("\n");
            popCommand.append("    @SP")                        ; popCommand.append("\n");
            popCommand.append("    M=M-1")                      ; popCommand.append("\n");
            popCommand.append("    A=M")                        ; popCommand.append("\n");
            popCommand.append("    D=M")                        ; popCommand.append("\n");
            popCommand.append("    @" + RX)                     ; popCommand.append("\n");
            popCommand.append("    M=D")                        ; popCommand.append("\n\n");
        } else if (arg1.equals("pointer")) {
            // pointer 0 -> THIS , pointer 1 -> THAT
            String PTR = (arg2 == 0) ? "THIS" : "THAT";
            popCommand.append("    // pop pointer " +arg2+ " (" +PTR+ ")") ; popCommand.append("\n");
            popCommand.append("    @SP")                        ; popCommand.append("\n");
            popCommand.append("    M=M-1")                      ; popCommand.append("\n");
            popCommand.append("    A=M")                        ; popCommand.append("\n");
            popCommand.append("    D=M")                        ; popCommand.append("\n");
            popCommand.append("    @" + PTR)                    ; popCommand.append("\n");
            popCommand.append("    M=D")                        ; popCommand.append("\n\n");
        } else if (arg1.equals("static")) {
            // static used the [filename.x] convention
            popCommand.append("    // pop static " +arg2)       ; popCommand.append("\n");
            popCommand.append("    @SP")                        ; popCommand.append("\n");
            popCommand.append("    M=M-1")                      ; popCommand.append("\n");
            popCommand.append("    A=M")                        ; popCommand.append("\n");
            popCommand.append("    D=M")                        ; popCommand.append("\n");
            popCommand.append("    @" +prefix+ "." +arg2)       ; popCommand.append("\n");
            popCommand.append("    M=D")                        ; popCommand.append("\n\n");
        } else{
            popCommand.append("    // pop " +arg1+ " " +arg2 )  ; popCommand.append("\n");
            popCommand.append("    @" +arg2 )                   ; popCommand.append("\n");
            popCommand.append("    D=A")                        ; popCommand.append("\n");
            popCommand.append("    @" +memorySegment.get(arg1)) ; popCommand.append("\n");
            popCommand.append("    D=M+D")                      ; popCommand.append("\n");
            popCommand.append("    @addr")                      ; popCommand.append("\n");
            popCommand.append("    M=D")                        ; popCommand.append("\n");
            popCommand.append("    @SP")                        ; popCommand.append("\n");
            popCommand.append("    M=M-1")                      ; popCommand.append("\n");
            popCommand.append("    A=M")                        ; popCommand.append("\n");
            popCommand.append("    D=M")                        ; popCommand.append("\n");
            popCommand.append("    @addr")                      ; popCommand.append("\n");
            popCommand.append("    A=M")                        ; popCommand.append("\n");
            popCommand.append("    M=D")                        ; popCommand.append("\n\n");
        }
        return popCommand.toString();
    }
    private String generateAdd(){
        StringBuilder addCommand = new StringBuilder();
        addCommand.append("    // add: pop (y,x), push (x+y)\n");
        addCommand.append("    @SP")                        ; addCommand.append("\n");
        addCommand.append("    M=M-1")                      ; addCommand.append("\n");
        addCommand.append("    A=M")                        ; addCommand.append("\n");
        addCommand.append("    D=M")                        ; addCommand.append("\n");
        addCommand.append("    @SP")                        ; addCommand.append("\n");
        addCommand.append("    M=M-1")                      ; addCommand.append("\n");
        addCommand.append("    A=M")                        ; addCommand.append("\n");
        addCommand.append("    M=M+D")                      ; addCommand.append("\n");
        addCommand.append("    @SP")                        ; addCommand.append("\n");
        addCommand.append("    M=M+1")                      ; addCommand.append("\n\n");
        return addCommand.toString();
    }
    private String generateSub(){
        StringBuilder subCommand = new StringBuilder();
        subCommand.append("    // sub: pop (y,x), push (x-y)\n");
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
    private String generateAnd(){
        StringBuilder andCommand = new StringBuilder();
        andCommand.append("    // and: pop (y,x), push (xANDy)\n");
        andCommand.append("    @SP")                        ; andCommand.append("\n");
        andCommand.append("    M=M-1")                      ; andCommand.append("\n");
        andCommand.append("    A=M")                        ; andCommand.append("\n");
        andCommand.append("    D=M")                        ; andCommand.append("\n");
        andCommand.append("    @SP")                        ; andCommand.append("\n");
        andCommand.append("    M=M-1")                      ; andCommand.append("\n");
        andCommand.append("    A=M")                        ; andCommand.append("\n");
        andCommand.append("    M=M&D")                      ; andCommand.append("\n");
        andCommand.append("    @SP")                        ; andCommand.append("\n");
        andCommand.append("    M=M+1")                      ; andCommand.append("\n\n");
        return andCommand.toString();
    }
    private String generateOr(){
        StringBuilder orCommand = new StringBuilder();
        orCommand.append("    // or: pop (y,x), push (xORy)\n");
        orCommand.append("    @SP")                        ; orCommand.append("\n");
        orCommand.append("    M=M-1")                      ; orCommand.append("\n");
        orCommand.append("    A=M")                        ; orCommand.append("\n");
        orCommand.append("    D=M")                        ; orCommand.append("\n");
        orCommand.append("    @SP")                        ; orCommand.append("\n");
        orCommand.append("    M=M-1")                      ; orCommand.append("\n");
        orCommand.append("    A=M")                        ; orCommand.append("\n");
        orCommand.append("    M=M|D")                      ; orCommand.append("\n");
        orCommand.append("    @SP")                        ; orCommand.append("\n");
        orCommand.append("    M=M+1")                      ; orCommand.append("\n\n");
        return orCommand.toString();
    }

    private String generateNeg(){
        StringBuilder negCommand = new StringBuilder();
        negCommand.append("    // neg: pop (y), push (-y)\n");
        negCommand.append("    @SP")                        ; negCommand.append("\n");
        negCommand.append("    M=M-1")                      ; negCommand.append("\n");
        negCommand.append("    A=M")                        ; negCommand.append("\n");
        negCommand.append("    M=-M")                       ; negCommand.append("\n");
        negCommand.append("    @SP")                        ; negCommand.append("\n");
        negCommand.append("    M=M+1")                      ; negCommand.append("\n\n");
        return negCommand.toString();
    }
    private String generateNot(){
        StringBuilder negCommand = new StringBuilder();
        negCommand.append("    // not: pop (y), push (!y)\n");
        negCommand.append("    @SP")                        ; negCommand.append("\n");
        negCommand.append("    M=M-1")                      ; negCommand.append("\n");
        negCommand.append("    A=M")                        ; negCommand.append("\n");
        negCommand.append("    M=!M")                       ; negCommand.append("\n");
        negCommand.append("    @SP")                        ; negCommand.append("\n");
        negCommand.append("    M=M+1")                      ; negCommand.append("\n\n");
        return negCommand.toString();
    }

    public String getOutputAsmCode() {
        return outputAsmCode;
    }

    private String generateEq(){
        // each call generate unique labels
        int id = index++;
        StringBuilder eqCommand = new StringBuilder();
        eqCommand.append("    // eq: pop (y,x), push (x==y)\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    M=M-1")                      ; eqCommand.append("\n");
        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
        eqCommand.append("    D=M")                        ; eqCommand.append("\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    M=M-1")                      ; eqCommand.append("\n");
        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
        eqCommand.append("    D=M-D")                      ; eqCommand.append("\n");
        eqCommand.append("    @EQUAL_" +id)                ; eqCommand.append("\n");
        eqCommand.append("    D;JEQ")                      ; eqCommand.append("\n");
        eqCommand.append("(NOT_EQUAL_" +id+ ")")           ; eqCommand.append("\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
        eqCommand.append("    M=0")                        ; eqCommand.append("\n");
        eqCommand.append("    @END_COND_" +id)             ; eqCommand.append("\n");
        eqCommand.append("    0;JMP")                      ; eqCommand.append("\n");
        eqCommand.append("(EQUAL_" +id+ ")")               ; eqCommand.append("\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
        eqCommand.append("    M=-1")                       ; eqCommand.append("\n");
        eqCommand.append("(END_COND_" +id+ ")")            ; eqCommand.append("\n");
        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
        eqCommand.append("    M=M+1")                      ; eqCommand.append("\n\n");
        return eqCommand.toString();
    }
    private String generateGt(){
        // each call generate unique labels
        int id = index++;
        StringBuilder gtCommand = new StringBuilder();
        gtCommand.append("    // gt: pop (y,x), push (x>y)\n");
        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
        gtCommand.append("    M=M-1")                      ; gtCommand.append("\n");
        gtCommand.append("    A=M")                        ; gtCommand.append("\n");
        gtCommand.append("    D=M")                        ; gtCommand.append("\n");
        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
        gtCommand.append("    M=M-1")                      ; gtCommand.append("\n");
        gtCommand.append("    A=M")                        ; gtCommand.append("\n");
        gtCommand.append("    D=M-D")                      ; gtCommand.append("\n");
        gtCommand.append("    @GT_" +id)                ; gtCommand.append("\n");
        gtCommand.append("    D;JGT")                       ; gtCommand.append("\n");
        gtCommand.append("(NOT_GT_" +id+ ")")           ; gtCommand.append("\n");
        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
        gtCommand.append("    A=M")                        ; gtCommand.append("\n");
        gtCommand.append("    M=0")                        ; gtCommand.append("\n");
        gtCommand.append("    @END_COND_" +id)             ; gtCommand.append("\n");
        gtCommand.append("    0;JMP")                      ; gtCommand.append("\n");
        gtCommand.append("(GT_" +id+ ")")                  ; gtCommand.append("\n");
        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
        gtCommand.append("    A=M")                        ; gtCommand.append("\n");
        gtCommand.append("    M=-1")                       ; gtCommand.append("\n");
        gtCommand.append("(END_COND_" +id+ ")")            ; gtCommand.append("\n");
        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
        gtCommand.append("    M=M+1")                      ; gtCommand.append("\n\n");
        return gtCommand.toString();
    }
    private String generateLt(){
        // each call generate unique labels
        int id = index++;
        StringBuilder ltCommand = new StringBuilder();
        ltCommand.append("    // lt: pop (y,x), push (x<y)\n");
        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
        ltCommand.append("    M=M-1")                      ; ltCommand.append("\n");
        ltCommand.append("    A=M")                        ; ltCommand.append("\n");
        ltCommand.append("    D=M")                        ; ltCommand.append("\n");
        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
        ltCommand.append("    M=M-1")                      ; ltCommand.append("\n");
        ltCommand.append("    A=M")                        ; ltCommand.append("\n");
        ltCommand.append("    D=M-D")                      ; ltCommand.append("\n");
        ltCommand.append("    @LT_" +id)                   ; ltCommand.append("\n");
        ltCommand.append("    D;JLT")                      ; ltCommand.append("\n");
        ltCommand.append("(NOT_LT_" +id+ ")")              ; ltCommand.append("\n");
        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
        ltCommand.append("    A=M")                        ; ltCommand.append("\n");
        ltCommand.append("    M=0")                        ; ltCommand.append("\n");
        ltCommand.append("    @END_COND_" +id)             ; ltCommand.append("\n");
        ltCommand.append("    0;JMP")                      ; ltCommand.append("\n");
        ltCommand.append("(LT_" +id+ ")")                  ; ltCommand.append("\n");
        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
        ltCommand.append("    A=M")                        ; ltCommand.append("\n");
        ltCommand.append("    M=-1")                       ; ltCommand.append("\n");
        ltCommand.append("(END_COND_" +id+ ")")            ; ltCommand.append("\n");
        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
        ltCommand.append("    M=M+1")                      ; ltCommand.append("\n\n");
        return ltCommand.toString();
    }
    private String generateLabel(String label){
        StringBuilder labelCommand = new StringBuilder();
        labelCommand.append("    // generate label\n");
        labelCommand.append("(" +label+ ")")               ; labelCommand.append("\n\n");
        return labelCommand.toString();
    }
    private String generateGoto(String destination){
        StringBuilder gotoCommand = new StringBuilder();
        gotoCommand.append("    // generate goto\n");
        gotoCommand.append("    @" +destination)           ; gotoCommand.append("\n");
        gotoCommand.append("    0;JMP")                    ; gotoCommand.append("\n\n");
        return gotoCommand.toString();
    }

    private String generateIfGoto(String destination){
        // pop and eval *(SP-1) , JNE = jump if not false
        // 0  = false (0000000000000000)
        // -1 = true  (1111111111111111)
        StringBuilder ifGotoCommand = new StringBuilder();
        ifGotoCommand.append("    // generate if-goto\n");
        ifGotoCommand.append("    @SP")                    ; ifGotoCommand.append("\n");
        ifGotoCommand.append("    M=M-1")                  ; ifGotoCommand.append("\n");
        ifGotoCommand.append("    A=M")                  ; ifGotoCommand.append("\n");
        ifGotoCommand.append("    D=M")                    ; ifGotoCommand.append("\n");
        ifGotoCommand.append("    @" +destination)         ; ifGotoCommand.append("\n");
        ifGotoCommand.append("    D;JNE")                  ; ifGotoCommand.append("\n\n");
        return ifGotoCommand.toString();
    }

    // function is a label,
    // needs to initialize nVars to '0'
    private String generateFunction(String name, Integer nVars){
        StringBuilder functionCommand = new StringBuilder();
        functionCommand.append("    // generate function with " +nVars+ " local vars\n");
        functionCommand.append("(" +name+ ")");                 functionCommand.append("\n");
        functionCommand.append("    @" +nVars);                       functionCommand.append("\n");
        functionCommand.append("    D=A");                      functionCommand.append("\n");

        functionCommand.append("    @nvars_" +name);            functionCommand.append("\n");
        functionCommand.append("    M=D");                      functionCommand.append("\n");
        functionCommand.append("    @counter_" +name);          functionCommand.append("\n");
        functionCommand.append("    M=0");                      functionCommand.append("\n");

        // init local vars to 0
        functionCommand.append("(" +name+ "$init_locals)"); functionCommand.append("\n");
        functionCommand.append("    @counter_" +name);          functionCommand.append("\n");
        functionCommand.append("    D=M");                      functionCommand.append("\n");
        functionCommand.append("    @nvars_" +name);            functionCommand.append("\n");
        functionCommand.append("    D=M-D");                    functionCommand.append("\n");

        functionCommand.append("    @" +name+ "$end_init_locals");   functionCommand.append("\n");
        functionCommand.append("    D;JEQ");                    functionCommand.append("\n");
        functionCommand.append("    @counter_" +name);          functionCommand.append("\n");
        functionCommand.append("    M=M+1");                    functionCommand.append("\n\n");

        // var[n] = 0
        functionCommand.append("    @0");                       functionCommand.append("\n");
        functionCommand.append("    D=A");                      functionCommand.append("\n");
        functionCommand.append("    @SP");                      functionCommand.append("\n");
        functionCommand.append("    A=M");                      functionCommand.append("\n");
        functionCommand.append("    M=D");                      functionCommand.append("\n");
        functionCommand.append("    @SP");                      functionCommand.append("\n");
        functionCommand.append("    M=M+1");                    functionCommand.append("\n\n");
        // done var[n] = 0

        functionCommand.append("    @" +name+ "$init_locals");  functionCommand.append("\n");
        // repeat nVar times
        functionCommand.append("    0;JMP");                    functionCommand.append("\n");

        functionCommand.append("("+name+ "$end_init_locals)");  functionCommand.append("\n\n");
        return functionCommand.toString();
    }


    private String generateCall(String functionName, Integer nArgs){
        Integer argOffset = 5 + nArgs;
        // 1. push returnAddress
        // 2. push LCL
        // 3. push ARG
        // 4. push THIS
        // 5. push THAT

        int id = index++;
        StringBuilder callCommand = new StringBuilder();
        callCommand.append("    // generate call " +functionName+ " " +nArgs+ " (id: " +id+ ")\n");
        callCommand.append("    @" +functionName+ "$ret." +id)              ; callCommand.append("\n");
        callCommand.append("    D=A")                    ; callCommand.append("\n");
        callCommand.append(pushValue())                  ; callCommand.append("\n");
        callCommand.append("    @LCL")                   ; callCommand.append("\n");
        callCommand.append("    D=M")                    ; callCommand.append("\n");
        callCommand.append(pushValue())                  ; callCommand.append("\n");
        callCommand.append("    @ARG")                   ; callCommand.append("\n");
        callCommand.append("    D=M")                    ; callCommand.append("\n");
        callCommand.append(pushValue())                  ; callCommand.append("\n");
        callCommand.append("    @THIS")                  ; callCommand.append("\n");
        callCommand.append("    D=M")                    ; callCommand.append("\n");
        callCommand.append(pushValue())                  ; callCommand.append("\n");
        callCommand.append("    @THAT")                  ; callCommand.append("\n");
        callCommand.append("    D=M")                    ; callCommand.append("\n");
        callCommand.append(pushValue())                  ; callCommand.append("\n");
        // set LCL for the callee
        callCommand.append("    @SP")                    ; callCommand.append("\n");
        callCommand.append("    D=M")                    ; callCommand.append("\n");
        callCommand.append("    @LCL")                   ; callCommand.append("\n");
        callCommand.append("    M=D")                    ; callCommand.append("\n");
        // ARG of the callee is SP - 5 pushed cells - num of args
        callCommand.append("    @" +argOffset)           ; callCommand.append("\n");
        callCommand.append("    D=A")                    ; callCommand.append("\n");
        callCommand.append("    @SP")                    ; callCommand.append("\n");
        callCommand.append("    A=M")                    ; callCommand.append("\n");
        callCommand.append("    D=A-D")                  ; callCommand.append("\n");
        callCommand.append("    @ARG")                   ; callCommand.append("\n");
        callCommand.append("    M=D")                    ; callCommand.append("\n");

        // jump to function
        callCommand.append("    @" +functionName)        ; callCommand.append("\n");
        callCommand.append("    0;JMP")                    ; callCommand.append("\n");

        // set return label to get jumped here when function returns
        callCommand.append("(" +functionName+ "$ret." +id+ ")")             ; callCommand.append("\n\n");
        return callCommand.toString();
    }

    private String generateReturn(){
        // 1. save returnAddress
        // 2. restore LCL
        // 3. restore ARG
        // 4. restore THIS
        // 5. restore THAT
        // jump

        int id = index++;
        StringBuilder returnCommand = new StringBuilder();
        returnCommand.append("    // generate return\n")   ;

        // save: endFrame = LCL
        returnCommand.append("    @LCL")                   ; returnCommand.append("\n");
        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
        returnCommand.append("    M=D")                    ; returnCommand.append("\n");

        // save: retAddr = *(endFrame-5) // TODO:check logic !
        returnCommand.append("    @5")                     ; returnCommand.append("\n");
        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
        returnCommand.append("    @retAddr")               ; returnCommand.append("\n");
        returnCommand.append("    M=D")                    ; returnCommand.append("\n");

        // copy return value to ARG ( will replace ARG0 with return value )
        returnCommand.append("    @SP")                    ; returnCommand.append("\n");
        returnCommand.append("    M=M-1")                  ; returnCommand.append("\n");
        returnCommand.append("    A=M")                    ; returnCommand.append("\n");
        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
        returnCommand.append("    @ARG")                   ; returnCommand.append("\n");
        returnCommand.append("    A=M")                    ; returnCommand.append("\n");
        returnCommand.append("    M=D")                    ; returnCommand.append("\n");

        // sp = ARG+1
        returnCommand.append("    @ARG")                   ; returnCommand.append("\n");
        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
        returnCommand.append("    @SP")                    ; returnCommand.append("\n");
        returnCommand.append("    M=D+1")                  ; returnCommand.append("\n");

        // restore THAT
        returnCommand.append("    @1")                     ; returnCommand.append("\n");
        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
        returnCommand.append("    @THAT")                  ; returnCommand.append("\n");
        returnCommand.append("    M=D")                    ; returnCommand.append("\n");

        // restore THIS
        returnCommand.append("    @2")                     ; returnCommand.append("\n");
        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
        returnCommand.append("    @THIS")                  ; returnCommand.append("\n");
        returnCommand.append("    M=D")                    ; returnCommand.append("\n");

        // restore ARG
        returnCommand.append("    @3")                     ; returnCommand.append("\n");
        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
        returnCommand.append("    @ARG")                   ; returnCommand.append("\n");
        returnCommand.append("    M=D")                    ; returnCommand.append("\n");

        // restore LCL
        returnCommand.append("    @4")                     ; returnCommand.append("\n");
        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
        returnCommand.append("    @LCL")                   ; returnCommand.append("\n");
        returnCommand.append("    M=D")                    ; returnCommand.append("\n");

        // return to caller after setup back the environment
        returnCommand.append("    @retAddr")               ; returnCommand.append("\n");
        returnCommand.append("    A=M")                    ; returnCommand.append("\n");
        returnCommand.append("    0;JMP")                  ; returnCommand.append("\n");

        return returnCommand.toString();
    }

    private String pushValue(){
        // pushing to stack and sp++
        StringBuilder sb = new StringBuilder();
        sb.append("    @SP")                        ; sb.append("\n");
        sb.append("    A=M")                        ; sb.append("\n");
        sb.append("    M=D")                        ; sb.append("\n");
        sb.append("    @SP")                        ; sb.append("\n");
        sb.append("    M=M+1")                      ; sb.append("\n\n");
        return sb.toString();
    }


    public void writeFilenameComment(String filename){
        outputAsmCode += "\n\n//\n// FILE: " + filename + "\n//\n\n";
    }

    public void writeInit(){
        // init sp=256
        // calls Sys.init()
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n//\n// BOOTSTRAP CODE\n//\n\n");
        sb.append("    @256")                    ; sb.append("\n");
        sb.append("    D=A")                     ; sb.append("\n");
        sb.append("    @SP")                     ; sb.append("\n");
        sb.append("    M=D")                     ; sb.append("\n");
        sb.append(generateCall("Sys.init", 0));
        outputAsmCode += sb.toString();
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
            if (command.getOperation().equals("eq"))  outputAsmCode += generateEq();
            if (command.getOperation().equals("gt"))  outputAsmCode += generateGt();
            if (command.getOperation().equals("lt"))  outputAsmCode += generateLt();
            if (command.getOperation().equals("and")) outputAsmCode += generateAnd();
            if (command.getOperation().equals("or"))  outputAsmCode += generateOr();
            if (command.getOperation().equals("not")) outputAsmCode += generateNot();

        }
        if  (command.getCommandType() == CommandType.C_LABEL) {
            outputAsmCode += generateLabel(command.getLabel());
        }
        if  (command.getCommandType() == CommandType.C_GOTO) {
            outputAsmCode += generateGoto(command.getJumpDestination());
        }
        if  (command.getCommandType() == CommandType.C_IF) {
            outputAsmCode += generateIfGoto(command.getJumpDestination());
        }
        if  (command.getCommandType() == CommandType.C_FUNCTION) {
            outputAsmCode += generateFunction(command.getFunctionName(),
                                              command.getLocalVars());
        }
        if  (command.getCommandType() == CommandType.C_CALL) {
            outputAsmCode += generateCall(command.getFunctionName(),
                    command.getnArgs());
        }
        if  (command.getCommandType() == CommandType.C_RETURN) {
            outputAsmCode += generateReturn();
        }
    }
}


