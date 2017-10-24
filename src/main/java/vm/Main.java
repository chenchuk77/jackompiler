package vm;

import hack.core.Code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenchuk on 10/21/17.
 */
public class Main {
    private Parser parser;
    private CodeWriter codeWriter;
    private Command currentCommand;

    private String inputFile;    // *.vm
    private String vmCode;       // input

    private String outputFile;   // *.asm
    private String asmCode;      // output

    public Main(){
        asmCode = "";
        inputFile = "vm-examples/StackTest.vm";
        outputFile = "vm-examples/StackTest.asm";
        //vmCode = ExampleCode.Ptr;
        vmCode = readInputFile(inputFile);
        CodeWriter codeWriter = new CodeWriter(inputFile, asmCode);
        Parser parser = new Parser(vmCode, codeWriter);
        parser.parse();
        //Command currentCommand = new Command();
        //currentCommand = parser.parse();
        //System.out.println(codeWriter.getOutputAsmCode());
        //asmCode

        writeOutputFile(outputFile, codeWriter.getOutputAsmCode());
    }

    private String readInputFile (String filename) {
        // returns String from input file
        String fileContent = "";
        try{
            List<String> lines = Files.readAllLines(Paths.get(filename));
            fileContent = String.join("\n", lines);
        }catch (IOException e){ e.printStackTrace();}
        return fileContent;
    }

    private void writeOutputFile (String filename, String code) {
        System.out.print(code);
        // write String to input file ( first split to lines )
        List<String> fileContent;
        try{
            //fileContent = Arrays.asList(code.split("\\s+\n\\s+"));
            fileContent = Arrays.asList(code.split("\n"));
            Files.write(Paths.get(filename), fileContent);
        }catch (IOException e){ e.printStackTrace();}
    }



    public static void main(String[] args) {
        new Main();
    }
}
