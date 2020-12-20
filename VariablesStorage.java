import java.util.HashMap;

public class VariablesStorage {

    private HashMap<String, Variable> varCollection; //name of var and the var himself

    public VariablesStorage() {
        this.varCollection = new HashMap<>();
        varCollection.put("none", new Variable("none"));
    }

    public void addVarToCollection(String name, Variable var) {
        this.varCollection.put(name, var);
    }

    public HashMap<String, Variable> getVarCollection() {
        return varCollection;
    }

    /**
     * copy var collection
     *
     * @return
     */
    public HashMap<String, Variable> copyVarCollection() {
        HashMap<String, Variable> copyVarColl = new HashMap<>();
        for (String name : this.getVarCollection().keySet()) {
            copyVarColl.put(name, this.getVarCollection().get(name));
        }
        return copyVarColl;
    }

    /**
     * get var by name
     *
     * @param name
     * @return
     */
    public Variable getVarByName(String name) {
        if (name == null) return null;
        Variable curr_var = this.getVarCollection().get(name);
        return curr_var;
    }
}
