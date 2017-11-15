package cmp;

import vm.CodeWriter;
import vm.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

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

    private Parser parser;
    private CodeWriter codeWriter;

    private String directoryName;
    private List<String> vmFiles;
    private Boolean addInitCode;

    private String inputFile;      // *.vm
    private String jackCode;       // input

    List<Character> chars;         // chars list of input Jack code
    private List<JackToken> tokens;
    private Iterator iterator;

    private JackToken currentToken;


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




//    public void init(){
//        iterator = tokens.iterator();
//    }

//    private String outputFile;   // *.asm
//    private String asmCode;      // output

    // ignore comments / whitespaces

    // input file / stream



    public JackTokenizer(String filename) {
        jackCode = readInputFile(filename);

        // after removing comments
        String jackNoComments = removeComments(jackCode);
        //System.out.println(removeComments(jackCode));

        // convert to list of chars for iteration
        chars = splitToListOfChar(jackNoComments);
        //System.out.println(chars);

        tokens = new ArrayList<>();
        tokenize();

        //System.out.println(tokens);

        // setup the iterator for the user
        currentToken = null;
        iterator = tokens.iterator();
        advance();

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
                tokens.add(new JackToken(string_const, TokenType.string_const));
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
                tokens.add(new JackToken(word, TokenType.int_const));
            } else {
                tokens.add(new JackToken(word, TokenType.identifier));
            }
            eatWhiteSpace(chars);
            continue;

        }

    }

    // convert String into List of chars
    public static List<Character> splitToListOfChar(String str) {
        return str.chars()
                .mapToObj(item -> (char) item)
                .collect(Collectors.toList());
    }

    // return true if number/digit/underscore
    public static boolean isLetterOrDigit (Character c){
        // convert to string, then looks for regex \w ([a-zA-Z0-9_])
        String cString = "" + c;
        return cString.matches ("\\w");
    }

    // return true if the input string is a number
    public static boolean isNumber (String s){
        try {
            Integer num = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    // eat whitespaces and tabs, will be called after recognizing a token
    public static void eatWhiteSpace(List<Character> cl){
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
    private String removeComments(String code){
        StringBuilder sb = new StringBuilder();
        Arrays.stream(code.split("\\r?\\n")).forEach(line -> {
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

//    // transform the non empty source code into list of vm commands
//    private List<String> makeList (String vmCode){
//        List<String> list = new ArrayList<String>();
//        Arrays.stream(vmCode.split("\\r?\\n")).forEach(line -> {
//            if (line != null && !line.isEmpty()){
//                list.add(line.trim());
//            }
//        });
//        return list;
//    }



//    // transform the non empty source code into list of vm commands
//    private List<String> makeList (String vmCode){
//        List<String> list = new ArrayList<String>();
//        Arrays.stream(vmCode.split("\\r?\\n")).forEach(line -> {
//            if (line != null && !line.isEmpty()){
//                list.add(line.trim());
//            }
//        });
//        return list;
//    }

//    addInitCode = true;
//    asmCode = "";

    // list of 1 or more files to process
//    jackFiles = getJackFiles(filename);
//    outputFile = getOutputFilename(filename);
//        System.out.println();
//        System.out.println(" initialized");
//        System.out.println("VM files: " +vmFiles);
//        System.out.println("Output asm file: " +outputFile);
//        System.out.println();


//
//    // looping all files in list
//        for (String vmFilename : vmFiles){
//        String currentAsmCode = "";
//
//        // open a file and read vm code
//        CodeWriter codeWriter = new CodeWriter(vmFilename);
//
//        //  if multiple files exists, adding INIT code ONLY once
//        if (vmFiles.size() > 1 && addInitCode == true){
//            System.out.println("adding bootstrap code");
//            addInitCode = false;
//            codeWriter.writeInit();
//        }
//        System.out.println("processing file: " +vmFilename);
//
//        // add comment to recognize filename in output asm code
//        codeWriter.writeFilenameComment(vmFilename);
//
//        // parser to handle 1 file
//        Parser parser = new Parser(vmCode, codeWriter);
//        parser.parse();
//
//        // add generated asm code
//        asmCode += codeWriter.getOutputAsmCode();
//    }
//
//        System.out.print("Hack assembly code of " +asmCode.length()+ " bytes written to " + outputFile);
//    writeOutputFile(outputFile, asmCode);
//        System.out.println();
//
//}

    private String readInputFile (String filename) {
        // returns String from input file
        String fileContent = "";
        try{
            List<String> lines = Files.readAllLines(Paths.get(filename));
            fileContent = String.join("\n", lines);
        }catch (IOException e){ e.printStackTrace();}
        return fileContent;
    }

//    private void writeOutputFile (String filename, String code) {
//        //System.out.print(code);
//        // write String to input file ( first split to lines )
//        List<String> fileContent;
//        try{
//            //fileContent = Arrays.asList(code.split("\\s+\n\\s+"));
//            fileContent = Arrays.asList(code.split("\n"));
//            Files.write(Paths.get(filename), fileContent);
//        }catch (IOException e){ e.printStackTrace();}
//    }
//
//
//    private List<String> getVmFiles(String userInput){
//        List<String> list = new ArrayList<>();
//        File file = new File(userInput);
//        // creating a list of dirname/*.vm files
//        if (file.isDirectory()) {
//            for (File f : file.listFiles()) {
//                String filename = f.getName();
//                if (filename.contains(".vm")) {
//                    list.add(userInput + "/" +filename);
//                }
//            }
//        } else {
//            // single *.vm file
//            list.add("./" +userInput);
//        }
//        return list;
//    }
//
//    public String getOutputFilename(String userInput){
//        File file = new File(userInput);
//        if (file.isDirectory())
//            return "./" +userInput+ "/" +userInput+ ".asm";
//        else
//            return "./" +userInput.replaceAll(".vm",".asm");
//    }
//
//




    public static void main(String[] args) throws Exception{
//        fileList = new ArrayList<>();
//        if (args.length == 0){
////            System.out.println("no args, current vm files in pwd:");
//            new VMFilesReader(args[0]);
//            fileList = VMFilesReader.
////            System.out.println("NOT YET IMPLEMENTED");
////            Files.newDirectoryStream(Paths.get("."),
////                    path -> path.toString().endsWith(".vm"))
////                    .forEach(x -> fileList.add(x.toString()));
//            System.out.println(fileList);
        //new JackTokenizer(args[0]);
        JackTokenizer tokenizer = new JackTokenizer(args[0]);
//        System.out.println(tokenizer.hasMoreTokens());
//        System.out.println(tokenizer.tokenType());
//        tokenizer.advance();
//        System.out.println(tokenizer.tokenType());
//        tokenizer.advance();
//        System.out.println(tokenizer.tokenType());
//        tokenizer.advance();
//        System.out.println(tokenizer.tokenType());
//        tokenizer.advance();

        while (tokenizer.hasMoreTokens()){
            System.out.println(tokenizer.tokenType() + "-" + tokenizer.tokenVal());
            tokenizer.advance();

        }
//        } else if(args.length == 1){
//            System.out.println(args[0]);


//            new VMFilesReader(args[0]);
//        } else {
//            System.out.println("too many args");
    }

//
//    hasMoreTokens()
//    advance()
//
//    tokenType()
//
//        // called for specific token type
//    keyWord()
//        symbol()
//            identifier()
//                intVal()
//                    stringVal()
//


}
