import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Factor {

    private ArrayList<Variable> factor_variables;
    private ArrayList<ArrayList<String>> table; //row table
    private int numOfSums = 0;
    private int numOfmult = 0;

    public Factor() {
        factor_variables = new ArrayList<>();
        table = new ArrayList<>();
    }

    /**
     * get factor by name
     *
     * @param factor
     * @param all_factors
     * @return
     */
    public String getFactorName(Factor factor, HashMap<String, Factor> all_factors) {
        String name = "";
        for (String factorName : all_factors.keySet()) {
            if (all_factors.get(factorName) == factor) {
                name = factorName;
            }
        }
        return name;
    }

    /**
     * get factor variable
     *
     * @param _evi_list
     * @return
     */
    public ArrayList<Variable> getFactor_variables(ArrayList<Evidence> _evi_list) {
        HashSet<Variable> evidences_ = new HashSet<>();
        for (Evidence evi : _evi_list) {
            for (Pair pair : evi.getCondition()) {
                evidences_.add(pair.getKey());
            }
        }
        ArrayList<Variable> evidences = new ArrayList<>();
        evidences.addAll(evidences_);
        for (Variable eviName : evidences) {
            if (factor_variables.contains(eviName)) {
                factor_variables.remove(eviName);
            }
        }

        return factor_variables;
    }

    public void setFactor_variables(HashSet<Variable> factor_variables) {
        this.factor_variables.addAll(factor_variables);
    }

    public ArrayList<ArrayList<String>> get_table() {
        return table;
    }

    /**
     * creat new factor
     *
     * @param var
     * @param _evi_list
     */
    public void Create(Variable var, ArrayList<Evidence> _evi_list) {
        factor_variables.add(var);
        for (Variable parent : var.getParents()) {
            if (!parent.getName().equals("none")) {
                factor_variables.add(parent);
            }
        }
    }

    /**
     * create all permutation
     * each permutation is a row in my factor table
     *
     * @param i
     * @param curr_permutation
     * @param _var_storage
     */
    public void createAllPermutation(int i, ArrayList<String> curr_permutation, VariablesStorage _var_storage) {
        HashMap<String, Double> my_val_and_pro;
        if (i == factor_variables.size()) {
            ArrayList<Pair> pairs = new ArrayList<>();
            for (int index = 1; index < factor_variables.size(); index++) { // start with 1 to get an array list of pairs who represents the parents only
                pairs.add(new Pair(factor_variables.get(index), curr_permutation.get(index)));
            }

            if (pairs.size() == 0) { // don't have parents
                factor_variables.add(_var_storage.getVarByName("none"));
                pairs.add(new Pair(factor_variables.get(1), ""));
                factor_variables.remove(1);
            }

            my_val_and_pro = factor_variables.get(0).getCpt().getValAndProByParents(pairs);
            curr_permutation.add(String.valueOf(my_val_and_pro.get(curr_permutation.get(0))));
            table.add((ArrayList<String>) curr_permutation.clone());
            curr_permutation.remove(curr_permutation.size() - 1);
            return;
        }
        for (String val : factor_variables.get(i).getValues()) {
            curr_permutation.set(i, val);
            createAllPermutation(i + 1, curr_permutation, _var_storage);
        }
    }

    public void setTable(ArrayList<ArrayList<String>> table) {
        this.table = table;
    }

    /**
     * normalize probability
     *
     * @throws Exception
     */
    public void normalize() throws Exception {
        if (factor_variables.size() > 1)
            throw new Exception("Cannot normalize factor. more then one factorName");
        double alpha;
        double sum = 0.0;
        for (ArrayList<String> row : table) {
            sum += Double.parseDouble(row.get(row.size() - 1));
            numOfSums++;
        }
        numOfSums--;
        alpha = 1.0 / sum;
        double numToSet;
        for (ArrayList<String> row : table) {
            numToSet = Double.parseDouble(row.get(row.size() - 1)) * alpha;
            row.set(row.size() - 1, Double.toString(numToSet));
        }
    }

    /**
     * get index of col
     *
     * @param name
     * @param factorName
     * @return
     */
    public int getIndxOfCol(String name, String factorName) {
        String[] factorNameArray = factorName.split(",");
        for (int i = 0; i < factorNameArray.length; i++) {
            if (factorNameArray[i].equals(name)) {
                return i;
            }
        }
        return -1; // factor name does not contain "name"
    }
}
