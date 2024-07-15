package com.k2.plan_it_cli.dao;

public interface FieldSetter<T, F> {
    T set(T entity, F field);
}
