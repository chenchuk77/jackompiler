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
        //System.out.println("parser init");

    }

    public Boolean hasMoreCommands(){
        return iterator.hasNext();
    }

    public void parse(){
        //private Command parse(){
        //System.out.println("parsing");
        while (hasMoreCommands()){
            advance();
            //System.out.println("parser return from advance");
            Command command = new Command();

            command.setCommandType(commandType(currentCommand));
            if (command.getCommandType()==CommandType.C_ARITHMETIC){
                command.setOperation(operation(currentCommand));
            } else if (command.getCommandType()==CommandType.C_PUSH ||
                        command.getCommandType()==CommandType.C_POP ){
                command.setArg1(arg1(currentCommand));
                command.setArg2(arg2(currentCommand));
            }

            //System.out.println("calling codeWriter with command : " + command);

            codeWriter.addAsm(command);
            //command;
            //
//            String operation =
//            if (cmd.getType() == "L_COMMAND"){
//                symbolTable.addEntry(parser.getLabel(), pc);
//                System.out.println("added entry : " + parser.getLabel() + " = " + (pc));
//            } else {
//                pc++;
//            }
        }
        //return null;
    }

    // 1st word in command to categorize the command
    private CommandType commandType(String commandString){
        String[] s = commandString.split(" ");
        if (s[0].equals("push")) return CommandType.C_PUSH;
        if (s[0].equals("pop"))  return CommandType.C_POP;

        List<String> arithmaticCommands = Arrays.asList(ARITHMETIC_OPERS);
        if (arithmaticCommands.contains(s[0])) return CommandType.C_ARITHMETIC;
        return null;
    }

    // 1st word is operation in case of C_ARITHMATIC
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
        Integer arg2value = Integer.parseInt(s[2]);
        return arg2value;
    }


    public void advance(){
        currentCommand = (String) iterator.next();
        // Reads the next command from the input and makes it the current
        // command. Should be called only if hasMoreCommands() is true.
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
