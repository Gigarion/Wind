import java.io.*;

public class Analyze {
    
    private static final int ROSEBINS = 12;
    private static final int HOURS = 24;
    
    private static final double CUTIN = 2.68;
    
    private static final String[] MONTH = {"January", "February", "March", 
        "April", "May", "June", "July", "August", "September", "October",
        "November", "December"};
    
    private static void printBlock(String toPrint)  {
        System.out.println("************************************************");
        System.out.println(toPrint);
        System.out.println("************************************************");
        System.out.println();
    }
    private static void printBlock(String[] toPrint) {
        System.out.println("************************************************");
        for (int i = 0; i < toPrint.length; i++)
            System.out.println(toPrint[i]);
        System.out.println("************************************************");
        System.out.println();
    }
    
    private static void basicStats(DataPoint[] data) throws IOException {
        
        // print date range
        printBlock("Data from " + data[0].getDate() + " to "
                       + data[data.length - 1].getDate());
        
        /*****************************************\
          * calculate basic stats:  average speed, 
          average power density
          \*****************************************/
        double avgSpeed = 0;
        double avgDensity = 0;
        Counter aboveCut = new Counter("Above Cut-in");
        for (int i = 0; i < data.length; i++) {
            avgSpeed += data[i].getSpeed();
            avgDensity += data[i].getDensity();
            if (data[i].getSpeed() > CUTIN)
                aboveCut.increment();
        }
        avgSpeed /= data.length;
        avgDensity /= data.length;
        
        // make them strings
        String speedText = "Average wind speed over set:   " 
            +  String.format("%5.2f", avgSpeed) + " m/s";
        String densityText = "Average power density over set: "
            + String.format("%5.2f", avgDensity) + " W/m^2";
        String[] textBlock = {speedText, densityText};
        
        printBlock(textBlock);
        
        double percentAbove = (double) aboveCut.tally()
            / data.length * 100;  
        printBlock(String.format("Percent of data above cut-in: %2.2f",
                                 percentAbove));
        double daysAbove = 3.65 * percentAbove;
        printBlock(String.format("Days worth of data above cut-in: %5.2f", 
                                 daysAbove));
        
    }
    
    private static void graphSet(DataPoint[] toGraph) {
        // creates a graph of the points as compared to the cutin 
        // finds max and min speed values to set graph scale
        double max = Integer.MIN_VALUE;
        double min = Integer.MAX_VALUE;
        
        for (int i = 0; i < toGraph.length; i++) {
            if (toGraph[i].getSpeed() > max)
                max = toGraph[i].getSpeed();
            if (toGraph[i].getSpeed() < min)
                min = toGraph[i].getSpeed();
        }
        
        MyDraw.setCanvasSize(1900, 200);
        MyDraw.setXscale(-1, toGraph.length + 1);
        MyDraw.setYscale(-1, max + 3);
        MyDraw.line(0, CUTIN, toGraph.length, CUTIN);
        
        printBlock("Please wait, producing graphs");
        
        for (int i = 0; i < toGraph.length - 1; i++) {
            if (toGraph[i].getSpeed() > CUTIN) {
                MyDraw.setPenColor(MyDraw.BLUE);
            }
            else
                MyDraw.setPenColor(MyDraw.BLACK);
            
            //MyDraw.line(i, toGraph[i].getSpeed(),i + 1, toGraph[i + 1].getSpeed());
            MyDraw.point(i, toGraph[i].getSpeed());
            
        }
        MyDraw.setPenColor(MyDraw.BLACK);
        MyDraw.textLeft(0, (max * .75), toGraph[0].getDate());
        MyDraw.textRight(toGraph.length, (max * .75), 
                         toGraph[toGraph.length - 1].getDate());
    }
    
    private static void windRose(DataPoint[] data) {
        Bin[] bin = new Bin[ROSEBINS];
        int binWidth = 360 / ROSEBINS;
        for (int i = 0; i < bin.length; i++) {
            bin[i] = new Bin(i * binWidth, (i + 1) * binWidth);
            // less than high, greater than or equal to low
        }
        for (int i = 0; i < data.length; i++) {
            int check = data[i].getDirection();
            for (int j = 0; j < bin.length; j++) {
                if (check >= bin[j].getLow() && check < bin[j].getHigh())
                    bin[j].increment();
            }
        }
        int max = 0;
        for (int i = 0; i < bin.length; i++)
            if (bin[i].getCount() > max) max = bin[i].getCount();
        MyDraw.clear();
        MyDraw.setCanvasSize(500, 500);
        MyDraw.setXscale(-max, max);
        MyDraw.setYscale(-max, max);
        
        int maxBin = 0;
        
        for(int i = 0; i < bin.length; i++) {
            double toRad1 = (2 * Math.PI) 
                - ((i * binWidth) * 2 * Math.PI / 360) + Math.PI / 2;
            double toRad2 = (2 * Math.PI) 
                - (((i + 1) * binWidth) * 2 * Math.PI / 360)  
                + Math.PI / 2;
            
            double[] xPoints = new double[3];
            double[] yPoints = new double[3];
            
            xPoints[0] = 0;
            xPoints[1] = bin[i].getCount() * Math.cos(toRad1);
            xPoints[2] = bin[i].getCount() * Math.cos(toRad2);
            
            yPoints[0] = 0;
            yPoints[1] = bin[i].getCount() * Math.sin(toRad1);
            yPoints[2] = bin[i].getCount() * Math.sin(toRad2);
            
            double line1x = max * Math.cos(toRad1);
            double line2x = max * Math.cos(toRad2);
            double line1y = max * Math.sin(toRad1);
            double line2y = max * Math.sin(toRad2);
            
            MyDraw.setPenColor(MyDraw.GRAY);
            MyDraw.line(0, 0, line1x, line1y);
            MyDraw.line(0, 0, line2x, line2y);
            if (bin[i].getCount() == max) {
                MyDraw.setPenColor(MyDraw.RED);
                maxBin = i;
            }
            else
                MyDraw.setPenColor(MyDraw.BLUE);
            MyDraw.filledPolygon(xPoints, yPoints);
            
            MyDraw.setPenColor(MyDraw.BLACK);
            double xAvg = (line1x + line2x) * .80 / 2;
            double yAvg = (line1y + line2y) * .80 / 2;
            MyDraw.text(xAvg, yAvg, bin[i].getLow() + " to " 
                            + bin[i].getHigh());
        }
        MyDraw.circle(0, 0, max);
        String printMe = "Primary wind direction: "
            + bin[maxBin].getLow() + " to " 
            + bin[maxBin].getHigh() + " degrees from North";
        MyDraw.text(0, max * 0.90, "NORTH");
        printBlock(printMe);
    }
    
