package com.k2.plan_it_cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan
public class PlanItCommandLineInterfaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanItCommandLineInterfaceApplication.class, args);
	}

}
