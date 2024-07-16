package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class NotUniqueExceptionTest {

    @Mock
    private Predicate<?> predicate;

    @BeforeEach
    public void setup() {
        doReturn("PREDICATE").when(predicate).toString();
    }

    @Test
    public void shouldConstructWithMessageForPredicateAndType() {
        // When
        NotUniqueException err = new NotUniqueException(predicate, "TYPE");

        // Then
        assertEquals("The predicate PREDICATE of TYPE is not unique", err.getMessage());
        assertEquals(predicate, err.getPredicate());
        assertEquals("TYPE", err.getType());
    }

    @Test
    public void shouldConstructWithMessageForKeyTypeAndCause() {
        // Given
        RuntimeException cause = new RuntimeException();
        // When
        NotUniqueException err = new NotUniqueException(predicate, "TYPE", cause);

        // Then
        assertEquals("The predicate PREDICATE of TYPE is not unique", err.getMessage());
        assertEquals(predicate, err.getPredicate());
        assertEquals("TYPE", err.getType());
        assertEquals(cause, err.getCause());
    }
}
