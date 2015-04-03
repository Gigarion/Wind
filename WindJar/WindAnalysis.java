import java.util.Scanner;
import java.io.*;

public class WindAnalysis {
    
    
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the Wind Analyst");
        System.out.println("Type file name to add");
        
        Scanner fromInput = new Scanner(System.in);
        String fileName = fromInput.next();
        fromInput.close();
        
        File adding = new File(fileName);
        Organize.addData(adding);
        
        File toCheck = new File("maxList.txt");
        DataPoint[] data = Organize.purify(toCheck);
        DataPoint.writeOver(toCheck, data);
    }
}
