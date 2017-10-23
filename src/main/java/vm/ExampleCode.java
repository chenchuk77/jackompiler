package vm;

/**
 * Created by chenchuk on 10/23/17.
 */
public class ExampleCode {
    public static final String StackTest =
            "push constant 17\n" +
            "push constant 17\n" +
            "eq\n" +
            "push constant 17\n" +
            "push constant 16\n" +
            "eq\n" +
            "push constant 16\n" +
            "push constant 17\n" +
            "eq\n" +
            "push constant 892\n" +
            "push constant 891\n" +
            "lt\n" +
            "push constant 891\n" +
            "push constant 892\n" +
            "lt\n" +
            "push constant 891\n" +
            "push constant 891\n" +
            "lt\n" +
            "push constant 32767\n" +
            "push constant 32766\n" +
            "gt\n" +
            "push constant 32766\n" +
            "push constant 32767\n" +
            "gt\n" +
            "push constant 32766\n" +
            "push constant 32766\n" +
            "gt\n" +
            "push constant 57\n" +
            "push constant 31\n" +
            "push constant 53\n" +
            "add\n" +
            "push constant 112\n" +
            "sub\n" +
            "neg\n" +
            "and\n" +
            "push constant 82\n" +
            "or\n" +
            "not\n";

    public static final String SimpleAdd =
            "push constant 7\n" +
            "push constant 8\n" +
            "add\n";


}
