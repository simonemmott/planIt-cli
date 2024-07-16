package com.k2.plan_it_cli.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FsDaoTest {

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

    FsDao<APojo> sut;

    @Test
    public void shouldLoadOnConstruct() {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            // When
            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper);

            // Then
            assertTrue(sut.exists("AAA"));
            assertFalse(sut.exists("BBB"));
        }
    }

    @Test
    public void shouldCallbackPostIndex() {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        Runnable postIndexCallback = mock(Runnable.class);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            // When
            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (entity, key) -> {entity.setKey(key); return entity;},
                    keyGenerator,
                    dir,
                    mapper,
                    postIndexCallback);

            // Then
            verify(postIndexCallback).run();
        }
    }

    @Test
    public void shouldThrowNotExistsExceptionWhenKeyDoesNotExistOnGet() {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
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
    public void shouldMapFileToEntityWhenKeyDoesExistOnGet() throws IOException, NotExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.xml");
        APojo entity = mock(APojo.class);
        doReturn(entity).when(mapper).readValue(paths.files.get(0), APojo.class);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            // Then When
            assertEquals(entity, sut.get("AAA"));
        }
    }

    @Test
    public void shouldReturnEntityOnGetWithPredicate() throws IOException, NotExistsException, NotUniqueException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.json");
        APojo aaa = mock(APojo.class);
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);
        APojo bbb = mock(APojo.class);
        doReturn(bbb).when(mapper).readValue(paths.files.get(1), APojo.class);
        Predicate<APojo> predicate = mock(Predicate.class);
        doReturn(true).when(predicate).test(aaa);
        doReturn(false).when(predicate).test(bbb);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            assertEquals(aaa, sut.get(predicate));
        }
    }

    @Test
    public void shouldThrowNotExistsExceptionOnGetWithPredicate() throws IOException, NotExistsException, NotUniqueException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.json");
        APojo aaa = mock(APojo.class);
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);
        APojo bbb = mock(APojo.class);
        doReturn(bbb).when(mapper).readValue(paths.files.get(1), APojo.class);
        Predicate<APojo> predicate = mock(Predicate.class);
        doReturn(false).when(predicate).test(aaa);
        doReturn(false).when(predicate).test(bbb);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            assertThrows(NotExistsException.class, () -> sut.get(predicate));
        }
    }

    @Test
    public void shouldThrowNotUniqueExceptionOnGetWithPredicate() throws IOException, NotExistsException, NotUniqueException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.json");
        APojo aaa = mock(APojo.class);
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);
        APojo bbb = mock(APojo.class);
        doReturn(bbb).when(mapper).readValue(paths.files.get(1), APojo.class);
        Predicate<APojo> predicate = mock(Predicate.class);
        doReturn(true).when(predicate).test(aaa);
        doReturn(true).when(predicate).test(bbb);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            assertThrows(NotUniqueException.class, () -> sut.get(predicate));
        }
    }

    @Test
    public void shouldStreamAllEntities() throws IOException, NotExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.json");
        APojo aaa = mock(APojo.class);
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);
        APojo bbb = mock(APojo.class);
        doReturn(bbb).when(mapper).readValue(paths.files.get(1), APojo.class);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            List<APojo> entities = sut.stream().collect(Collectors.toList());

            // Then
            assertEquals(aaa, entities.get(0));
            assertEquals(bbb, entities.get(1));
        }
    }

    @Test
    public void shouldStreamAllEntitiesForAPredicate() throws IOException, NotExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json", "BBB.json");
        APojo aaa = mock(APojo.class);
        doReturn(aaa).when(mapper).readValue(paths.files.get(0), APojo.class);
        APojo bbb = mock(APojo.class);
        doReturn(bbb).when(mapper).readValue(paths.files.get(1), APojo.class);
        Predicate<APojo> predicate = mock(Predicate.class);
        doReturn(true).when(predicate).test(aaa);
        doReturn(false).when(predicate).test(bbb);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            // When
            List<APojo> entities = sut.stream(predicate).collect(Collectors.toList());

            // Then
            assertEquals(aaa, entities.get(0));
            assertEquals(1, entities.size());
        }
    }

    @Test
    public void shouldThrowAlreadyExistsExceptionWhenEntityAlreadyExistsOnInsert() throws IOException, NotExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            APojo entity = new APojo("AAA", "NAME");

            // Then When
            assertThrows(AlreadyExistsException.class, () -> sut.insert(entity));
        }
    }

    @Test
    public void shouldSaveNewEntityOnInsert() throws IOException, NotExistsException, AlreadyExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);
            FsDao<APojo> spy = spy(sut);
            File bbbFile = mock(File.class);
            doReturn(bbbFile).when(spy).newFile(dir, "BBB.json");

            APojo entity = new APojo("BBB", "NAME");

            // When
            spy.insert(entity);

            // Then
            verify(mapper).writeValue(bbbFile, entity);
            assertTrue(sut.exists("BBB"));
        }
    }

    @Test
    public void shouldGenerateKeyAndSaveNewEntityOnInsert() throws IOException, NotExistsException, AlreadyExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);
            doReturn("BBB").when(keyGenerator).get();
            FsDao<APojo> spy = spy(sut);
            File bbbFile = mock(File.class);
            doReturn(bbbFile).when(spy).newFile(dir, "BBB.json");

            APojo entity = new APojo(null, "NAME");

            // When
            APojo result = spy.insert(entity);

            // Then
            verify(mapper).writeValue(bbbFile, entity);
            assertTrue(sut.exists("BBB"));
            assertEquals("BBB", result.getKey());
        }
    }

    @Test
    public void shouldThrowNotExistsExceptionWhenEntityDoesNotExistsOnUpdate() throws IOException, NotExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            APojo entity = new APojo("BBB", "NAME");

            // Then When
            assertThrows(NotExistsException.class, () -> sut.update(entity));
        }
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionWhenNoPrimaryKeyOnUpdate() throws IOException, NotExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<APojo>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            APojo entity = new APojo(null, "NAME");

            // Then When
            assertThrows(DataIntegrityViolationException.class, () -> sut.update(entity));
        }
    }

    @Test
    public void shouldUpdateEntityOnUpdate() throws IOException, NotExistsException, DataIntegrityViolationException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<APojo>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);
            FsDao<APojo> spy = spy(sut);

            APojo entity = new APojo("AAA", "NAME");

            // When
            APojo result = spy.update(entity);

            // Then
            verify(mapper).writeValue(paths.files.get(0), entity);
        }
    }

    @Test
    public void shouldThrowNotExistsExceptionWhenNoEntityForKeyOnDelete() throws IOException, NotExistsException {
        // Given
        Paths paths = mockPaths(dir, "AAA.json");
        APojo entity = new APojo("AAA", "NAME");
        doReturn(entity).when(mapper).readValue(paths.files.get(0), APojo.class);
        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);

            // Then When
            assertEquals(entity, sut.delete("AAA"));
            assertFalse(sut.exists("AAA"));
        }

    }

    @Test
    public void shouldDeleteForKeyAndReturnDeletedEntity() {
        // Given
        Paths paths = mockPaths(dir, "AAA.json");

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(() -> {
                Files.list(paths.path);
            }).thenReturn(paths.entries.stream());

            sut = new FsDao<APojo>(
                    APojo.class,
                    APojo::getKey,
                    (obj, key) -> {obj.setKey(key); return obj;},
                    keyGenerator,
                    dir,
                    mapper);


            // Then When
            assertThrows(NotExistsException.class, () -> sut.delete("BBB"));
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
