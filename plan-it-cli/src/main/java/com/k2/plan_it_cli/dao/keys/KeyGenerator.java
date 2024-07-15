package com.k2.plan_it_cli.dao.keys;

public interface KeyGenerator {
    String nextKey(Class<?> type);
}
