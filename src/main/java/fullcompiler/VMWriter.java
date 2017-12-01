package fullcompiler;

/**
 * Created by chenchuk on 10/21/17.
 */
public class VMWriter {

    private String padder = "";
    private String vmCode;
    private String compiledXml;
    private String className;

    private String subName;
    private Integer subArgs;

    private SymbolTable classVarsST;
    private SymbolTable subVarsST;


    private String filename ; // should be passed in

    // TODO:
    //public writePush(segment[CONST,ARG,LOCAL,STATIC,THIS,THAT,POINTER,TEMP] , int index){}

    //public writePop(segment[CONST,ARG,LOCAL,STATIC,THIS,THAT,POINTER,TEMP] , int index){


    // write a push this to pass this as arg
    public void writePushThis() {
        vmCode += String.format("push pointer 0\n");
    }



    // write a push command after lookup in both ST's
    public void writePush(String id){
        if (subVarsST.getVar(id) != null){
            vmCode += String.format("push %s %d\n",
                    subVarsST.KindOf(id),
                    subVarsST.IndexOf(id));
        } else if (classVarsST.getVar(id) != null){
            String kind = classVarsST.KindOf(id);
            // for methods. emitting this after aligning this pointer to the field vars
            if (kind.equals("field")){ kind = "this"; }
            vmCode += String.format("push %s %d\n",
                    kind,
                    classVarsST.IndexOf(id));
        } else {
            System.out.println("error: undeclared variable used: " +id);
            //System.exit(1);
        }
    }
    // write a pop command after lookup in both ST's
    public void writePop(String id){
        if (subVarsST.getVar(id) != null){
            vmCode += String.format("pop %s %d\n",
                    subVarsST.KindOf(id),
                    subVarsST.IndexOf(id));
        } else if (classVarsST.getVar(id) != null){
            String kind = classVarsST.KindOf(id);
            // for constructors. emitting this after aligning this pointer to the field vars
            if (kind.equals("field")){ kind = "this"; }
            vmCode += String.format("pop %s %d\n",
                            kind,
                            classVarsST.IndexOf(id));
        } else {
            System.out.println("error: assignment to an undeclared variable: " +id);
            //System.exit(1);
        }
    }

    //public void writeFunction(int subLocalVars, Boolean isConstructor){
    public void writeFunction(int subLocalVars, String subType){
        if (subType.equals("constructor")){
            // ask alloc for object ( size = num of field vars )
            int numOfFieldVars = classVarsST.varCount("field");
            vmCode += String.format("function %s.%s %s\n", className, subName, subLocalVars);
            vmCode += String.format("push constant %s\n", numOfFieldVars);
            vmCode += String.format("call Memory.alloc 1\n");
            vmCode += String.format("pop pointer 0\n");
        } else if  (subType.equals("method")){
            vmCode += String.format("function %s.%s %s\n", className, subName, subLocalVars);
            // set 'this' pointer to the current object
            vmCode += String.format("push argument 0\n");
            vmCode += String.format("pop pointer 0\n");
        } else {
            vmCode += String.format("function %s.%s %s\n", className, subName, subLocalVars);
        }

    }

    // get the final generated vm code
    public String getCompiledVmCode() {
        return vmCode;
    }

//    public void writeVoidFunctionCall(String id, int numOfArgs){
//        vmCode += String.format("call %s %s\n", id, numOfArgs);
//        vmCode += String.format("pop temp 0\n");
////        vmCode += String.format("push constant 0\n");
////        vmCode += String.format("return\n");
   // }
    public void writeDummyPop(){
        vmCode += String.format("pop temp 0\n");
    }




    public void writeFunctionCall(String id, int numOfArgs){
        vmCode += String.format("call %s %s\n", id, numOfArgs);
    }

    public void writeMethodCall(String id, String fullname, int numOfArgs){
        //String varName = id.split(".")[0];
        vmCode += String.format("push %s %s\n",
                subVarsST.getVar(id).getKind(),
                subVarsST.IndexOf(id));
        vmCode += String.format("call %s %s\n", fullname, numOfArgs);
    }

//    public void writeLocalMethodCall(String fullname, int numOfArgs){
//        //String varName = id.split(".")[0];
//        vmCode += String.format("push pointer 0\n");
//        //vmCode += String.format("call %s %s\n", fullname, numOfArgs);
//    }

    public void writeIntegerTerm(String term){
        vmCode += String.format("push constant %s\n",term);
    }

    // true = -1
    public void writeTrueTerm(){
        vmCode += String.format("push constant 1\n");
        vmCode += String.format("neg\n");
    }
    public void writeFalseTerm(){
        vmCode += String.format("push constant 0\n");
    }
    public void writeThisTerm() {
        vmCode += String.format("push pointer 0\n");
    }


    public void writeExpression(String op){
        if (op.equals("+")) vmCode += String.format("add\n");
        if (op.equals("-")) vmCode += String.format("sub\n");
        if (op.equals("*")) vmCode += String.format("call Math.multiply 2\n");
        if (op.equals("/")) vmCode += String.format("call Math.divide 2\n");
        if (op.equals("&")) vmCode += String.format("and\n");
        if (op.equals("|")) vmCode += String.format("or\n");
        if (op.equals("<")) vmCode += String.format("lt\n");
        if (op.equals(">")) vmCode += String.format("gt\n");
        if (op.equals("=")) vmCode += String.format("eq\n");
    }
    public void writeUnary(String op){
        if (op.equals("~")) vmCode += String.format("not\n");
        if (op.equals("-")) vmCode += String.format("neg\n");
    }

