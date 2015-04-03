import java.util.*;
import java.io.*;

public class Organize {
    private static final String CUTOFF = "CH12Min";
    
    public static DataPoint[] purify(DataPoint[] data) 
        throws IOException {
        SortKey[] holdKeys = new SortKey[data.length];
        TreeSet<SortKey> myTree = new TreeSet<SortKey>();
        for (int i = 0; i < data.length; i++) {
            holdKeys[i] = new SortKey(data[i].stamp(), i);
            myTree.add(holdKeys[i]);
        }
        DataPoint[] sorted = new DataPoint[myTree.size()];
        int i = 0;
        for (SortKey key : myTree) {
            sorted[i] = data[key.getIndex()];
            i++;
        }
        return sorted;
    }
    
    public static DataPoint[] purify(File source) throws IOException {
        DataPoint[] data = DataPoint.fromFile(source);
        return purify(data);
    }
    
    private static void endFile(String date, String time, 
                                Scanner fileRead) 
        throws IOException {
        
        File maxList = new File("maxList.txt");
        File maxTemp = new File("maxTemp.txt");
        
        PrintWriter writeTemp = new PrintWriter(maxTemp);
        
        if (!maxList.createNewFile()) {
            Scanner readMax = new Scanner(maxList);
            
            while (readMax.hasNext()) {
                writeTemp.println(readMax.next());
            }
            readMax.close();
        }
        
        writeTemp.println(date);
        writeTemp.println(time);
        
        while (fileRead.hasNext()) {
            writeTemp.println(fileRead.next());
        }
        writeTemp.close();
        
        transfer(maxTemp, maxList, true);
        purify(maxList);
    }  
    
    public static void transfer(File source, File target, boolean delete)
        throws IOException{
        Scanner copy = new Scanner(source);
        PrintWriter paste = new PrintWriter(target);
        
        while (copy.hasNext())
            paste.println(copy.next());
        
        copy.close();
        paste.close();
        
        if (delete)
            source.delete();
    }
        
    public static void addData(File toAdd) throws IOException {
        
        File masterTab = new File("masterTab.txt");
        File output = new File("output.txt");
        output.createNewFile();
        PrintWriter writeOutput = new PrintWriter(output);
        
        if (!masterTab.createNewFile()) {           
            Scanner readMaster = new Scanner(masterTab);
            
            while (readMaster.hasNext())               
                writeOutput.println(readMaster.next()); 
            
            readMaster.close();
        }
        Scanner adding = new Scanner(toAdd);
        String cutOff = "";
        while (!cutOff.equals(CUTOFF)) {
            cutOff = adding.next();
        }
        
        int endCount = 0;
        int month = -1;
        int hour = -1;
        
        while (adding.hasNext()) {
            String holder = adding.next();
            
            if (endCount == 0) {
                int monthTemp = Integer.parseInt(holder.substring(0, 2));
                String timeStamp = adding.next();
                int hourTemp = Integer.parseInt(timeStamp.substring(0, 2));
                int minuteTemp = Integer.parseInt(timeStamp.substring(3, 5));
                
                if (month != -1) {
                    if (minuteTemp % 10 != 0) 
                        endFile(holder, timeStamp, adding);
                    else if (!(monthTemp - month == 0 ^ monthTemp - month == 1
                                   ^ monthTemp - month == 11))
                        endFile(holder, timeStamp, adding);
                    else if (!(hourTemp - hour == 0 ^ hourTemp - hour == 1
                                   ^ hourTemp - hour == -23))
                        endFile(holder, timeStamp, adding);
                    else {
                        writeOutput.println(holder);
                        writeOutput.println(timeStamp);
                    }
                }
                else {
                    writeOutput.println(holder);
                    writeOutput.println(timeStamp);
                }
                month = monthTemp;
                hour = hourTemp;
            }
            else { 
                if (!(endCount == 4 ^ endCount == 2))
                    writeOutput.println(holder);
            }
            endCount++;      
            if (endCount == 5)
                endCount = 0;
        }
        adding.close();
        writeOutput.close();
        
        transfer(output, masterTab, true);
        purify(masterTab);
    }
    public static void main(String[] args) throws IOException {
        File toCheck = new File("masterTab.txt");
        System.out.println(DataPoint.validCheck(toCheck));
        
        DataPoint[] moreCheck = DataPoint.fromFile(toCheck);
        DataPoint.writeOver(toCheck, purify(moreCheck));
        System.out.println(DataPoint.validCheck(toCheck));
    }
}
