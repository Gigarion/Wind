public class SortKey implements Comparable<SortKey> {
    private long key;
    private int index;
    public SortKey (long keyVal, int indexVal) {
        key = keyVal;
        index = indexVal;
    }
    public long getKey() {
        return this.key;
    }
    public int getIndex() {
        return this.index;
    }
    
    public int compareTo(SortKey a) {
        if (this.key < a.key) return -1;
        else if (this.key > a.key) return 1;
        else return 0;
    }
}