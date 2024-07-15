package com.k2.plan_it_cli.dao.keys;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RandomKeyGeneratorTest {

    @Test
    public void shouldProvideRandomKeyOfGivenLength() {
        // Given
        KeyGenerator keyGenerator = new RandomKeyGenerator();

        // Then When
        assertNotNull(keyGenerator.nextKey(String.class));
    }
}
