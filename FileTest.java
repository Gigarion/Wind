import java.util.Scanner;
import java.io.*;

public class FileTest {
    public static void main(String[] args) throws IOException {
        File testing = new File("testing.txt");
        File trial = new File("trial.txt");
        File output = new File("output.txt");
        
        Scanner keepData = new Scanner(testing);
        Scanner addData = new Scanner(trial);
        PrintWriter writeFile = new PrintWriter(output);
        while (keepData.hasNext()) {
            writeFile.println(keepData.next());
            System.out.println("done");
        }
        while (addData.hasNext()){
            writeFile.println(addData.next());
            System.out.println("yep");
        }
        keepData.close();
        addData.close();
        writeFile.close();
    }
}