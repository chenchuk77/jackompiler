package hack.core;

import hack.cmd.Command;

public class Assembler {

    private Parser parser;
    private Code code;
    private SymbolTable symbolTable;
    Integer pc;             // program counter
    String hackCode = "";

    // setup a new Assembler and translate the provided asmCode
    public Assembler(String asmCode) {
        pc = 0;
        parser = new Parser(asmCode);
        symbolTable = new SymbolTable();
        code = new Code(symbolTable);

        firstPass();
        symbolTable.show();
        parser.init();
        secondPass();
    }

    // returns the binary representation
    public String getHackCode(){
        return hackCode;
    }

    // checking for labels
    private void firstPass(){
        System.out.println("starting 1st pass");
        while (parser.hasMoreCommands()){
            parser.advance();
            Command cmd = parser.commandType();
            if (cmd.getType() == "L_COMMAND"){
                symbolTable.addEntry(parser.getLabel(), pc);
                System.out.println("added entry : " + parser.getLabel() + " = " + (pc));
            } else {
                pc++;
            }
        }
    }

    // parse and assemble, adding each command to the output
    private void secondPass(){
        System.out.println("starting 2nd pass");
        StringBuilder sb = new StringBuilder();
        while (parser.hasMoreCommands()) {
            parser.advance();
            Command cmd = parser.commandType();
            if (cmd.getType() == "A_COMMAND") {
                sb.append(code.generateAcommand(parser.symbol()));
                sb.append("\n");
            }
            if (cmd.getType() == "C_COMMAND") {
                sb.append(code.generateCcommand(parser.dest(), parser.comp(), parser.jump()));
                sb.append("\n");
            }
        }
        hackCode = sb.toString();
    }
}
