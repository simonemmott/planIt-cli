package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlreadyExistsExceptionTest {
    @Test
    public void shouldConstructWithMessageForKeyAndType() {
        // When
        AlreadyExistsException err = new AlreadyExistsException("KEY", "TYPE");

        // Then
        assertEquals("A TYPE already exists with key KEY", err.getMessage());
        assertEquals("KEY", err.getKey());
        assertEquals("TYPE", err.getType());
    }

    @Test
    public void shouldConstructWithMessageForKeyTypeAndCause() {
        // Given
        RuntimeException cause = new RuntimeException();
        // When
        AlreadyExistsException err = new AlreadyExistsException("KEY", "TYPE", cause);

        // Then
        assertEquals("A TYPE already exists with key KEY", err.getMessage());
        assertEquals("KEY", err.getKey());
        assertEquals("TYPE", err.getType());
        assertEquals(cause, err.getCause());
    }
}
