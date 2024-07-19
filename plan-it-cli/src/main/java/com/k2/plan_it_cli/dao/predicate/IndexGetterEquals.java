package com.k2.plan_it_cli.dao.predicate;

import com.k2.plan_it_cli.dao.IndexGetter;

import java.util.function.Predicate;

public record IndexGetterEquals<T>(IndexGetter<T, ?> getter, Object target) implements Predicate<T> {

    @Override
    public boolean test(T source) {
        return target.equals(getter.getGetter().get(source));
    }

    @Override
    public Predicate<T> and(Predicate<? super T> other) {
        return Predicates.and(this, other);
    }
}
