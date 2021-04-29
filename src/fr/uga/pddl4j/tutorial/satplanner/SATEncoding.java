package fr.uga.pddl4j.tutorial.satplanner;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.util.*;

import java.util.*;


/**
 * This class implements a planning problem/domain encoding into DIMACS
 *
 * @author H. Fiorino
 * @version 1.0 - 30.03.2021
 */
public final class SATEncoding {

    /*
     * A SAT problem in dimacs format is a list of int list a.k.a clauses
     */
    private final List<int[]> dimacs;

    //hashmap permettant de passer d'une étape à l'autre
    private HashMap<Integer, ArrayList<Integer>> clausesPrec;

    // Le but à atteindre
    private final BitState goal;

    // RelevantFacts
    private final List<IntExp> facts;
    //le problem
    private final CodedProblem problem;

    /*
     * Current number of steps of the SAT encoding
     */
    private int steps;

    /**
     *
     * @param
     */
    public SATEncoding(final CodedProblem problem, final int steps) {

        this.steps = steps;
        this.problem = problem;
        // We get the GOAL and Relevant Facts
        this.goal = new BitState(problem.getGoal());
        this.facts = problem.getRelevantFacts();

        dimacs = new ArrayList<>();

        // Encoding of init
        // Each fact is a unit clause
        final BitState init = new BitState(problem.getInit());
        for (int i = 0; i < this.facts.size(); i++)
        {
            int actionEncode = pair(i, 1);  //Encodage
            if (init.get(i))
            {
                //positifs
                this.dimacs.add(new int[]{actionEncode});
            }
            else
            {
                //négatifs :  encodage * -1
                this.dimacs.add(new int[]{-actionEncode});
            }
        }

        //On génère les clauses jusqu'à l'étape que l'on souhaite réaliser
        for (int i = 1; i < steps; i++)
        {
            next();
        }
    }

    /**
     *
     * @return : liste des clauses du but
     */
    private List<int[]> getGoal() {

        List<int[]> clauses = new ArrayList<>();
        for (int i = 0; i < this.facts.size(); i++)
        {
            int[] clause = new int[1];
            if (this.goal.get(i))
            {
                //on code seulement les choses positives du but
                clause[0] = pair(i, this.steps + 1);
                clauses.add(clause);
            }
        }

        return clauses;
    }

    /**
     * ajoute une clause au fichier dimacs
     */
    private void addClause(int[] clause)
    {
        this.dimacs.add(clause);
    }

    /**
     *
     * @return les clauses générées à cet étape
     */
    public List<int[]> next() {

        this.clausesPrec = new HashMap<>();

        //on ajoute les opérations dans l'ensemble des clauses
        for (int i = 0; i < problem.getOperators().size(); i++)
        {
            addAction(i);
        }

        addTransitionsToDimacs();

        // Return the result of the current step
        List<int[]> res = new ArrayList<>(dimacs);
        res.addAll(getGoal());
        steps++;
        return res;
    }


    // action => precondition1 ^ ..... preconditionN ^ positifefect1 ^ ... positifefectN ^ - negatifeffect1 ^ ... - negatifeffectN
    // A => B  equivalent a  -A v B
    // -code_op v ( pre1 ^ pre2 ^ pre3 ^ positifefect1 ^ positifeffect2 ^ - negatifeffect1 )
    // (-code_op v pre1) ^ (-code_op  v pre2) ...

