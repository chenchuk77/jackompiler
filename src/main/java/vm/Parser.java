package vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenchuk on 10/21/17.
 */
public class Parser {
    private String inputVmCode;
    private Iterator iterator;
    private List<String> vmCommandsList;
    private String currentCommand;
    private CodeWriter codeWriter;

    public static final String[] ARITHMETIC_OPERS = {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};

    public Parser(String inputVmCode, CodeWriter codeWriter){
        this.inputVmCode = inputVmCode;
        this.codeWriter = codeWriter;
        vmCommandsList = makeList(inputVmCode);
        init();
    }

    public void init(){
        iterator = vmCommandsList.iterator();
    }

    public Boolean hasMoreCommands(){
        return iterator.hasNext();
    }

    public void parse(){
        while (hasMoreCommands()){
            advance();
            Command command = new Command();
            command.setCommandType(commandType(currentCommand));
            if (command.getCommandType()==CommandType.C_ARITHMETIC){
                command.setOperation(operation(currentCommand));
            } else if (command.getCommandType()==CommandType.C_PUSH ||
                    command.getCommandType()==CommandType.C_POP ){
                command.setArg1(arg1(currentCommand));
                command.setArg2(arg2(currentCommand));
            } else if (command.getCommandType()==CommandType.C_LABEL) {
                command.setLabel(arg1(currentCommand));
            } else if (command.getCommandType()==CommandType.C_GOTO) {
                command.setJumpDestination(arg1(currentCommand));
            } else if (command.getCommandType()==CommandType.C_IF) {
                command.setJumpDestination(arg1(currentCommand));
            } else if (command.getCommandType()==CommandType.C_FUNCTION) {
                command.setFunctionName(arg1(currentCommand));
                command.setLocalVars(arg2(currentCommand));
            } else if (command.getCommandType()==CommandType.C_CALL) {
                command.setFunctionName(arg1(currentCommand));
                command.setnArgs(arg2(currentCommand));
        }
            codeWriter.addAsm(command);
        }
    }

    // 1st word in command to categorize the command
    private CommandType commandType(String commandString){
        String[] s = commandString.split(" ");
        if (s[0].equals("push"))       return CommandType.C_PUSH;
        if (s[0].equals("pop"))        return CommandType.C_POP;
        if (s[0].equals("label"))      return CommandType.C_LABEL;
        if (s[0].equals("goto"))       return CommandType.C_GOTO;
        if (s[0].equals("if-goto"))    return CommandType.C_IF;
        if (s[0].equals("function"))   return CommandType.C_FUNCTION;
        if (s[0].equals("call"))       return CommandType.C_CALL;
        if (s[0].equals("return"))     return CommandType.C_RETURN;
        List<String> arithmeticCommands = Arrays.asList(ARITHMETIC_OPERS);
        if (arithmeticCommands.contains(s[0])) return CommandType.C_ARITHMETIC;
        return null;
    }

    // 1st word is operation in case of C_ARITHMETIC
    private String operation(String commandString){
        String[] s = commandString.split(" ");
        return  s[0];
    }



    // 2nd word in command
    public String arg1 (String commandString) {
        String[] s = commandString.split(" ");
        return s[1];
    }

    // 3rd word in command as integer
    public Integer arg2 (String commandString) {
        String[] s = commandString.split(" ");
        Integer arg2value = Integer.parseInt(s[2].trim());
        return arg2value;
    }


    public void advance(){
        // Reads the next command from the input and makes it the current
        // command. Should be called only if hasMoreCommands() is true.
        // removing commments
        String vmInstruction = (String) iterator.next();
        currentCommand = vmInstruction.replaceAll("//","");
    }

    // transform the non empty source code into list of vm commands
    private List<String> makeList (String vmCode){
        List<String> list = new ArrayList<String>();
        Arrays.stream(vmCode.split("\\r?\\n")).forEach(line -> {
            if (line != null && !line.isEmpty()){
                list.add(line.trim());
            }
        });
        return list;
    }



}
