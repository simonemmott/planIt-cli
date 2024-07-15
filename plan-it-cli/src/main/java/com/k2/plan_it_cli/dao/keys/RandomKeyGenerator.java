package com.k2.plan_it_cli.dao.keys;


import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomKeyGenerator implements KeyGenerator {
    private final int keyLength;
    private final Random random = new Random();

    public RandomKeyGenerator() {
        this(10);
    }
    public RandomKeyGenerator(int keyLength) {
        this.keyLength = keyLength;
    }

    @Override
    public String nextKey(Class<?> type) {
        return generate();
    }

    private String generate() {
        // numeral '0'
        int leftLimit = 48;
        // letter 'z'
        int rightLimit = 122;
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(keyLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
