import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BayesianNetwork {

    public static VariablesStorage varStorage;

    public BayesianNetwork() {
        this.varStorage = new VariablesStorage();
    }

    /**
     * read from file and build the BN
     */
    public void ReadFromFile() {
        String Answer = "";
        try {
            File myObj = new File("input2.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                while (data.contains("Var") && !data.contains("Variables")) {
                    data = data.replace("Var ", "");
                    String var_name = data;
                    data = myReader.nextLine();

                    if (data.contains("Values: ")) {
                        ArrayList<String> var_values = new ArrayList<>();
                        data = data.replace("Values: ", ""); //empty string
                        String tempValue;
                        while (data.length() != 0) {
                            if (data.contains(",")) {
                                tempValue = data.substring(0, data.indexOf(","));
                            } else {
                                tempValue = data;
                            }
                            var_values.add(tempValue); //split varValues
                            if (data.contains(",")) {
                                data = data.replaceFirst(tempValue + ",", "");
                            } else {
                                data = data.replace(tempValue, "");
                            }
                        }
                        data = myReader.nextLine();

                        if (data.contains("Parents: ")) {
                            ArrayList<Variable> var_parents = new ArrayList<>();
                            data = data.replace("Parents: ", "");
                            if (!data.equals("none")) {
                                while (data.length() != 0) {
                                    String parentName;
                                    if (data.contains(",")) {
                                        parentName = data.substring(0, data.indexOf(","));
                                    } else {
                                        parentName = data;
                                    }
                                    var_parents.add(varStorage.getVarCollection().get(parentName));
                                    if (data.contains(",")) {
                                        data = data.replace(parentName + ",", "");
                                    } else {
                                        data = data.replace(parentName, "");
                                    }
                                }
                            } else {
                                var_parents.add(varStorage.getVarCollection().get("none"));
                            }
                            data = myReader.nextLine();

                            if (data.contains("CPT:")) {
                                data = myReader.nextLine();
                                CPT myCpt = new CPT(); // send to the empty constructor
                                while (!data.equals("")) { // break point - empty line
                                    String parents_val = data.substring(0, data.indexOf("="));
                                    data = data.replaceFirst(parents_val, "");
                                    HashMap<String, Double> myProb = new HashMap<>();
                                    while (!data.equals("")) {
                                        String cpt_var_val;
                                        data = data.replaceFirst("=", "");
                                        cpt_var_val = data.substring(0, data.indexOf(","));
                                        data = data.replaceFirst((cpt_var_val + ","), "");

                                        Double cpt_pro;
                                        if (data.contains(",")) {
                                            cpt_pro = Double.parseDouble(data.substring(0, data.indexOf(",")));
                                            data = data.replace(data.substring(0, data.indexOf(",")) + ",", "");
                                        } else {
                                            cpt_pro = Double.parseDouble(data);// into my HM cpt
                                            data = "";
                                        }
                                        myProb.put(cpt_var_val, cpt_pro);
                                    }

                                    myCpt.addProb(var_values, var_parents, parents_val, myProb); //pair(var parents and string for then val), hm(myProb)
                                    Variable BayesianNode = new Variable(var_name, var_values, var_parents, myCpt);
                                    varStorage.addVarToCollection(var_name, BayesianNode); // add to my var storage

//                                    System.out.println("var name: " + var_name +
//                                            " var values: " + var_values +
//                                            " var parents: " + var_parents +
//                                            " cpt: " + var_values + var_parents + parents_val + myProb);
                                    data = myReader.nextLine();
                                }
                                data = myReader.nextLine(); //**
                            }
                        }
                    }
                }

                if (data.contains("Queries")) {
                    data = myReader.nextLine();
                    Queries myQueries = new Queries();
                    while (!data.equals("")) { // break point - empty line
                        data = data.replace("P(", "");
                        String name_pro = data.substring(0, data.indexOf("="));
                        data = data.replaceFirst(name_pro + "=", "");
                        String val_pro = data.substring(0, data.indexOf("|"));
//                        System.out.println(name_pro + " " + val_pro);
                        data = data.replace(val_pro + "|", "");

                        Variable var = varStorage.getVarCollection().get(name_pro);
                        HashMap<String, ArrayList<Evidence>> val_and_evi;
                        if (!myQueries.getQueries().containsKey(var)) { // my var name is not found
                            val_and_evi = new HashMap<>();
                            val_and_evi.put(val_pro, new ArrayList<>());
                            myQueries.getQueries().put(var, val_and_evi);
                        } else if (!myQueries.getQueries().get(var).containsKey(val_pro)) { // my var name is found but my val no
                            myQueries.getQueries().get(var).put(val_pro, new ArrayList<>());
                            val_and_evi = myQueries.getQueries().get(var);
                        } else { //my var and my val is found
                            val_and_evi = myQueries.getQueries().get(var);
                        }

                        Evidence evidences = new Evidence();
                        val_and_evi.get(val_pro).add(evidences);
                        while (data.charAt(0) != ')') {
                            String name_evi = data.substring(0, data.indexOf("="));
                            data = data.replaceFirst(name_evi + "=", "");
                            String val_evi;
                            if (data.indexOf(",") < data.indexOf(")")) {
                                val_evi = data.substring(0, data.indexOf(","));
                                data = data.replaceFirst(val_evi + ",", "");
                            } else {
                                val_evi = data.substring(0, data.indexOf(")"));
                                data = data.replace(val_evi, "");
                            }
                            Variable temp_var = varStorage.getVarCollection().get(name_evi);
                            evidences.addPair(new Pair(temp_var, val_evi)); //create a new pair to my evi list
                        }
                        data = data.replace("),", "");
                        int num_of_algo = data.charAt(0) - '0';

                        switch (num_of_algo) {

                            case 1:
                                num_of_algo = 1;
                                Algo_1 algo_1 = new Algo_1(var, val_pro, myQueries.getQueries().get(var).get(val_pro), varStorage);
                                Answer += algo_1.run().toString() + "\n";
                                break;
                            case 2:
                                num_of_algo = 2;
                                Algo_2 algo_2 = new Algo_2(var, val_pro, myQueries.getQueries().get(var).get(val_pro), varStorage);
                                Answer += algo_2.run().toString() + "\n";
                                break;
                            case 3:
                                num_of_algo = 3;
                                Algo_3 algo_3 = new Algo_3(var, val_pro, myQueries.getQueries().get(var).get(val_pro), varStorage);
                                Answer += algo_3.run().toString() + "\n";
                                break;
                            default:
                                break;
                        }

                        if (myReader.hasNextLine()) {
                            data = myReader.nextLine();
                        } else {
                            data = "";
                        }
                    }
                }
            }
            myReader.close();

        } catch (
                FileNotFoundException e) {
            System.out.println("I can not read this txt");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("output.txt");
            myWriter.write(Answer);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

