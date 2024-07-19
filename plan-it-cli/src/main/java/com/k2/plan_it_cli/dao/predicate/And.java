package com.k2.plan_it_cli.dao.predicate;

import com.k2.plan_it_cli.dao.IndexGetter;

import java.util.function.Predicate;

public record And<T>(Predicate<? super T> ... predicates) implements Predicate<T> {

    @Override
    public boolean test(T source) {
        for (Predicate<? super T> predicate : predicates) {
            if (!predicate.test(source)) {
                return false;
            }
        }
        return true;
    }
}
