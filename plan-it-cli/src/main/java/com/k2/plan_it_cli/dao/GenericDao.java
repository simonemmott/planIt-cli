package com.k2.plan_it_cli.dao;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface GenericDao<T> {
    String ofType();
    KeyGetter<T> keyGetter();
    boolean exists(String key);
    T get(String key) throws NotExistsException;
    T get(Predicate<? super T> predicate) throws NotExistsException, NotUniqueException;
    Stream<T> stream();
    Stream<T> stream(Predicate<? super T> predicate);
    T insert(T entity) throws AlreadyExistsException;
    T update(T entity) throws NotExistsException, DataIntegrityViolationException;
    T delete(String key) throws NotExistsException;
    void registerPostIndexCallback(Runnable postIndexCallback);
}
