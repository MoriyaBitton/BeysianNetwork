import java.util.ArrayList;

public class Evidence {

    //priority, right side
    private ArrayList<Pair> condition;

    public Evidence() {
        this.condition = new ArrayList<>();
    }

    public ArrayList<Pair> getCondition() {
        return condition;
    }

    public void addPair(Pair p) {
        condition.add(p);
    }
}
