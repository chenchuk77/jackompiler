package fullcompiler;

/**
 * Created by chenchuk on 11/10/17.
 */
public class JackToken {
    private String name;
    private TokenType type;

    public JackToken(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public TokenType getType() {
        return type;
    }


    @Override
    public String toString() {
        return String.format("%-12s %-20s\n", type, name);
    }
}
