import java.io.*;
//import acm.graphics.*;

public class Analyze {
    
    private static final int BINS = 12;
    
    private static final double CUTIN = 2.68;
    
    public static void printBlock(String toPrint)  {
        System.out.println("************************************************");
        System.out.println(toPrint);
        System.out.println("************************************************");
        System.out.println();
    }
    public static void printBlock(String[] toPrint) {
        System.out.println("************************************************");
        for (int i = 0; i < toPrint.length; i++)
            System.out.println(toPrint[i]);
        System.out.println("************************************************");
        System.out.println();
    }
    
    public static void basicStats(File source) throws IOException {
        basicStats(DataPoint.fromFile("masterTab.txt"));
    }
    public static void basicStats(DataPoint[] data) throws IOException {
        
        // print date range
        printBlock("Data from " + data[0].getDate() + " to "
                       + data[data.length - 1].getDate());
        
        /*****************************************\
          * calculate basic stats:  average speed, 
          * average direction, average power density
          \*****************************************/
        double avgSpeed = 0;
        double avgDirection = 0;
        double avgDensity = 0;
        Counter aboveCut = new Counter("Above Cut-in");
        for (int i = 0; i < data.length; i++) {
            avgSpeed += data[i].getSpeed();
            avgDirection += data[i].getDirection();
            avgDensity += data[i].getDensity();
            if (data[i].getSpeed() > CUTIN)
                aboveCut.increment();
        }
        avgSpeed /= data.length;
        avgDirection /= data.length;
        avgDensity /= data.length;
        
        // make them strings
        String speedText = "Average wind speed over set:   " 
            +  String.format("%5.2f", avgSpeed) + " m/s";
        String directionText = "Average direction over set:     "
            + String.format("%5g", avgDirection);
        String densityText = "Average power density over set: "
            + String.format("%5.2f", avgDensity) + " W/m^2";
        String[] textBlock = {speedText, densityText, directionText};
        
        printBlock(textBlock);
        
        double percentAbove = (double) aboveCut.tally()
            / data.length * 100;  
        printBlock(String.format("Percent of data above cut-in: %2.2f",
                                 percentAbove));
        double daysAbove = 3.65 * percentAbove;
        printBlock(String.format("Days worth of data above cut-in: %5.2f", 
                                 daysAbove));
        
    }
    
    public static void graphSet(DataPoint[] toGraph) {
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
        
        StdDraw.setCanvasSize(1900, 200);
        StdDraw.setXscale(-1, toGraph.length + 1);
        StdDraw.setYscale(-1, max + 3);
        StdDraw.line(0, CUTIN, toGraph.length, CUTIN);
        
        for (int i = 0; i < toGraph.length - 1; i++) {
            if (toGraph[i].getSpeed() > CUTIN) {
                StdDraw.setPenColor(StdDraw.BLUE);
            }
            else
                StdDraw.setPenColor(StdDraw.BLACK);
            
            StdDraw.line(i, toGraph[i].getSpeed(),
                         i + 1, toGraph[i + 1].getSpeed());
        }
    }
    
    public static void graphSetMonth(DataPoint[] data, int month, int year) {
        Counter findDate = new Counter("date find");
        boolean inYear = false;
        boolean inMonth = false;
        if (data[0].getYear() == year)
            inYear = true;
        for (int i = 0; !inYear; i++) {
            if (data[i].getYear() < year)
                findDate.increment();
            else if (data[i].getYear() == year)
                inYear = true;
            else {
                printBlock("YEAR NOT IN DATA SET");
                return;
            }
        }
        int firstIndex = findDate.tally();
        System.out.println(firstIndex);
        if (firstIndex != data.length) {
            for (int i = 0; !inMonth; i++) {
                if (data[i].getMonth() == month)
                    findDate.increment();
                else if (data[i].getMonth() > month)
                    inMonth = true;
                else {
                    printBlock("MONTH NOT IN DATA SET");
                    return;
                }
            }
        }
        int lastIndex = findDate.tally() - 1;
        System.out.println(firstIndex);
        System.out.println(lastIndex);
        System.out.println(data.length);
        int difference = lastIndex - firstIndex;
        DataPoint[] cutData = new DataPoint[difference + 1];
        System.out.println(cutData.length);
        for (int i = firstIndex; i <= lastIndex; i++)
            cutData[i - firstIndex] = data[i];
        graphSet(cutData);
    }
    
