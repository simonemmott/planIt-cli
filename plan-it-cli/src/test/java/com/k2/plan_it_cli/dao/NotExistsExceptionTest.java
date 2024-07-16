package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class NotExistsExceptionTest {

    @Test
    public void shouldConstructWithMessageForKeyAndType() {
        // When
        NotExistsException err = new NotExistsException("KEY", "TYPE");

        // Then
        assertEquals("No TYPE exists for key KEY", err.getMessage());
        assertEquals("KEY", err.getKey());
        assertEquals("TYPE", err.getType());
    }

    @Test
    public void shouldConstructWithMessageForKeyTypeAndCause() {
        // Given
        RuntimeException cause = new RuntimeException();
        // When
        NotExistsException err = new NotExistsException("KEY", "TYPE", cause);

        // Then
        assertEquals("No TYPE exists for key KEY", err.getMessage());
        assertEquals("KEY", err.getKey());
        assertEquals("TYPE", err.getType());
        assertEquals(cause, err.getCause());
    }

    @Test
    public void shouldConstructWithMessageForPredicateAndType() {
        // Given
        Predicate<String> predicate = mock(Predicate.class);
        doReturn("PREDICATE").when(predicate).toString();
        // When
        NotExistsException err = new NotExistsException(predicate, "TYPE");

        // Then
        assertEquals("No TYPE exists for predicate PREDICATE", err.getMessage());
        assertNull(err.getKey());
        assertEquals("TYPE", err.getType());
        assertEquals(predicate, err.getPredicate());
    }

    @Test
    public void shouldConstructWithMessageForPredicateTypeAndCause() {
        // Given
        RuntimeException cause = new RuntimeException();
        Predicate<String> predicate = mock(Predicate.class);
        doReturn("PREDICATE").when(predicate).toString();
        // When
        NotExistsException err = new NotExistsException(predicate, "TYPE", cause);

        // Then
        assertEquals("No TYPE exists for predicate PREDICATE", err.getMessage());
        assertNull(err.getKey());
        assertEquals("TYPE", err.getType());
        assertEquals(predicate, err.getPredicate());
        assertEquals(cause, err.getCause());
    }
}
