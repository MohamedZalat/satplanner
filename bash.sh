#!/bin/bash

javac -d classes -cp "lib/pddl4j-3.8.3.jar;lib/sat4j-sat.jar;lib/graphplan.jar;lib/pddl4j-v1.jar" src/fr/uga/pddl4j/tutorial/satplanner/*.java -Xlint:unchecked

#########  blocksworld SATPlanner DONE

for i in `seq 2 9`;
do
  java -cp "out/production/satplanner;lib/pddl4j-3.8.3.jar;lib/sat4j-sat.jar;lib/graphplan.jar;lib/pddl4j-v1.jar" fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/blocksworld2/blocksworld.pddl -f pddl/exemples/blocksworld2/"pb"$i".pddl" -n 30 -t 300 -p 0
done


#for i in `seq 10 15`;
#do
#  java -cp "out/production/satplanner;lib/pddl4j-3.8.3.jar;lib/sat4j-sat.jar;lib/graphplan.jar;lib/pddl4j-v1.jar" fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/blocksworld2/blocksworld.pddl -f pddl/exemples/blocksworld2/"pb"$i".pddl" -n 30 -t 300 -p 0
#done

#########  blocksworld GraphPlan DONE

for i in `seq 2 9`;
do
  java -cp "out/production/satplanner;lib/pddl4j-3.8.3.jar;lib/sat4j-sat.jar;lib/graphplan.jar;lib/pddl4j-v1.jar" fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/blocksworld2/blocksworld.pddl -f pddl/exemples/blocksworld2/"pb"$i".pddl" -n 30 -t 300 -p 1
done


for i in `seq 10 15`;
do
  java -cp "out/production/satplanner;lib/pddl4j-3.8.3.jar;lib/sat4j-sat.jar;lib/graphplan.jar;lib/pddl4j-v1.jar" fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/blocksworld2/blocksworld.pddl -f pddl/exemples/blocksworld2/"pb"$i".pddl" -n 30 -t 300 -p 1
done

##########     depots
#
#for i in `seq 3 9`;
#do
#  java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/depots/domain.pddl -f pddl/exemples/depots/"p0"$i".pddl" -n 30 -t 300
#done
#
#java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/depots/domain.pddl -f pddl/exemples/depots/p10.pddl -n 30 -t 300
#
#
#
##########     gripper
#
#for i in `seq 1 9`;
#do
#  java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/gripper/domain.pddl -f pddl/exemples/gripper/"p0"$i".pddl" -n 30 -t 300
#done
#
#
#java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/gripper/domain.pddl -f pddl/exemples/gripper/p10.pddl -n 30 -t 300
#
#
##########     logistics  DONE
#
#for i in `seq 1 9`;
#do
#  java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/logistics/domain.pddl -f pddl/exemples/logistics/"p0"$i".pddl" -n 30 -t 300
#done
#
#  java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/exemples/logistics/domain.pddl -f pddl/exemples/logistics/p10.pddl -n 30 -t 300
