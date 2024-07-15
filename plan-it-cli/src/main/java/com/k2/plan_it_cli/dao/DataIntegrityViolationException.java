package com.k2.plan_it_cli.dao;

import lombok.Getter;

import java.io.File;
import java.text.MessageFormat;

public class DataIntegrityViolationException extends GenericDaoException {

    @Getter
    private final String key;
    @Getter
    private final Class<?> type;
    @Getter
    private final String rationale;

    public DataIntegrityViolationException(String key, Class<?> type, String rationale) {
        super(message(key, type, rationale));
        this.key = key;
        this.type = type;
        this.rationale = rationale;
    }

    public DataIntegrityViolationException(String key, Class<?> type, String rationale, Throwable cause) {
        super(message(key, type,rationale), cause);
        this.key = key;
        this.type = type;
        this.rationale = rationale;
    }

    private static String message(String key, Class<?> type, String rationale) {
        return MessageFormat.format(
                "Data integrity violation for {0} with key {1} [{2}]", type.getSimpleName(), key, rationale
        );
    }
}
