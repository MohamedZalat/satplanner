javac -d classes -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar src/fr/uga/pddl4j/tutorial/asp/ASP.java -Xlint:unchecked


#########  blocksworld

#for i in `seq 1 9`;
#do
#  java -cp classes:lib/pddl4j-3.8.3.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/blocksworld/domain.pddl -f pddl/exemples/blocksworld/"p0"$i".pddl" -t 300
#done


#for i in `seq 10 15`;
#do
#  java -cp classes:lib/pddl4j-3.8.3.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/blocksworld/domain.pddl -f pddl/exemples/blocksworld/"p"$i".pddl" -t 300
#done


#########  depots

#for i in `seq 1 9`;
#do
#  java -cp classes:lib/pddl4j-3.8.3.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/depots/domain.pddl -f pddl/exemples/depots/"p0"$i".pddl" -t 300
#done


#for i in `seq 10 15`;
#do
#  java -cp classes:lib/pddl4j-3.8.3.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/depots/domain.pddl -f pddl/exemples/depots/"p"$i".pddl" -t 300
#done

#########  gripper

for i in `seq 1 9`;
do
  java -cp classes:lib/pddl4j-3.8.3.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/gripper/domain.pddl -f pddl/exemples/gripper/"p0"$i".pddl" -t 300
done


for i in `seq 10 15`;
do
  java -cp classes:lib/pddl4j-3.8.3.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/gripper/domain.pddl -f pddl/exemples/gripper/"p"$i".pddl" -t 300
done

#########  logistics

for i in `seq 1 9`;
do
  java -cp classes:lib/pddl4j-3.8.3.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/logistics/domain.pddl -f pddl/exemples/logistics/"p0"$i".pddl" -t 300
done


for i in `seq 10 15`;
do
  java -cp classes:lib/pddl4j-3.8.3.jar fr.uga.pddl4j.tutorial.asp.ASP -o pddl/exemples/logistics/domain.pddl -f pddl/exemples/logistics/"p"$i".pddl" -t 300
done
