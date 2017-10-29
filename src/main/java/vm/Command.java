package vm;

/**
 * Created by chenchuk on 10/14/17.
 */
public class Command {
    private CommandType commandType;
    private String operation;
    private String label;
    private String functionName;
    private Integer localVars;

    public Integer getnArgs() {
        return nArgs;
    }

    public void setnArgs(Integer nArgs) {
        this.nArgs = nArgs;
    }

    private Integer nArgs;
    private String jumpDestination;
    private String arg1;
    private Integer arg2;

    public Integer getLocalVars() {
        return localVars;
    }
    public void setLocalVars(Integer localVars) {
        this.localVars = localVars;
    }

    public String getFunctionName() {
        return functionName;
    }
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getJumpDestination() {
        return jumpDestination;
    }
    public void setJumpDestination(String jumpDestination) {
        this.jumpDestination = jumpDestination;
    }

    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getArg1() {
        return arg1;
    }
    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public Integer getArg2() {
        return arg2;
    }
    public void setArg2(Integer arg2) {
        this.arg2 = arg2;
    }

    public CommandType getCommandType() {
        return commandType;
    }
    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public String toString() {
        return "Command{" +
                "type='" + commandType + '\'' +
                '}';
    }
}
