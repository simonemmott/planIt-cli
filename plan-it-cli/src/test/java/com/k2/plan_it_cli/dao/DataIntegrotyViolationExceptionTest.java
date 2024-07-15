package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataIntegrotyViolationExceptionTest {

    @Test
    public void shouldConstructWithMessageForKeyAndType() {
        // When
        DataIntegrityViolationException err = new DataIntegrityViolationException("KEY", String.class, "RATIONALE");

        // Then
        assertEquals("Data integrity violation for String with key KEY [RATIONALE]", err.getMessage());
        assertEquals("KEY", err.getKey());
        assertEquals(String.class, err.getType());
        assertEquals("RATIONALE", err.getRationale());
    }

    @Test
    public void shouldConstructWithMessageForKeyTypeAndCause() {
        // Given
        RuntimeException cause = new RuntimeException();
        // When
        DataIntegrityViolationException err = new DataIntegrityViolationException("KEY",String.class, "RATIONALE", cause);

        // Then
        assertEquals("Data integrity violation for String with key KEY [RATIONALE]", err.getMessage());
        assertEquals("KEY", err.getKey());
        assertEquals(String.class, err.getType());
        assertEquals("RATIONALE", err.getRationale());
        assertEquals(cause, err.getCause());
    }
}
