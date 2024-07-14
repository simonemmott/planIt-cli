package com.k2.plan_it_cli.commands;

import com.k2.plan_it_cli.home.PlanItHome;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;

import java.io.File;
import java.text.MessageFormat;

@Command(command = "home")
public class Home {

    @Getter
    private final PlanItHome planItHome;

    @Autowired
    public Home(PlanItHome planItHome) {
        this.planItHome = planItHome;
    }

    @Command(command = "show")
    public String show() {
        planItHome.report(System.out);
        return "";
    }

}