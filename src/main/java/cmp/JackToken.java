package cmp;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%-10s: %-20s\n", type, name);
    }
}
