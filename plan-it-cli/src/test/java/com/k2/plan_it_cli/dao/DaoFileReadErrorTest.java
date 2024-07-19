package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DaoFileReadErrorTest {

    @Test
    public void shouldConstructWithMessageForFileAndType() {
        // Given
        File file = mock(File.class);
        doReturn("FILE").when(file).toString();
        Class<?> type = String.class;

        // When
        DaoFileReadError err = new DaoFileReadError(file, type);

        // Then
        assertEquals(file, err.getFile());
        assertEquals(type, err.getType());
        assertEquals("Unable to marshal file FILE into a instance of String", err.getMessage());
    }

    @Test
    public void shouldConstructWithMessageForKeyTypeAndCause() {
        // Given
        File file = mock(File.class);
        doReturn("FILE").when(file).toString();
        Class<?> type = String.class;
        RuntimeException cause = new RuntimeException();

        // When
        DaoFileReadError err = new DaoFileReadError(file, type, cause);

        // Then
        assertEquals("Unable to marshal file FILE into a instance of String", err.getMessage());
        assertEquals(file, err.getFile());
        assertEquals(type, err.getType());
        assertEquals(cause, err.getCause());
    }
}
