package cmp;

/**
 * Created by chenchuk on 11/15/17.
 */
public class CompilationEngine {

    private JackTokenizer tokenizer;
    private String padder = "" ;

    public CompilationEngine (String filename) {
//        while (tokenizer.hasMoreTokens()) {
//            System.out.println(tokenizer.tokenType() + "-" + tokenizer.tokenVal());
//            tokenizer.advance();
//        }
        tokenizer = new JackTokenizer(filename);
        compileClass();

    }


    // NO JACK METHODS:
    // type
    // className
    // subroutineName
    // variableName
    // statements
    // soubroutineCall



    // Compiles a complete class.
    private void compileClass(){
        emitString("class");
        eat("class");
        eat(tokenizer.tokenVal());
        eat("{");
        //eat(tokenizer.tokenVal());
        //compileVarDec();
        CompileClassVarDec();
        CompileSubroutine();
        eat("}");
        emitBackString("class");
    }

    // Compiles a static declaration or a field declaration.
    private void CompileClassVarDec(){
        while (tokenizer.tokenVal().equals("static") ||
               tokenizer.tokenVal().equals("field")){
            emitString("classVarDec");
            eat(tokenizer.tokenVal());    // static/field
            eat(tokenizer.tokenVal());    // type
            eat(tokenizer.tokenVal());    // varname
            while (tokenizer.tokenVal().equals(",")){
                eat(",");
                eat(tokenizer.tokenVal());    // varname2, varname3, etc ....
//                emitBackString("classVarDec");
            }
            eat(";");           // ;.
            emitBackString("classVarDec");

        }
//        emitBackString("classVarDec");
    }


    // Compiles a complete method, function, or constructor.
    private void CompileSubroutine(){
        // subroutineDec: ('constructor' | 'function' | 'method') ('void' | type) subroutineName
        // '(' parameterList ')' subroutineBody
        while (tokenizer.tokenVal().equals("method") ||
               tokenizer.tokenVal().equals("function") ||
               tokenizer.tokenVal().equals("constructor")) {
            //System.out.println("fff=" +tokenizer.tokenVal());

            emitString("subroutineDec");
            eat(tokenizer.tokenVal());     // sub-type
            eat(tokenizer.tokenVal());     // sub-return-type (classname / void)
            eat(tokenizer.tokenVal());     // sub-name
            eat("(");
            compileParameterList();
            eat(")");
            emitString("subroutineBody");
            eat("{");

            // subroutineBody: '{' varDec* statements '}'
            if (tokenizer.tokenVal().equals("var")) {
                compileVarDec();
            }
            compileStatements();
            eat("}");
            emitBackString("subroutineBody");

            emitBackString("subroutineDec");
            //System.out.println("fff=" +tokenizer.tokenVal());
        }
    }

    // Compiles a (possibly empty) parameter list, not including the enclosing “()”.
    private void compileParameterList(){
        emitString("parameterList"); // prints also on empty list
//        if (tokenizer.tokenVal().equals(")")){return;} // empty list

        // type: 'int' | 'char' | 'boolean' | className
        while ( tokenizer.tokenVal().equals("int") ||
                tokenizer.tokenVal().equals("char") ||
                tokenizer.tokenVal().equals("boolean") ||
                tokenizer.tokenType().equals(TokenType.identifier)){

            eat(tokenizer.tokenVal());    // type
            eat(tokenizer.tokenVal());    // varname
            if (tokenizer.tokenVal().equals(",")){
                eat(",");
            } else {
                break;
            }
        }
        emitBackString("parameterList");
    }

    // Compiles a var declaration.
    // GRAMMER :
    // varDec: 'var' type varName (',' varName)* ';'
    private void compileVarDec(){
        if (!tokenizer.tokenVal().equals("var")){return;}// empty varDec list

        emitString("varDec");
        while (tokenizer.tokenVal().equals("var")){
            eat("var");         // var
            eat(tokenizer.tokenVal());    // type
            eat(tokenizer.tokenVal());    // varname
            eat(";");           // ;
        }
        emitBackString("varDec");
    }

    // Compiles a sequence of statements, not including the enclosing “{}”.
    private void compileStatements(){
        emitString("statements"); //
        while (tokenizer.tokenVal().equals("let") ||
                tokenizer.tokenVal().equals("if") ||
                tokenizer.tokenVal().equals("while") ||
                tokenizer.tokenVal().equals("do") ||
                tokenizer.tokenVal().equals("return")) {

            if (tokenizer.tokenVal().equals("let"))    { compileLet();    }
            if (tokenizer.tokenVal().equals("if"))     { compileIf();     }
            if (tokenizer.tokenVal().equals("while"))  { compileWhile();  }
            if (tokenizer.tokenVal().equals("do"))     { compileDo();     }
            if (tokenizer.tokenVal().equals("return")) { compileReturn(); }

        }
        emitBackString("statements"); //


//            System.out.println("--"+tokenizer.tokenVal());
//            switch (tokenizer.tokenVal()) {
//                case "let":    compileLet();
//                case "if":     compileIf();
//                case "while":  compileWhile();
//                case "do":     compileDo();
//                case "return": compileReturn();
//                default: System.out.println("error, unknown statement");
//            }

//        }

        //while (tokenizer.tokenVal().equals("let")){ compileLet();}

    }

    // Compiles a do statement.
    private void compileDo(){
        emitString("doStatement");
        eat("do");           // do
        eat(tokenizer.tokenVal());    // subname to call
        if (tokenizer.tokenVal().equals(".")) {
            eat(".");
            eat(tokenizer.tokenVal());    // identifier
        }
        eat("(");           // ;
        compileExpressionList();
        eat(")");           // ;
        eat(";");           // ;
        emitBackString("doStatement");

    }

