package fr.uga.pddl4j.tutorial.satplanner;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.util.BitOp;
import fr.uga.pddl4j.util.BitState;
import fr.uga.pddl4j.util.BitVector;

import java.util.ArrayList;
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
    private List<String> dimacs = new ArrayList<String>();

    /*
     * Current number of steps of the SAT encoding
     */
    private int steps;

    /**
     *
     * @param
     */
    public SATEncoding(final CodedProblem problem, final int steps) {
        super();
        this.steps = steps;
        // We get the initial state from the planning problem
        final BitState init = new BitState(problem.getInit());
        //System.out.println("init : "+init);
        // Encoding of init
        String list_init = "";
        int cpt = 0;
        for (int i = 0; i < init.cardinality(); i++)
        {
            cpt = init.nextSetBit(cpt);
            list_init += cpt;
            if(i != init.cardinality()-1)
            {
                list_init += ".";
            }
            cpt++;
        }
        this.dimacs.add(list_init);
        // Each fact is a unit clause

        // We get the goal from the planning problem
        final BitState goal = new BitState(problem.getGoal());
        //System.out.println("goal : "+goal);

        // Encoding of goal
        //ArrayList<Integer> allInt = new ArrayList<>();
        String list_goal = "";
        cpt = 0;
        for (int i = 0; i < goal.cardinality(); i++)
        {
            cpt = goal.nextSetBit(cpt);
            list_goal += cpt;
            if(i != goal.cardinality()-1)
            {
                list_goal += ".";
            }
            cpt++;
        }
        this.dimacs.add(list_goal);

        // We get the operators of the problem
        for (int i = 0; i < problem.getOperators().size(); i++)
        {
            final BitOp a = problem.getOperators().get(i);
            final BitVector precond = a.getPreconditions().getPositive();
            final BitVector positive = a.getUnconditionalEffects().getPositive();
            final BitVector negative = a.getUnconditionalEffects().getNegative();

            // Encoding of precond
            //ArrayList<Integer> allInt = new ArrayList<>();
            String list_inter = "";
            cpt = 0;
            //System.out.println("precond "+precond.cardinality()+ "  "+precond.toString());
            for (int j = 0; j < precond.cardinality(); j++)
            {

                cpt = precond.nextSetBit(cpt);
                list_inter += cpt;
                if(j != precond.cardinality()-1)
                {
                    list_inter += ".";
                }
                cpt++;
            }

            list_inter += ".";

            // Encoding of positive
            //ArrayList<Integer> allInt = new ArrayList<>();
            cpt = 0;
            for (int j = 0; j < positive.cardinality(); j++)
            {
                cpt = positive.nextSetBit(cpt);
                list_inter += cpt;
                if(j != positive.cardinality()-1)
                {
                    list_inter += ".";
                }
                cpt++;
            }

            list_inter += ".";

            // Encoding of negative
            //ArrayList<Integer> allInt = new ArrayList<>();
            cpt = 0;
            for (int j = 0; j < negative.cardinality(); j++)
            {
                cpt = negative.nextSetBit(cpt);
                list_inter += -cpt;
                if(j != negative.cardinality()-1)
                {
                    list_inter += ".";
                }
                cpt++;
            }
            this.dimacs.add(list_inter);
        }
        /*
        for(Object a: dimacs)
        {
            System.out.println(" Dimacs : " + a.toString());
        }
        */
    }

    /*
     * SAT encoding for next step
     */

    public List<String> getDimacs() {
        return dimacs;
    }

    public List next()
    {
        return this.dimacs;
    }

    public static int pair(int a, int b)
    {
        int result =  (a + b) * (a + b + 1)/2 + b;
        return result;
    }


    public static int[] unpair(int c)
    {
        //fonction de décodage
        int w = (int) floor((sqrt(8 * c + 1) - 1)/2);
        int t = (w * w + w) / 2;
        int y = c - t;
        int x = w - y;
        //System.out.println(x +" x et y "+ y);
        int result[] = {x, y};
        return result;

    }

}