package com.k2.plan_it_cli.dao;

import com.k2.plan_it_cli.dao.predicate.And;
import com.k2.plan_it_cli.dao.predicate.IndexGetterEquals;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IndexedDao<T> implements GenericDao<T> {
    private final GenericDao<T> dao;
    private final List<GenericIndex<T>> indexes = new ArrayList<>();
    private Map<GenericIndex<T>,Map<Object, T>> uniqueIndexes = new HashMap<>();
    private Map<GenericIndex<T>,Map<Object, List<T>>> rangeIndexes = new HashMap<>();

    public IndexedDao(Function<IndexationHandler<T>,GenericDao<T>> daoSupplier, GenericIndex<T> ... indexes) {
        Collections.addAll(this.indexes, indexes);
        this.dao = daoSupplier.apply(new ThisIndexationHandler<>(this));
    }

    private static class ThisIndexationHandler<T> implements IndexationHandler<T> {
        private final IndexedDao<T> indexedDao;

        private Map<GenericIndex<T>,Map<Object, T>> thisUniqueIndexes = new HashMap<>();
        private Map<GenericIndex<T>,Map<Object, List<T>>> thisRangeIndexes = new HashMap<>();

        private ThisIndexationHandler(IndexedDao<T> indexedDao) {
            this.indexedDao = indexedDao;
        }

        @Override
        public void start() {
            thisUniqueIndexes = new HashMap<>();
            thisRangeIndexes = new HashMap<>();
            indexedDao.indexes.forEach(index -> {
                if (index.isUnique()) {
                    thisUniqueIndexes.put(index, new HashMap<>());
                } else {
                    thisRangeIndexes.put(index, new HashMap<>());
                }
            });
        }

        @Override
        public void accept(IndexedEntityCallback<T> indexedEntityCallback) {
            addToIndexes(indexedEntityCallback.getEntity());
        }

        @Override
        public void end() {
            indexedDao.uniqueIndexes = thisUniqueIndexes;
            indexedDao.rangeIndexes = thisRangeIndexes;
        }

        private void addToIndexes(T entity) {
            indexedDao.indexes
                    .forEach(index -> {
                        Object indexKey = index.indexGetter().getGetter().get(entity);
                        if (index.isUnique()) {
                            thisUniqueIndexes.get(index).put(indexKey, entity);
                        } else {
                            thisRangeIndexes.get(index).computeIfAbsent(indexKey, k -> new ArrayList<>());
                            thisRangeIndexes.get(index).get(indexKey).add(entity);
                        }
                    });
        }
    }

    @Override
    public String ofType() {
        return dao.ofType();
    }

    @Override
    public KeyGetter<T> keyGetter() {
        return dao.keyGetter();
    }

    @Override
    public boolean exists(String key) {
        return dao.exists(key);
    }

    @Override
    public T get(String key) throws NotExistsException {
        return dao.get(key);
    }

    @Override
    public T get(Predicate<? super T> predicate) throws NotExistsException, NotUniqueException {
        try {
            GenericIndex<T> supportingIndex = indexFor(predicate);
            if (supportingIndex.isUnique()) {
                T entity = uniqueIndexes.get(supportingIndex).get(targetFrom(predicate));
                if (entity == null) {
                    throw new NotExistsException(predicate, ofType());
                }
                return entity;
            } else {
                Object target = targetFrom(predicate);
                if (rangeIndexes.get(supportingIndex).get(target) == null) {
                    throw new NotExistsException(predicate, ofType());
                }
                List<T> found = rangeIndexes.get(supportingIndex).get(target).stream()
                        .filter(predicate)
                        .toList();
                if (found.isEmpty()) {
                    throw new NotExistsException(predicate, ofType());
                }
                if (found.size() > 1) {
                    throw new NotUniqueException(predicate, ofType());
                }
                return found.get(0);
            }
        } catch(NoSuchElementException err) {
            return dao.get(predicate);
        }
    }

    @Override
    public Stream<T> stream() {
        return dao.stream();
    }

    @Override
    public Stream<T> stream(Predicate<? super T> predicate) {
        try {
            GenericIndex<T> supportingIndex = indexFor(predicate);
            if (supportingIndex.isUnique()) {
                return Stream.of(uniqueIndexes.get(supportingIndex).get(targetFrom(predicate))).filter(predicate);
            } else {
                return rangeIndexes.get(supportingIndex).get(targetFrom(predicate)).stream().filter(predicate);
            }
        } catch(NoSuchElementException err) {
            return dao.stream(predicate);
        }
    }

    @Override
    public T insert(T entity) throws AlreadyExistsException {
        T inserted = dao.insert(entity);
        addToIndexes(inserted);
        return inserted;
    }

    @Override
    public T update(T entity) throws NotExistsException, DataIntegrityViolationException {
        String key = dao.keyGetter().get(entity);
        T existing = dao.get(key);
        T updated = dao.update(entity);
        updateInIndexes(key, existing, updated);
        return updated;
    }

    @Override
    public T delete(String key) throws NotExistsException {
        T deleted = dao.delete(key);
        removeFromIndexes(key, deleted);
        return deleted;
    }

    private void removeFromIndexes(String key, T entity) {
        indexes.stream()
                .forEach(index -> {
                    Object indexKey = index.indexGetter().getGetter().get(entity);
                    if (index.isUnique()) {
                        uniqueIndexes.get(index).remove(indexKey);
                    } else {
                        Optional<T> optionalIndexEntity = rangeIndexes.get(index).get(indexKey).stream()
                                .filter(e -> key.equals(keyGetter().get(e)))
                                .findFirst();
                        optionalIndexEntity.ifPresent(e -> rangeIndexes.get(index).get(indexKey).remove(e));
                    }
                });
    }

    private void addToIndexes(T entity) {
        indexes.stream()
                .forEach(index -> {
                    Object indexKey = index.indexGetter().getGetter().get(entity);
                    if (index.isUnique()) {
                        uniqueIndexes.get(index).put(indexKey, entity);
                    } else {
                        rangeIndexes.get(index).computeIfAbsent(indexKey, k -> new ArrayList<>());
                        rangeIndexes.get(index).get(indexKey).add(entity);
                    }
                });
    }

    private void updateInIndexes(String key, T existing, T updated) {
        removeFromIndexes(key, existing);
        addToIndexes(updated);
    }

    private GenericIndex<T> indexFor(Predicate<? super T> predicate) {
        return indexes.stream()
                .filter(index -> index.supports(predicate))
                .findFirst()
                .get();
    }

    private Object targetFrom(Predicate<? super T> predicate) {
        if (predicate instanceof IndexGetterEquals<? super T> getter) {
            return getter.target();
        }
        if (predicate instanceof And<? super T> and) {
            for (Predicate<? super T> childPredicate : and.predicates()) {
                try {
                    return targetFrom(childPredicate);
                } catch(IllegalArgumentException err) {
                    continue;
                }
            }
        }
        throw new IllegalArgumentException(MessageFormat.format(
                "Unable to get target from predicate {0}", predicate));
    }
}