    // IF / IF-ELSE BLOCK
    //
    // if statement header
    public void writeIfStatement(Integer id){
        vmCode += String.format("not\n");
        vmCode += String.format("if-goto IFNOT_%d\n", id);
    }
    // header for else (if exist)
    public void writeElseClause(Integer id){
        vmCode += String.format("goto IFELSEEND_%d\n", id);
        vmCode += String.format("label IFNOT_%d\n", id);
    }
    //footer for else (if exists)
    public void writeElseEnd(Integer id){
        vmCode += String.format("label IFELSEEND_%d\n", id);
    }
    // end if (used only when no else clause)
    public void writeIfEnd(Integer id){
        vmCode += String.format("label IFNOT_%d\n", id);
    }

    // WHILE BLOCK
    //
    // while statement header (before exp to eval)
    public void writeWhileStatement(Integer id){
        vmCode += String.format("label WHILEEXP_%d\n", id);
    }
    // while statement header

    public void writeWhileStart(Integer id){
        vmCode += String.format("not\n");
        vmCode += String.format("if-goto WHILEEND_%d\n", id);
    }
    // end while
    public void writeWhileEnd(Integer id){
        vmCode += String.format("goto WHILEEXP_%d\n", id);
        vmCode += String.format("label WHILEEND_%d\n", id);
    }


    // RETURN ( with/without value)
    //
    // pushing dummy value before return if no expression exists ( return; )
    public void writeReturnDummyValue(){
        vmCode += String.format("push constant 0\n");
    }

    // constructors returns 'this' ref
    public void writeReturnThis(){
        vmCode += String.format("push pointer 0\n");
    }
    // return
    public void writeReturn(){
        vmCode += String.format("return\n");
    }

    // setting base array address
    public void writeArrayBaseAddress(String id){
        writePush(id);
    }

    // adding array base addr + offset
    public void writeArrayOffset(){
        vmCode += String.format("add\n");
    }

    // input into the array cell after align and offset
    public void writeToArray(){
        vmCode += String.format("pop temp 0\n");
        vmCode += String.format("pop pointer 1\n");
        vmCode += String.format("push temp 0\n");
        vmCode += String.format("pop that 0\n");
    }

    // allign the array and push
    public void writeArrayTerm(){
        vmCode += String.format("pop pointer 1\n");
        vmCode += String.format("push that 0\n");
    }

    // allign the array and push
    public void writeStringTerm(String string){
        vmCode += String.format("push constant %d\n", string.length());
        vmCode += String.format("call String.new 1\n");
        for (char c: string.toCharArray()) {

            vmCode += String.format("push constant %d\n", (int)c);
            vmCode += String.format("call String.appendChar 2\n");
        }
    }




    //
//    public void codeWrite(String expression){
//        if expression is number :
//            "push n"
//        if expression is var :
//            "push var"
//
//        if expression is exp1 op exp2 :
//            codeWrite exp1
//            codeWrite exp2
//            "op "
//        if expression is op exp :
//            codeWrite exp
//            "op "
//
//        if expression is f(exp1, exp2) :
//            codeWrite exp1
//            codeWrite exp2
//
//            "call f "
//
//
//    }



    //public writeArithmetic(command[ADD,SUB,NEG,EQ,GT,LT,AND,OR,NOT]){}

    //public writeLabel(String label){}
    //public writeGoto(String label){}
    //public writeIf(String label){}

    //public writeCall(String name, int nArgs){}
    //public writeFunction(String name, int nLocals){}
    //public writeReturn(){}
    //public close(){} // close file

    public Var getClassVar(String id){
        return classVarsST.getVar(id);
    }
    public Var getSubVar(String id){
        return subVarsST.getVar(id);
    }

    // return var type after lookup in both tables, null if not found
    public String varType (String id){
        if (getSubVar(id) != null){
            return getSubVar(id).getType() ;
        }
        if (getClassVar(id) != null){
            return getClassVar(id).getType() ;
        }
        return null;
    }

    // lookup a varname in both tables
    public Boolean hasVar(String id){
        return (getSubVar(id) != null || getClassVar(id) != null);
    }

    public void addClassVar(Var var){
        classVarsST.define(var);
    }
    public void addSubVar(Var var){
        subVarsST.define(var);
    }

    public void subInit(){
        // each function
        subVarsST = new SymbolTable();
    }

    public void setSubArgs(Integer subArgs){
        this.subArgs = subArgs;
    }

    public void setSubName(String subName){
        this.subName = subName;
    }


    private void emitVarIdenntifier(Var var, String defineOrUsed){
        compiledXml += padder + "<identifier> " +
                var + " [" + (defineOrUsed.equals("defined") ? "defined" : "used")+ "]" +
                " </identifier>\n";
    }
    // return type of a function/method/constructor
    private void emitClassIdenntifier(String className){
        compiledXml += padder + "<identifier> " +"class: "+className+ " </identifier>\n";
    }
    // sub name
    private void emitSubroutineIdenntifier(String subName){
        compiledXml += padder + "<identifier> " +"sub: "+subName+ " </identifier>\n";
    }

