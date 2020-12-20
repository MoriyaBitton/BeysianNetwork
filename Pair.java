public class Pair {

    private Variable key; //left
    private String val; //right

    public Pair(Variable key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public Variable getKey() {
        return key;
    }

    /**
     * compare between two pairs
     *
     * @param p
     * @return
     */
    public boolean compare(Pair p) {
        if ((this.getKey() == p.getKey()) && (this.getVal().equals(p.getVal()))) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", val='" + val + '\'' +
                '}';
    }
}
