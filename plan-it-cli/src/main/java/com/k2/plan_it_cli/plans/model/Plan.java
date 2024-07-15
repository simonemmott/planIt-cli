package com.k2.plan_it_cli.plans.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Plan {
    @Getter
    private String reference;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String path;

    public Plan(String reference, String path) {
        this(reference, reference, "", path);
    }

    public Plan(String reference, String name, String path) {
        this(reference, name, "", path);
    }

    public Plan(String reference, String name, String description, String path) {
        this.reference = reference;
        this.name = name;
        this.description = description;
        this.path = path;
    }
}
