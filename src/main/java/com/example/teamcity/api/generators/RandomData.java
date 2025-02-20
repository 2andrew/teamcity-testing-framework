package com.example.teamcity.api.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;

public final class RandomData {
    private static final int MAX_LENGTH = 10;

    private static final String TEST_PREFIX = "test_";
    private static final int TEST_PREFIX_LENGTH = TEST_PREFIX.length();

    private static final String UNDERSCORE_TEST_PREFIX = "_test";
    private static final List<String> EMOJIS = List.of("😊", "😂", "😍", "👍", "🙌", "🎉");

    public static String getString() {
        return TEST_PREFIX + RandomStringUtils.randomAlphabetic(MAX_LENGTH);
    }

    public static String getString(int length) {
        return TEST_PREFIX + RandomStringUtils
                .randomAlphabetic(Math.max(length - TEST_PREFIX_LENGTH, MAX_LENGTH));
    }

    public static String getAlphaNumericString() {
        return  TEST_PREFIX + RandomStringUtils.randomAlphanumeric(MAX_LENGTH);
    }

    public static String getUnderscoreString() {
        return UNDERSCORE_TEST_PREFIX + RandomStringUtils.randomAlphabetic(MAX_LENGTH);
    }

    public static String getRandomCharacter() {
        return RandomStringUtils.randomAlphabetic(1);
    }

    public static String getRandomNumber() {
        return RandomStringUtils.randomNumeric(1);
    }

    public static String getEmoji() {
        Random random = new Random();
        return EMOJIS.get(random.nextInt(EMOJIS.size()));
    }
}