    // class name
    private void emitClass(String className){
        compiledXml += padder + "<identifier> " +className+ " </identifier>\n";
    }


    public VMWriter(){
        classVarsST = new SymbolTable();
        vmCode = "" ;
    }

    public void setClassName (String className){
        this.className = className;
    }

    public void  writeArithmetic(){
        // add to output string
        vmCode += "";
    }
    public String writePushPop(String command){
        // add to output string
        vmCode += "";
        return "";
    }
    public void close(){
        // close file
    }











//
//
//    private String generatePush(String arg1, Integer arg2){
//        StringBuilder pushCommand = new StringBuilder();
//
//        // const is direct value
//        if (arg1.equals("constant")) {
//            pushCommand.append("    // push constant " + arg2); pushCommand.append("\n");
//            pushCommand.append("    @" + arg2);                 pushCommand.append("\n");
//            pushCommand.append("    D=A");                      pushCommand.append("\n");
//            pushCommand.append("    @SP");                      pushCommand.append("\n");
//            pushCommand.append("    A=M");                      pushCommand.append("\n");
//            pushCommand.append("    M=D");                      pushCommand.append("\n");
//            pushCommand.append("    @SP");                      pushCommand.append("\n");
//            pushCommand.append("    M=M+1");                    pushCommand.append("\n\n");
//        } else if (arg1.equals("temp")) {
//            // temp segment is 8 Fixed addresses
//            Integer equivRegister = arg2 + 5; // temp 0 mapped to R5,.... temp 7 mapped to R12
//            String RX = "R" + equivRegister.toString();
//            pushCommand.append("    // push temp " +arg2+ " (" +RX+ ")") ; pushCommand.append("\n");
//            pushCommand.append("    @" +RX )                     ; pushCommand.append("\n");
//            pushCommand.append("    D=M")                        ; pushCommand.append("\n");
//            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
//            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
//            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
//            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
//            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
//        } else if (arg1.equals("pointer")) {
//            // pointer 0 -> THIS , pointer 1 -> THAT
//            String PTR = (arg2 == 0) ? "THIS" : "THAT";
//            pushCommand.append("    // push pointer " +arg2)     ; pushCommand.append("\n");
//            pushCommand.append("    @" +PTR )                    ; pushCommand.append("\n");
//            pushCommand.append("    D=M")                        ; pushCommand.append("\n");
//            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
//            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
//            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
//            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
//            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
//        } else if (arg1.equals("static")) {
//            // static used the [filename.x] convention
//            pushCommand.append("    // push static " +arg2)      ; pushCommand.append("\n");
//            pushCommand.append("    @" +prefix+ "." +arg2)     ; pushCommand.append("\n");
//            pushCommand.append("    D=M")                        ; pushCommand.append("\n");
//            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
//            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
//            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
//            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
//            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
//        } else {
//            // pointers of base addresses
//            pushCommand.append("    // push " +arg1+ " " +arg2 ) ; pushCommand.append("\n");
//            pushCommand.append("    @" +arg2 )                   ; pushCommand.append("\n");
//            pushCommand.append("    D=A")                        ; pushCommand.append("\n");
//            pushCommand.append("    @" +memorySegment.get(arg1)) ; pushCommand.append("\n");
//            pushCommand.append("    A=M+D")                      ; pushCommand.append("\n");
//            pushCommand.append("    D=M")                        ; pushCommand.append("\n");
//            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
//            pushCommand.append("    A=M")                        ; pushCommand.append("\n");
//            pushCommand.append("    M=D")                        ; pushCommand.append("\n");
//            pushCommand.append("    @SP")                        ; pushCommand.append("\n");
//            pushCommand.append("    M=M+1")                      ; pushCommand.append("\n\n");
//        }
//        return pushCommand.toString();
//    }
//    private String generatePop(String arg1, Integer arg2){
//        StringBuilder popCommand = new StringBuilder();
//        if (arg1.equals("temp")) {
//            // temp segment is 8 Fixed addresses
//            Integer equivRegister = arg2 + 5; // temp 0 mapped to R5,.... temp 7 mapped to R12
//            String RX = "R" + equivRegister.toString();
//            popCommand.append("    // pop temp " +arg2+ " (" +RX+ ")") ; popCommand.append("\n");
//            popCommand.append("    @SP")                        ; popCommand.append("\n");
//            popCommand.append("    M=M-1")                      ; popCommand.append("\n");
//            popCommand.append("    A=M")                        ; popCommand.append("\n");
//            popCommand.append("    D=M")                        ; popCommand.append("\n");
//            popCommand.append("    @" + RX)                     ; popCommand.append("\n");
//            popCommand.append("    M=D")                        ; popCommand.append("\n\n");
//        } else if (arg1.equals("pointer")) {
//            // pointer 0 -> THIS , pointer 1 -> THAT
//            String PTR = (arg2 == 0) ? "THIS" : "THAT";
//            popCommand.append("    // pop pointer " +arg2+ " (" +PTR+ ")") ; popCommand.append("\n");
//            popCommand.append("    @SP")                        ; popCommand.append("\n");
//            popCommand.append("    M=M-1")                      ; popCommand.append("\n");
//            popCommand.append("    A=M")                        ; popCommand.append("\n");
//            popCommand.append("    D=M")                        ; popCommand.append("\n");
//            popCommand.append("    @" + PTR)                    ; popCommand.append("\n");
//            popCommand.append("    M=D")                        ; popCommand.append("\n\n");
//        } else if (arg1.equals("static")) {
//            // static used the [filename.x] convention
//            popCommand.append("    // pop static " +arg2)       ; popCommand.append("\n");
//            popCommand.append("    @SP")                        ; popCommand.append("\n");
//            popCommand.append("    M=M-1")                      ; popCommand.append("\n");
//            popCommand.append("    A=M")                        ; popCommand.append("\n");
//            popCommand.append("    D=M")                        ; popCommand.append("\n");
//            popCommand.append("    @" +prefix+ "." +arg2)       ; popCommand.append("\n");
//            popCommand.append("    M=D")                        ; popCommand.append("\n\n");
//        } else{
//            popCommand.append("    // pop " +arg1+ " " +arg2 )  ; popCommand.append("\n");
//            popCommand.append("    @" +arg2 )                   ; popCommand.append("\n");
//            popCommand.append("    D=A")                        ; popCommand.append("\n");
//            popCommand.append("    @" +memorySegment.get(arg1)) ; popCommand.append("\n");
//            popCommand.append("    D=M+D")                      ; popCommand.append("\n");
//            popCommand.append("    @addr")                      ; popCommand.append("\n");
//            popCommand.append("    M=D")                        ; popCommand.append("\n");
//            popCommand.append("    @SP")                        ; popCommand.append("\n");
//            popCommand.append("    M=M-1")                      ; popCommand.append("\n");
//            popCommand.append("    A=M")                        ; popCommand.append("\n");
//            popCommand.append("    D=M")                        ; popCommand.append("\n");
//            popCommand.append("    @addr")                      ; popCommand.append("\n");
//            popCommand.append("    A=M")                        ; popCommand.append("\n");
//            popCommand.append("    M=D")                        ; popCommand.append("\n\n");
//        }
//        return popCommand.toString();
//    }
//    private String generateAdd(){
//        StringBuilder addCommand = new StringBuilder();
//        addCommand.append("    // add: pop (y,x), push (x+y)\n");
//        addCommand.append("    @SP")                        ; addCommand.append("\n");
//        addCommand.append("    M=M-1")                      ; addCommand.append("\n");
//        addCommand.append("    A=M")                        ; addCommand.append("\n");
//        addCommand.append("    D=M")                        ; addCommand.append("\n");
//        addCommand.append("    @SP")                        ; addCommand.append("\n");
//        addCommand.append("    M=M-1")                      ; addCommand.append("\n");
//        addCommand.append("    A=M")                        ; addCommand.append("\n");
//        addCommand.append("    M=M+D")                      ; addCommand.append("\n");
//        addCommand.append("    @SP")                        ; addCommand.append("\n");
//        addCommand.append("    M=M+1")                      ; addCommand.append("\n\n");
//        return addCommand.toString();
//    }
//    private String generateSub(){
//        StringBuilder subCommand = new StringBuilder();
//        subCommand.append("    // sub: pop (y,x), push (x-y)\n");
//        subCommand.append("    @SP")                        ; subCommand.append("\n");
//        subCommand.append("    M=M-1")                      ; subCommand.append("\n");
//        subCommand.append("    A=M")                        ; subCommand.append("\n");
//        subCommand.append("    D=M")                        ; subCommand.append("\n");
//        subCommand.append("    @SP")                        ; subCommand.append("\n");
//        subCommand.append("    M=M-1")                      ; subCommand.append("\n");
//        subCommand.append("    A=M")                        ; subCommand.append("\n");
//        subCommand.append("    M=M-D")                      ; subCommand.append("\n");
//        subCommand.append("    @SP")                        ; subCommand.append("\n");
//        subCommand.append("    M=M+1")                      ; subCommand.append("\n\n");
//        return subCommand.toString();
//    }
//    private String generateAnd(){
//        StringBuilder andCommand = new StringBuilder();
//        andCommand.append("    // and: pop (y,x), push (xANDy)\n");
//        andCommand.append("    @SP")                        ; andCommand.append("\n");
//        andCommand.append("    M=M-1")                      ; andCommand.append("\n");
//        andCommand.append("    A=M")                        ; andCommand.append("\n");
//        andCommand.append("    D=M")                        ; andCommand.append("\n");
//        andCommand.append("    @SP")                        ; andCommand.append("\n");
//        andCommand.append("    M=M-1")                      ; andCommand.append("\n");
//        andCommand.append("    A=M")                        ; andCommand.append("\n");
//        andCommand.append("    M=M&D")                      ; andCommand.append("\n");
//        andCommand.append("    @SP")                        ; andCommand.append("\n");
//        andCommand.append("    M=M+1")                      ; andCommand.append("\n\n");
//        return andCommand.toString();
//    }
//    private String generateOr(){
//        StringBuilder orCommand = new StringBuilder();
//        orCommand.append("    // or: pop (y,x), push (xORy)\n");
//        orCommand.append("    @SP")                        ; orCommand.append("\n");
//        orCommand.append("    M=M-1")                      ; orCommand.append("\n");
//        orCommand.append("    A=M")                        ; orCommand.append("\n");
//        orCommand.append("    D=M")                        ; orCommand.append("\n");
//        orCommand.append("    @SP")                        ; orCommand.append("\n");
//        orCommand.append("    M=M-1")                      ; orCommand.append("\n");
//        orCommand.append("    A=M")                        ; orCommand.append("\n");
//        orCommand.append("    M=M|D")                      ; orCommand.append("\n");
//        orCommand.append("    @SP")                        ; orCommand.append("\n");
//        orCommand.append("    M=M+1")                      ; orCommand.append("\n\n");
//        return orCommand.toString();
//    }
//
//    private String generateNeg(){
//        StringBuilder negCommand = new StringBuilder();
//        negCommand.append("    // neg: pop (y), push (-y)\n");
//        negCommand.append("    @SP")                        ; negCommand.append("\n");
//        negCommand.append("    M=M-1")                      ; negCommand.append("\n");
//        negCommand.append("    A=M")                        ; negCommand.append("\n");
//        negCommand.append("    M=-M")                       ; negCommand.append("\n");
//        negCommand.append("    @SP")                        ; negCommand.append("\n");
//        negCommand.append("    M=M+1")                      ; negCommand.append("\n\n");
//        return negCommand.toString();
//    }
//    private String generateNot(){
//        StringBuilder negCommand = new StringBuilder();
//        negCommand.append("    // not: pop (y), push (!y)\n");
//        negCommand.append("    @SP")                        ; negCommand.append("\n");
//        negCommand.append("    M=M-1")                      ; negCommand.append("\n");
//        negCommand.append("    A=M")                        ; negCommand.append("\n");
//        negCommand.append("    M=!M")                       ; negCommand.append("\n");
//        negCommand.append("    @SP")                        ; negCommand.append("\n");
//        negCommand.append("    M=M+1")                      ; negCommand.append("\n\n");
//        return negCommand.toString();
//    }
//
//    public String getVmCode() {
//        return vmCode;
//    }
//
//    private String generateEq(){
//        // each call generate unique labels
//        int id = index++;
//        StringBuilder eqCommand = new StringBuilder();
//        eqCommand.append("    // eq: pop (y,x), push (x==y)\n");
//        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
//        eqCommand.append("    M=M-1")                      ; eqCommand.append("\n");
//        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
//        eqCommand.append("    D=M")                        ; eqCommand.append("\n");
//        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
//        eqCommand.append("    M=M-1")                      ; eqCommand.append("\n");
//        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
//        eqCommand.append("    D=M-D")                      ; eqCommand.append("\n");
//        eqCommand.append("    @EQUAL_" +id)                ; eqCommand.append("\n");
//        eqCommand.append("    D;JEQ")                      ; eqCommand.append("\n");
//        eqCommand.append("(NOT_EQUAL_" +id+ ")")           ; eqCommand.append("\n");
//        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
//        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
//        eqCommand.append("    M=0")                        ; eqCommand.append("\n");
//        eqCommand.append("    @END_COND_" +id)             ; eqCommand.append("\n");
//        eqCommand.append("    0;JMP")                      ; eqCommand.append("\n");
//        eqCommand.append("(EQUAL_" +id+ ")")               ; eqCommand.append("\n");
//        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
//        eqCommand.append("    A=M")                        ; eqCommand.append("\n");
//        eqCommand.append("    M=-1")                       ; eqCommand.append("\n");
//        eqCommand.append("(END_COND_" +id+ ")")            ; eqCommand.append("\n");
//        eqCommand.append("    @SP")                        ; eqCommand.append("\n");
//        eqCommand.append("    M=M+1")                      ; eqCommand.append("\n\n");
//        return eqCommand.toString();
//    }
//    private String generateGt(){
//        // each call generate unique labels
//        int id = index++;
//        StringBuilder gtCommand = new StringBuilder();
//        gtCommand.append("    // gt: pop (y,x), push (x>y)\n");
//        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
//        gtCommand.append("    M=M-1")                      ; gtCommand.append("\n");
//        gtCommand.append("    A=M")                        ; gtCommand.append("\n");
//        gtCommand.append("    D=M")                        ; gtCommand.append("\n");
//        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
//        gtCommand.append("    M=M-1")                      ; gtCommand.append("\n");
//        gtCommand.append("    A=M")                        ; gtCommand.append("\n");
//        gtCommand.append("    D=M-D")                      ; gtCommand.append("\n");
//        gtCommand.append("    @GT_" +id)                ; gtCommand.append("\n");
//        gtCommand.append("    D;JGT")                       ; gtCommand.append("\n");
//        gtCommand.append("(NOT_GT_" +id+ ")")           ; gtCommand.append("\n");
//        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
//        gtCommand.append("    A=M")                        ; gtCommand.append("\n");
//        gtCommand.append("    M=0")                        ; gtCommand.append("\n");
//        gtCommand.append("    @END_COND_" +id)             ; gtCommand.append("\n");
//        gtCommand.append("    0;JMP")                      ; gtCommand.append("\n");
//        gtCommand.append("(GT_" +id+ ")")                  ; gtCommand.append("\n");
//        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
//        gtCommand.append("    A=M")                        ; gtCommand.append("\n");
//        gtCommand.append("    M=-1")                       ; gtCommand.append("\n");
//        gtCommand.append("(END_COND_" +id+ ")")            ; gtCommand.append("\n");
//        gtCommand.append("    @SP")                        ; gtCommand.append("\n");
//        gtCommand.append("    M=M+1")                      ; gtCommand.append("\n\n");
//        return gtCommand.toString();
//    }
//    private String generateLt(){
//        // each call generate unique labels
//        int id = index++;
//        StringBuilder ltCommand = new StringBuilder();
//        ltCommand.append("    // lt: pop (y,x), push (x<y)\n");
//        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
//        ltCommand.append("    M=M-1")                      ; ltCommand.append("\n");
//        ltCommand.append("    A=M")                        ; ltCommand.append("\n");
//        ltCommand.append("    D=M")                        ; ltCommand.append("\n");
//        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
//        ltCommand.append("    M=M-1")                      ; ltCommand.append("\n");
//        ltCommand.append("    A=M")                        ; ltCommand.append("\n");
//        ltCommand.append("    D=M-D")                      ; ltCommand.append("\n");
//        ltCommand.append("    @LT_" +id)                   ; ltCommand.append("\n");
//        ltCommand.append("    D;JLT")                      ; ltCommand.append("\n");
//        ltCommand.append("(NOT_LT_" +id+ ")")              ; ltCommand.append("\n");
//        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
//        ltCommand.append("    A=M")                        ; ltCommand.append("\n");
//        ltCommand.append("    M=0")                        ; ltCommand.append("\n");
//        ltCommand.append("    @END_COND_" +id)             ; ltCommand.append("\n");
//        ltCommand.append("    0;JMP")                      ; ltCommand.append("\n");
//        ltCommand.append("(LT_" +id+ ")")                  ; ltCommand.append("\n");
//        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
//        ltCommand.append("    A=M")                        ; ltCommand.append("\n");
//        ltCommand.append("    M=-1")                       ; ltCommand.append("\n");
//        ltCommand.append("(END_COND_" +id+ ")")            ; ltCommand.append("\n");
//        ltCommand.append("    @SP")                        ; ltCommand.append("\n");
//        ltCommand.append("    M=M+1")                      ; ltCommand.append("\n\n");
//        return ltCommand.toString();
//    }
//    private String generateLabel(String label){
//        StringBuilder labelCommand = new StringBuilder();
//        labelCommand.append("    // generate label\n");
//        labelCommand.append("(" +label+ ")")               ; labelCommand.append("\n\n");
//        return labelCommand.toString();
//    }
//    private String generateGoto(String destination){
//        StringBuilder gotoCommand = new StringBuilder();
//        gotoCommand.append("    // generate goto\n");
//        gotoCommand.append("    @" +destination)           ; gotoCommand.append("\n");
//        gotoCommand.append("    0;JMP")                    ; gotoCommand.append("\n\n");
//        return gotoCommand.toString();
//    }
//
//    private String generateIfGoto(String destination){
//        // pop and eval *(SP-1) , JNE = jump if not false
//        // 0  = false (0000000000000000)
//        // -1 = true  (1111111111111111)
//        StringBuilder ifGotoCommand = new StringBuilder();
//        ifGotoCommand.append("    // generate if-goto\n");
//        ifGotoCommand.append("    @SP")                    ; ifGotoCommand.append("\n");
//        ifGotoCommand.append("    M=M-1")                  ; ifGotoCommand.append("\n");
//        ifGotoCommand.append("    A=M")                  ; ifGotoCommand.append("\n");
//        ifGotoCommand.append("    D=M")                    ; ifGotoCommand.append("\n");
//        ifGotoCommand.append("    @" +destination)         ; ifGotoCommand.append("\n");
//        ifGotoCommand.append("    D;JNE")                  ; ifGotoCommand.append("\n\n");
//        return ifGotoCommand.toString();
//    }
//
//    // function is a label,
//    // needs to initialize nVars to '0'
//    private String generateFunction(String name, Integer nVars){
//        StringBuilder functionCommand = new StringBuilder();
//        functionCommand.append("    // generate function with " +nVars+ " local vars\n");
//        functionCommand.append("(" +name+ ")");                 functionCommand.append("\n");
//        functionCommand.append("    @" +nVars);                       functionCommand.append("\n");
//        functionCommand.append("    D=A");                      functionCommand.append("\n");
//
//        functionCommand.append("    @nvars_" +name);            functionCommand.append("\n");
//        functionCommand.append("    M=D");                      functionCommand.append("\n");
//        functionCommand.append("    @counter_" +name);          functionCommand.append("\n");
//        functionCommand.append("    M=0");                      functionCommand.append("\n");
//
//        // init local vars to 0
//        functionCommand.append("(" +name+ "$init_locals)"); functionCommand.append("\n");
//        functionCommand.append("    @counter_" +name);          functionCommand.append("\n");
//        functionCommand.append("    D=M");                      functionCommand.append("\n");
//        functionCommand.append("    @nvars_" +name);            functionCommand.append("\n");
//        functionCommand.append("    D=M-D");                    functionCommand.append("\n");
//
//        functionCommand.append("    @" +name+ "$end_init_locals");   functionCommand.append("\n");
//        functionCommand.append("    D;JEQ");                    functionCommand.append("\n");
//        functionCommand.append("    @counter_" +name);          functionCommand.append("\n");
//        functionCommand.append("    M=M+1");                    functionCommand.append("\n\n");
//
//        // var[n] = 0
//        functionCommand.append("    @0");                       functionCommand.append("\n");
//        functionCommand.append("    D=A");                      functionCommand.append("\n");
//        functionCommand.append("    @SP");                      functionCommand.append("\n");
//        functionCommand.append("    A=M");                      functionCommand.append("\n");
//        functionCommand.append("    M=D");                      functionCommand.append("\n");
//        functionCommand.append("    @SP");                      functionCommand.append("\n");
//        functionCommand.append("    M=M+1");                    functionCommand.append("\n\n");
//        // done var[n] = 0
//
//        functionCommand.append("    @" +name+ "$init_locals");  functionCommand.append("\n");
//        // repeat nVar times
//        functionCommand.append("    0;JMP");                    functionCommand.append("\n");
//
//        functionCommand.append("("+name+ "$end_init_locals)");  functionCommand.append("\n\n");
//        return functionCommand.toString();
//    }
//
//
//    private String generateCall(String functionName, Integer nArgs){
//        Integer argOffset = 5 + nArgs;
//        // 1. push returnAddress
//        // 2. push LCL
//        // 3. push ARG
//        // 4. push THIS
//        // 5. push THAT
//
//        int id = index++;
//        StringBuilder callCommand = new StringBuilder();
//        callCommand.append("    // generate call " +functionName+ " " +nArgs+ " (id: " +id+ ")\n");
//        callCommand.append("    @" +functionName+ "$ret." +id)              ; callCommand.append("\n");
//        callCommand.append("    D=A")                    ; callCommand.append("\n");
//        callCommand.append(pushValue())                  ; callCommand.append("\n");
//        callCommand.append("    @LCL")                   ; callCommand.append("\n");
//        callCommand.append("    D=M")                    ; callCommand.append("\n");
//        callCommand.append(pushValue())                  ; callCommand.append("\n");
//        callCommand.append("    @ARG")                   ; callCommand.append("\n");
//        callCommand.append("    D=M")                    ; callCommand.append("\n");
//        callCommand.append(pushValue())                  ; callCommand.append("\n");
//        callCommand.append("    @THIS")                  ; callCommand.append("\n");
//        callCommand.append("    D=M")                    ; callCommand.append("\n");
//        callCommand.append(pushValue())                  ; callCommand.append("\n");
//        callCommand.append("    @THAT")                  ; callCommand.append("\n");
//        callCommand.append("    D=M")                    ; callCommand.append("\n");
//        callCommand.append(pushValue())                  ; callCommand.append("\n");
//        // set LCL for the callee
//        callCommand.append("    @SP")                    ; callCommand.append("\n");
//        callCommand.append("    D=M")                    ; callCommand.append("\n");
//        callCommand.append("    @LCL")                   ; callCommand.append("\n");
//        callCommand.append("    M=D")                    ; callCommand.append("\n");
//        // ARG of the callee is SP - 5 pushed cells - num of args
//        callCommand.append("    @" +argOffset)           ; callCommand.append("\n");
//        callCommand.append("    D=A")                    ; callCommand.append("\n");
//        callCommand.append("    @SP")                    ; callCommand.append("\n");
//        callCommand.append("    A=M")                    ; callCommand.append("\n");
//        callCommand.append("    D=A-D")                  ; callCommand.append("\n");
//        callCommand.append("    @ARG")                   ; callCommand.append("\n");
//        callCommand.append("    M=D")                    ; callCommand.append("\n");
//
//        // jump to function
//        callCommand.append("    @" +functionName)        ; callCommand.append("\n");
//        callCommand.append("    0;JMP")                    ; callCommand.append("\n");
//
//        // set return label to get jumped here when function returns
//        callCommand.append("(" +functionName+ "$ret." +id+ ")")             ; callCommand.append("\n\n");
//        return callCommand.toString();
//    }
//
//    private String generateReturn(){
//        // 1. save returnAddress
//        // 2. restore LCL
//        // 3. restore ARG
//        // 4. restore THIS
//        // 5. restore THAT
//        // jump
//
//        int id = index++;
//        StringBuilder returnCommand = new StringBuilder();
//        returnCommand.append("    // generate return\n")   ;
//
//        // save: endFrame = LCL
//        returnCommand.append("    @LCL")                   ; returnCommand.append("\n");
//        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
//        returnCommand.append("    M=D")                    ; returnCommand.append("\n");
//
//        // save: retAddr = *(endFrame-5) // TODO:check logic !
//        returnCommand.append("    @5")                     ; returnCommand.append("\n");
//        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
//        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
//        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
//        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
//        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    @retAddr")               ; returnCommand.append("\n");
//        returnCommand.append("    M=D")                    ; returnCommand.append("\n");
//
//        // copy return value to ARG ( will replace ARG0 with return value )
//        returnCommand.append("    @SP")                    ; returnCommand.append("\n");
//        returnCommand.append("    M=M-1")                  ; returnCommand.append("\n");
//        returnCommand.append("    A=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    @ARG")                   ; returnCommand.append("\n");
//        returnCommand.append("    A=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    M=D")                    ; returnCommand.append("\n");
//
//        // sp = ARG+1
//        returnCommand.append("    @ARG")                   ; returnCommand.append("\n");
//        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    @SP")                    ; returnCommand.append("\n");
//        returnCommand.append("    M=D+1")                  ; returnCommand.append("\n");
//
//        // restore THAT
//        returnCommand.append("    @1")                     ; returnCommand.append("\n");
//        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
//        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
//        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
//        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
//        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    @THAT")                  ; returnCommand.append("\n");
//        returnCommand.append("    M=D")                    ; returnCommand.append("\n");
//
//        // restore THIS
//        returnCommand.append("    @2")                     ; returnCommand.append("\n");
//        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
//        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
//        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
//        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
//        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    @THIS")                  ; returnCommand.append("\n");
//        returnCommand.append("    M=D")                    ; returnCommand.append("\n");
//
//        // restore ARG
//        returnCommand.append("    @3")                     ; returnCommand.append("\n");
//        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
//        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
//        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
//        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
//        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    @ARG")                   ; returnCommand.append("\n");
//        returnCommand.append("    M=D")                    ; returnCommand.append("\n");
//
//        // restore LCL
//        returnCommand.append("    @4")                     ; returnCommand.append("\n");
//        returnCommand.append("    D=A")                    ; returnCommand.append("\n");
//        returnCommand.append("    @endFrame")              ; returnCommand.append("\n");
//        returnCommand.append("    D=M-D")                  ; returnCommand.append("\n");
//        returnCommand.append("    A=D")                    ; returnCommand.append("\n");
//        returnCommand.append("    D=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    @LCL")                   ; returnCommand.append("\n");
//        returnCommand.append("    M=D")                    ; returnCommand.append("\n");
//
//        // return to caller after setup back the environment
//        returnCommand.append("    @retAddr")               ; returnCommand.append("\n");
//        returnCommand.append("    A=M")                    ; returnCommand.append("\n");
//        returnCommand.append("    0;JMP")                  ; returnCommand.append("\n");
//
//        return returnCommand.toString();
//    }
//
//    private String pushValue(){
//        // pushing to stack and sp++
//        StringBuilder sb = new StringBuilder();
//        sb.append("    @SP")                        ; sb.append("\n");
//        sb.append("    A=M")                        ; sb.append("\n");
//        sb.append("    M=D")                        ; sb.append("\n");
//        sb.append("    @SP")                        ; sb.append("\n");
//        sb.append("    M=M+1")                      ; sb.append("\n\n");
//        return sb.toString();
//    }
//
//
//    public void writeFilenameComment(String filename){
//        vmCode += "\n\n//\n// FILE: " + filename + "\n//\n\n";
//    }
//
//    public void writeInit(){
//        // init sp=256
//        // calls Sys.init()
//        StringBuilder sb = new StringBuilder();
//        sb.append("\n\n//\n// BOOTSTRAP CODE\n//\n\n");
//        sb.append("    @256")                    ; sb.append("\n");
//        sb.append("    D=A")                     ; sb.append("\n");
//        sb.append("    @SP")                     ; sb.append("\n");
//        sb.append("    M=D")                     ; sb.append("\n");
//        sb.append(generateCall("Sys.init", 0));
//        vmCode += sb.toString();
//    }
//
//
//    public void addAsm(Command command) {
//        StringBuilder sb = new StringBuilder();
//        if (command.getCommandType() == CommandType.C_PUSH) {
//            vmCode += generatePush(command.getArg1(), command.getArg2());
//        }
//        if  (command.getCommandType() == CommandType.C_POP) {
//            vmCode += generatePop(command.getArg1(), command.getArg2());
//        }
//        if  (command.getCommandType() == CommandType.C_ARITHMETIC) {
//
//            if (command.getOperation().equals("add")) vmCode += generateAdd();
//            if (command.getOperation().equals("sub")) vmCode += generateSub();
//            if (command.getOperation().equals("neg")) vmCode += generateNeg();
//            if (command.getOperation().equals("eq"))  vmCode += generateEq();
//            if (command.getOperation().equals("gt"))  vmCode += generateGt();
//            if (command.getOperation().equals("lt"))  vmCode += generateLt();
//            if (command.getOperation().equals("and")) vmCode += generateAnd();
//            if (command.getOperation().equals("or"))  vmCode += generateOr();
//            if (command.getOperation().equals("not")) vmCode += generateNot();
//
//        }
//        if  (command.getCommandType() == CommandType.C_LABEL) {
//            vmCode += generateLabel(command.getLabel());
//        }
//        if  (command.getCommandType() == CommandType.C_GOTO) {
//            vmCode += generateGoto(command.getJumpDestination());
//        }
//        if  (command.getCommandType() == CommandType.C_IF) {
//            vmCode += generateIfGoto(command.getJumpDestination());
//        }
//        if  (command.getCommandType() == CommandType.C_FUNCTION) {
//            vmCode += generateFunction(command.getFunctionName(),
//                                              command.getLocalVars());
//        }
//        if  (command.getCommandType() == CommandType.C_CALL) {
//            vmCode += generateCall(command.getFunctionName(),
//                    command.getnArgs());
//        }
//        if  (command.getCommandType() == CommandType.C_RETURN) {
//            vmCode += generateReturn();
//        }
//    }
}


