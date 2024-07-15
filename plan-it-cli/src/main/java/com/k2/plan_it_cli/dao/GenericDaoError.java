package com.k2.plan_it_cli.dao;

public class GenericDaoError extends RuntimeException{
    public GenericDaoError(String message) {
        super(message);
    }

    public GenericDaoError(String message, Throwable cause) {
        super(message, cause);
    }
}
