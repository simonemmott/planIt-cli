package com.k2.plan_it_cli.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CachingDirectoryDao<T> extends DirectoryDao<T> {
    private Map<String, T> cache;

    public CachingDirectoryDao(
            Class<T> type,
            KeyGetter<T> keyGetter,
            KeySetter<T> keySetter,
            Supplier<String> keyGenerator,
            File dir,
            ObjectMapper mapper) {
        super(type, keyGetter, keySetter, keyGenerator, dir, mapper);
    }

    @Override
    public T get(String key) throws NotExistsException {
        T entity = cache.get(key);
        if (entity == null) {
            throw new NotExistsException(key, type.getSimpleName());
        }
        return entity;
    }

    @Override
    public Stream<T> stream() {
        return cache.values().stream();
    }

    @Override
    public Stream<T> stream(Predicate<? super T> predicate) {
        return cache.values().stream()
                .filter(predicate);
    }

    @Override
    public T insert(T entity) throws AlreadyExistsException {
        T inserted = super.insert(entity);
        cache.put(keyGetter.get(inserted), inserted);
        return inserted;
    }

    @Override
    public T update(T entity) throws NotExistsException, DataIntegrityViolationException {
        T updated = super.update(entity);
        cache.put(keyGetter.get(updated), updated);
        return updated;
    }

    @Override
    public T delete(String key) throws NotExistsException {
        T deleted = super.delete(key);
        cache.remove(key);
        return deleted;
    }

    @Override
    protected void index() {
        Map<String, T> newCache = new HashMap<>();
        super.index();
        super.stream()
                .forEach(entity -> newCache.put(keyGetter.get(entity), entity));
        this.cache = newCache;
    }
}
