package com.k2.plan_it_cli.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k2.plan_it_cli.dao.predicate.Predicates;
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
public class IndexedDaoTest {

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
    GenericDao<APojo> dao;

    KeyGetter<APojo> keyGetter = APojo::getKey;
    IndexGetter<APojo, String> nameIndexGetter = new IndexGetter<>(APojo::getName);
    GenericIndex<APojo> uniqueNameIndex = new GenericIndex<>("PKey", nameIndexGetter, true);
    FieldGetter<? super APojo, String> getFirstLetterOfNameInUpper = (entity) -> entity.getName().substring(0,1).toUpperCase();
    IndexGetter<APojo, String> upperFistLetterIndexGetter = new IndexGetter<>(getFirstLetterOfNameInUpper);
    GenericIndex<APojo> rangeNameIndex = new GenericIndex<>("UpperFirstLetter", upperFistLetterIndexGetter, false);
    APojo aPojo1 = new APojo("1", "aaa");
    APojo aPojo2 = new APojo("2", "abc");
    APojo aPojo3 = new APojo("3", "bbb");

    IndexedDao<APojo> sut;

    @BeforeEach
    public void setup() {
        //doReturn(Stream.of(aPojo1, aPojo2, aPojo3)).when(dao).stream();
        sut = new IndexedDao<>(aPojoIndexationHandler -> {
            aPojoIndexationHandler.start();
            aPojoIndexationHandler.accept(new IndexedEntityCallback<APojo>() {
                @Override
                public String getKey() {
                    return "1";
                }

                @Override
                public APojo getEntity() {
                    return aPojo1;
                }
            });
            aPojoIndexationHandler.accept(new IndexedEntityCallback<APojo>() {
                @Override
                public String getKey() {
                    return "2";
                }

                @Override
                public APojo getEntity() {
                    return aPojo2;
                }
            });
            aPojoIndexationHandler.accept(new IndexedEntityCallback<APojo>() {
                @Override
                public String getKey() {
                    return "3";
                }

                @Override
                public APojo getEntity() {
                    return aPojo3;
                }
            });
            aPojoIndexationHandler.end();
            return dao;
        }, uniqueNameIndex, rangeNameIndex);
    }

    @Test
    public void shouldLoadOnConstruct() {
        // Given
        doReturn(true).when(dao).exists("1");

        // Then
        assertTrue(sut.exists("1"));
    }

    @Test
    public void shouldReturnEntityForKey() throws NotExistsException {
        // Given
        doReturn(aPojo1).when(dao).get("1");
        // Then When
        assertEquals(aPojo1, sut.get("1"));
    }

    @Test
    public void shouldGetEntityForPredicateFromUniqueIndex() throws NotExistsException, NotUniqueException {
        // Then When
        assertEquals(aPojo3, sut.get(Predicates.equals(nameIndexGetter, "bbb")));
    }

    @Test
    public void shouldThrowNotExistsExceptionUsingUniqueIndex() throws NotExistsException, NotUniqueException {
        // Given
        doReturn("APojo").when(dao).ofType();

        // Then When
        assertThrows(NotExistsException.class, () -> {sut.get(Predicates.equals(nameIndexGetter, "ccc"));});
    }

    @Test
    public void shouldGetEntityForPredicateFromRangeIndex() throws NotExistsException, NotUniqueException {
        // Then When
        assertEquals(aPojo3, sut.get(Predicates.equals(upperFistLetterIndexGetter, "B")));
    }

    @Test
    public void shouldThrowNotExistsExceptionForPredicateFromRangeIndex() throws NotExistsException, NotUniqueException {
        // Then When
        assertThrows(NotExistsException.class, () -> sut.get(Predicates.equals(upperFistLetterIndexGetter, "C")));
    }

    @Test
    public void shouldThrowNotUniqueExceptionForPredicateFromRangeIndex() throws NotExistsException, NotUniqueException {
        // Then When
        assertThrows(NotUniqueException.class, () -> sut.get(Predicates.equals(upperFistLetterIndexGetter, "A")));
    }