    public static void graphSetYear(DataPoint[] data, int year) {
        Counter findYear = new Counter("year find");
        boolean inYear = false;
        if (data[0].getYear() == year)
            inYear = true;
        for (int i = 0; !inYear; i++) {
            if (data[i].getYear() < year)
                findYear.increment();
            else if (data[i].getYear() == year)
                inYear = true;
            else {
                printBlock("YEAR NOT IN DATA SET");
                return;
            }
        }
        int firstIndex = findYear.tally();
        
        if (firstIndex != data.length) {
            for (int i = firstIndex; inYear; i++) {
                if (findYear.tally() == data.length)
                    inYear = false;
                else if (data[i].getYear() == year)
                    findYear.increment();
                else if (data[i].getYear() > year)
                    inYear = false;
                else {
                    printBlock("YEAR NOT IN DATA SET");
                    return;
                }
            }
        }
        int lastIndex = findYear.tally() - 1;
        int difference = lastIndex - firstIndex;
        DataPoint[] cutData = new DataPoint[difference + 1];
        System.out.println(data.length);
        for (int i = firstIndex; i <= lastIndex; i++)
            cutData[i - firstIndex] = data[i];
        graphSet(cutData);
    }
    
    public static void monthStats(int month, int year) throws IOException {
        File master = new File("masterTab.txt");
        
        // makes sure there is data to analyze
        if (master.createNewFile()) {
            System.out.println("No DATA STORED"); 
            return;
        }
        
        else {
            DataPoint[] data = new DataPoint[DataPoint.validCheck(master)];
            data = DataPoint.fromFile(master);
            boolean inMonth = false;
            Counter findYear = new Counter("year find");
            boolean inYear = false;
            if (data[0].getYear() == year)
                inYear = true;
            for (int i = 0; !inYear; i++) {
                if (data[i].getYear() < year)
                    findYear.increment();
                else if (data[i].getYear() == year)
                    inYear = true;
                else {
                    printBlock("YEAR NOT IN DATA SET");
                    return;
                }
            }
            int firstIndex = findYear.tally();
            
            if (firstIndex != data.length) {
                for (int i = firstIndex; inMonth; i++) {
                    if (findYear.tally() == data.length)
                        inYear = false;
                    else if (data[i].getMonth() == month)
                        findYear.increment();
                    else if (data[i].getMonth() > month)
                        inYear = false;
                    else {
                        printBlock("MONTH NOT IN DATA SET");
                        return;
                    }
                }
            }
            System.out.println(firstIndex);
            
            int lastIndex = findYear.tally() - 1;
            System.out.println(lastIndex);
            int difference = lastIndex - firstIndex;
            DataPoint[] cutData = new DataPoint[difference + 1];
            System.out.println(cutData.length);
            for (int i = firstIndex; i <= lastIndex; i++)
                cutData[i - firstIndex] = data[i];
            
            // print date range
            printBlock("Data from " + cutData[0].getDate() + " to "
                           + cutData[data.length - 1].getDate());
            
            /*****************************************\
              * calculate basic stats:  average speed, 
              * average direction, average power density
              \*****************************************/
            double avgSpeed = 0;
            double avgDirection = 0;
            double avgDensity = 0;
            Counter aboveCut = new Counter("Above Cut-in");
            for (int i = 0; i < cutData.length; i++) {
                avgSpeed += cutData[i].getSpeed();
                avgDirection += cutData[i].getDirection();
                avgDensity += cutData[i].getDensity();
                if (cutData[i].getSpeed() > CUTIN)
                    aboveCut.increment();
            }
            avgSpeed /= cutData.length;
            avgDirection /= cutData.length;
            avgDensity /= cutData.length;
            
            // make them strings
            String speedText = "Average wind speed over set:   " 
                +  String.format("%5.2f", avgSpeed) + " m/s";
            String directionText = "Average direction over set:     "
                + String.format("%5g", avgDirection);
            String densityText = "Average power density over set: "
                + String.format("%5.2f", avgDensity) + " W/m^2";
            String[] textBlock = {speedText, densityText, directionText};
            
            printBlock(textBlock);
            
            double percentAbove = (double) aboveCut.tally()
                / cutData.length * 100;  
            printBlock(String.format("Percent of data above cut-in: %2.2f",
                                     percentAbove));
            double daysAbove = 3.65 * percentAbove;
            printBlock(String.format("Days worth of data above cut-in: %5.2f", 
                                     daysAbove));
            
            graphSetMonth(cutData, month, year);          
        }
    }
    
