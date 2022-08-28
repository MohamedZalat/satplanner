package fr.uga.pddl4j.tutorial.satplanner;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.ProblemFactory;
import fr.uga.pddl4j.planners.statespace.AbstractStateSpacePlanner;
import fr.uga.pddl4j.planners.statespace.StateSpacePlanner;
import fr.uga.pddl4j.util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;
import pddl4j.PDDLObject;
import pddl4j.Parser;
import pddl4j.graphplan.Graphplan;

import static fr.uga.pddl4j.tutorial.satplanner.SATEncoding.unpair;

/**
 * This class implements a simple SAT planner based on SAT4J.
 *
 * @author H. Fiorino
 * @version 1.0 - 29.03.2021
 */
public final class SATPlanner extends AbstractStateSpacePlanner {

    /*
     * The arguments of the planner.
     */
    private Properties arguments;

    static String problemName;

    /**
     * Creates a new SAT planner with the default parameters.
     *
     * @param arguments the arguments of the planner.
     */
    public SATPlanner(final Properties arguments) {
        super();
        this.arguments = arguments;
    }

    /**
     * Solves the planning problem and returns the first solution found.
     *
     * @param problem the problem to be solved.
     * @return a solution search or null if it does not exist.
     */
    @Override
    public Plan search(final CodedProblem problem) {
        // The solution plan is sequential
        final Plan plan = new SequentialPlan();
        // We get the initial state from the planning problem
        final BitState init = new BitState(problem.getInit());
        // We get the goal from the planning problem
        final BitState goal = new BitState(problem.getGoal());
        // Nothing to do, goal is already satisfied by the initial state
        if (init.satisfy(problem.getGoal())) {
            return plan;
        }
        // Otherwise, we start the search
        else {

            List<int[]> clauses = null;
            // SAT solver timeout
            final int timeout = ((int) this.arguments.get(Planner.TIMEOUT));
            // SAT solver max number of var
            int MAXVAR = 50000;
            // SAT solver max number of clauses
            int NBCLAUSES = 100000;



            int max_step = (Integer) this.arguments.get("steps");
            //On a pas eu le temps d optimiser pour avoir une étape initial cohérente avec le probleme donc on commence a 1
            int current_step = 1;
            SATEncoding encoder = new SATEncoding(problem, current_step);


            IProblem ip = null;
            boolean solutionFound = false;
            //boucle tant que le sat solver ne trouve pas de solution ou que l'on atteint pas une limite (nb clauses ou temps)
            try {
                while ((current_step <= max_step) && !solutionFound) {
                    try {
                        //Initialize solver
                        ISolver solver = SolverFactory.newDefault();
                        solver.setTimeout(timeout);
                        solver.newVar(MAXVAR);
                        solver.setExpectedNumberOfClauses(NBCLAUSES);

                        //on genere les clauses a l'etape +1
                        clauses = encoder.next();
                        //ajout des clauses au solver
                        for (int[] clause : clauses) {
                            solver.addClause(new VecInt(clause));
                        }
                        ip = solver;
                        //si c'est solvable on s'arrete
                        if (ip.isSatisfiable()) {
                            solutionFound = true;
                        }
                        //si ce n'est pas solvable a l'etape n on boucle pour tester a n+1
                        current_step++;
                    } catch (ContradictionException e) {
                        current_step++;
                    }
                }
                //si on atteint la borne temporelle on s'arrete
            } catch (TimeoutException e) {
                System.out.println("Timeout! No solution found!");
                System.exit(1);
            }

            int factSize = problem.getRelevantFacts().size();
            BitOp[] actions = new BitOp[current_step];
            //réalisation du plan a partir de la solution rendu par le SAT solver
            for (int variable : ip.model()) {
                //traduction la variable pour obtenir le bitnum et l'etape
                int[] resDecoded = unpair(variable);
                int bitnum = resDecoded[0];
                int step = resDecoded[1];
                // si le bitnum est plus grand que le nombre de fait alors c'est une action
                if (bitnum > factSize) {
                    //on récupere l'encodage de l'action
                    BitOp action = problem.getOperators().get(bitnum - factSize - 1);
                    //on la sauvegarde dans l'ordre grace a son étape
                    actions[step - 1] = action;
                }
            }
            //on sauvegarde les actions dans le plan
            for (int i = 0; i < current_step - 1; i++) {
                plan.add(0, actions[i + 1]);
            }

            //si on n'a pas enlevé l'affichage on le réalise :
            if ((int) arguments.get("quiet") == 0) {
                System.out.println(problem.toString(plan));
            }
            return plan;
        }
    }

