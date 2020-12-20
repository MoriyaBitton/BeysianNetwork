# BeysianNetwork

This Bayesian Network calculation difference Queries by given some information for each variable:
variable name
variable possible values
variable parents (variable with no parents will get "none" as a parent)
CPT- the CPT included the values of all parents list and the probability of the var himself by getting his "parents" as an Evidence 

## Algorithm 

1. Simple heating, without any improvements.
2. Elimination Variable, with the removal of unnecessary variables at the beginning, when ordering The elimination of the variables is in the order of the ABC.
3. Like 2, when you heuristically determine the elimination order of the variables.
Because run times vary from computer to computer, the parameter that the program will measure is the number of multiplication and connection operations performed.

Note: In algorithms 2 and 3 it is also necessary to determine the order of multiplication of the factors when canceling a variable. This order will choose so That as few lines as possible be formed in the new factor. If there are several equivalent options, choose the option that creates a factor That the ASCII amount of its variables is the smallest.

## Links

[Bayesian Network - Wikipedia](https://en.wikipedia.org/wiki/Bayesian_network)

![](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZnzooVhotsU3CjvAzkm_Az2UP3BpUAp3DAQ&usqp=CAU)
