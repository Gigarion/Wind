import java.io.*;
import java.util.Scanner;

public class DataPoint {
    private final double AIRDENSITY = 1.225;
    
    private final int month;
    private final int day;
    private final int year;
    
    private final int hour;
    private final int minute;
    
    private final double speed;
    private final double pdensity;
    
    private final int direction;
    
    public DataPoint(String dateStr, String timeStr, double spd, int dir) {
        this.month = Integer.parseInt(dateStr.substring(0, 2));
        this.day = Integer.parseInt(dateStr.substring(3, 5));
        this.year = Integer.parseInt(dateStr.substring(6, 10));
        
        this.hour = Integer.parseInt(timeStr.substring(0, 2));
        this.minute = Integer.parseInt(timeStr.substring(3, 5));
        
        this.speed = spd * .447;
        this.pdensity = spd * spd * spd * AIRDENSITY;
        this.direction = dir;
    }
    
    public boolean equals(DataPoint a) {
        boolean monthTest = (this.month == a.month);
        boolean dayTest = (this.day == a.day);
        boolean yearTest = (this.year == a.year);
        boolean hourTest = (this.hour == a.hour);
        boolean minuteTest = (this.minute == a.minute);
        if (monthTest && dayTest && yearTest && hourTest && minuteTest)
            return true;
        else return false;
    }
    
    public static int validCheck(File source) throws IOException {
        Scanner validCheck = new Scanner(source);
        int place = 0;
        int count = 0;
        
        while (validCheck.hasNext()) {
            validCheck.next();
            place++;
            if (place == 4) {
                count++;
                place = 0;
            }
        }
        validCheck.close();
        
        if (place != 0) {
            System.out.println();
            System.out.println("Invalid Data, please reformat");
            return -place;
        }
        else return count;
    }
    
    public void printOne(File target, PrintWriter refill) {
        refill.println(this.getDate());
        refill.println(this.getTime());
        refill.println(this.getSpeed());
        refill.println(this.getDirection());
    }
    
    public static void writeOver(File target, DataPoint[] data) 
        throws IOException {
        PrintWriter refill = new PrintWriter(target);
        for (int i = 0; i < data.length; i++) {
            data[i].printOne(target, refill);
        }
        refill.close();
    } 
    
    public void addOne(File target) throws IOException {
        File temp = new File("temp.txt");
        PrintWriter spewData = new PrintWriter(temp);
        temp.createNewFile();
        Scanner keepData = new Scanner(target);
        while(keepData.hasNext()) 
            spewData.println(keepData.next());
        keepData.close();
        
        printOne(temp, spewData);
        
        spewData.close();
        Organize.transfer(temp, target, true);
    }
    
    public void addSelect(File target, boolean date, boolean time,
                          boolean speed, boolean direction) throws IOException
    {
        File temp = new File("temp.txt");
        PrintWriter spewData = new PrintWriter(temp);
        
        if (!temp.createNewFile()) {
            Scanner keepData = new Scanner(target);
            while(keepData.hasNext()) 
                spewData.println(keepData.next());
            keepData.close();
        }        
        
        if (date)
            spewData.println(this.getDate());
        if (time)
            spewData.println(this.getTime());
        if (speed)
            spewData.println(speed);
        if (direction)
            spewData.println(direction);
        
        spewData.close();
        
        Organize.transfer(temp, target, true);
    }
    
    public static void writeOn(File target, DataPoint[] data) 
        throws IOException {
        File temp = new File("temp.txt");
        temp.createNewFile();
        PrintWriter spewData = new PrintWriter(temp);
        if (!target.createNewFile()) {
            Scanner keepData = new Scanner(target);
            
            while (keepData.hasNext())
                spewData.println(keepData.next());
            keepData.close();
        }
        
        for (int i = 0; i < data.length; i++) {
            data[i].printOne(target, spewData);
        }
        spewData.close();
        Organize.transfer(temp, target, true);
    }
    
    private static String smallFix(int toFix) {
        String fixed = Integer.toString(toFix);
        if (toFix < 10)
            fixed = 0 + fixed;
        return fixed;              
    }
    
    public String getDate() {
        String dateStamp = smallFix(this.month) + "/" 
            + smallFix(this.day) + "/" + Integer.toString(this.year);
        return dateStamp;
    }
    
    public String getTime() {
        String timeStamp = smallFix(this.hour)  + ":" 
            + smallFix(this.minute) + ":00";    
        return timeStamp;
    }
    
    public int getDirection() {
        return this.direction;
    }
    
    public double getSpeed() {
        return this.speed;
    }
    
    public double getDensity() {
        return this.pdensity;
    }
    
    public int getYear() {
        return this.year;
    }
    
    public int getMonth() {
        return this.month;
    }
    
    public int getDay() {
        return this.day;
    }
    
    public int getHour() {
        return this.hour;
    }
    
    public int getMinute() {
        return this.minute;
    }
    
    public static DataPoint[] fromFile(File source) throws IOException {
        int count = validCheck(source);
        if (count > 0) {
            DataPoint[] data = new DataPoint[count];
            
            Scanner toData = new Scanner(source);
            for (int i = 0; i < count; i++) {
                String dateStr = toData.next();
                String timeStr = toData.next();
                double speed = Double.parseDouble(toData.next());
                int direction = Integer.parseInt(toData.next());
                data[i] = new DataPoint(dateStr, timeStr, speed, direction); 
            }
            toData.close();
            return data;
        }
        else return null;
    }
    
    public static DataPoint[] fromFile(String fileName) throws IOException {
        File toRead = new File(fileName);
        return fromFile(toRead);
    }
    
    public long stamp() {
        String toTurn = Integer.toString(this.year)
            + smallFix(this.month) 
            + smallFix(this.day) + smallFix(this.hour) + smallFix(this.minute);
        long stampVal = Long.parseLong(toTurn);
        return stampVal;
    }
    
    public static void main(String[] args) throws IOException {

    }
}