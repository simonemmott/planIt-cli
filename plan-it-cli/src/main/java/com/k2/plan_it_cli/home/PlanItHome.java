package com.k2.plan_it_cli.home;

import com.k2.plan_it_cli.config.PlanItConstants;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.stream.Stream;

@Component
public class PlanItHome {
    public static final String PLAN_IT_HOME_ENV = "PLAN_IT_HOME";
    final File home;
    final File bin;
    final File config;
    final File plans;

    public PlanItHome() {
        String planitHomePath = getHomeEnv();
        if (planitHomePath == null) {
            home = null;
            bin = null;
            config = null;
            plans = null;
        } else {
            home = new File(planitHomePath);
            bin = new File(home, "bin");
            config = new File(home, "config");
            plans = new File(home, "plans");
        }
    }

    PlanItHome(File home, File bin, File config, File plans) {
        this.home = home;
        this.bin = bin;
        this.config = config;
        this.plans = plans;
    }

    public void report(PrintStream out) {
        checkHome();
        out.println(MessageFormat.format("{0} Home Details", PlanItConstants.APP_NAME));
        out.println("=".repeat(PlanItConstants.APP_NAME.length() + 15));
        out.println();
        out.println(MessageFormat.format("{0} home directory: {1}", PlanItConstants.APP_NAME, home));
        out.println(MessageFormat.format("{0} bin directory: {1}", PlanItConstants.APP_NAME, bin));
        out.println(MessageFormat.format("{0} config directory: {1}", PlanItConstants.APP_NAME, config));
        out.println(MessageFormat.format("{0} plans directory: {1}", PlanItConstants.APP_NAME, plans));
    }

    void checkHome() {
        if (home == null) {
            throw new IllegalStateException(MessageFormat.format(
                    "No {0} home directory defined", PlanItConstants.APP_NAME
            ));
        }
        if (!home.exists()) {
            throw new IllegalStateException(MessageFormat.format(
                    "The {0} home directory {1} does not exist", PlanItConstants.APP_NAME, home
            ));
        }
        if (!bin.exists()) {
            throw new IllegalStateException(MessageFormat.format(
                    "The {0} bin directory {1} does not exist", PlanItConstants.APP_NAME, bin
            ));
        }
        if (!config.exists()) {
            throw new IllegalStateException(MessageFormat.format(
                    "The {0} config directory {1} does not exist", PlanItConstants.APP_NAME, config
            ));
        }
        if (!plans.exists()) {
            throw new IllegalStateException(MessageFormat.format(
                    "The {0} plans directory {1} does not exist", PlanItConstants.APP_NAME, plans
            ));
        }
    }

    public File getBin() {
        checkHome();
        return bin;
    }

    public File getConfig() {
        checkHome();
        return config;
    }

    public File getPlans() {
        checkHome();
        return plans;
    }

    public static String getHomeEnv() {
        return System.getenv(PLAN_IT_HOME_ENV);
    }

    public static boolean setup(PrintStream out, File home) {
        out.println(MessageFormat.format("Setting up PlanIt home directory in {0}", home));
        home.mkdir();
        setupBin(out, home);
        setupConfig(out, home);
        setupPlans(out, home);
        return true;
    }

    private static void setupBin(PrintStream out, File home) {
        out.println(MessageFormat.format("Creating bin directory in {0}", home));
        File bin = new File(home, "bin");
        bin.mkdir();
    }

    private static void setupConfig(PrintStream out, File home) {
        out.println(MessageFormat.format("Creating config directory in {0}", home));
        File bin = new File(home, "config");
        bin.mkdir();
    }

    private static void setupPlans(PrintStream out, File home) {
        out.println(MessageFormat.format("Creating plans directory in {0}", home));
        File bin = new File(home, "plans");
        bin.mkdir();
    }

    private static boolean dirIsEmpty(File dir) {
        if (Files.isDirectory(dir.toPath())) {
            try (Stream<Path> entries = Files.list(dir.toPath())) {
                return !entries.findFirst().isPresent();
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    public static boolean checkHomeDir(File home) {
        if (!home.getParentFile().exists()) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "The parent directory {0}, does not exist", home.getParentFile()
            ));
        } else {
            if (home.getParentFile().isFile()) {
                throw new IllegalArgumentException(MessageFormat.format(
                        "The parent directory {0}, exists but is a file", home.getParentFile()
                ));
            }
        }
        if (home.exists()) {
            if (home.isFile()) {
                throw new IllegalArgumentException(MessageFormat.format(
                        "The home directory {0}, exists but is a file", home
                ));
            }
            if (home.isDirectory() && !dirIsEmpty(home)) {
                throw new IllegalArgumentException(MessageFormat.format(
                        "The home directory {0}, exists but is not empty", home
                ));
            }
        }
        return true;
    }


}
