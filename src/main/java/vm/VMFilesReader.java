package vm;

//
// tester, delete me
//

import java.io.File;
public class VMFilesReader {
    public static void main( String [] args ) {
        File arg0 = new File("FibonacciElement");
        if (arg0.isDirectory()){
            for( File f : arg0.listFiles()){
                System.out.println( f.getName() );
            }

        }
    }
}
