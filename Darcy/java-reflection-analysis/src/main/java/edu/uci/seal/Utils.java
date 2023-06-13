//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.uci.seal;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.BytecodeOffsetTag;
import soot.tagkit.Tag;
import soot.util.Chain;

public class Utils {
    public Utils() {
    }

    public static void setupDummyMainMethod() {
        Config.applyWholeProgramSootOptions();
        Config.applyWholeProgramSootOptions();
        Scene.v().loadNecessaryClasses();
    }

    public static void setupAndroidAppForBody() {
        Config.applyBodySootOptions();
        Scene.v().loadNecessaryClasses();
    }

    public static String createTabsStr(int tabs) {
        String tabsStr = "";

        for(int i = 0; i < tabs; ++i) {
            tabsStr = tabsStr + "\t";
        }

        return tabsStr;
    }

    public static Set<SootMethod> getApplicationMethods() {
        Chain<SootClass> appClasses = Scene.v().getApplicationClasses();
        Set<SootMethod> appMethods = new LinkedHashSet();
        Iterator var2 = appClasses.iterator();

        while(var2.hasNext()) {
            SootClass clazz = (SootClass)var2.next();
            appMethods.addAll(clazz.getMethods());
        }

        return appMethods;
    }

    public static List<SootMethod> getMethodsInReverseTopologicalOrder() {
        List<SootMethod> entryPoints = Scene.v().getEntryPoints();
        CallGraph cg = Scene.v().getCallGraph();
        List<SootMethod> topologicalOrderMethods = new ArrayList();
        Stack<SootMethod> methodsToAnalyze = new Stack();
        Iterator var4 = entryPoints.iterator();

        label41:
        while(true) {
            SootMethod entryPoint;
            do {
                if (!var4.hasNext()) {
                    List<SootMethod> rtoMethods = Lists.reverse(topologicalOrderMethods);
                    return rtoMethods;
                }

                entryPoint = (SootMethod)var4.next();
            } while(!isApplicationMethod(entryPoint));

            methodsToAnalyze.push(entryPoint);

            while(true) {
                SootMethod method;
                do {
                    do {
                        if (methodsToAnalyze.isEmpty()) {
                            continue label41;
                        }

                        method = (SootMethod)methodsToAnalyze.pop();
                    } while(topologicalOrderMethods.contains(method));
                } while(!method.hasActiveBody());

                topologicalOrderMethods.add(method);
                Iterator var7 = getOutgoingEdges(method, cg).iterator();

                while(var7.hasNext()) {
                    Edge edge = (Edge)var7.next();
                    methodsToAnalyze.push(edge.tgt());
                }
            }
        }
    }

    public static boolean isApplicationMethod(SootMethod method) {
        Chain<SootClass> applicationClasses = Scene.v().getApplicationClasses();
        Iterator var2 = applicationClasses.iterator();

        SootClass appClass;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            appClass = (SootClass)var2.next();
        } while(!appClass.getMethods().contains(method));

