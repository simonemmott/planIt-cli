package com.k2.plan_it_cli.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FsDao<T> implements GenericDao<T>{
    protected final Class<T> type;
    protected final KeyGetter<T> keyGetter;
    protected final KeySetter<T> keySetter;
    protected final Supplier<String> keyGenerator;
    private final File dir;
    private final ObjectMapper mapper;
    private Map<String, File> index = new HashMap<>();
    private final Runnable postIndexCallback;

    public FsDao(
            Class<T> type,
            KeyGetter<T> keyGetter,
            KeySetter<T> keySetter,
            Supplier<String> keyGenerator,
            File dir,
            ObjectMapper mapper) {
        this(type, keyGetter, keySetter, keyGenerator, dir, mapper, null);
    }

    public FsDao(
            Class<T> type,
            KeyGetter<T> keyGetter,
            KeySetter<T> keySetter,
            Supplier<String> keyGenerator,
            File dir,
            ObjectMapper mapper,
            Runnable postIndexCallback) {
        this.type = type;
        this.keyGetter = keyGetter;
        this.keySetter = keySetter;
        this.keyGenerator = keyGenerator;
        this.dir = dir;
        this.mapper = mapper;
        this.postIndexCallback = postIndexCallback;
        index();
    }

    @Override
    public boolean exists(String key) {
        return index.containsKey(key);
    }

    @Override
    public T get(String key) throws NotExistsException {
        File file = index.get(key);
        if (file == null) {
            throw new NotExistsException(key, type.getName());
        }
        try {
            return mapper.readValue(file, type);
        } catch (IOException e) {
            throw new FsDaoFileReadError(file, type, e);
        }
    }

    @Override
    public T get(Predicate<? super T> predicate) throws NotExistsException, NotUniqueException {
        List<T> list = stream(predicate).toList();
        if (list == null || list.isEmpty()) {
            throw new NotExistsException(predicate, type.getSimpleName());
        }
        if (list.size() > 1) {
            throw new NotUniqueException(predicate, type.getSimpleName());
        }
        return list.get(0);
    }

    @Override
    public Stream<T> stream() {
        return index.values().stream()
                .map(file -> {
                    try {
                        return mapper.readValue(file, type);
                    } catch (IOException e) {
                        throw new FsDaoFileReadError(file, type, e);
                    }
                });
    }

    @Override
    public Stream<T> stream(Predicate<? super T> prediacte) {
        return index.values().stream()
                .map(file -> {
                    try {
                        return mapper.readValue(file, type);
                    } catch (IOException e) {
                        throw new FsDaoFileReadError(file, type, e);
                    }
                })
                .filter(prediacte);
    }

    @Override
    public T insert(T entity) throws AlreadyExistsException {
        String key = keyGetter.get(entity);
         if (key != null) {
            if (exists(key)) {
                throw new AlreadyExistsException(key, type.getSimpleName());
            }
        } else {
            key = keyGenerator.get();
            keySetter.set(entity, key);
        }
        File file = newFile(dir, key+".json");
        try {
            mapper.writeValue(file, entity);
            index.put(key, file);
        } catch (Throwable e) {
            throw new FsDaoFileWriteError(file, type, key, e);
        }
        return entity;
    }

    @Override
    public T update(T entity) throws NotExistsException, DataIntegrityViolationException {
        String key = keyGetter.get(entity);
        if (key != null) {
            if (!exists(key)) {
                throw new NotExistsException(key, type.getSimpleName());
            }
        } else {
            throw new DataIntegrityViolationException(key, type, "No primary key defined during update");
        }
        File file = index.get(key);
        try {
            mapper.writeValue(file, entity);
        } catch (Throwable e) {
            throw new FsDaoFileWriteError(file, type, key, e);
        }
        return entity;
    }

    @Override
    public T delete(String key) throws NotExistsException {
        T deleted = get(key);
        File file = index.get(key);
        try {
            Files.delete(file.toPath());
            index.remove(key);
        } catch (IOException e) {
            throw new FsDaoFileWriteError(file, type, key, e);
        }
        return deleted;
    }

    File newFile(File dir, String name) {
        return new File(dir, name);
    }

    protected void index() {
        Map<String, File> index = new HashMap<>();
        try (Stream<Path> entries = Files.list(dir.toPath())) {
            entries.forEach(path -> {
                File file = path.toFile();
                if (path.endsWith(".json")) {
                    String key = file.getName().replaceFirst("[.][^.]+$", "");
                    index.put(key, file);
                }
            });
            this.index = index;
        } catch (IOException err) {
            throw new GenericDaoError(MessageFormat.format("Unable to load {0}s from {1}", type, dir), err);
        }
        if (postIndexCallback != null) {
            postIndexCallback.run();
        }
    }
}
