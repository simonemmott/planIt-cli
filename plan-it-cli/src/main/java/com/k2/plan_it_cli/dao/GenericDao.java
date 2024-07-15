package com.k2.plan_it_cli.dao;

import java.util.stream.Stream;

public interface GenericDao<T> {
    boolean exists(String key);
    T get(String key) throws NotExistsException;
    Stream<T> stream();
    T insert(T entity) throws AlreadyExistsException;
    T update(T entity) throws NotExistsException, DataIntegrityViolationException;
    T delete(String key) throws NotExistsException;
}
