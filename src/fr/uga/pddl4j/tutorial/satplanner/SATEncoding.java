package fr.uga.pddl4j.tutorial.satplanner;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

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
    public ArrayList<int[]> dimacs;
    private CodedProblem problem;

    /*
     * Current number of steps of the SAT encoding
     */
    private int steps;

    /**
     *
     * @param
     */
    public SATEncoding(final CodedProblem _problem, final int steps) {
        super();
        this.steps = steps;
        this.problem = _problem;
        dimacs = new ArrayList<int[]>();

        // ----------- We get the initial state from the planning problem
        final BitState init = new BitState(problem.getInit());

        // Encoding of init
        // Each fact is a unit clause
        int last = 0;
        while ( last < problem.getRelevantFacts().size()){

            int actionEncode = pair(last+1,steps); // on encode bit+1 pour éviter le 0

            if(!init.get(last))
            {
                //effets négatifs
                actionEncode *= -1;
            }
            dimacs.add(new int[]{actionEncode});
            last++;
        }

        System.out.println("DEBUT");
        for (int[] op: dimacs) {
            System.out.println(Arrays.toString(op));
        }
        System.out.println("FIN");

        System.out.println("***************** FIN INIT **********************");

        // Pour chaque étape step de 0 à steps - 1
        for(int step = 1; step < steps; step++)
        {
                next();
        }

        System.out.println("***************** DEBUT GOAL **********************");

        final BitState goal = new BitState(problem.getGoal());

        last = 0;
        while (last < problem.getRelevantFacts().size()){

            int actionEncode = pair(last+1,steps); // on encode bit+1 pour éviter le 0

            if(goal.get(last))
            {
                //effets positifs
                dimacs.add(new int[]{actionEncode});
            }

            last++;
        }

        System.out.println("***************** FIN GOAL **********************");

    }

    /*
     * SAT encoding for next step
     */
    public List next() {
        for (int action=0; action < this.problem.getOperators().size(); action++ )
        {

            final BitOp a = this.problem.getOperators().get(action);
            final BitVector precond = a.getPreconditions().getPositive();
            final BitVector positive = a.getUnconditionalEffects().getPositive();
            final BitVector negative = a.getUnconditionalEffects().getNegative();

            //on code le numéro de l'action, la première action a le num nbFacts+1
            int action_code = pair(action+ this.problem.getRelevantFacts().size(), steps);

            //genere les clause qui encode l'action
            for (int i = 0; i < problem.getRelevantFacts().size(); i++) {

                //genere les clauses pour les préconditions de l'action à l'etape courante
                if (precond.get(i)) {
                    int[] clause = new int[2];
                    clause[0] = -action_code;
                    clause[1] = pair(i, steps);
                    dimacs.add(clause);
                }

                //genere les clause pour les effets positifs qu'elle entraine à l'etape +1
                if (positive.get(i)) {
                    int[] clause = new int[2];
                    clause[0] = -action_code;
                    clause[1] = pair(i, steps + 1);
                    dimacs.add(clause);
                    //addtransition(i, code_op);
                }
                //genere les clause pour les effets négatif qu'elle entraine à l'etape +1
                if (negative.get(i)) {
                    int[] clause = new int[2];
                    clause[0] = -action_code;
                    clause[1] = -pair(i, steps + 1);
                    dimacs.add(clause);
                    //addtransition(-i, code_op);
                }
            }

        }

        return dimacs;
    }

    public static int pair(int a, int b) {
        int result =  (a + b) * (a + b + 1)/2 + b;
        return result;
    }

    public static int[] unpair(int c) {
        //fonction de décodage
        int w = (int) floor((sqrt(8 * c + 1) - 1)/2);
        int t = (w * w + w) / 2;
        int y = c - t;
        int x = w - y;
        //System.out.println(x +" x et y "+ y);
        int result[] = {x, y};
        return result;
    }

    @Override
    public String toString() {
        String res = "";
        int i = 0;
        for (int[] clause:dimacs) {
            res+= i+"  :  ";
            for (int elt: clause) {
                res += elt+" ";
            }
            res += "\n";
            i++;
        }
        return res;
    }
}
