package vm;

import ch.qos.logback.core.net.SyslogOutputStream;
import hack.core.Code;
import sun.misc.VM;

import java.io.File;
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
 * VMTranslator for single *.vm file or a directory.
 *
 * if directory supplied then a Main.vm file must exists with main function
 * - bootstrap (set env)
 * - call OS function Sys.init()
 * - Sys.init calls Main.main and enter infinite loop
 *
 */
public class VMTranslator {

    private Parser parser;
    private CodeWriter codeWriter;
    private Command currentCommand;

    private String directoryName;
    private List<String> vmFiles;
    private Boolean addInitCode;

    private String inputFile;    // *.vm
    private String vmCode;       // input

    private String outputFile;   // *.asm
    private String asmCode;      // output

    public VMTranslator(String filename){

        addInitCode = true;
        asmCode = "";

        // list of 1 or more files to process
        vmFiles = getVmFiles(filename);
        outputFile = getOutputFilename(filename);
        System.out.println();
        System.out.println("VMTranslator initialized");
        System.out.println("VM files: " +vmFiles);
        System.out.println("Output asm file: " +outputFile);
        System.out.println();

        // looping all files in list
        for (String vmFilename : vmFiles){
            String currentAsmCode = "";

            // open a file and read vm code
            vmCode = readInputFile(vmFilename);
            CodeWriter codeWriter = new CodeWriter(vmFilename);

            //  if multiple files exists, adding INIT code ONLY once
            if (vmFiles.size() > 1 && addInitCode == true){
                System.out.println("adding bootstrap code");
                addInitCode = false;
                codeWriter.writeInit();
            }
            System.out.println("processing file: " +vmFilename);

            // add comment to recognize filename in output asm code
            codeWriter.writeFilenameComment(vmFilename);

            // parser to handle 1 file
            Parser parser = new Parser(vmCode, codeWriter);
            parser.parse();

            // add generated asm code
            asmCode += codeWriter.getOutputAsmCode();
        }

        System.out.print("Hack assembly code of " +asmCode.length()+ " bytes written to " + outputFile);
        writeOutputFile(outputFile, asmCode);
        System.out.println();

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
        //System.out.print(code);
        // write String to input file ( first split to lines )
        List<String> fileContent;
        try{
            //fileContent = Arrays.asList(code.split("\\s+\n\\s+"));
            fileContent = Arrays.asList(code.split("\n"));
            Files.write(Paths.get(filename), fileContent);
        }catch (IOException e){ e.printStackTrace();}
    }


    private List<String> getVmFiles(String userInput){
        List<String> list = new ArrayList<>();
        File file = new File(userInput);
        // creating a list of dirname/*.vm files
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                String filename = f.getName();
                if (filename.contains(".vm")) {
                    list.add(userInput + "/" +filename);
                }
            }
        } else {
            // single *.vm file
            list.add("./" +userInput);
        }
        return list;
    }

    public String getOutputFilename(String userInput){
        File file = new File(userInput);
        if (file.isDirectory())
            return "./" +userInput+ "/" +userInput+ ".asm";
        else
            return "./" +userInput.replaceAll(".vm",".asm");
    }






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
            new VMTranslator(args[0]);
//        } else if(args.length == 1){
//            System.out.println(args[0]);


//            new VMFilesReader(args[0]);
//        } else {
//            System.out.println("too many args");
        }

//    }
}
