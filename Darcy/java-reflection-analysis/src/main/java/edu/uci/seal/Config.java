//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.uci.seal;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import soot.PhaseOptions;
import soot.options.Options;

public class Config {
    public static final String androidJAR = System.getenv("ANDROID_HOME") + "/android-19/android.jar";
    public static String apkFilePath;
    public static BufferedWriter intentCmdsWriter = null;

    public Config() {
    }

    public static void applyBodySootOptions() {
        Options.v().set_src_prec(5);
        Options.v().set_whole_program(false);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_time(false);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_show_exception_dests(false);
        Options.v().set_verbose(false);
        Options.v().set_android_jars(System.getenv("ANDROID_HOME"));
        Options.v().set_soot_classpath(apkFilePath + File.pathSeparator + androidJAR);
        List<String> processDirs = new ArrayList();
        processDirs.add(apkFilePath);
        Options.v().set_process_dir(processDirs);
        Options.v().set_keep_line_number(true);
        Options.v().set_coffi(true);
    }

    public static void applyWholeProgramSootOptions() {
        Options.v().set_src_prec(5);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_time(false);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_show_exception_dests(false);
        Options.v().set_verbose(false);
        PhaseOptions.v().setPhaseOption("cg", "verbose:true");
        Options.v().set_android_jars(System.getenv("ANDROID_HOME"));
        Options.v().set_soot_classpath(apkFilePath + File.pathSeparator + androidJAR + File.pathSeparator + System.getenv("ANDROID_SDKS") + "/extras/android/support/v7/appcompat/libs/android-support-v7-appcompat.jar" + File.pathSeparator + System.getenv("ANDROID_SDKS") + "/extras/android/support/v7/appcompat/libs/android-support-v4.jar");
        List<String> processDirs = new ArrayList();
        processDirs.add(apkFilePath);
        Options.v().set_process_dir(processDirs);
        Options.v().set_keep_line_number(true);
        Options.v().set_coffi(true);
    }
}
