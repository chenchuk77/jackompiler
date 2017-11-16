package cmp;

import vm.CodeWriter;
import vm.Command;
import vm.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenchuk on 10/21/17.
 * JackAnalyzer for single *.jack file or a directory.
 * The ctor initialize a new jack tokenizer and a compilation engine
 * for each file.
 * an XML output file generated for each source jack file
 * representing the compiled structure of the jack code
 *
 */
public class JackAnalyzer {

    private JackTokenizer tokenizer;
    private CompilationEngine compiler;
    private List<String> jackFiles;
    private String jackCode;       // input
    //private String outputFile;     // *.asm

    public JackAnalyzer(String filename){
        // list of 1 or more files to process
        jackFiles = getJackFiles(filename);
        //outputFile = getOutputFilename(filename);
        System.out.println();
        System.out.println("JackAnalyzer initialized");
        System.out.println("Jack files: " + jackFiles);
        System.out.println();

        // looping all files in list
        for (String jackFilename : jackFiles){
            System.out.println("processing file: " +jackFilename);
            String xmlCode = "" ;
            // parser to handle 1 file
            tokenizer = new JackTokenizer(jackFilename);
            //System.out.println(tokenizer.getTokens());
            compiler = new CompilationEngine(tokenizer);
            xmlCode = compiler.getCompiledXml();
            writeOutputFile(xmlFilename(jackFilename), xmlCode);
            System.out.println("XML code of " + xmlCode.length()+ " bytes written to " + xmlFilename(jackFilename));
            System.out.println();
        }
    }

    private void writeOutputFile (String filename, String code) {
        List<String> fileContent;
        try{
            Files.deleteIfExists(Paths.get(filename));
            fileContent = Arrays.asList(code.split("\n"));
            Files.write(Paths.get(filename), fileContent);
        }catch (IOException e){ e.printStackTrace();}
    }

    private List<String> getJackFiles(String userInput){
        List<String> list = new ArrayList<>();
        File file = new File(userInput);
        // creating a list of dirname/*.vm files
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                String filename = f.getName();
                if (filename.endsWith(".jack")) {
                    list.add(userInput + "/" +filename);
                }
            }
        } else {
            // single *.jack file
            list.add("./" +userInput);
        }
        return list;
    }


    public String xmlFilename(String jackFilename){
        return jackFilename.replaceAll(".jack", ".xml");
    }


    public String getOutputFilename(String userInput){
        File file = new File(userInput);
        if (file.isDirectory())
            return "./" +userInput+ "/" +userInput+ ".xml";
        else
            return "./" +userInput.replaceAll(".jack",".xml");
    }

    public static void main(String[] args) throws Exception{
            new JackAnalyzer(args[0]);
        }
}
