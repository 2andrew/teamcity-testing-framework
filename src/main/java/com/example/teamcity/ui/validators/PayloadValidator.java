package com.example.teamcity.ui.validators;

import lombok.AllArgsConstructor;
import org.testng.asserts.SoftAssert;

import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
public class PayloadValidator {
    protected SoftAssert softy;

    public void validateSize(int actualSize, int expectedSize) {
        softy.assertEquals(actualSize, expectedSize);
    }

    public void validateAttribute(String actualAttribute, String expectedAttribute) {
        softy.assertEquals(actualAttribute, expectedAttribute);
    }

    public <T> void assertEqualsIfPresent(Optional<T> optional,
                                          T expected,
                                          String errorMessage,
                                          Function<T, String> attrExtractor) {
        softy.assertNotNull(optional);
        optional.ifPresent(value -> {
            String actualName = attrExtractor.apply(value);
            String expectedName = attrExtractor.apply(expected);
            softy.assertEquals(actualName, expectedName, errorMessage);
        });
    }
}
