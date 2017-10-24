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

    public static final String Ptr =
            "push constant 3\n" +
            "pop pointer 0\n" +
            "push constant 4\n" +
            "pop pointer 1\n" +
            "add\n";
    public static final String SimpleAdd =
            "push constant 7\n" +
            "push constant 8\n" +
            "add\n";
    public static final String PointerTest =
            "push constant 3030\n" +
            "pop pointer 0\n" +
            "push constant 3040\n" +
            "pop pointer 1\n" +
            "push constant 32\n" +
            "pop this 2\n" +
            "push constant 46\n" +
            "pop that 6\n" +
            "push pointer 0\n" +
            "push pointer 1\n" +
            "add\n" +
            "push this 2\n" +
            "sub\n" +
            "push that 6\n" +
            "add\n";
    public static final String BasicTest =
            "push constant 10\n" +
            "pop local 0\n" +
            "push constant 21\n" +
            "push constant 22\n" +
            "pop argument 2\n" +
            "pop argument 1\n" +
            "push constant 36\n" +
            "pop this 6\n" +
            "push constant 42\n" +
            "push constant 45\n" +
            "pop that 5\n" +
            "pop that 2\n" +
            "push constant 510\n" +
            "pop temp 6\n" +
            "push local 0\n" +
            "push that 5\n" +
            "add\n" +
            "push argument 1\n" +
            "sub\n" +
            "push this 6\n" +
            "push this 6\n" +
            "add\n" +
            "sub\n" +
            "push temp 6\n" +
            "add\n";

    public static final String TEST_TEMP =
            "push temp 7\n" +
            "pop temp 2\n" +
            "or\n";
    public static final String TEST_STATIC =
            "push static 7\n" +
            "pop static 2\n" +
            "or\n";


}
