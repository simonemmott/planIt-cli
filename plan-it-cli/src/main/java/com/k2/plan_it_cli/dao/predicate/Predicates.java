package com.k2.plan_it_cli.dao.predicate;

import com.k2.plan_it_cli.dao.FieldGetter;
import com.k2.plan_it_cli.dao.IndexGetter;

import java.util.function.Predicate;

public class Predicates {
    public static <R,T> IndexGetterEquals<R> equals(IndexGetter<R, T> getter, T target) {
        return new IndexGetterEquals<R>(getter, target);
    }
    public static <T> And<T> and(Predicate<? super T> ... predicates) {
        return new And<T>(predicates);
    }
}