    /**
     * ajoute les clause en lien avec l'action a Dimac
     *
     * @param actionIndex le bit qui encode l'action
     */
    private void addAction(int actionIndex) {
        final BitOp action = problem.getOperators().get(actionIndex);
        final BitVector precond = action.getPreconditions().getPositive();
        final BitVector positive = action.getUnconditionalEffects().getPositive();
        final BitVector negative = action.getUnconditionalEffects().getNegative();

        int code_op = pair(actionIndex + facts.size(), steps);

        //genere les clauses de disjonction (pour eviter de faire 2 actions pour une étape)
        for (int i = actionIndex + 1; i < problem.getOperators().size(); i++) {
            int code_other_op = pair(i + facts.size(), steps);
            int[] clause = new int[2];
            clause[0] = -code_op;
            clause[1] = -code_other_op;
            addClause(clause);
        }

        //genere les clause qui encode l'action
        for (int i = 0; i < facts.size(); i++) {

            //genere les clauses pour les préconditions de l'action à l'etape courante
            if (precond.get(i)) {
                int[] clause = new int[2];
                clause[0] = -code_op;
                clause[1] = pair(i, steps);
                addClause(clause);
            }

            //genere les clause pour les effets positifs qu'elle entraine à l'etape +1
            if (positive.get(i)) {
                int[] clause = new int[2];
                clause[0] = -code_op;
                clause[1] = pair(i, steps + 1);
                addClause(clause);

                this.clausesPrec.putIfAbsent(i, new ArrayList<>());
                this.clausesPrec.get(i).add(code_op);
            }
            //genere les clause pour les effets négatif qu'elle entraine à l'etape +1
            if (negative.get(i)) {
                int[] clause = new int[2];
                clause[0] = -code_op;
                clause[1] = -pair(i, steps + 1);
                addClause(clause);

                this.clausesPrec.putIfAbsent(-i, new ArrayList<>());
                this.clausesPrec.get(-i).add(code_op);
            }
        }
    }

    // fi ^ - fi+1 => a v a4 v ... ai
    // -fi v fi+1 v a v a4 v ... ai

    /**
     * genere et ajoute à dimac les clauses de transition à partir de la map de transition
     */
    private void addTransitionsToDimacs() {
        for (Map.Entry<Integer, ArrayList<Integer>> op : this.clausesPrec.entrySet()) {
            ArrayList<Integer> clause = op.getValue();

            if (op.getKey() > 0) {
                //codage de l'opération
                clause.add(pair(op.getKey(), steps));
                //négation de l'opération à l'étape d'après
                clause.add(-pair(op.getKey(), steps + 1));

            } else {
                //négatif à cet etape donc positif a l'étape d'après
                clause.add(-pair(-op.getKey(), steps));
                clause.add(pair(-op.getKey(), steps + 1));
            }

            int[] tab_param = new int[clause.size()];
            int cpt = 0;
            for (int param: clause)
            {
                tab_param[cpt] = param;
                cpt++;
            }
            //ajout de la clause dans dimacs
            addClause(tab_param);
        }

    }

    /**
     *
     * @param a : opération, fact
     * @param b : etape
     * @return : codage de a et b sous un unique entier
     */
    private static int pair(int a, int b) {
        return ((a + b) * (a + b + 1) / 2 + b) + 1;
    }

    /**
     *
     * @param z : l'entier a décodé
     * @return : un tableau avec le numéro de l'op ou du fact en premier et l'étape en deuxième
     */
    public static int[] unpair(int z) {

        //valeur absolue pour lé décodage !
        int[] tmp = decodage(Math.abs(z));

        if (z >= 0)
        {
            //action, fact positif
            return tmp;
        }
        else
        {
            //action, fact négatif
            return new int[]{-tmp[0], tmp[1]};
        }
    }

    /**
     *
     * @param z : l'entier a décodé
     * @return : un tableau avec le numéro de l'op ou du fact en premier et l'étape en deuxième
     */
    private static int[] decodage(int z) {
        z--;

        int t = (int) (Math.floor((Math.sqrt(8 * z + 1) - 1) / 2));
        int x = t * (t + 3) / 2 - z;
        int y = z - t * (t + 1) / 2;
        x++;
        y++;
        return new int[]{x,y};
    }

    /**
     * affiche les clauses de la liste
     *
     * @param : clauses la liste a afficher
     */
    public void AfficheClauses(List<int[]> clauses)
    {
        for (int[] clause : clauses)
        {
            System.out.print("[ ");
            for (int variable : clause)
            {
                System.out.print(variable + " v");
            }
            System.out.println(" ] ^");
        }
    }

    /**
     * affiche les clauses décodées
     *
     * @param : clauses la liste à afficher
     */
    public void showUnpairing(List<int[]> clauses) {
        for (int[] clause : clauses)
        {
            for (int variable : clause)
            {
                int[] tmp = unpair(variable);
                System.out.print("( " + tmp[0] + " , " + tmp[1] + " ) ");
            }
            System.out.println(" ^ ");
        }
    }

}