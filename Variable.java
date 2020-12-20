import java.util.ArrayList;

public class Variable {

    private String name;
    private ArrayList<String> values;
    private ArrayList<Variable> parents;
    private CPT cpt;

    public Variable(String name) {
        this.name = name;
        this.values = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.cpt = new CPT();
    }

    public Variable(String name, ArrayList<String> values, ArrayList<Variable> parents, CPT cpt) {
        this.name = name;
        this.values = values;
        this.parents = parents;
        this.cpt = cpt;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public ArrayList<Variable> getParents() {
        return parents;
    }

    public CPT getCpt() {
        return cpt;
    }

}






