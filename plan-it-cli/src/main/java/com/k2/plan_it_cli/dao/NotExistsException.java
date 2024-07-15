package com.k2.plan_it_cli.dao;

import lombok.Getter;

import java.text.MessageFormat;

public class NotExistsException extends GenericDaoException {
    @Getter
    private final String key;
    @Getter
    private final String type;

    public NotExistsException(String key, String type) {
        super(message(key, type));
        this.key = key;
        this.type = type;
    }
    public NotExistsException(String key, String type, Throwable cause) {
        super(message(key, type), cause);
        this.key = key;
        this.type = type;
    }

    private static String message(String key, String type) {
        return MessageFormat.format(
                "No {0} exists for key {1}", type, key
        );
    }
}
