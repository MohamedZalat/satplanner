package fr.uga.pddl4j.tutorial.satplanner;

import static fr.uga.pddl4j.tutorial.satplanner.SATEncoding.pair;
import static fr.uga.pddl4j.tutorial.satplanner.SATEncoding.unpair;

public class TestCodage {
    public static void main(String[] args) {
        System.out.println("a = "+5+" b = "+6);
        int encode = pair(5,6);
        System.out.println("encode ="+encode);
        int[] decode = unpair(encode);
        System.out.println("decode[0] ="+decode[0]+"   decode[1] ="+decode[1]);

    }
}