        return true;
    }

    public static SootClass getLibraryClass(String className) {
        Chain<SootClass> libraryClasses = Scene.v().getLibraryClasses();
        Iterator var2 = libraryClasses.iterator();

        SootClass libClass;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            libClass = (SootClass)var2.next();
        } while(!libClass.getName().equals(className));

        return libClass;
    }

    public static List<Edge> getOutgoingEdges(SootMethod method, CallGraph cg) {
        Iterator<Edge> edgeIterator = cg.edgesOutOf(method);
        List<Edge> outgoingEdges = Lists.newArrayList(edgeIterator);
        return outgoingEdges;
    }

    public static void printInputStream(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }

    }

    public static void runCmdAsProcess(String[] cmdArr) {
        List<String> cmd = Arrays.asList(cmdArr);
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Map<String, String> environ = builder.environment();

        try {
            Process process = builder.start();
            InputStream is = process.getInputStream();
            System.out.println("normal output: ");
            printInputStream(is);
            InputStream es = process.getErrorStream();
            System.out.println("error output: ");
            printInputStream(es);
            System.out.println("Program terminated!");
        } catch (IOException var7) {
            var7.printStackTrace();
        }

    }

    public static BytecodeOffsetTag extractByteCodeOffset(Unit unit) {
        Iterator var1 = unit.getTags().iterator();

        Tag tag;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            tag = (Tag)var1.next();
        } while(!(tag instanceof BytecodeOffsetTag));

        BytecodeOffsetTag bcoTag = (BytecodeOffsetTag)tag;
        return bcoTag;
    }

    public static Logger setupLogger(Class inClass, String apkName) {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        lc.reset();
        lc.putProperty("toolName", inClass.getName());
        lc.putProperty("apkName", apkName);
        configurator.setContext(lc);

        try {
            configurator.doConfigure("logback-fileAppender.xml");
        } catch (JoranException var5) {
            var5.printStackTrace();
        }

        Logger logger = LoggerFactory.getLogger(inClass);
        return logger;
    }

    public static Logger setupVerboseLogger(Class inClass, String apkName) {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        lc.reset();
        lc.putProperty("toolName", inClass.getName());
        lc.putProperty("apkName", apkName);
        configurator.setContext(lc);

        try {
            configurator.doConfigure("logback-fileAppender-verbose.xml");
        } catch (JoranException var5) {
            var5.printStackTrace();
        }

        Logger logger = LoggerFactory.getLogger(inClass);
        return logger;
    }

    public static void printTagsOfMethod(Logger logger, SootMethod method) {
        Iterator var2 = method.getActiveBody().getUnits().iterator();

        while(true) {
            Unit unit;
            do {
                if (!var2.hasNext()) {
                    return;
                }

                unit = (Unit)var2.next();
            } while(unit.getTags().isEmpty());

            Iterator var4 = unit.getTags().iterator();

            while(var4.hasNext()) {
                Tag tag = (Tag)var4.next();
                logger.debug("unit: " + unit);
                logger.debug("\ttag: " + tag.getName() + "," + tag.toString());
            }
        }
    }

    public static void makeFileEmpty(String filename) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), Charset.defaultCharset());
            writer.close();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static String getFullCompName(String packageName, String compName) {
        if (compName.startsWith(".")) {
            return packageName + compName;
        } else {
            return compName.contains(".") ? compName : packageName + "." + compName;
        }
    }

    public static void deletePathIfExists(Path path) {
        if (Files.exists(path, new LinkOption[0])) {
            try {
                Files.delete(path);
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static InvokeExpr getInvokeExprOfAssignStmt(Unit unit) {
        if (unit instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt)unit;
            if (assignStmt.getRightOp() instanceof InvokeExpr) {
                InvokeExpr invokeExpr = (InvokeExpr)assignStmt.getRightOp();
                return invokeExpr;
            }
        }

        return null;
    }

    public static void storeIntentControlledTargets(File apkFile, Logger logger, Set<Triplet<Unit, BytecodeOffsetTag, SootMethod>> targets) {
        String targetsFilename = apkFile.getName() + "_ic_tgt_units.txt";
        logger.debug("Saving intent-controlled targets to " + targetsFilename);

        try {
            PrintWriter writer = new PrintWriter(targetsFilename);
            Iterator var5 = targets.iterator();

            while(var5.hasNext()) {
                Triplet<Unit, BytecodeOffsetTag, SootMethod> target = (Triplet)var5.next();
                writer.write(target.getValue1() + "#" + target.getValue2() + "\n");
            }

            writer.close();
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        }

    }

    public static void storeIntentControlledTargetsWithSpecialKeys(File apkFile, Logger logger, Set<Quartet<Unit, BytecodeOffsetTag, SootMethod, String>> targets, String abbrv) {
        String targetsFilename = apkFile.getName() + "_" + abbrv + "_ic_tgt_units.txt";
        logger.debug("Saving intent-controlled targets to " + targetsFilename);

        try {
            PrintWriter writer = new PrintWriter(targetsFilename);
            int i = 0;

            for(Iterator var7 = targets.iterator(); var7.hasNext(); ++i) {
                Quartet<Unit, BytecodeOffsetTag, SootMethod, String> target = (Quartet)var7.next();
                writer.write(target.getValue1() + "#" + target.getValue2() + "#" + (String)target.getValue3() + "\n");
            }

            writer.close();
        } catch (FileNotFoundException var9) {
            var9.printStackTrace();
        }

    }
}
