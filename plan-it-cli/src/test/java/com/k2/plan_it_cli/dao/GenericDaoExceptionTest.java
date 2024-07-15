package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenericDaoExceptionTest {

    @Test
    public void shouldConstructWithMessage() {
        // When
        GenericDaoException err = new GenericDaoException("MESSAGE");

        // Then
        assertEquals("MESSAGE", err.getMessage());
    }

    @Test
    public void shouldConstructWithMessageAndCause() {
        // Given
        RuntimeException cause = new RuntimeException();
        // When
        GenericDaoException err = new GenericDaoException("MESSAGE", cause);

        // Then
        assertEquals("MESSAGE", err.getMessage());
        assertEquals(cause, err.getCause());
    }
}
