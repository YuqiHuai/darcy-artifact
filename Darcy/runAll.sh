#!/bin/bash

# check if argument is provided
if [ -z "$1" ]
  then
    >&2 echo "No project path argument supplied"
    exit 1
fi

projectPath=$1
# check if argument is an existing directory
if [ ! -d "$projectPath" ]
  then
    >&2 echo "Project path argument is not a directory"
    exit 1
fi

JAVA_8_HOME=/usr/lib/jvm/amazon-corretto-8.372.07.1-linux-x64
JAVA_17_HOME=/usr/lib/jvm/jdk-17

foo=" "
rm -r ./XMLreports/
mkdir XMLreports

echo "======== Constructing modules.txt and module-infos.txt ========"
start_pre=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
start_glob=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
for f in $(find $projectPath -type f -name 'module-info.class')
do
    DIR=$(dirname "${f}")
    foo="$foo${DIR}#"
done
echo $foo > ./modules.txt

for f in $(find $projectPath -type f -name 'module-info.java')
do
    DIR=$(dirname "${f}")
    dirpath="$dirpath${DIR}#"    
done
echo $dirpath > ./module-infos.txt
end_pre=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_pre=$((end_pre - start_pre))

echo "======== Static Analysis Using Classycle ========"
start_classycle=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
python ./TraverseDir.py $projectPath
end_classycle=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_classycle=$((end_classycle - start_classycle))
echo "-------- Classycle Static Analysis Finished --------"

echo "======== Parsing XML Files ========"
start_parse=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
python ./ParseXML.py
end_parse=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_parse=$((end_parse - start_parse))
echo "-------- Finished Parsing XML Files --------"

echo "======== Checking for Inconsistencies ========"
start_incon=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
java -jar Check_Consistency.jar
end_incon=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_incon=$((end_incon - start_incon))
echo "-------- Finished Checking Inconsistencies --------"

echo "======== Checking for opens Inconsistencies: Java Reflection Analysis ========"
start_open=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
# changing Java to 1.8
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/
# sudo update-alternatives --set java $JAVA_8_HOME/bin/java
# sudo update-alternatives --set javac $JAVA_8_HOME/bin/javac

$JAVA_8_HOME/bin/java -jar java-reflection-analysis.jar
end_open=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_open=$((end_open - start_open))
echo "-------- Finished Checking opens Inconsistencies --------"

echo "======== Checking for uses Inconsistencies: Uses Provides Analysis ========"
start_uses=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')

java -jar uses-provides-analysis.jar

end_uses=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_uses=$((end_uses - start_uses))
echo "-------- Finished Checking uses Inconsistencies --------"


echo "======== Repairing: Transforming module-info Files ========"
start_repair=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')

# changing Java to 1.9
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-9.jdk/Contents/Home/
# sudo update-alternatives --set java $JAVA_17_HOME/bin/java
# sudo update-alternatives --set javac $JAVA_17_HOME/bin/javac

$JAVA_17_HOME/bin/java -jar ModuleInfoTransformer.jar
end_repair=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_repair=$((end_repair - start_repair))
echo "-------- Finished Repairing --------"
echo "======== Stats ========"
end_glob=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_total=$((end_glob - start_glob))
echo "pre-process: $elapsed_pre ms"
echo "Static Analysis (Classycle): $elapsed_classycle ms"
echo "Parsing XML: $elapsed_parse ms"
echo "Checking Inconsistencies: $elapsed_incon ms"
echo "Reflection Analysis: $elapsed_open ms"
echo "Uses Analysis: $elapsed_uses ms"
echo "Repair: $elapsed_repair ms"
echo "Total Time: $elapsed_total ms"
