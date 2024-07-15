package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