    // Compiles a let statement.
    private void compileLet(){
        emitString("letStatement");
        // type: 'int' | 'char' | 'boolean' | className
        eat(tokenizer.tokenVal());    // let
        eat(tokenizer.tokenVal());    // identifier
        eat("=");           // =
        compileExpression();
        eat(";");           // ;
        emitBackString("letStatement");
    }

    // Compiles a while statement.
    private void compileWhile(){
        identForward();
        eat("while");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
    }

    // Compiles a return statement.
    private void compileReturn(){
        emitString("returnStatement");
        eat(tokenizer.tokenVal());    // return

        // expression if not 'return ;'
        if (!tokenizer.tokenVal().equals(";")){
            compileExpression();
        }
        eat(";");           // ;
        emitBackString("returnStatement");
    }

    // Compiles an if statement. possibly with a trailing else clause.
    private void compileIf(){
        emitString("ifStatement");
        eat("if");           // if
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
        if (tokenizer.tokenVal().equals("else")) {
            eat("{");
            compileStatements();
            eat("}");
        }
        emitBackString("ifStatement");
    }

    // Compiles an expression.
    private void compileExpression(){
        emitString("expression");
        compileTerm();
//        eat(tokenizer.tokenVal());    // assuming expr is just another identifier TODO: FIX
        emitBackString("expression");

    }

    // Compiles a term. This routine is faced with a slight difficulty when trying to decide
    // between some of the alternative parsing rules. Specifically, if the current token is an
    // identifier, the routine must distinguish between a variable, an array entry, and a
    // subroutine call. A single look-ahead token, which may be one of “[“, “(“, or “.”
    // suffices to distinguish between the three possibilities. Any other token is not part of
    // this term and should not be advanced over.
    private void compileTerm(){
        emitString("term");

//        if (tokenizer.tokenVal().equals("this")) {
//            eat("this");
//        }

        eat(tokenizer.tokenVal());    // assuming expr is just another identifier TODO: FIX

//        eat(tokenizer.tokenVal());    // the term
//
//        if (tokenizer.tokenVal().equals("[")){
//            eat("[");
//            compileExpression();
//            eat("]");
//        } else if (tokenizer.tokenVal().equals(".")){
//            eat(".");
//            eat(tokenizer.tokenVal());    // identifier
//
//        }
//
//
//
//
//        eat("do");           // do
//        eat("(");           // ;
//        eat(")");           // ;
//        eat(";");           // ;
        emitBackString("term");



    }

    // Compiles a (possibly empty) commaseparated list of expressions.
    // (expression (',' expression)* )?
    private void compileExpressionList(){
        emitString("expressionList");
        if (tokenizer.tokenVal().equals(")")) {
            emitBackString("expressionList");
            return;
        }
        compileExpression();
        while (tokenizer.tokenVal().equals(",")) {
            eat(",");
            compileExpression();
        }
        emitBackString("expressionList");
    }





















    // identing right the XML output each method call
    private void identForward(){
        padder += "  ";
    }

    // identing left the XML output each method return
    private void identBackward(){
        if (padder.length() >= 2){
            padder = padder.substring(0,padder.length()-2);
        }
    }

    // XML output + identation forword
    private void emitString(String xmlElement){
        System.out.println(padder + "<" +xmlElement+ ">");
        identForward();
    }

    // XML output + identation backword
    private void emitBackString(String xmlElement){
        identBackward();
        System.out.println(padder + "</" +xmlElement+ ">");

    }

    // comparing input to fixed string
    private boolean eat (String tokenVal){
        if (tokenizer.tokenVal().equals(tokenVal)){
            System.out.println(padder + "<" +tokenizer.tokenType()+ "> " +
                                             tokenizer.tokenVal()+
                                        " </"+tokenizer.tokenType()+ ">");

            if (tokenizer.hasMoreTokens()){
                tokenizer.advance();
            }
            return true;
        }
        else {
            System.out.println("error: expecting " + tokenVal + ", found: " + tokenizer.tokenVal());
            return false;
        }
    }

//    // comparing input token
//    private boolean eat (JackToken token){
//        if (tokenizer.tokenType().equals(TokenType.identifier)){
//            System.out.println(padder + "<" +tokenizer.tokenType()+ ">" +
//                                            tokenizer.tokenVal()+
//                                            "</"+tokenizer.tokenType()+ ">");
//        }
//        if (tokenizer.tokenVal().equals(tokenVal)){
//            System.out.println(padder + "<" +tokenizer.tokenType()+ ">" +
//                    tokenizer.tokenVal()+
//                    "</"+tokenizer.tokenType()+ ">");
//
//            tokenizer.advance();
//            return true;
//        }
//        else {
//            System.out.println("error: expecting " + tokenVal);
//            return false;
//        }
//    }
//

//    private void eat (String tokenVal){
//        if (tokenizer.tokenVal().equals(tokenVal)){
//            System.out.println(padder + "<" +tokenizer.tokenType()+ ">" +
//                    tokenizer.tokenVal()+
//                    "</" +tokenizer.tokenType()+ ">");
//            tokenizer.advance();
//        }
//        else {
//            System.out.println("error: expecting " + tokenVal);
//        }
//    }

    public static void main(String[] args) {
        //JackTokenizer tokenizer = new JackTokenizer(args[0]);
        new CompilationEngine(args[0]);

    }
}
