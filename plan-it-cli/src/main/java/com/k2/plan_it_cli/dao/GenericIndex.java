package com.k2.plan_it_cli.dao;

import com.k2.plan_it_cli.dao.predicate.And;
import com.k2.plan_it_cli.dao.predicate.IndexGetterEquals;

import java.util.function.Predicate;

public record GenericIndex<T>(String alias, IndexGetter<T, ?> indexGetter, boolean isUnique) {

    public boolean supports(Predicate<? super T> predicate) {
        if (predicate instanceof IndexGetterEquals<? super T> indexGetterEquals) {
            return indexGetter.equals(indexGetterEquals.getter());
        };
        if (predicate instanceof And<? super T> and) {
            for (Predicate<? super T> childPredicate : and.predicates()) {
                if (supports(childPredicate)) {
                    return true;
                }
            }
            return false;
        };
        return false;
    }

    @Override
    public String toString() {
        return "GenericIndex{" +
                "alias='" + alias + '\'' +
                ", isUnique=" + isUnique +
                '}';
    }
}