    /**
     * Print the usage of the SAT planner.
     */
    private static void printUsage() {
        final StringBuilder strb = new StringBuilder();
        strb.append("\nusage of PDDL4J:\n")
                .append("OPTIONS   DESCRIPTIONS\n")
                .append("-o <str>    operator file name\n")
                .append("-f <str>    fact file name\n")
                .append("-t <num>    SAT solver timeout in seconds\n")
                .append("-n <num>    Max number of steps\n")
                .append("-q          quiet console output\n")
                .append("-p <num>    0 for SATPlanner, 1 for Graphplan\n")
                .append("-h          print this message\n\n");
        Planner.getLogger().trace(strb.toString());
    }

    /**
     * Parse the command line and return the planner's arguments.
     *
     * @param args the command line.
     * @return the planner arguments or null if an invalid argument is encountered.
     */
    private static Properties parseCommandLine(String[] args) {

        // Get the default arguments from the super class
        final Properties arguments = StateSpacePlanner.getDefaultArguments();
        arguments.put("quiet", 0);
        arguments.put(Planner.TIMEOUT, 300);
        // Parse the command line and update the default argument value
        for (int i = 0; i < args.length; i += 2) {
            if ("-o".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
                if (!new File(args[i + 1]).exists()) return null;
                arguments.put(Planner.DOMAIN, new File(args[i + 1]));
            } else if ("-f".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
                if (!new File(args[i + 1]).exists()) return null;
                arguments.put(Planner.PROBLEM, new File(args[i + 1]));
                problemName = args[i + 1];
            } else if ("-t".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
                final int timeout = Integer.parseInt(args[i + 1]);
                if (timeout < 0) return null;
                arguments.put(Planner.TIMEOUT, timeout);
            } else if ("-q".equalsIgnoreCase(args[i])) {
                arguments.put("quiet", 1);
                i--;
            } else if ("-n".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
                final int steps = Integer.parseInt(args[i + 1]);
                if (steps > 0)
                    arguments.put("steps", steps);
                else
                    return null;
            } else {
//                return null;
            }
        }
        // Return null if the domain or the problem was not specified
        return (arguments.get(Planner.DOMAIN) == null
                || arguments.get(Planner.PROBLEM) == null) ? null : arguments;
    }

