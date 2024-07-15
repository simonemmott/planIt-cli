package com.k2.plan_it_cli.dao;

public interface FieldGetter<T,F> {
    F get(T entity);
}
