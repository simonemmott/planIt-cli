package com.k2.plan_it_cli.dao;

import lombok.Getter;

import java.io.File;
import java.text.MessageFormat;

public class DaoFileWriteError extends GenericDaoError {

    @Getter
    private final File file;
    @Getter
    private final Class<?> type;
    @Getter
    private final String key;

    public DaoFileWriteError(File file, Class<?> type, String key) {
        super(message(file, type, key));
        this.file = file;
        this.type = type;
        this.key = key;
    }

    public DaoFileWriteError(File file, Class<?> type, String key, Throwable cause) {
        super(message(file, type, key), cause);
        this.file = file;
        this.type = type;
        this.key = key;
    }

    private static String message(File file, Class<?> type, String key) {
        return MessageFormat.format(
                "Unable to serialise an instance of {0} with key {1} into file {2}",
                type.getSimpleName(),
                key,
                file
        );
    }
}
