import java.util.*;

public class Algo_3 {

    private Variable _var_q;
    private String _value_q;
    private ArrayList<Evidence> _evi_list;
    private VariablesStorage _var_storage;
    public int numOfSums = 0;
    public int numOfmult = 0;

    public Algo_3(Variable _my_var, String _my_values, ArrayList<Evidence> _my_evi_list, VariablesStorage _my_var_storage) {
        this._var_q = _my_var;
        this._value_q = _my_values;
        this._evi_list = _my_evi_list;
        this._var_storage = _my_var_storage;
        this.numOfSums = 0;
        this.numOfmult = 0;
    }

    /**
     * run class Algo_2
     *
     * @return
     */
    public Answer run() {
        HashMap<String, Variable> _copy_var_coll = _var_storage.copyVarCollection();
        _copy_var_coll.remove("none");
        ArrayList<Variable> var_list = getVarList();
        ArrayList<Variable> vars_hidden_array = filterVariables(_copy_var_coll); //find all hidden

        HashMap<String, Factor> all_factors = new HashMap<>(); //list of all my factors : <name + par, factor>

        for (Variable variable : var_list) { //send a var and create his factor
            String name_factor = variable.getName() + ",";
            for (Variable parent : variable.getParents()) {
                if (!parent.getName().equals("none")) {
                    name_factor += parent.getName() + ",";
                }
            }
            all_factors.put(name_factor.substring(0, name_factor.length() - 1), genFactor(variable, name_factor));
        }

        // Join factors by eliminate system; algo 3 case: num of parents
        deleteEvidenceVal(all_factors);
        Factor union_factor = joinFactors(all_factors, vars_hidden_array);

        try {
            assert union_factor != null;
            union_factor.normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int row = 0;
        for (int i = 0; i < union_factor.getFactor_variables(_evi_list).size(); i++) { //col of var name
            if (_var_q == (union_factor.getFactor_variables(_evi_list).get(i))) {
                for (int j = 0; j < union_factor.get_table().size(); j++) {
                    if (_value_q.equals(union_factor.get_table().get(j).get(i))) {
                        row = j;
                        break;
                    }
                }
            }
        }
        int colIndex = union_factor.get_table().get(row).size() - 1;
        double ans = Double.parseDouble(union_factor.get_table().get(row).get(colIndex));

        return new Answer(ans, numOfSums, numOfmult);
    }

    /**
     * deleted evidences vars from my factors
     * because I get them val.
     *
     * @param all_factors
     */
    private void deleteEvidenceVal(HashMap<String, Factor> all_factors) {
        HashSet<Variable> evidences_ = new HashSet<>();
        for (Evidence evi : _evi_list) {
            for (Pair pair : evi.getCondition()) {
                evidences_.add(pair.getKey());
            }
        }
        ArrayList<Variable> evidences = new ArrayList<>();
        evidences.addAll(evidences_);

        int numOfVar = 0;
        HashMap<String, Factor> factorAllEvi = new HashMap<>();
        for (String factor_name : all_factors.keySet()) {
            numOfVar = all_factors.get(factor_name).getFactor_variables(_evi_list).size();
            if (numOfVar == 0) {
                factorAllEvi.put(factor_name, all_factors.get(factor_name));
            }
        }

        for (String nameToRemove : factorAllEvi.keySet()) {
            all_factors.remove(nameToRemove);
        }

        for (String factor_name : all_factors.keySet()) {
            for (int i = 0; i < evidences.size(); i++) {
                ArrayList<ArrayList<String>> newTable = new ArrayList<>();

                if (factor_name.contains(evidences.get(i).getName())) { // I have a evi var in my factor's collection
                    int evi_index = all_factors.get(factor_name).getIndxOfCol(evidences.get(i).getName(), factor_name);
                    String tempName = factor_name.substring(0, evi_index * 2);
                    if (i > 0) {
                        for (String c : tempName.split(",")) {
                            if (evidences.contains(_var_storage.getVarByName(c))) {
                                evi_index--;
                            }
                        }
                    }
                    Variable evi_var = _var_storage.getVarByName(evidences.get(i).getName());
                    String evi_val = "";
                    for (Evidence evi : _evi_list) {
                        for (Pair pair : evi.getCondition()) {
                            if (pair.getKey() == evi_var) {
                                evi_val = pair.getVal();
                                break;
                            }
                        }
                    }
                    if (evi_index != -1) {
                        for (ArrayList<String> row : all_factors.get(factor_name).get_table()) {
                            if (row.get(evi_index).equals(evi_val)) {
                                row.remove(evi_index);
                                newTable.add(row);
                                all_factors.get(factor_name).getFactor_variables(_evi_list).remove(evidences.get(i).getName());
                            } else {
                                row.remove(evi_index);
                                all_factors.get(factor_name).getFactor_variables(_evi_list).remove(evidences.get(i).getName());
                            }
                        }
                    }
                    all_factors.get(factor_name).setTable(newTable);
                }
            }
        }
    }

    /**
     * remove all my hidden var from my factors collection
     *
     * @param union_factor
     * @param var_to_remove
     * @return
     */
    private HashMap<String, Factor> removeAllHiddenVarEliminate(HashMap<String, Factor> union_factor, Variable var_to_remove) { // delete by hidden and eliminate
        if ((union_factor.size() == 1) && (union_factor.containsKey(var_to_remove.getName()))) {
            return null;
        }
        int numofval = var_to_remove.getValues().size();
        HashMap<String, Factor> factor_eliminate = new HashMap<>();
        String new_name = "";
        int num_of_row = 1;
        int index_of_var_to_remove = 0;
        Factor new_factor = new Factor();
        for (Factor factor : union_factor.values()) {
            index_of_var_to_remove = factor.getIndxOfCol(var_to_remove.getName(), factor.getFactorName(factor, union_factor));
            for (Variable var : factor.getFactor_variables(_evi_list)) {
                if (var != var_to_remove) {
                    num_of_row *= var.getValues().size(); // num of rows in my new factor
                }
            }
            while (new_factor.get_table().size() < num_of_row) {
                for (int i = 0; i < factor.get_table().size(); i++) { // col
                    ArrayList<String> copy_table_row = (ArrayList<String>) factor.get_table().get(i).clone();
                    int counter = 0;

                    for (int j = i + 1; j < factor.get_table().size(); j++) {// col
                        boolean is_equal = true;
                        for (int index = 0; index < (factor.get_table().get(i).size() - 1) & is_equal; index++) {
                            if (index != index_of_var_to_remove) {
                                if (!factor.get_table().get(i).get(index).equals(factor.get_table().get(j).get(index))) {
                                    is_equal = false;
                                }
                            }
                        }
                        if (is_equal) {
                            double pro_1 = Double.parseDouble(copy_table_row.get(copy_table_row.size() - 1));
//                            System.out.println("pro num 1 : " + pro_1);
                            double pro_2 = Double.parseDouble(factor.get_table().get(j).get(factor.get_table().get(0).size() - 1));
//                            System.out.println("pro num 2 : " + pro_2);
                            double new_pro = pro_1 + pro_2;
                            counter++;
//                            System.out.println("new pro (pro 1 + pro 2) : " + new_pro);
//                            System.out.println();
                            numOfSums++;
                            copy_table_row.set(factor.get_table().get(0).size() - 1, String.valueOf(new_pro));
                            if (counter + 1 == numofval) {
                                new_factor.get_table().add(copy_table_row);
                            }
                        }
                    }
                }
            }

            String factorName = factor.getFactorName(factor, union_factor);
            for (String name : factorName.split(",")) {
                if (!name.equals(var_to_remove.getName())) {
                    new_name += name + ",";
                }
            }
            new_name = new_name.substring(0, new_name.length() - 1);

            HashSet<Variable> factor_eliminate_variable_collection = new HashSet<>();
            for (Variable var : factor.getFactor_variables(_evi_list)) {
                if (!var.getName().equals(var_to_remove.getName())) {
                    factor_eliminate_variable_collection.add(var);
                }
            }
            new_factor.setFactor_variables(factor_eliminate_variable_collection);
        }

        for (ArrayList<String> remove_hidden : new_factor.get_table()) {
            remove_hidden.remove(index_of_var_to_remove);
        }

        factor_eliminate.put(new_name, new_factor);
        return factor_eliminate;
    }

    /**
     * join factors collection
     * I can join two rows with the same values in each col, but difference val in "hidden col"
     *
     * @param all_factors
     * @param vars_hidden_array
     * @return
     */
    private Factor joinFactors(HashMap<String, Factor> all_factors, ArrayList<Variable> vars_hidden_array) {
        // two for a time
        int i = 0;
        Collections.sort(vars_hidden_array, (a, b) -> Integer.compare(b.getParents().size(), a.getParents().size()));
        HashMap<String, Factor> factor_to_be_added = new HashMap<>();

        for (Variable var_hidden : vars_hidden_array) {
            ArrayList<String> factor_to_delete = new ArrayList<>();
            HashMap<String, Factor> factors_contains_hidden = new HashMap<>();
            for (String factor_name : all_factors.keySet()) {
                if (factor_name.contains(var_hidden.getName())) {
                    factors_contains_hidden.put(factor_name, all_factors.get(factor_name));
                    factor_to_delete.add(factor_name);
                }
            }
            for (String to_delete : factor_to_delete) {
                all_factors.remove(to_delete);
            }
            HashMap<String, Factor> tempFactor = join(factors_contains_hidden);
            factor_to_be_added.putAll(removeAllHiddenVarEliminate(tempFactor, var_hidden));// eliminate my union factor and only one with this hidden
            for (String sameName : factor_to_be_added.keySet()) {
                if (all_factors.containsKey(sameName)) {
                    all_factors.putAll(mergeTwoFactorsFinal(factor_to_be_added.get(sameName), all_factors.get(sameName), sameName));
                } else {
                    all_factors.putAll(factor_to_be_added);
                }
            }
            for (String key : factor_to_be_added.keySet()) {
                factor_to_be_added.remove(key);
            }
        }

        for (Factor factor : all_factors.values()) {
            return factor;
        }
        return null;
    }

    /**
     * join two factors
     *
     * @param factorsContainsHidden
     * @return
     */
    private HashMap<String, Factor> join(HashMap<String, Factor> factorsContainsHidden) {
        HashMap<String, Factor> margeFactors = new HashMap<>();
        HashMap<String, Factor> ansFactorHashMap = new HashMap<>();
        ArrayList<String> to_delete = new ArrayList<>();
        String tempFactorName = "";

        while (factorsContainsHidden.size() > 1) {
            Factor temp_factor = null;
            for (String factor_key : factorsContainsHidden.keySet()) {
                if (temp_factor == null) {
                    temp_factor = factorsContainsHidden.get(factor_key);
                    tempFactorName = factor_key;
                    to_delete.add(factor_key);
                } else {
                    to_delete.add(factor_key);
                    margeFactors.putAll(mergeTwoFactors(temp_factor, factorsContainsHidden.get(factor_key), factorsContainsHidden));
                    temp_factor = null;
                }
            }

            for (String delete : to_delete) {
                factorsContainsHidden.remove(delete);
            }
            for (String key : margeFactors.keySet()) { //infinity loop
                factorsContainsHidden.put(key, margeFactors.get(key));
            }
            for (String key : factorsContainsHidden.keySet()) {
                margeFactors.remove(key);
            }

            if (temp_factor != null) {
                factorsContainsHidden.put(tempFactorName, temp_factor);
            }
        }

        if (ansFactorHashMap.size() == 0) {
            ansFactorHashMap.putAll(factorsContainsHidden);
        }
        return ansFactorHashMap; // my last factor which contain this hidden
    }

    /**
     * merge between two factors
     *
     * @param f1
     * @param f2
     * @param all_factors
     * @return
     */
    private HashMap<String, Factor> mergeTwoFactors(Factor f1, Factor f2, HashMap<String, Factor> all_factors) {
        ArrayList<ArrayList<String>> new_table = new ArrayList<>();
        ArrayList<Variable> var_intersect = new ArrayList<>();
        HashSet<Variable> all_vars = new HashSet<>();
        for (Variable var_inters_1 : f1.getFactor_variables(_evi_list)) {
            for (Variable var_inters_2 : f2.getFactor_variables(_evi_list)) {
                if (var_inters_1 == var_inters_2) {
                    var_intersect.add(var_inters_1);
                }
                all_vars.add(var_inters_2);
            }
            all_vars.add(var_inters_1);
        }
        Factor union_f1_f2 = new Factor();
        union_f1_f2.setFactor_variables(all_vars); //all variables in my two factors

        String factorName_1 = "";
        String factorName_2 = "";
        for (String name : all_factors.keySet()) {
            if (f1 == all_factors.get(name)) {
                factorName_1 = name;
            }
            if (f2 == all_factors.get(name)) {
                factorName_2 = name;
            }
        }

        ArrayList<Integer> intersect_index_1 = intersectIndex(f1, var_intersect, factorName_1);
        ArrayList<Integer> intersect_index_2 = intersectIndex(f2, var_intersect, factorName_2);
        for (ArrayList<String> row_1 : f1.get_table()) {
            for (ArrayList<String> row_2 : f2.get_table()) {
                boolean is_similar = true;
                for (int i = 0; i < intersect_index_1.size() & is_similar; i++)
                    if (!row_1.get(intersect_index_1.get(i)).equals(row_2.get(intersect_index_2.get(i)))) {
                        is_similar = false;
                    }
                if (is_similar) {
                    ArrayList<String> new_row = new ArrayList<>();
                    new_row.addAll(row_1);
                    double pro_1 = Double.parseDouble(new_row.get(new_row.size() - 1));
//                    System.out.println("pro 1 : " + pro_1);
                    new_row.remove(new_row.size() - 1); // delete pro
                    for (int i = 0; i < row_2.size(); i++) {
                        if (!intersect_index_2.contains(i)) {
                            new_row.add(row_2.get(i));
                        }
                    }
                    double pro_2 = Double.parseDouble(row_2.get(row_2.size() - 1));
//                    System.out.println("pro 2 : " + pro_2);
                    double new_pro = pro_1 * pro_2;
//                    System.out.println("new pro (pro 1 * pro 2) : " + new_pro);
//                    System.out.println();
                    numOfmult++;
                    new_row.set(new_row.size() - 1, String.valueOf(new_pro)); //add my new pro to the table
                    new_table.add(new_row);
                }
            }
        }

        HashSet<Variable> evidences_ = new HashSet<>();
        for (Evidence evi : _evi_list) {
            for (Pair pair : evi.getCondition()) {
                evidences_.add(pair.getKey());
            }
        }
        ArrayList<Variable> evidences = new ArrayList<>();
        evidences.addAll(evidences_);

        String unionFactorName = "";
        for (String name : factorName_1.split(",")) {
            if (!evidences.contains(_var_storage.getVarByName(name))) {
                unionFactorName += name + ",";
            }
        }
        for (String name : factorName_2.split(",")) {
            if ((!evidences.contains(_var_storage.getVarByName(name))) && (!unionFactorName.contains(name))) {
                unionFactorName += name + ",";
            }
        }
        unionFactorName = unionFactorName.substring(0, unionFactorName.length() - 1);
        HashMap<String, Factor> return_factor = new HashMap<>();
        union_f1_f2.setTable(new_table);
        return_factor.put(unionFactorName, union_f1_f2); //add my union factor to the collection

        return return_factor;
    }

    /**
     * merge two factors with the same name
     * for the final calc
     *
     * @param f1
     * @param f2
     * @param name
     * @return
     */
    private HashMap<String, Factor> mergeTwoFactorsFinal(Factor f1, Factor f2, String name) {
        ArrayList<ArrayList<String>> new_table = new ArrayList<>();
        ArrayList<Variable> var_intersect = new ArrayList<>();
        HashSet<Variable> all_vars = new HashSet<>();
        for (Variable var_inters_1 : f1.getFactor_variables(_evi_list)) {
            for (Variable var_inters_2 : f2.getFactor_variables(_evi_list)) {
                if (var_inters_1 == var_inters_2) {
                    var_intersect.add(var_inters_1);
                }
                all_vars.add(var_inters_2);
            }
            all_vars.add(var_inters_1);
        }
        Factor union_f1_f2 = new Factor();
        HashSet<Variable> factor_variables = new HashSet<>();
        factor_variables.add(_var_q);
        union_f1_f2.setFactor_variables(factor_variables);

        ArrayList<Integer> intersect_index_1 = intersectIndex(f1, var_intersect, name);
        ArrayList<Integer> intersect_index_2 = intersectIndex(f2, var_intersect, name);
        for (ArrayList<String> row_1 : f1.get_table()) {
            for (ArrayList<String> row_2 : f2.get_table()) {
                boolean is_similar = true;
                for (int i = 0; i < intersect_index_1.size() & is_similar; i++) {
                    if (!row_1.get(intersect_index_1.get(i)).equals(row_2.get(intersect_index_2.get(i)))) {
                        is_similar = false;
                        break;
                    }
                }
                if (is_similar) {
                    ArrayList<String> new_row = new ArrayList<>();
                    new_row.addAll(row_1);
                    double pro_1 = Double.parseDouble(new_row.get(new_row.size() - 1));
                    new_row.remove(new_row.size() - 1); // delete pro
                    for (int i = 0; i < row_2.size(); i++) {
                        if (!intersect_index_2.contains(i)) {
                            new_row.add(row_2.get(i));
                        }
                    }
                    double pro_2 = Double.parseDouble(row_2.get(row_2.size() - 1));
                    double new_pro = pro_1 * pro_2;
                    numOfmult++;
                    new_row.set(new_row.size() - 1, String.valueOf(new_pro)); //add my new pro to the table
                    new_table.add(new_row);
                }
            }
        }
        String new_factor_name = name;

        //add my union factor to the collection
        HashMap<String, Factor> return_factor = new HashMap<>();
        return_factor.put(new_factor_name, union_f1_f2);
        union_f1_f2.setTable(new_table);
        return return_factor;
    }

    /**
     * get Intersect Indexes
     *
     * @param f1
     * @param var_intersect
     * @param nameFactor
     * @return
     */
    private ArrayList<Integer> intersectIndex(Factor f1, ArrayList<Variable> var_intersect, String nameFactor) {
        HashSet<Variable> evidences_ = new HashSet<>();
        for (Evidence evi : _evi_list) {
            for (Pair pair : evi.getCondition()) {
                evidences_.add(pair.getKey());
            }
        }
        ArrayList<Variable> evidences = new ArrayList<>();
        evidences.addAll(evidences_);

        ArrayList<Integer> indexes = new ArrayList<>();
        String[] f1Vars = nameFactor.split(",");
        ArrayList<String> f1Names = new ArrayList<>();
        for (String name : f1Vars) {
            if (!evidences.contains(_var_storage.getVarByName(name))) {
                f1Names.add(name + ",");
            }
        }

        for (Variable var : var_intersect) {
            for (int i = 0; i < f1Names.size(); i++) {
                if ((var.getName() + ",").equals(f1Names.get(i))) {
                    indexes.add(i);
                }
            }
        }
        return indexes;
    }

    /**
     * @param variable
     * @param factor_name
     * @return
     */
    private Factor genFactor(Variable variable, String factor_name) {
        Factor factor = new Factor();
        factor.Create(variable, _evi_list);
        return initFactorWithCPT(factor, factor_name);
    }

    /**
     * init factor with CPT
     * part 1
     *
     * @param factor
     * @param factor_name
     * @return
     */
    private Factor initFactorWithCPT(Factor factor, String factor_name) {

        //get names of vars for columns
        String[] varsToInitForFactors = factor_name.split(",");

        ArrayList<String> varsToInitForFactors_col = new ArrayList<>();
        varsToInitForFactors_col.addAll(Arrays.asList(varsToInitForFactors));

        factor.createAllPermutation(0, varsToInitForFactors_col, _var_storage);
        return factor;
    }

    /**
     * get variable list
     *
     * @return
     */
    private ArrayList<Variable> getVarList() {
        ArrayList<Variable> var_list = new ArrayList<>();
        for (Variable var : _var_storage.getVarCollection().values()) {
            if (!var.getName().equals("none")) {
                var_list.add(var);
            }
        }
        return var_list;
    }

    /**
     * filter variable to get a collection of hidden vars
     *
     * @param copy_var_coll
     * @return
     */
    private ArrayList<Variable> filterVariables(HashMap<String, Variable> copy_var_coll) {
        copy_var_coll.remove("none");
        copy_var_coll.remove(_var_q.getName()); //remove q variable
        for (Pair pair_evi : _evi_list.get(0).getCondition()) {
            copy_var_coll.remove(pair_evi.getKey().getName()); //deleted my var name from the pair -> array list of pair -> evi
        }
        return new ArrayList<>(copy_var_coll.values());
    }
}

