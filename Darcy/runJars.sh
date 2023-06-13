JAVA_HOME=/usr/lib/jvm/jdk-17

# check if flag named -fix is set
if [[ $* == *--fix* ]]
  then
    fix=true
  else
    fix=false
fi

start_pre=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
start_glob=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
echo "======== Checking for Inconsistencies ========"
start_incon=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
java -jar Check_Consistency.jar
end_incon=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_incon=$((end_incon - start_incon))
echo "-------- Finished Checking Inconsistencies --------"

echo "======== Checking for opens Inconsistencies: Java Reflection Analysis ========"
start_open=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')

$JAVA_HOME/bin/java -jar java-reflection-analysis.jar
end_open=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_open=$((end_open - start_open))
echo "-------- Finished Checking opens Inconsistencies --------"

echo "======== Checking for uses Inconsistencies: Uses Provides Analysis ========"
start_uses=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')

$JAVA_HOME/bin/java -jar uses-provides-analysis.jar

end_uses=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_uses=$((end_uses - start_uses))
echo "-------- Finished Checking uses Inconsistencies --------"

echo "======== Repairing: Transforming module-info Files ========"
start_repair=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
if [ "$fix" = true ]
  then
    $JAVA_HOME/bin/java -jar ModuleInfoTransformer.jar
  else
    echo "Not fixing inconsistencies, skipping repair"
fi
end_repair=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_repair=$((end_repair - start_repair))
echo "-------- Finished Repairing --------"

echo "======== Stats ========"
end_glob=$(ruby -e 'puts (Time.now.to_f * 1000).to_i')
elapsed_total=$((end_glob - start_glob))
echo "pre-process: $elapsed_pre ms"
echo "Checking Inconsistencies: $elapsed_incon ms"
echo "Reflection Analysis: $elapsed_open ms"
echo "Uses Analysis: $elapsed_uses ms"
echo "Repair: $elapsed_repair ms"
echo "Total Time: $elapsed_total ms"
