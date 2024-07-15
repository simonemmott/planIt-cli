package com.k2.plan_it_cli.dao;

import lombok.Getter;

import java.io.File;
import java.text.MessageFormat;

public class FsDaoFileReadError extends GenericDaoError {

    @Getter
    private final File file;
    @Getter
    private final Class<?> type;

    public FsDaoFileReadError(File file, Class<?> type) {
        super(message(file, type));
        this.file = file;
        this.type = type;
    }

    public FsDaoFileReadError(File file, Class<?> type, Throwable cause) {
        super(message(file, type), cause);
        this.file = file;
        this.type = type;
    }

    private static String message(File file, Class<?> type) {
        return MessageFormat.format(
                "Unable to marshal file {0} into a instance of {1}", file, type.getSimpleName()
        );
    }
}
