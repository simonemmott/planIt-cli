package com.k2.plan_it_cli.dao;

public class GenericDaoException extends Exception{
    public GenericDaoException(String message) {
        super(message);
    }

    public GenericDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
