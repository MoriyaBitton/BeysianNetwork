import java.util.ArrayList;
import java.util.HashMap;

public class Algo_1 {

    private Variable _var_q;
    private String _value_q;
    private ArrayList<Evidence> _evi_list;
    private VariablesStorage _var_storage;
    public int numOfSums = 0;
    public int numOfmult = 0;

    public Algo_1(Variable _my_var, String _my_values, ArrayList<Evidence> _my_evi_list, VariablesStorage _my_var_storage) {
        this._var_q = _my_var;
        this._value_q = _my_values;
        this._evi_list = _my_evi_list;
        this._var_storage = _my_var_storage;
        this.numOfSums = 0;
        this.numOfmult = 0;
    }

    public Answer run() {
        if ((_var_q != null) && (_value_q != null)) {
            double ans = proByEvidence(_var_q, _value_q, _evi_list);
            return new Answer(ans, numOfSums, numOfmult);
        }
        return null;
    }

    /**
     * calc pro of Queries by list of evidences
     *
     * @param var
     * @param val_to_calc
     * @param evidences
     * @return
     */
    public double proByEvidence(Variable var, String val_to_calc, ArrayList<Evidence> evidences) {
        double mone = calcIntersectProb(_var_q, _evi_list, true); // query: P(b= set|j,m) : b val: set, get, stay : P(b=set,j,m) + P(b=get,j,m) + P(b=stay,j,m)
        double mechane = 0;

        mechane += calcIntersectProb(var, evidences, false); //calc all permutation of var q possible values
        numOfSums++;

        double Alpha = (mone / mechane);
        double ans = Alpha;

//        System.out.println();
//        System.out.println("mone: " + mone + " mechane: " + mechane);
//        System.out.println("ans: " + ans + " num of sum: " + _numOfSums + " num of mult: " + _numOfmult);

        return ans;
    }

    /**
     * calc insert probability
     *
     * @param var
     * @param evidences
     * @param is_mone
     * @return
     */
    private double calcIntersectProb(Variable var, ArrayList<Evidence> evidences, boolean is_mone) {
        ArrayList<String[]> permutations = new ArrayList<>();
        double ans = 0;

        HashMap<String, Variable> _copy_var_coll = _var_storage.copyVarCollection();
        _copy_var_coll.remove("none");
        if ((_var_q != null) && (is_mone)) {
            _copy_var_coll.remove(_var_q.getName()); //remove q variable
        }
        for (Pair pair_evi : _evi_list.get(0).getCondition()) {
            _copy_var_coll.remove(pair_evi.getKey().getName()); //deleted my var name from the pair -> array list of pair -> evi
        }
        ArrayList<Variable> vars_array = new ArrayList<>(_copy_var_coll.values());
        setAllPermutation(permutations, vars_array, 0, new String[vars_array.size()]); //set all permutation

        for (String[] arr_per : permutations) {
//            System.out.println();
//            System.out.println("permutation : " + Arrays.toString(arr_per));
            ans += calcPermutation(arr_per, evidences.get(0), vars_array, var);
            numOfSums++;
        }
        return ans;
    }

    /**
     * setting all my permutation
     *
     * @param permutations
     * @param vars_array
     * @param i
     * @param curr_per
     */
    private void setAllPermutation(ArrayList<String[]> permutations, ArrayList<Variable> vars_array, int i, String[] curr_per) {
        if (i == vars_array.size()) {
            permutations.add(curr_per.clone());
            return;
        }
        for (String val : vars_array.get(i).getValues()) {
            curr_per[i] = val;
            setAllPermutation(permutations, vars_array, i + 1, curr_per);
        }
    }

    /**
     * calc all my permutation
     *
     * @param permutation
     * @param evi
     * @param vars_array
     * @param var
     * @return
     */
    private double calcPermutation(String[] permutation, Evidence evi, ArrayList<Variable> vars_array, Variable var) {
        double pro_mult = 1;
        int i = 0;

        ArrayList<Pair> pairs_of_parents = new ArrayList<>(); //var :val

        //create per_pair Arraylist of Pair.
        //each pair showed: var (left), val (right)
        //per_pair is the whole permutation vars and them values;
        while (i < permutation.length) {
            for (Variable var_to_pair : vars_array) { //create a array list of pair (key: variable, string: value)
                Pair pair = new Pair(var_to_pair, permutation[i]);
                pairs_of_parents.add(pair);
                i++;
            }
        }
        for (Pair pair_evi : evi.getCondition()) {
            pairs_of_parents.add(pair_evi);
        }
        if (pairs_of_parents.size() != (_var_storage.getVarCollection().size() - 1)) {
            Pair pair = new Pair(var, _value_q);
            pairs_of_parents.add(pair);
        }
//        System.out.println("per_pair size is: " + pairs_of_parents.size());

        for (Pair splited_pairs : pairs_of_parents) {
            Variable temp_var = splited_pairs.getKey();
            String temp_val = splited_pairs.getVal();
            double splited_pro;

            if ((splited_pairs.getKey().getParents().size() == 1) &&
                    (splited_pairs.getKey().getParents().get(0).equals(_var_storage.getVarCollection().get("none")))) { //don't have parents
                ArrayList<Pair> arr_list_pair = new ArrayList<>();
                arr_list_pair.add(new Pair(_var_storage.getVarByName("none"), ""));
                splited_pro = temp_var.getCpt().getValAndProByParents(arr_list_pair).get(temp_val);
//                System.out.println("var name: " + temp_var.getName() + " var val: " + temp_val + " pro: " + splited_pro);
                pro_mult *= splited_pro;
//                System.out.println("pro mult: " + pro_mult);
                numOfmult++;
            } else { //have a parents
                int num_of_par = splited_pairs.getKey().getParents().size();
                ArrayList<Pair> parents_list = new ArrayList<>();
                for (int j = 0; j < num_of_par; j++) { //list of my parents in the correct val as he showed in the permutation
                    Variable parent_num_j = splited_pairs.getKey().getParents().get(j);

                    for (int index = 0; index < pairs_of_parents.size(); index++) { //index of my Pair which his key is my var "par_num_j"
                        if (pairs_of_parents.get(index).getKey().equals(parent_num_j)) {
                            String parent_num_j_val = pairs_of_parents.get(index).getVal();
                            parents_list.add(new Pair(parent_num_j, parent_num_j_val));
                        }
                    }
                }
                splited_pro = temp_var.getCpt().getValAndProByParents(parents_list).get(temp_val);
//                System.out.println("var name: " + temp_var.getName() + " var val: " + temp_val + " pro: " + splited_pro);
                pro_mult *= splited_pro;
//                System.out.println("pro mult: " + pro_mult);
                numOfmult++;
            }
        }
        return pro_mult;
    }
}

