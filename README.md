# satplanner
=======
SATPlanner based on PDDL4J and SAT4J.


### 1- COMPILATION

- Our satPlanner: <br/>
    javac -d classes -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar src/fr/uga/pddl4j/tutorial/satplanner/*.java -Xlint:unchecked

- ASP Planner: <br/>
    javac -d classes -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar src/fr/uga/pddl4j/tutorial/asp/ASP.java -Xlint:unchecked

### 2- Execution

- Our satPlanner: <br/>
    java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/blocksworld/domain.pddl -f pddl/exemples/blocksworld/p01.pddl -n 30 -t 300

- ASP Planner: <br/>
    java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/blocksworld/domain.pddl -f pddl/exemples/blocksworld/p01.pddl -t 300
    
### 3- Automated test

- Script shell: <br />
     ./bash.sh

**Attention** : Takes time to test all examples, you can find results of execution in "Resultat" Folder (time + cost).

### Authors:
  - BOUKRIS Walid
  - GAUTHIER Florian
  - HENRION Morgane
  - GROS François-Xavier
  - CHOSSON Aurélien
