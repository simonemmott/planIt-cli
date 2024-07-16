package com.k2.plan_it_cli.dao;

import lombok.Getter;

import java.text.MessageFormat;
import java.util.function.Predicate;

public class NotExistsException extends GenericDaoException {
    @Getter
    private final String key;
    @Getter
    private final Predicate<?> predicate;
    @Getter
    private final String type;

    public NotExistsException(String key, String type) {
        super(message(key, type));
        this.key = key;
        this.type = type;
        this.predicate = null;
    }
    public NotExistsException(String key, String type, Throwable cause) {
        super(message(key, type), cause);
        this.key = key;
        this.type = type;
        this.predicate = null;
    }

    public NotExistsException(Predicate<?> predicate, String type) {
        super(message(predicate, type));
        this.key = null;
        this.type = type;
        this.predicate = predicate;
    }
    public NotExistsException(Predicate<?> predicate, String type, Throwable cause) {
        super(message(predicate, type), cause);
        this.key = null;
        this.type = type;
        this.predicate = predicate;
    }

    private static String message(String key, String type) {
        return MessageFormat.format(
                "No {0} exists for key {1}", type, key
        );
    }
    private static String message(Predicate<?> predicate, String type) {
        return MessageFormat.format(
                "No {0} exists for predicate {1}", type, predicate
        );
    }
}
