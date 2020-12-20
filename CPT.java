import java.util.ArrayList;
import java.util.HashMap;

public class CPT {
    private HashMap<ArrayList<Pair>, HashMap<String, Double>> prob;
    private VariablesStorage varStorage;

    public CPT() {
        this.prob = new HashMap<>();
    }

    public CPT(VariablesStorage varStorage) {
        this.prob = new HashMap<>();
        ArrayList<Pair> conditions = new ArrayList<>();
        this.varStorage = varStorage;
        conditions.add(new Pair(varStorage.getVarCollection().get("none"), "none"));
    }

    /**
     * ad probability
     *
     * @param varValues
     * @param parentVar
     * @param cond
     * @param currProb
     */
    public void addProb(ArrayList<String> varValues, ArrayList<Variable> parentVar, String cond, HashMap<String, Double> currProb) {
        ArrayList<Pair> condition = new ArrayList<>();
        if (cond.equals("")) {
            for (ArrayList<Pair> nonParentVal : prob.keySet()) {
                if ((nonParentVal.size() == 1) && (nonParentVal.get(0).getKey() == varStorage.getVarCollection().get("none"))) {
                    condition = nonParentVal;
                    break;
                }
            }
        }

        String[] val = cond.split(","); //my parents values
        int i = 0;
        for (Variable var : parentVar) {
            Pair pair = new Pair(var, val[i]);
            i++;
            condition.add(pair); //add my new pair to array
        }

        double sumPro = 0;
        String mashlim_val = "";
        for (String values : varValues) {
            if (currProb.containsKey(values)) {
                sumPro += currProb.get(values);
            } else {
                mashlim_val = values; //the val that i didn't get
            }
        }
        double mashlim_pro = 1 - sumPro;
        currProb.put(mashlim_val, mashlim_pro);
        prob.put(condition, currProb);
    }

    /**
     * @param parents_list
     * @return
     */
    public HashMap<String, Double> getValAndProByParents(ArrayList<Pair> parents_list) {

        for (ArrayList<Pair> parents : prob.keySet()) {
            if (parents.size() != parents_list.size()) {
                continue; //next
            }
            int index = 0;
            for (Pair pair_parent : parents_list) {
                if (!pair_parent.compare(parents.get(index))) { //if the pair r not equal
                    break;
                }
                index++;
            }
            if (index == parents_list.size()) {
                return prob.get(parents);
            }
        }
        return null;
    }

}

