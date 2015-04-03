public class Turbine {
    private final double cutIn;
    private final int rating;
    private static final double PERFCUT = 0.4;
    public Turbine(double cut, int rate) {
        this.cutIn = cut;
        this.rating = rate;
    }
    public double getCutIn() {
        return cutIn;
    }
    
    public int getRating() {
        return rating;
    }
}