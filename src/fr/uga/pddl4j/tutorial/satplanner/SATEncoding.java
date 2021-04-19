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
    public ArrayList<int[]> dimacs;

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
        dimacs = new ArrayList();

        // ----------- We get the initial state from the planning problem
        final BitState init = new BitState(problem.getInit());

        // Encoding of init
        // Each fact is a unit clause

        for (int i = 0; i < problem.getRelevantFacts().size();i++){
            if(init.nextSetBit(i) == i){
                //System.out.println("w"+i);
                int actionEncode = pair(i+1,0);
                dimacs.add(new int[]{actionEncode});
            }/*else{
                //System.out.println("-w"+i);
                dimacs.add(new int[]{(i+1)*-1});
            }*/
        }


        // ----------- We get the goal from the planning problem
        final BitState goal = new BitState(problem.getGoal());
        int elt = 0;
        while ( elt!=-1){
            elt = goal.nextSetBit(elt);
            if(elt!=-1){
                int actionEncode = pair(elt+1,steps);
                dimacs.add(new int[]{actionEncode});
                //System.out.println("w"+elt);
                elt++;
            }
        }

        // ----------- We get the operators of the problem
        for(int step = 0; step < steps; step++){
          for (int action=0; action < problem.getOperators().size(); action++ ) {
            final BitOp a = problem.getOperators().get(action);
            int actionEncode = pair(action+1,step);

            //  Preconditions
            final BitVector precond = a.getPreconditions().getPositive();
            elt = 0;
            while ( elt!=-1){
              elt = precond.nextSetBit(elt);
              if(elt != -1){
                  int actionEncode2 = pair(elt+1,step);
                  dimacs.add(new int[]{-actionEncode, actionEncode2});
                  elt++;
              }
            }

            //  Effects POSITIVE
            final BitVector positive = a.getUnconditionalEffects().getPositive();
            elt = 0;
            while ( elt!=-1){
              elt = positive.nextSetBit(elt);
              if(elt != -1){
                  int actionEncode2 = pair(elt+1,step+1);
                  dimacs.add(new int[]{-actionEncode,actionEncode2});
                  elt++;
              }
            }

            //  Effects Negative
            final BitVector negative = a.getUnconditionalEffects().getNegative();
            elt = 0;
            while ( elt!=-1){
              elt = negative.nextSetBit(elt);
              if(elt != -1){
                  int actionEncode2 = pair(elt+1,step+1);
                  dimacs.add(new int[]{-actionEncode,actionEncode2*-1});
                  elt++;
              }
            }
          }
        }
    }

    /*
     * SAT encoding for next step
     */
    public List next() {
        return null;
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
