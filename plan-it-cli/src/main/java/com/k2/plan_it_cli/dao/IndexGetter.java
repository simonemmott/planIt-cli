package com.k2.plan_it_cli.dao;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

public class IndexGetter<T,F> {
    private final UUID uuid = UUID.randomUUID();
    @Getter
    private final FieldGetter<? super T,F> getter;

    public IndexGetter(FieldGetter<? super T, F> getter) {
        this.getter = getter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexGetter<?, ?> getter = (IndexGetter<?, ?>) o;
        return Objects.equals(uuid, getter.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
