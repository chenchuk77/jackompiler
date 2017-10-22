package hack.core;

import hack.cmd.A_Command;
import hack.cmd.C_Command;
import hack.cmd.Command;
import hack.cmd.L_Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenchuk on 10/14/17.
 */
public class Parser {

    private String currentCommand = "";
    private Integer numOfLines = 0;
    private Integer index = 0;

    Iterator iterator;
    List<String> sourceList;

//    public Parser(String filename){
//        // Opens the input file/stream and gets ready to parse it.
//        // structure in a list of non empty, non commented clear commands
//        sourceList = makeList(filename);
//        init();
//    }
    public Parser(String asm){
        // asm : '\n' delimited asm code
        sourceList = makeList(asm);
        init();
    }
    public void init(){
        iterator = sourceList.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int index=0;
        while (iterator.hasNext()){
            sb.append("" + index++ + ": " + iterator.next() + "\n");
        }
        return sb.toString();
    }
    private String removeComments (String command){
        return command.replaceAll("//.*", "");
    }
    // read file to list without comments and newlines
    private List<String> makeList (String asm){
        List<String> list = new ArrayList<String>();
        Arrays.stream(asm.split("\\r?\\n")).forEach(line -> {
                if (line != null){
                    String command = removeComments(line);
                    if(!command.isEmpty()){
                        list.add(command.trim());
                    }

                }
            });
        numOfLines = list.size();
        return list;
    }


    public Boolean hasMoreCommands(){
        return iterator.hasNext();
    }
    public void advance(){
        currentCommand = (String) iterator.next();
        // Reads the next command from the input and makes it the current
        // command. Should be called only if hasMoreCommands() is true.
    }

    public Command commandType () {
        if (currentCommand.startsWith("@")){
            return new A_Command(currentCommand);
        }
        if (currentCommand.startsWith("(")){
            return new L_Command(currentCommand);
        }
        if (currentCommand.startsWith("M") ||
            currentCommand.startsWith("D") ||
            currentCommand.startsWith("A") ||
            currentCommand.startsWith("0")){
            return new C_Command(currentCommand);
        }
        else return null;
    }

    public String getLabel () {
        // trim the '(' and ')'
        return currentCommand.substring(1, currentCommand.length()-1);
    }


    public String symbol() {
        // Returns the symbol or decimal Xxx of the current command @Xxx or (Xxx).
        // Should be called only when commandType() is A_COMMAND or L_COMMAND.
        return currentCommand.substring(1);
    }
    public String dest() {
        // Returns the dest mnemonic in the current C-command (8 possibilities).
        // Should be called only when commandType() is C_COMMAND.
        if (currentCommand.contains("=")){
            return currentCommand.substring(0, currentCommand.indexOf("="));

        } else return "null";
    }
    public String comp() {
        // Returns the comp mnemonic in the current C-command (28 possibilities).
        // Should be called only when commandType() is C_COMMAND.
        if (currentCommand.contains("=") && currentCommand.contains(";")){
            return currentCommand.substring(currentCommand.indexOf("=") + 1, currentCommand.indexOf(";"));
        }
        if (currentCommand.contains("=")){
            return currentCommand.substring(currentCommand.indexOf("=") + 1);
        }
        if (currentCommand.contains(";")){
            return currentCommand.substring(0, currentCommand.indexOf(";"));
        }
        return "COMP_PARSE_ERROR";

    }
    public String jump(){
        // Returns the jump mnemonic in the current C-command (8 possibilities).
        // Should be called only when commandType() is C_COMMAND.
        if (currentCommand.contains(";")){
            return currentCommand.substring(currentCommand.indexOf(";") + 1);
        }
        return "null";
    }

}