    public static void windRose(DataPoint[] data) {
        Bin[] bin = new Bin[BINS];
        int binWidth = 360 / BINS;
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
        StdDraw.clear();
        StdDraw.setXscale(-max, max);
        StdDraw.setYscale(-max, max);
        
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
            
            StdDraw.setPenColor(StdDraw.GRAY);
            StdDraw.line(0, 0, line1x, line1y);
            StdDraw.line(0, 0, line2x, line2y);
            if (bin[i].getCount() == max) {
                StdDraw.setPenColor(StdDraw.RED);
                maxBin = i;
            }
            else
                StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.filledPolygon(xPoints, yPoints);
            
            StdDraw.setPenColor(StdDraw.BLACK);
            double xAvg = (line1x + line2x) * .80 / 2;
            double yAvg = (line1y + line2y) * .80 / 2;
            StdDraw.text(xAvg, yAvg, bin[i].getLow() + " to " 
                             + bin[i].getHigh());
        }
        StdDraw.circle(0, 0, max);
        String printMe = "Primary wind direction: "
            + bin[maxBin].getLow() + " to " 
            + bin[maxBin].getHigh() + " degrees from North";
        StdDraw.text(0, max * 0.90, "NORTH");
        printBlock(printMe);
    }
    
    public static DataPoint[] trimData(DataPoint[] data, int month, int year) {
        
        boolean inMonth = false;
        Counter findYear = new Counter("year find");
        boolean inYear = false;
        if (data[0].getYear() == year)
            inYear = true;
        for (int i = 0; !inYear; i++) {
            if (data[i].getYear() < year)
                findYear.increment();
            else if (data[i].getYear() == year)
                inYear = true;
            else {
                printBlock("YEAR NOT IN DATA SET");
                return null;
            }
        }
        int firstIndex = findYear.tally();
        
        if (firstIndex != data.length) {
            for (int i = firstIndex; inMonth; i++) {
                if (findYear.tally() == data.length)
                    inYear = false;
                else if (data[i].getMonth() == month)
                    findYear.increment();
                else if (data[i].getMonth() > month)
                    inYear = false;
                else {
                    printBlock("MONTH NOT IN DATA SET");
                    return null;
                }
            }
        }
        System.out.println(firstIndex);
        
        int lastIndex = findYear.tally() - 1;
        System.out.println(lastIndex);
        int difference = lastIndex - firstIndex;
        DataPoint[] cutData = new DataPoint[difference + 1];
        System.out.println(cutData.length);
        for (int i = firstIndex; i <= lastIndex; i++)
            cutData[i - firstIndex] = data[i];
        
        // print date range
        printBlock("Data from " + cutData[0].getDate() + " to "
                       + cutData[data.length - 1].getDate());
        
        return cutData;
    }
    
    public static DataPoint[] cutData(int month, int year) {
        File master = new File("masterTab.txt");
        return cutData(master, month, year);
    }
    
    public static DataPoint[] cutData(File source, int month, int year) {
        return cutData(source, month, year);
    }
    
    public static DataPoint[] cutData(File source, int month) {
        
    }
    
    public static void main(String[] args) throws IOException {
        windRose(DataPoint.fromFile("masterTab.txt"));
    }
}