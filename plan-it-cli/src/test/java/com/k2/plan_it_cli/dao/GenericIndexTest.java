package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GenericIndexTest {

    @Test
    public void shouldConstructWithAliasPredicateAndIsUnique() {
        // Given
        FieldGetter<Integer, Boolean> getter = i -> i>0;
        IndexGetter<Integer, Boolean> indexGetter = new IndexGetter<>(getter);
        // When
        GenericIndex<Integer> sut = new GenericIndex<>("ALIAS", indexGetter, false);

        // Then
        assertEquals(indexGetter, sut.indexGetter());
        assertEquals(false, sut.isUnique());
    }
}
