package com.k2.plan_it_cli.commands;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.stream.Stream;

@Command
public class Install {

    @Command(command = "install")
    public String install(File home) {
        checkHomeDir(home);
        setup(home);
        return MessageFormat.format("Installed PlanIt home dir in {0}", home);
    }

    private static void setup(File home) {
        System.out.println(MessageFormat.format("Setting up PlanIt home directory in {0}", home));
        home.mkdir();
        setupBin(home);
        setupConfig(home);
        setupPlans(home);
    }

    private static void setupBin(File home) {
        System.out.println(MessageFormat.format("Creating bin directory in {0}", home));
        File bin = new File(home, "bin");
        bin.mkdir();
    }

    private static void setupConfig(File home) {
        System.out.println(MessageFormat.format("Creating config directory in {0}", home));
        File bin = new File(home, "config");
        bin.mkdir();
    }

    private static void setupPlans(File home) {
        System.out.println(MessageFormat.format("Creating plans directory in {0}", home));
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

    private static void checkHomeDir(File home) {
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
    }


}