package com.k2.plan_it_cli.commands;

import com.k2.plan_it_cli.home.PlanItHome;
import org.springframework.shell.command.annotation.Command;

import java.io.File;
import java.text.MessageFormat;

@Command
public class Install {

    @Command(command = "install")
    public String install(File home) {
        PlanItHome.checkHomeDir(home);
        PlanItHome.setup(System.out, home);
        return MessageFormat.format("Installed PlanIt home dir in {0}", home);
    }

}