    public static int parseTypeOfPlanner(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if ("-p".equalsIgnoreCase(args[i]) && ((i + 1) < args.length))
                return Integer.parseInt(args[i + 1]);
        }
        return 0;
    }

    /**
     * The main method of the <code>SATPlanner</code> example. The command line syntax is as
     * follow:
     * usage of SATPlanner:
     * <p>
     * OPTIONS   DESCRIPTIONS
     * <p>
     * -o <i>str</i>   operator file name
     * -f <i>str</i>   fact file name
     * -t <i>num</i>   specifies the maximum CPU-time in seconds
     * -n <i>num</i>   specifies the maximum number of steps
     * -q              quiet console output
     * -h              print this message
     *
     * @param args the arguments of the command line.
     */
    public static void main(String[] args) throws IOException {
        final Properties arguments = SATPlanner.parseCommandLine(args);
        final int plannerType = SATPlanner.parseTypeOfPlanner(args);

        if (arguments == null) {
            SATPlanner.printUsage();
            System.exit(0);
        }

        final String domainFilePath = ((File) arguments.get(Planner.DOMAIN)).getPath();
        final String problemFileName = ((File) arguments.get(Planner.PROBLEM)).getPath();
        if(plannerType == 0)
            SATPlannerMain(domainFilePath, problemFileName, arguments);
        else if(plannerType == 1) {
            graphPlanMain(domainFilePath, problemFileName, arguments);
        }

    }

    public static void SATPlannerMain(String domainFilePath, String problemFileName, Properties arguments) {
        final SATPlanner planner = new SATPlanner(arguments);
        final ProblemFactory factory = ProblemFactory.getInstance();

        File domain = (File) arguments.get(Planner.DOMAIN);
        File problem = (File) arguments.get(Planner.PROBLEM);
        ErrorManager errorManager = null;
        try {
            errorManager = factory.parse(domain, problem);
        } catch (IOException e) {
            Planner.getLogger().trace("\nUnexpected error when parsing the PDDL files.");
            System.exit(0);
        }

        if (!errorManager.isEmpty()) {
            errorManager.printAll();
            System.exit(0);
        } else {
            if ((int) arguments.get("quiet") == 0) {
                Planner.getLogger().trace("\nParsing domain file: successfully done");
                Planner.getLogger().trace("\nParsing problem file: successfully done\n");
            }
        }
        final CodedProblem pb = factory.encode();
        if ((int) arguments.get("quiet") == 0) {
            Planner.getLogger().trace("\nGrounding: successfully done ("
                    + pb.getOperators().size() + " ops, "
                    + pb.getRelevantFacts().size() + " facts)\n");

            if (!pb.isSolvable()) {
                Planner.getLogger().trace(String.format("Goal can be simplified to FALSE."
                        + "No search will solve it%n%n"));
                System.exit(0);
            }
        }

        long begin = System.currentTimeMillis();
        final Plan plan = planner.search(pb);
        planner.getStatistics().setTimeToSearch(System.currentTimeMillis() - begin);
        Planner.getLogger().trace(String.format("%8.2f seconds searching%n",(System.currentTimeMillis() - begin)/1000.0));
        Planner.getLogger().trace(String.format("%nplan total cost: %4.2f%n%n", plan.cost()));


        //Ecrire dans un fichier :
        float time = (float) ((System.currentTimeMillis() - begin)/1000.0);
        String timeString = String.format("%f",time);
        String cost = String.format("%f", plan.cost());

        String[] split_nom = problemName.split("/");
        String[] nomPddl = split_nom[split_nom.length-1].split("\\.");
        String d = split_nom[split_nom.length-2];
        problemName = nomPddl[0];

        System.out.println("domain :" +d);
        System.out.println("problemName :" +problemName);

        System.out.println("time :" +timeString);
        System.out.println("cost :" +cost);

        try {
            enregistreResultat("SATPlanner", problemName,time,"Resultat/"+d+"/time.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void graphPlanMain(String domainFilePath, String problemFileName, Properties arguments){
        try {
                // Gets the pddl compiler options
                Properties options = Graphplan.getParserOptions();
                // Creates an instance of the java pddl compiler
                Parser parser = new Parser(options);
                PDDLObject domain = parser.parse(new File(domainFilePath));
                PDDLObject problem = parser.parse(new File(problemFileName));
                PDDLObject pb = null;
                if (domain != null && problem != null) {
                    pb = parser.link(domain, problem);
                }
                // Gets the error manager of the pddl compiler
                pddl4j.ErrorManager mgr = parser.getErrorManager();
                // If the compilation produces errors we print it and stop
                if (mgr.contains(pddl4j.ErrorManager.Message.ERROR)) {
                    mgr.print(pddl4j.ErrorManager.Message.ALL);
                }
                // else we print the warning and start the planning process
                else {

                    mgr.print(pddl4j.ErrorManager.Message.WARNING);

                    System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
                    System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");

                    Graphplan planner = new Graphplan(pb);
                    planner.preprocessing();
                    pddl4j.graphplan.Plan plan = planner.solve();

                    if (plan != null) {
                        System.out.println("\nfound plan as follows:\n");
                        Graphplan.printPlan(plan);
                    } else {
                        System.out.println("\nno solution plan found\n");
                    }

                    final double p_time = planner.p_time/1000000000.0;
                    final double g_time = planner.g_time/1000000000.0;
                    final double s_time = planner.s_time/1000000000.0;
                    final double m_time = planner.m_time/1000000000.0;
                    final double t_time = p_time + g_time + s_time;
                    System.out.printf("Time spent : %8.2f seconds preprocessing \n", p_time);
                    System.out.printf("             %8.2f seconds build graph \n", g_time);
                    System.out.printf("             %8.2f seconds calculating exclusions \n", m_time);
                    System.out.printf("             %8.2f seconds searching graph \n", s_time);
                    System.out.printf("             %8.2f seconds total time \n", t_time);

                    //Ecrire dans un fichier :
                    float time = (float) (t_time);
                    String timeString = String.format("%f",time);

                    String[] split_nom = problemName.split("/");
                    String[] nomPddl = split_nom[split_nom.length-1].split("\\.");
                    String d = split_nom[split_nom.length-2];
                    problemName = nomPddl[0];

                    System.out.println("time :" +timeString);

                    try {
                        enregistreResultat("GraphPlan", problemName,(float)t_time,"Resultat/"+d+"/time.csv");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            t.printStackTrace(System.err);
        }
    }

    /**
     *
     * @param nomProblem : le nom du problème qui a été lancé ( ici, généralement p01, p02...)
     * @param resultat : le résultat que l'on obtient pour la métrique soit de temps soit de coût du plan
     * @param chemin : chemin vers le fichier dans lequel on enregistre les données (le créé s'il n'existe pas)
     * @throws IOException
     */
    static public void enregistreResultat(String nomPlanner, String nomProblem, float resultat, String chemin) throws IOException {
        boolean creation = false;
        if(!new File(chemin).exists())
        {
            new File(chemin).createNewFile();
            creation = true;
        }
        //ouverture du fichier en écriture, doit être un fichier csv
        FileWriter fw = new FileWriter(chemin,true);
        PrintWriter out = new PrintWriter(fw);

        if (creation)
        {
            //on ajoute le nom des colonnes
            out.println("Planner Name;Problem Name;Total Time");
        }

        //ecriture du résultat
        String texte = nomPlanner+";"+nomProblem+";"+resultat;
        out.println(texte);

        out.close();
        fw.close();
    }
}