    private static DataPoint[] trimData(DataPoint[] data, int month, int year) {
        boolean inYear = false;
        boolean inMonth = false;
        Counter index = new Counter("index holder");
        if (data[0].getYear() > year) {
            System.out.println("YEAR NOT IN RANGE");
            return null;
        }
        for (int i = 0; !inYear || !inMonth; i++) {
            if (data[i].getYear() == year) inYear = true;
            if (data[i].getMonth() == month) inMonth = true;
            else inMonth = false;
            index.increment();
            if (index.tally() == data.length) {
                System.out.println("MONTH NOT IN RANGE");
                return null;
            }
        }
        int startIndex = index.tally();
        
        for (int i = startIndex; inMonth; i++) {
            if (data[i].getMonth() != month) inMonth = false;
            index.increment();
        }
        int range = index.tally() - startIndex - 1;
        DataPoint[] cutData = new DataPoint[range];
        for (int i = 0; i < range; i++)
            cutData[i] = data[startIndex + i];
        return cutData;
    }
    
    private static DataPoint[] getSingleMonthData(int month, int year) 
        throws IOException {
        DataPoint[] allData = DataPoint.fromFile("masterTab.txt");
        return trimData(allData, month, year);
    }
    
    
    private static DataPoint[] getYearData(int year) throws IOException {
        DataPoint[] data = DataPoint.fromFile("masterTab.txt");
        boolean inYear = false;
        Counter index = new Counter("index holder");
        if (data[0].getYear() > year) {
            System.out.println("YEAR NOT IN RANGE");
            return null;
        }
        for (int i = 0; !inYear; i++) {
            if (data[i].getYear() == year) inYear = true;
            index.increment();                
        }
        int startIndex = index.tally();
        for (int i = 0; inYear; i++) {
            if (data[i].getYear() != year) inYear = false;
            index.increment();
            if (index.tally() == data.length) {
                inYear = false;
            }
        }
        int range = index.tally() - startIndex - 1;
        DataPoint[] cutData = new DataPoint[range];
        for (int i = 0; i < range; i++)
            cutData[i] = data[startIndex + i];
        return cutData;
    }
    
    public static void analyzeAll() throws IOException {
        File master = new File("masterTab.txt");
        DataPoint[] allData = DataPoint.fromFile(master);
        if (master.createNewFile()) {
            System.out.println("NO DATA AVAILABLE");
            return;
        }
        else {
            basicStats(allData);
            graphSet(allData);
            MyDraw.save("TotalGraph.png");
            windRose(allData);
            MyDraw.save("TotalWindRose.png");
            diurnal(allData);
        }
    }
    
    public static void analyzeMonth(int month, int year) throws IOException {
        DataPoint[] monthData = getSingleMonthData(month, year);
        basicStats(monthData);
        graphSet(monthData);
        MyDraw.save(MONTH[month - 1] + year + "TimeGraph.png");
        windRose(monthData);
        MyDraw.save(MONTH[month - 1] + year + "Rose.png");
        diurnal(monthData);
    }
    
    public static void analyzeYear(int year) throws IOException {
        DataPoint[] yearData = getYearData(year);
        basicStats(yearData);
        graphSet(yearData);
        MyDraw.save(year + "TimeGraph.png");
        windRose(yearData);
        MyDraw.save(year + "Rose.png");
        diurnal(yearData);
    }
    
    private static void diurnal(DataPoint[] data) throws IOException {
        double[] speeds = new double[HOURS];
        Counter[] hourCount = new Counter[HOURS];
        for (int i = 0; i < HOURS; i++)
            hourCount[i] = new Counter(i + "hour counter");
        for (int i = 0; i < data.length; i++) {
            speeds[data[i].getHour()] += data[i].getSpeed();
            hourCount[data[i].getHour()].increment();
        }
        String rangeStamp = MONTH[data[0].getMonth() - 1] + data[0].getYear() 
            + " to " + MONTH[data[data.length - 2].getMonth()] 
            + data[0].getYear();
        File diurnalText = new File("Diurnal " + rangeStamp + ".txt");
        diurnalText.createNewFile();
        PrintStream diurnalWrite = new PrintStream(diurnalText);
        
        String[] strings = new String[HOURS];
        diurnalWrite.println(rangeStamp);
        diurnalWrite.println("Average speed per hour:");
        for (int i = 0; i < HOURS; i++) {
            double tempAvg = speeds[i] / hourCount[i].tally();
            strings[i] = i + ": " + String.format("%5.2f", tempAvg);
        }
        System.setOut(diurnalWrite);
        printBlock(strings);
        diurnalWrite.flush();
        System.setOut(System.out);
        diurnalWrite.close();
    }
    
    public static void main(String[] args) throws IOException {
        analyzeAll();
    }
}