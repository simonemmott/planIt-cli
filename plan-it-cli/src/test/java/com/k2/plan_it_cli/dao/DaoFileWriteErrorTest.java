package com.k2.plan_it_cli.dao;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DaoFileWriteErrorTest {

    @Test
    public void shouldConstructWithMessageForFileAndType() {
        // Given
        File file = mock(File.class);
        doReturn("FILE").when(file).toString();
        Class<?> type = String.class;
        String key = "KEY";

        // When
        DaoFileWriteError err = new DaoFileWriteError(file, type, key);

        // Then
        assertEquals(file, err.getFile());
        assertEquals(type, err.getType());
        assertEquals(key, err.getKey());
        assertEquals("Unable to serialise an instance of String with key KEY into file FILE", err.getMessage());
    }

    @Test
    public void shouldConstructWithMessageForKeyTypeAndCause() {
        // Given
        File file = mock(File.class);
        doReturn("FILE").when(file).toString();
        Class<?> type = String.class;
        String key = "KEY";
        RuntimeException cause = new RuntimeException();

        // When
        DaoFileWriteError err = new DaoFileWriteError(file, type, key, cause);

        // Then
        assertEquals("Unable to serialise an instance of String with key KEY into file FILE", err.getMessage());
        assertEquals(file, err.getFile());
        assertEquals(type, err.getType());
        assertEquals(key, err.getKey());
        assertEquals(cause, err.getCause());
    }
}
