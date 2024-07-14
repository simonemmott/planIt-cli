package com.k2.plan_it_cli;

import org.springframework.shell.command.annotation.Command;

@Command(command = "parent")
public class ExampleCommand {

    @Command(command = "example")
    public String example() {
        return "Hello";
    }

}
