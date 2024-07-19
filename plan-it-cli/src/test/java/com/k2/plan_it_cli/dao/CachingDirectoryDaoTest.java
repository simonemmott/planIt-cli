package com.k2.plan_it_cli.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CachingDirectoryDaoTest {
    private class APojo {
        @Getter
        @Setter
        private String key;
        @Getter
        private final String name;

        private APojo(String key, String name) {
            this.key = key;
            this.name = name;
        }
    }

    @Mock
    Supplier<String> keyGenerator;
    @Mock
    File dir;
    @Mock
    ObjectMapper mapper;

    CachingDirectoryDao<APojo> sut;

    @Test
    public void shouldLoadOnConstruct() throws IOException, NotExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        APojo aaa = new APojo("AAA", "NAME");
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            // When
            sut = new CachingDirectoryDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper);

            // Then
            assertTrue(sut.exists("AAA"));
            assertFalse(sut.exists("BBB"));
            assertEquals(aaa, sut.get("AAA"));
        }

    }

    @Test
    public void shouldThrowNotExistsExceptionWhenKeyDoesNotExistOnGet() throws IOException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        APojo aaa = new APojo("AAA", "NAME");
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new CachingDirectoryDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper);

            // Then When
            assertThrows(NotExistsException.class, () -> sut.get("BBB"));
        }
    }

    @Test
    public void shouldStreamAllEntitiesOnStream() throws IOException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        APojo aaa = new APojo("AAA", "NAME");
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new CachingDirectoryDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            List<APojo> results = sut.stream().toList();

            // Then
            assertTrue(results.contains(aaa));
            assertEquals(1, results.size());
        }

    }

    @Test
    public void shouldStreamMatchingEntitiesOnStreamWithPredicate() throws IOException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        APojo aaa = new APojo("AAA", "NAME");
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);
        Predicate<APojo> predicate = mock(Predicate.class);
        doReturn(true).when(predicate).test(aaa);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new CachingDirectoryDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            List<APojo> results = sut.stream(predicate).toList();

            // Then
            assertTrue(results.contains(aaa));
            assertEquals(1, results.size());
        }

    }

    @Test
    public void shouldInsertNewEntity() throws IOException, NotExistsException, AlreadyExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        APojo aaa = new APojo("AAA", "NAME");
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);
        APojo bbb = new APojo(null, "NAME");
        doReturn("BBB").when(keyGenerator).get();
        File b = mock(File.class);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new CachingDirectoryDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper);

            CachingDirectoryDao<APojo> spy = spy(sut);
            doReturn(b).when(spy).newFile(dir, "BBB.json");

            // When
            APojo result = spy.insert(bbb);

            // Then
            assertTrue(spy.exists("BBB"));
            assertEquals("BBB", result.getKey());
            verify(mapper).writeValue(b, bbb);
        }

    }

    @Test
    public void shouldUpdateEntity() throws IOException, NotExistsException, AlreadyExistsException, DataIntegrityViolationException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        APojo aaa = new APojo("AAA", "NAME");
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);
        APojo updated = new APojo("AAA", "UPDATED");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new CachingDirectoryDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            APojo result = sut.update(updated);

            // Then
            assertEquals(updated, result);

        }

    }

    @Test
    public void shouldDeleteEntity() throws IOException, NotExistsException, AlreadyExistsException, DataIntegrityViolationException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        APojo aaa = new APojo("AAA", "NAME");
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new CachingDirectoryDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            APojo result = sut.delete("AAA");

            // Then
            assertFalse(sut.exists("AAA"));

        }

    }


    private class Paths {
        @Getter
        private final Path path;
        @Getter
        private final List<Path> entries;
        @Getter
        private final List<File> files;

        private Paths(Path path, List<Path> entries, List<File> files) {
            this.path = path;
            this.entries = entries;
            this.files = files;
        }
    }
    private Paths mockPaths(File dir, String ... contents) {
        List<Path> paths = new ArrayList<>();
        List<File> files = new ArrayList<>();
        Path path = mock(Path.class);
        doReturn(path).when(dir).toPath();
        for (String entry : contents) {
            Path p = mock(Path.class);
            File f = mock(File.class);
            paths.add(p);
            files.add(f);
            if(entry.endsWith(".json")) {
                doReturn(true).when(p).endsWith(".json");
            } else {
                doReturn(false).when(p).endsWith(".json");
            }
            doReturn(f).when(p).toFile();
            if (p.endsWith(".json")) {
                doReturn(entry).when(f).getName();
            }
        }
        return new Paths(path, paths, files);
    }

}