    @Test
    public void shouldGetEntityFromDaoWhenNotIndexedOnGetForPredicate() throws NotExistsException, NotUniqueException {
        // Given
        Predicate<APojo> predicate = mock(Predicate.class);
        doReturn(aPojo1).when(dao).get(predicate);
        // Then When
        assertEquals(aPojo1, sut.get(predicate));
    }

    @Test
    public void shouldThrowNotExistsExceptionForAndPredicateFromRangeIndex() throws NotExistsException, NotUniqueException {
        Predicate<APojo> predicate = Predicates.equals(upperFistLetterIndexGetter, "A")
                .and(aPojo -> aPojo.getName().contains("d"));

        // Then When
        assertThrows(NotExistsException.class, () -> sut.get(predicate));
    }

    @Test
    public void shouldGetEntityForAndPredicateFromRangeIndex() throws NotExistsException, NotUniqueException {
        Predicate<APojo> predicate = Predicates.equals(upperFistLetterIndexGetter, "A")
                .and(aPojo -> aPojo.getName().contains("b"));

        // Then When
        assertEquals(aPojo2, sut.get(predicate));
    }

    @Test
    public void shouldStreamFromDao() {
        // Given
        Stream<APojo> stream = mock(Stream.class);
        doReturn(stream).when(dao).stream();

        // Then When
        assertEquals(stream, sut.stream());
    }

    @Test
    public void shouldStreamForPredicateUsingUniqueIndex() {
        // When
        List<APojo> result = sut.stream(Predicates.equals(nameIndexGetter, "bbb")).toList();

        // Then
        assertEquals(1, result.size());
        assertEquals(aPojo3, result.get(0));
    }

    @Test
    public void shouldStreamForPredicateUsingRangeIndex() {
        // When
        List<APojo> result = sut.stream(Predicates.equals(upperFistLetterIndexGetter, "A")).toList();

        // Then
        assertEquals(2, result.size());
        assertEquals(aPojo1, result.get(0));
        assertEquals(aPojo2, result.get(1));
    }

    @Test
    public void shouldStreamForPredicateWithoutUsingIndexes() {
        // Given
        Predicate<APojo> predicate = mock(Predicate.class);
        Stream<APojo> stream = mock(Stream.class);
        doReturn(stream).when(dao).stream(predicate);

        // Then When
        assertEquals(stream, sut.stream(predicate));
    }

    @Test
    public void shouldAddEntityToIndexes() throws AlreadyExistsException, NotExistsException, NotUniqueException {
        // Given
        APojo aPojo4 = new APojo("4", "ccc");
        doReturn(aPojo4).when(dao).insert(aPojo4);

        // When
        sut.insert(aPojo4);

        // Then
        assertEquals(aPojo4, sut.get(Predicates.equals(nameIndexGetter, "ccc")));
    }

    @Test
    public void shouldUpdateEntityInIndexes() throws NotExistsException, DataIntegrityViolationException, NotUniqueException {
        // Given
        APojo aPojo3Updated = new APojo("3", "ccc");
        doReturn(keyGetter).when(dao).keyGetter();
        when(dao.get("3")).thenReturn(aPojo3, aPojo3Updated);
        doReturn(aPojo3Updated).when(dao).update(aPojo3Updated);

        // When
        assertEquals(aPojo3Updated, sut.update(aPojo3Updated));

        // Then
        assertEquals(aPojo3Updated, sut.get(Predicates.equals(nameIndexGetter, "ccc")));
    }

    @Test
    public void shouldRemoveEntityFromIndexes() throws NotExistsException, DataIntegrityViolationException, NotUniqueException {
        // Given
        doReturn(aPojo3).when(dao).delete("3");
        doReturn(keyGetter).when(dao).keyGetter();

        // When
        assertEquals(aPojo3, sut.delete("3"));

        // Then
        assertThrows(NotExistsException.class, () -> sut.get(Predicates.equals(nameIndexGetter, "bbb")));
    }

    @Test
    public void shouldRegisterPostIndexCallback() {
        // Given
        Runnable runnable = mock(Runnable.class);

        // When
        sut.registerPostIndexCallback(runnable);

        // Then
        verify(dao).registerPostIndexCallback(runnable);
    }
}
