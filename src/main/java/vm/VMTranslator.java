package vm;

import ch.qos.logback.core.net.SyslogOutputStream;
import hack.core.Code;
import sun.misc.VM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by chenchuk on 10/21/17.
 */
public class VMTranslator {

    private static List<String> fileList;
    private Parser parser;
    private CodeWriter codeWriter;
    private Command currentCommand;

    private String inputFile;    // *.vm
    private String vmCode;       // input

    private String outputFile;   // *.asm
    private String asmCode;      // output

    public VMTranslator(){
        asmCode = "";
        inputFile = "./3.vm";
        outputFile = "./3.asm";
        vmCode = readInputFile(inputFile);
        CodeWriter codeWriter = new CodeWriter(inputFile, asmCode);
        Parser parser = new Parser(vmCode, codeWriter);
        parser.parse();

        writeOutputFile(outputFile, codeWriter.getOutputAsmCode());
    }
//    public VMTranslator(List<String> fileList){
//        asmCode = "";
//        inputFile = "./1.vm";
//        outputFile = "./1.asm";
//        vmCode = readInputFile(inputFile);
//        CodeWriter codeWriter = new CodeWriter(inputFile, asmCode);
//        Parser parser = new Parser(vmCode, codeWriter);
//        parser.parse();
//
//        writeOutputFile(outputFile, codeWriter.getOutputAsmCode());
//    }

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

    public static void main(String[] args) throws Exception{
        fileList = new ArrayList<>();
        if (args.length == 0){
            System.out.println("no args, current vm files in pwd:");
            System.out.println("NOT YET IMPLEMENTED");
//            Files.newDirectoryStream(Paths.get("."),
//                    path -> path.toString().endsWith(".vm"))
//                    .forEach(x -> fileList.add(x.toString()));
//            System.out.println(fileList);
//            new VMTranslator(fileList);
        } else if(args.length == 1){
            System.out.println(args[0]);
            new VMTranslator();
        } else {
            System.out.println("too many args");
        }

    }
}
