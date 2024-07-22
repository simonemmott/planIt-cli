package com.k2.plan_it_cli.dao;

public interface IndexationHandler<T> {
    void start();
    void accept(IndexedEntityCallback<T> indexedEntityCallback);
    void end();
}
