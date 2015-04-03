// majority of this class taken from the Princeton computer science booksite
public class Counter implements Comparable<Counter> {
    
    private final String name;
    private int count = 0;
    
    public Counter(String id) {
        name = id;
    }
    
    public void increment() {
        count++;
    }
    
    public int tally() {
        return count;
    }
    
    public String toString() {
        return name + ": " + count;
    }
    
    public int compareTo(Counter that) {
        if (this.count < that.count) return -1;
        else if (this.count > that.count) return +1;
        else return 0;
    }
}