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
        dimacs = new ArrayList<int[]>();

        // ----------- We get the initial state from the planning problem
        final BitState init = new BitState(problem.getInit());

        // Encoding of init
        // Each fact is a unit clause
        int bit = 0;
        while ( bit!=-1){
            bit = init.nextSetBit(bit);
            if(bit!=-1){// À l'étape 0 bit est vrai
                int actionEncode = pair(bit+1,0); // on encode bit+1 pour éviter le 0
                dimacs.add(new int[]{actionEncode});
                bit++;
            }
        }

        // ----------- We get the goal from the planning problem
        final BitState goal = new BitState(problem.getGoal());
        bit = 0;
        while ( bit!=-1){
            bit = goal.nextSetBit(bit);
            if(bit!=-1){// À la dernière étape steps bit est vrai
                int actionEncode = pair(bit+1,steps); // on encode bit+1 pour éviter le 0
                dimacs.add(new int[]{actionEncode});
                bit++;
            }
        }

        // Pour chaque étape step de 0 à steps - 1
        for(int step = 0; step < steps; step++){
            // ----------- We get the operators of the problem
            for (int action=0; action < problem.getOperators().size(); action++ ) {
                final BitOp a = problem.getOperators().get(action);
                int actionEncode = pair(action+1,step);     // Encodage de l'action (+1 pour éviter le 0) a l'etape STEP

                //  Preconditions
                final BitVector precond = a.getPreconditions().getPositive();
                bit = 0;
                while ( bit!=-1){
                    bit = precond.nextSetBit(bit);
                  if(bit != -1){
                      int actionEncode2 = pair(bit+1,step); // Encodage de la precondition bit a l'etape STEP
                      dimacs.add(new int[]{-actionEncode, actionEncode2});  // ajout de la clause not(action) ou precondition
                      bit++;
                  }
                }

            //  Effects POSITIVE
            final BitVector positive = a.getUnconditionalEffects().getPositive();

            bit = 0;
            while ( bit!=-1){
                bit = positive.nextSetBit(bit);
              if(bit != -1){
                  int actionEncode2 = pair(bit+1,step+1);  // Encodage de l'effet Positive bit a l'etape STEP+1
                  dimacs.add(new int[]{-actionEncode,actionEncode2}); //  ajout de la clause not(action) ou Effet Positive
                  bit++;
              }
            }

            //  Effects Negative
            final BitVector negative = a.getUnconditionalEffects().getNegative();
            bit = 0;
            while ( bit!=-1){
              bit = negative.nextSetBit(bit);
              if(bit != -1){
                  int actionEncode2 = pair(bit+1,step+1);  // Encodage de l'effet Negative bit a l'etape STEP+1
                  dimacs.add(new int[]{-actionEncode,actionEncode2*-1});//  ajout de la clause not(action) ou not(Effet Negative)
                  bit++;
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
