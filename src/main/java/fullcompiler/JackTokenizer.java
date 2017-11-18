package fullcompiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by chenchuk on 11/10/17.
 */
public class JackTokenizer {


    private static final String[] KEYWORDS = {"class", "method", "function", "constructor", "int",
                                             "boolean", "char", "void", "var", "static", "field",
                                             "let","do", "if", "else", "while", "return", "true",
                                             "false", "null","this"};

    private static final Character[] SYMBOLS = {'{', '}', '(', ')', '[', ']', '.', ',', ';',
                                                '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'};

    private List<Character> chars;         // chars list of input Jack code
    private List<JackToken> tokens;
    private Iterator iterator;
    private JackToken currentToken;


    public JackTokenizer(String filename) {

        // read file
        String jackCode = readInputFile(filename);

        // remove comments ( // and /*...*/ )
        String jackNoComments = removeComments(jackCode);

        // convert to list of chars for iteration
        chars = splitToListOfChar(jackNoComments);

        tokens = new ArrayList<>();
        tokenize();

        // System.out.print(getTokens());
        // setup the iterator for the user of the class
        currentToken = null;
        iterator = tokens.iterator();
        advance();
    }

    public List<JackToken> getTokens() {
        return tokens;
    }

    // API
    public Boolean hasMoreTokens(){
        return iterator.hasNext();
    }

    // API
    public void advance(){
        // get the next token and makes it the current
        // Should be called only if hasMoreTokens() is true.
        currentToken = (JackToken) iterator.next();
    }

    // API
    public String tokenType(){
        // get the next token and makes it the current
        // Should be called only if hasMoreTokens() is true.
        return currentToken.getType().toString();
    }

    // non API
    public String tokenVal(){
        // get the next token and makes it the current
        // Should be called only if hasMoreTokens() is true.
        return currentToken.getName();
    }

    // tokenizing the input chars
    private void tokenize(){
        while (chars != null && !chars.isEmpty()) {

            // check if starting with symbol ( symbol terminates a token )
            if (isSymbol(chars.get(0))) {
                tokens.add(new JackToken("" + chars.get(0), TokenType.symbol));
                chars.remove(0);
                eatWhiteSpace(chars);
                continue;
            }

            // check if [ " ] then a string constant token and a terminating [ " ]
            if (isQuotes(chars.get(0))) {
                chars.remove(0);    // remove starting [ " ]

                // collect all digits until the next terminating [ " ]
                String string_const = "";
                while (!isQuotes(chars.get(0))) {
                    // add and eat char
                    string_const = string_const + chars.get(0);
                    chars.remove(0);
                }
                tokens.add(new JackToken(string_const, TokenType.stringConstant));
                chars.remove(0);    // remove terminating [ " ]
                eatWhiteSpace(chars);
                continue;
            }

            // otherwise collect letters to form a word/number
            String word = "";
            while (isLetterOrDigit(chars.get(0))) {
                // add and eat char
                word = word + chars.get(0);
                chars.remove(0);
            }

            // word found, check if known keyword
            if (isKeyword(word)) {
                tokens.add(new JackToken(word, TokenType.keyword));
            } else if (isNumber(word)){
                tokens.add(new JackToken(word, TokenType.integerConstant));
            } else {
                tokens.add(new JackToken(word, TokenType.identifier));
            }
            eatWhiteSpace(chars);
            continue;

        }

    }

    // convert String into List of chars
    private static List<Character> splitToListOfChar(String str) {
        return str.chars()
                .mapToObj(item -> (char) item)
                .collect(Collectors.toList());
    }

    // return true if number/digit/underscore
    private static boolean isLetterOrDigit (Character c){
        // convert to string, then looks for regex \w ([a-zA-Z0-9_])
        String cString = "" + c;
        return cString.matches ("\\w");
    }

    // return true if the input string is a number
    private static boolean isNumber (String s){
        try {
            Integer num = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    // eat whitespaces and tabs, will be called after recognizing a token
    private static void eatWhiteSpace(List<Character> cl){
        // mutate the list if starting with ' '
        if (cl != null && !cl.isEmpty()){
            while (cl.get(0) == ' ' || cl.get(0) == '\t'){
                cl.remove(0);
            }
        }
    }

    // return true if the input string is a Jack language keyword
    private static boolean isKeyword(String s){
        for (String keyword : KEYWORDS) {
            if (s.equals(keyword)){
                return true;
            }
        }
        return false;
    }

    // return true if the input string is a Jack language symbol
    public static boolean isSymbol(Character c){
        for (Character symbol : SYMBOLS) {
            if (symbol == c){
                return true;
            }
        }
        return false;
    }

    // reuturn true if the char is double-quotes
    public static boolean isQuotes(Character c){
        return c == '"';
    }


    // removing comments from each line, and collect them to a non commented code string
    private String removeComments(String commentedCode){
        // remove multiline comments
        String multilineComments = "/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/";
        Pattern p = Pattern.compile(multilineComments, Pattern.DOTALL);
        String slashSlashCommentedCode = p.matcher(commentedCode).replaceAll("");
        // remove single line comments and serialize to flat string (without \n)
        StringBuilder sb = new StringBuilder();
        Arrays.stream(slashSlashCommentedCode.split("\\r?\\n")).forEach(line -> {
            if (line != null && !line.isEmpty()){
                if (line.contains("//")){
                    line = line.substring(0,line.indexOf("//"));
                }
                sb.append(line.trim());
            }
        });
        String nonCommentedJackCode = sb.toString();
        return nonCommentedJackCode;
    }


    // returns a String from an input filename
    private String readInputFile (String filename) {
        String fileContent = "";
        try{
            List<String> lines = Files.readAllLines(Paths.get(filename));
            fileContent = String.join("\n", lines);
        }catch (IOException e){ e.printStackTrace();}
        return fileContent;
    }
}
