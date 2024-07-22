package com.k2.plan_it_cli.dao;

public interface IndexedEntityCallback<T> {
    String getKey();
    T getEntity();
}
