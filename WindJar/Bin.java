public class Bin {
    private final int lowLim;
    private final int highLim;
    private Counter binCount;
    public Bin(int low, int high) {
        lowLim = low;
        highLim = high;
        binCount = new Counter(lowLim + " to " + highLim);
    }
    
    public void increment() {
        binCount.increment();
    }
    
    public int getLow() {
        return lowLim;
    }
    
    public int getHigh() {
        return highLim;
    }
    
    public int getCount() {
        return binCount.tally();
    }
}