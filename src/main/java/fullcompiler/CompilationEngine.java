package fullcompiler;

/**
 * Created by chenchuk on 11/15/17.
 */

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private String padder = "" ;
    private String compiledXml;
    private String compiledVmCode;
    private SymbolTable classVarsST;
    private SymbolTable subVarsST;
    private String className;

    // ctor gets a tokenizer to parse
    public CompilationEngine(JackTokenizer tokenizer) {
        //tokenizer = new JackTokenizer(filename);
        compiledXml = "";
        compiledVmCode = "";
        this.tokenizer = tokenizer;
        compileClass();
    }

    // Compiles a complete class.
    private void compileClass(){
        //one and only init of class-wide symbol table
        classVarsST = new SymbolTable();
        emitString("class");
        eat("class");
        className = eat(tokenizer.tokenVal());
        emitClass(className);
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
            Var classVar = new Var();
            classVar.setKind(eat(tokenizer.tokenVal()));    // static/field
            classVar.setType(eat(tokenizer.tokenVal()));    // type
            classVar.setName(eat(tokenizer.tokenVal()));    // varname
            classVarsST.define(classVar);
            emitVarIdenntifier(classVarsST.getVar(classVar.getName()), "defined");
            while (tokenizer.tokenVal().equals(",")){
                eat(",");
                Var classVarN = new Var();
                classVarN.setKind(classVar.getKind());
                classVarN.setType(classVar.getType());
                classVarN.setName(eat(tokenizer.tokenVal()));    // varname2, varname3, etc ....
                classVarsST.define(classVarN);
                emitVarIdenntifier(classVarsST.getVar(classVarN.getName()), "defined");

            }
            eat(";");           // ;.
            emitBackString("classVarDec");
        }
        //System.out.println("classST: \n" +classVarsST);

    }

    // Compiles a complete method, function, or constructor.
    private void CompileSubroutine(){
        // init once per subroutine
        subVarsST = new SymbolTable();

        // subroutineDec: ('constructor' | 'function' | 'method') ('void' | type) subroutineName
        // '(' parameterList ')' subroutineBody
        while (tokenizer.tokenVal().equals("method") ||
               tokenizer.tokenVal().equals("function") ||
               tokenizer.tokenVal().equals("constructor")) {

            // argument0 is always 'this'
            if (tokenizer.tokenVal().equals("method")){
                subVarsST.define(new Var("this", className, "argument", 0));
            }

            emitString("subroutineDec");
            eat(tokenizer.tokenVal());     // sub-type
            String returnType = tokenizer.tokenVal();
            if (!returnType.equals("void")){
                emitClassIdenntifier(tokenizer.tokenVal());
            }
            eat(tokenizer.tokenVal());     // sub-return-type (classname / void)
            emitSubroutineIdenntifier(tokenizer.tokenVal());
            subVarsST.setSubName(eat(tokenizer.tokenVal()));     // sub-name
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
        //System.out.println("sub:" +subVarsST);
    }

    // Compiles a (possibly empty) parameter list, not including the enclosing “()”.
    private void compileParameterList(){
        emitString("parameterList"); // prints also on empty list
        // type: 'int' | 'char' | 'boolean' | className
        while ( tokenizer.tokenVal().equals("int") ||
                tokenizer.tokenVal().equals("char") ||
                tokenizer.tokenVal().equals("boolean") ||
                tokenizer.tokenType().equals(TokenType.identifier)){
            Var subVar = new Var();
            subVar.setType(eat(tokenizer.tokenVal()));    // type
            subVar.setName(eat(tokenizer.tokenVal()));    // varname
            subVar.setKind("argument");
            subVarsST.define(subVar);
            emitVarIdenntifier(subVarsST.getVar(subVar.getName()), "defined");

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
            Var subVar = new Var();
            eat("var");         // var
            subVar.setType(eat(tokenizer.tokenVal()));    // type
            subVar.setName(eat(tokenizer.tokenVal()));    // varname
            subVar.setKind("var");
            subVarsST.define(subVar);
            emitVarIdenntifier(subVarsST.getVar(subVar.getName()), "defined");
            while (tokenizer.tokenVal().equals(",")){
                eat(",");
                Var subVarN = new Var();
                subVarN.setKind(subVar.getKind());
                subVarN.setType(subVar.getType());
                subVarN.setName(eat(tokenizer.tokenVal()));    // varname2, varname3, etc ....
                subVarsST.define(subVarN);
                emitVarIdenntifier(subVarsST.getVar(subVarN.getName()), "defined");
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
        String id = eat(tokenizer.tokenVal());    // subname to call
        if (tokenizer.tokenVal().equals(".")) {
            emitClassIdenntifier(id);
            eat(".");
            String idAfterDot = eat(tokenizer.tokenVal());    // identifier
            emitSubroutineIdenntifier(idAfterDot);
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
        String id = eat(tokenizer.tokenVal());    // identifier
        lookupAndEmitVar(id);
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



//
//    String id = eat(tokenizer.tokenVal());    // identifier
//        if (subVarsST.getVar(id) != null){
//        emitVarIdenntifier(subVarsST.getVar(id), "used (subST)");
//    } else if (classVarsST.getVar(id) != null){
//        emitVarIdenntifier(classVarsST.getVar(id), "used (classST)");
//    } else {
//        System.out.println("undeclared variable used.");
//    }

    private void compileTerm(){
        emitString("term");
        if (tokenizer.tokenType().equals("identifier")){
            //System.out.println("*** found identifier in term ****" + tokenizer.tokenVal());
            String id = eat(tokenizer.tokenVal());
            //System.out.println("*** found identifier in term ****" + id);
            if (tokenizer.tokenVal().equals("[")){
                // lookup : var identifier must be declared in a pattern like : x[]
                if (subVarsST.getVar(id) != null){
                    emitVarIdenntifier(subVarsST.getVar(id), "used (subST)");
                } else if (classVarsST.getVar(id) != null){
                    emitVarIdenntifier(classVarsST.getVar(id), "used (classST)");
                } else {
                    System.out.println("undeclared variable used as array pointer.");
                    //System.exit(1);
                }
                eat("[");
                compileExpression();
                eat("]");
            } else if (tokenizer.tokenVal().equals("(")){
                // function call
                emitSubroutineIdenntifier(id);
                eat("(");
                compileExpressionList();
                eat(")");
            } else if (tokenizer.tokenVal().equals(".")){
                // lookup to realize which kind of function call
                // if found : its a var.function() call
                // if not   : its a Class.function() call
                //System.out.println(classVarsST);
                //System.out.println("found .");
                if (subVarsST.getVar(id) != null){
                    //System.out.println(id + ". found in subST");
                    emitVarIdenntifier(subVarsST.getVar(id), "used (subST)");
                } else if (classVarsST.getVar(id) != null){
                    //System.out.println(id + ". found in classST");
                    emitVarIdenntifier(classVarsST.getVar(id), "used (classST)");
                } else {
                    //System.out.println(id + ". is a classname");
                    emitClassIdenntifier(id);
                }
                eat(".");
                //System.out.println(id + "-" + tokenizer.tokenVal());
                emitSubroutineIdenntifier(eat(tokenizer.tokenVal()));
                eat("(");
                compileExpressionList();
                eat(")");
            } else {
                // normal identifier without [] or . or ()
                if (subVarsST.getVar(id) != null){
                    emitVarIdenntifier(subVarsST.getVar(id), "used (subST)");
                } else if (classVarsST.getVar(id) != null){
                    emitVarIdenntifier(classVarsST.getVar(id), "used (classST)");
                } else {
                    System.out.println("undeclared variable used as array pointer.");
                    //System.exit(1);
                }
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

    // get the XML output, used only for unit testing the pasres logic
    public String getCompiledXml() {
        return compiledXml;
    }

    // get the final generated vm code
    public String getCompiledVmCode() {
        return compiledVmCode;
    }


    // eat and advance
    private String eat (String tokenVal){
        if (tokenizer.tokenVal().equals(tokenVal)){
            // replace < with HTML equiv
            String val = tokenVal;
            if (tokenVal.equals("<")) val = "&lt;";
            if (tokenVal.equals(">")) val = "&gt;";
            if (tokenVal.equals("&")) val = "&amp;";
            // print only non-identifiers
            if (!tokenizer.tokenType().equals("identifier")){
                compiledXml += padder + "<" +tokenizer.tokenType()+ "> " +
                                                   val+
                                        " </"+tokenizer.tokenType()+ ">\n";
            }

            if (tokenizer.hasMoreTokens()){
                tokenizer.advance();
            }
        }

        else {
            System.out.println("error: expecting " + tokenVal + ", found: " + tokenizer.tokenVal());
            //System.exit(1);
        }
        return tokenVal;
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
    // class name
    private void lookupAndEmitVar(String id){
        if (subVarsST.getVar(id) != null){
            emitVarIdenntifier(subVarsST.getVar(id), "used (subST)");
        } else if (classVarsST.getVar(id) != null){
            emitVarIdenntifier(classVarsST.getVar(id), "used (classST)");
        } else {
            System.out.println("undeclared variable used.");
            //System.exit(1);
        }
    }



}
