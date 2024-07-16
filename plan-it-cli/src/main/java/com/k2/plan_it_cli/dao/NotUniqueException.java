package com.k2.plan_it_cli.dao;

import lombok.Getter;

import java.text.MessageFormat;
import java.util.function.Predicate;

public class NotUniqueException extends GenericDaoException {
    @Getter
    private final Predicate<?> predicate;
    @Getter
    private final String type;

    public NotUniqueException(Predicate<?> predicate, String type) {
        super(message(predicate, type));
        this.predicate = predicate;
        this.type = type;
    }
    public NotUniqueException(Predicate<?> predicate, String type, Throwable cause) {
        super(message(predicate, type), cause);
        this.predicate = predicate;
        this.type = type;
    }

    private static String message(Predicate<?> predicate, String type) {
        return MessageFormat.format(
                "The predicate {0} of {1} is not unique", predicate, type
        );
    }
}
