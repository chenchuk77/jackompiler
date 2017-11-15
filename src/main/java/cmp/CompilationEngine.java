package cmp;

/**
 * Created by chenchuk on 11/15/17.

 // NO JACK METHODS:
 // type
 // className
 // subroutineName
 // variableName
 // statements
 // soubroutineCall

 *
 *
 */
public class CompilationEngine {

    private JackTokenizer tokenizer;
    private String padder = "" ;

    public CompilationEngine (String filename) {
        tokenizer = new JackTokenizer(filename);
        compileClass();

    }

    // Compiles a complete class.
    private void compileClass(){
        emitString("class");
        eat("class");
        eat(tokenizer.tokenVal());
        eat("{");
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
            }
            eat(";");           // ;.
            emitBackString("classVarDec");
        }
    }

    // Compiles a complete method, function, or constructor.
    private void CompileSubroutine(){
        // subroutineDec: ('constructor' | 'function' | 'method') ('void' | type) subroutineName
        // '(' parameterList ')' subroutineBody
        while (tokenizer.tokenVal().equals("method") ||
               tokenizer.tokenVal().equals("function") ||
               tokenizer.tokenVal().equals("constructor")) {
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
        }
    }

    // Compiles a (possibly empty) parameter list, not including the enclosing “()”.
    private void compileParameterList(){
        emitString("parameterList"); // prints also on empty list
        // type: 'int' | 'char' | 'boolean' | className
        while ( tokenizer.tokenVal().equals("int") ||
                tokenizer.tokenVal().equals("char") ||
                tokenizer.tokenVal().equals("boolean") ||
                tokenizer.tokenType().equals(TokenType.identifier)){
            eat(tokenizer.tokenVal());    // type
            eat(tokenizer.tokenVal());    // varname
            if (tokenizer.tokenVal().equals(",")){
                eat(",");
            } else { break; }
        }
        emitBackString("parameterList");
    }

    // Compiles a var declaration.
    // varDec: 'var' type varName (',' varName)* ';'
    private void compileVarDec(){
        if (!tokenizer.tokenVal().equals("var")){
            emitString("varDec");
            emitBackString("varDec");
            return;
        } // empty varDec list
        while (tokenizer.tokenVal().equals("var")){
            emitString("varDec");
            eat("var");         // var
            eat(tokenizer.tokenVal());    // type
            eat(tokenizer.tokenVal());    // varname
            while (tokenizer.tokenVal().equals(",")){
                eat(",");
                eat(tokenizer.tokenVal());    // varname2, varname3, etc ....
            }
            eat(";");           // ;.
            emitBackString("varDec");
        }
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
        if (tokenizer.tokenVal().equals("[")) {
            eat("[");
            compileExpression();
            eat("]");
        }

        eat("=");           // =
        compileExpression();
        eat(";");           // ;
        emitBackString("letStatement");
    }

    // Compiles a while statement.
    private void compileWhile(){
        emitString("whileStatement");
        eat("while");           // if
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
        emitBackString("whileStatement");
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
            eat("else");
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
        emitBackString("term");


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



    // XML output functions
    //
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
    private void eat (String tokenVal){
        if (tokenizer.tokenVal().equals(tokenVal)){
            System.out.println(padder + "<" +tokenizer.tokenType()+ "> " +
                                             tokenizer.tokenVal()+
                                        " </"+tokenizer.tokenType()+ ">");
            if (tokenizer.hasMoreTokens()){
                tokenizer.advance();
            }
        }
        else {
            System.out.println("error: expecting " + tokenVal + ", found: " + tokenizer.tokenVal());
        }
    }


    public static void main(String[] args) {
        //JackTokenizer tokenizer = new JackTokenizer(args[0]);
        new CompilationEngine(args[0]);

    }
}
