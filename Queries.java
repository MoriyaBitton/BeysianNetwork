import java.util.ArrayList;
import java.util.HashMap;

public class Queries {

    private HashMap<Variable, HashMap<String, ArrayList<Evidence>>> queries;

    public Queries() {
        this.queries = new HashMap<>();
    }

    public Queries(HashMap<Variable, HashMap<String, ArrayList<Evidence>>> _queries) {
        this.queries = _queries;
    }

    public HashMap<Variable, HashMap<String, ArrayList<Evidence>>> getQueries() {
        return queries;
    }

}
