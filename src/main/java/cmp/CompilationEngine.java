package cmp;

/**
 * Created by chenchuk on 11/15/17.
 */

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private String padder = "" ;
    private String compiledXml = "" ;

    // ctor gets a tokenizer to parse
    public CompilationEngine (JackTokenizer tokenizer) {
        //tokenizer = new JackTokenizer(filename);
        this.tokenizer = tokenizer;
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
        while (tokenizer.tokenVal().equals("+") ||
                tokenizer.tokenVal().equals("-") ||
                tokenizer.tokenVal().equals("*") ||
                tokenizer.tokenVal().equals("/") ||
                tokenizer.tokenVal().equals("&") ||
                tokenizer.tokenVal().equals("|") ||
                tokenizer.tokenVal().equals("<") ||
                tokenizer.tokenVal().equals(">") ||
                tokenizer.tokenVal().equals("=")) {
            eat(tokenizer.tokenVal());
            compileTerm();
        }
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
        if (tokenizer.tokenType().equals("identifier")){
            eat(tokenizer.tokenVal());
            if (tokenizer.tokenVal().equals("[")){
                eat("[");
                compileExpression();
                eat("]");
            } else if (tokenizer.tokenVal().equals("(")){
                eat("(");
                compileExpressionList();
                eat(")");
            } else if (tokenizer.tokenVal().equals(".")){
                eat(".");
                eat(tokenizer.tokenVal());
                eat("(");
                compileExpressionList();
                eat(")");
            }
        } else if (tokenizer.tokenType().equals("stringConstant")){
            eat(tokenizer.tokenVal());
        } else if (tokenizer.tokenType().equals("integerConstant")){
            eat(tokenizer.tokenVal());
        } else if (tokenizer.tokenVal().equals("true") ||
                tokenizer.tokenVal().equals("false") ||
                tokenizer.tokenVal().equals("null") ||
                tokenizer.tokenVal().equals("this")) {
            eat(tokenizer.tokenVal());
        } else if (tokenizer.tokenVal().equals("(")){
            eat("(");
            compileExpression();
            eat(")");
        } else {
            while (tokenizer.tokenVal().equals("-") ||
                    tokenizer.tokenVal().equals("~")) {
                eat(tokenizer.tokenVal());
                compileTerm();
            }
        }
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

    // XML output methods
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
        compiledXml += padder + "<" +xmlElement+ ">\n";
        identForward();
    }

    // XML output + identation backword
    private void emitBackString(String xmlElement){
        identBackward();
        compiledXml += padder + "</" +xmlElement+ ">\n";
    }

    // get the XML output
    public String getCompiledXml() {
        return compiledXml;
    }


    // eat and advance
    private void eat (String tokenVal){
        if (tokenizer.tokenVal().equals(tokenVal)){
            // replace < with HTML equiv
            String val = tokenVal;
            if (tokenVal.equals("<")) val = "&lt;";
            if (tokenVal.equals(">")) val = "&gt;";
            if (tokenVal.equals("&")) val = "&amp;";
            compiledXml += padder + "<" +tokenizer.tokenType()+ "> " +
                                                    val+
                                    " </"+tokenizer.tokenType()+ ">\n";
            if (tokenizer.hasMoreTokens()){
                tokenizer.advance();
            }
        }
        else {
            System.out.println("error: expecting " + tokenVal + ", found: " + tokenizer.tokenVal());
        }
    }
}
