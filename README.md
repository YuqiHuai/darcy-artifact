# Darcy #
This repository contains:
* Darcy, a tool designed to automatically detect and repair architectural inconsistencies that may arise in developing Java applications using Java Platform Module System
* The input data needed for running this tool
* The materials used in evaluating it

In the following sections, we explain all the materials contained in this repository.

## 1. Darcy: Detection and Repair of Architectural Inconsistencies in Java ##
Darcy’s main goal is to detect and repair architectural inconsistencies that may arise within Java applications using Java Platform Module System (JPMS), introduced in the 9th version of Java. In the paper, we formally specify eight types of inconsistent dependencies that may arise when using JPMS. Darcy leverages these definitions and static analyses to automatically (1) detect the specified inconsistent dependencies within Java applications and (2) repair those identified inconsistencies.Darcy’s implementation contains several components:
* Class Dependency Analysis: To identify actual dependencies of an input Java application, Darcy relies on an static analysis tool, Classycle [1] , which provides a complete report of all dependencies in source code of a Java application at both the class and package levels. (`TraverseDir.py`)
* Parsing the XML files generated by Classycle, implemented in Python (`ParseXML.py`)
* Java Inconsistency Analysis: To identify all types of inconsistency scenarios, for each directive in a module-info.java file, Java Inconsistency Analysis explores actual and specified dependencies, stored in their respective database components. It identifies any occurrence of an inconsistent dependency previously defined and reports the identified architectural inconsistency, the module affected,and the specific directive involved. This component is implemented in Java (`Check_Consistency.jar`).
* Java Reflection Analysis: It leverages a custom static analysis, which we have implemented using the Soot framework [2], to identify usage of reflection in the input application. This component is implemented in Java (`java_reflection_analysis.jar`)
* ServiceLoader Analysis: It leverages a custom static analysis using the Soot framework to identify usage of java.util.ServiceLoader in the input application. This component is implemented in Java (`uses_provides_analysis.jar`)
* Module-Info Transformer: This component leverages ANTLR [3] and deletes or modifies the explicit dependencies defined in the module-info files to repair the identified inconsistencies within Java applications. This component is implemented in Java (`ModuleInfoTransformer.jar`).

The `runAll.sh` script, located in the _Darcy_ directory, runs all these components in the explained order. After running this script, it asks for a Java project path as an input, runs Darcy’s components, and reports its output. The output contains all the reports regarding each Darcy’s component. The identified inconsistencies are reported as "_FOUND INCONSISTENCIES_", and also saved in a text file `excessDirectives.txt` in the Darcy directory. The identified inconsistencies of type _opens_  are reported in the Java reflection analysis output and also saved in `excessOpenDirectives.txt` in Darcy directory. The inconsistencies of type _uses_ are reported as "_Excess Uses_", and also saved in `excessUsesDirectives.txt`. Finally, in the output of the repair section, the modified module-info files are printed after Darcy’s repair. The original module-info files within the target Java project is also modified and saved.
You can leverage the virtual machine image we provide [here](https://drive.google.com/file/d/1xRyR_K3LABRaVe9iX6wfc77rx0CQ9oph/view?usp=sharing). There is a directory `Darcy` on the desktop which contains all the source files and the runAll script. The username and password for logging into the VM image are:
```
Username: negar
password : "1"
```
## 2. Input Data ##
This repository also contains the input data for executing Darcy; this data were used in evaluating Darcy as reported in the paper. This input data contains open-source Java-9 applications which are located in a folder named `dataset_projects`:
```
Darcy/dataset_projects
```
You  can use any of the Java projects within this folder, however, for more convenience, you can use the following example input:
1. First go to `Darcy` directory and run the script:
```
cd Darcy
./runAll.sh
```
2. When it asks for "_Project Path_", enter the following address:

`/home/negar/Desktop/Darcy/Java9-Module-Inconsistencies/dataset_projects/sense-nine-start-point`

The output for this example input is provided in `exampleOutPut.txt` in the root directory.

## 3. Evaluation Artifacts ##
We have provided all the materials used in the evaluation process located at a folder named `evaluation` in the root directory of this repo. There is an excel file `results.xlsx` in the `evaluation` folder that contains all the evaluation results presented in the paper (Table 2-6) along with their calculations. Each table’s result is represented in a separate sheet:
* __Identified Inconsistencies__: This sheet contains information about all 38 Java-9 application in our dataset, along with their identified inconsistencies, separated by their types, and whether they were successfully compiled after the repair. It also shows the test passing rate for those project containing test suite, after Darcy’s repair.
* __Attack Surface Reduction__: This sheet includes information about Darcy’s ability in reduction of attack surface within Java-9 applications and its calculations using the number of exposed packages.
* __Encapsulation Improvement__: This sheet contains information about the encapsulation improvement within Java projects of our dataset after Darcy's repair, along with the calculations of the two metrics used in the paper (RoC and NCD).
* __Software Bloat Reduction__: This sheet includes information about the runtime memory reduction in Java-9 applications after Darcy’s repair.
* __Execution Time__: This sheet includes the execution time of each components of Darcy for all Java-9 applications within our dataset.

## References: ##
[1] F.-J. Elmer, "Classycle: Analysing Tools for Java Class and Package Dependencies," How Classycle works, 2012.

[2] R.Valle ́e-Rai,P.Co,E.Gagnon, L.Hendren, P.Lam, and V.Sundaresan, "Soot: A Java bytecode optimization framework," in CASCON First Decade High Impact Papers. IBM Corp., 2010, pp. 214–224.

[3] "ANTLR," http://www.antlr.org, 2018.

