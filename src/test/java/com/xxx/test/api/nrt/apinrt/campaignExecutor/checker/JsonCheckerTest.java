package com.xxx.test.api.nrt.apinrt.campaignExecutor.checker;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonCheckerTest {

    @ParameterizedTest
    @MethodSource("provideJsonsToCompareCases")
    void testMatches(String expectedJsonText, String actualJsonText, boolean expectedResult) {

        JsonChecker jsonChecker = new JsonChecker();
        boolean foundResult = jsonChecker.matches(expectedJsonText, actualJsonText);
        assertThat(foundResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideJsonsToCompareCases() {
        Stream<Arguments> result = Stream.concat(
                simpleObjectCases(),
                nestedObjectCases());
        result = Stream.concat(result, simpleArrayCases());
        return result;
    }

    static Stream<Arguments> simpleObjectCases() {
         return Stream.of(
                 Arguments.of("""
                        {"a":[]}
                        """, """
                        {"a":["X"]}
                        """, false),
                Arguments.of("""
                        {}
                        """, """
                        {}
                        """, true),
                 Arguments.of("""
                        {}
                        """, """
                        {"a":null}
                        """, true),
                 Arguments.of("""
                        {"a":null}
                        """, """
                        {}
                        """, true),
                 Arguments.of("""
                        {"a":1}
                        """, """
                        {"a":1}
                        """, true),
                 Arguments.of("""
                        {"a":1, "b":null}
                        """, """
                        {"a":1}
                        """, true),
                 Arguments.of("""
                        {"a":1}
                        """, """
                        {"a":1, "b":null}
                        """, true),
                Arguments.of("""
                        {"a":1, "b":2}
                        """, """
                        {"a":1, "b":2}
                        """, true),
                Arguments.of("""
                        {"a":1, "b":2}
                        """, """
                        {"b":2, "a":1}
                        """, true),

                 Arguments.of("""
                        {"a":1}
                        """, """
                        {}
                        """, false),
                 Arguments.of("""
                        {"a":1}
                        """, """
                        {"a":2}
                        """, false),
                 Arguments.of("""
                        {"a":1}
                        """, """
                        {"b":1}
                        """, false),
                 Arguments.of("""
                        {}
                        """, """
                        {"a":1}
                        """, false),
                Arguments.of("""
                        {"a":1}
                        """, """
                        {"a":null}
                        """, false),
                Arguments.of("""
                        {"a":null}
                        """, """
                        {"a":1}
                        """, false),
                Arguments.of("""
                        {"a":1, "b":2}
                        """, """
                        {"a":1, "b":null}
                        """, false),
                Arguments.of("""
                        {"a":1, "b":null}
                        """, """
                        {"a":1, "b":2}
                        """, false),
                Arguments.of("""
                        {"a":1, "b":2}
                        """, """
                        {"a":1}
                        """, false),
                Arguments.of("""
                        {"a":1}
                        """, """
                        {"a":1, "b":2}
                        """, false));
    }

    static Stream<Arguments> nestedObjectCases() {
         return Stream.of(
                Arguments.of("""
                        {"a":{}}
                        """, """
                       {"a":{}}
                       """, true),
                Arguments.of("""
                        {"b":{"a":1}}
                        """, """
                       {"b":{"a":1}}
                       """, true),
                Arguments.of("""
                       {"b":{"a":1, "b":2}}
                       """, """
                       {"b":{"a":1, "b":2}}
                       """, true),
                Arguments.of("""
                       {"b":{"a":1}}
                       """, """
                       {"b":{"a":null}}
                       """, false),
                Arguments.of("""
                       {"b":{"a":1, "b":2}}
                       """, """
                       {"b":{"a":1}}
                       """, false),
                Arguments.of("""
                       {"b":{"a":1}}
                       """, """
                       {"b":{"a":1, "b":2}}
                       """, false),
                Arguments.of("""
                       {"b":{"a":1, "b":2}}
                       """, """
                       {"b":{"a":1, "b":3}}
                       """, false));
    }

    static Stream<Arguments> simpleArrayCases() {
        return Stream.of(
                Arguments.of("""
                        {"a":[]}
                        """, """
                        {"a":[]}
                       """, true),
                Arguments.of("""
                        {"a":["X"]}
                        """, """
                        {"a":["X"]}
                        """, true),
                Arguments.of("""
                        {"a":["X", "Y"]}
                        """, """
                        {"a":["X", "Y"]}
                        """, true),
                Arguments.of("""
                        {"a":["X", "Y"]}
                        """, """
                        {"a":["Y", "X"]}
                        """, true),
                Arguments.of("""
                        {"a":["X", "X"]}
                        """, """
                        {"a":["X", "X"]}
                        """, true),
                Arguments.of("""
                        {"a":["Y", "X", "X"]}
                        """, """
                        {"a":["X", "Y", "X"]}
                        """, true),
                Arguments.of("""
                        {"a":["X", "X", "Y"]}
                        """, """
                        {"a":["X", "Y", "X"]}
                        """, true),
                Arguments.of("""
                        {"a":["Y", "X", "X"]}
                        """, """
                        {"a":["X", "X", "Y"]}
                        """, true),
                Arguments.of("""
                        {"a":["X", {}]}
                        """, """
                        {"a":["X", {}]}
                        """, true),
                Arguments.of("""
                        {"a":["X", {}]}
                        """, """
                        {"a":[{}, "X"]}
                        """, true),

                Arguments.of("""
                        {"a":["X"]}
                        """, """
                        {"b":["X"]}
                        """, false),
                Arguments.of("""
                        {"a":["X"]}
                        """, """
                        {"a":["Y"]}
                        """, false),
                Arguments.of("""
                        {"a":[]}
                        """, """
                        {"a":["X"]}
                        """, false),
                Arguments.of("""
                        {"a":["X"]}
                        """, """
                        {"a":[]}
                        """, false),
                Arguments.of("""
                        {"a":["X"]}
                        """, """
                        {"a":["X", "Y"]}
                        """, false),
                Arguments.of("""
                        {"a":["X", "Y"]}
                        """, """
                        {"a":["X"]}
                        """, false),
                Arguments.of("""
                        {"a":["X", "Y"]}
                        """, """
                        {"a":["X", "Z"]}
                        """, false),
                Arguments.of("""
                        {"a":["X", "X", "Y"]}
                        """, """
                        {"a":["X", "Y", "Y"]}
                        """, false),
                Arguments.of("""
                        {"a":["X", "X"]}
                        """, """
                        {"a":["X"]}
                        """, false),
                Arguments.of("""
                        {"a":["X"]}
                        """, """
                        {"a":["X", "X"]}
                        """, false),
                Arguments.of("""
                        {"a":["X", {}]}
                        """, """
                        {"a":["Y", {}]}
                        """, false),
                Arguments.of("""
                        {"a":["X", {}]}
                        """, """
                        {"a":["X", {"a":1}]}
                        """, false),
                Arguments.of("""
                        {"a":[{}, "X"]}
                        """, """
                        {"a":[{"a":1}, "X"]}
                        """, false),
                Arguments.of("""
                        {"a":[[], "X"]}
                        """, """
                        {"a":[{"a":1}, "X"]}
                        """, false),
                Arguments.of("""
                        {"a":[ "X", []]}
                        """, """
                        {"a":["X",{"a":1}]}
                        """, false),
                Arguments.of("""
                        {"a":[[], "X"]}
                        """, """
                        {"a":[null, "X"]}
                        """, false),
                Arguments.of("""
                        {"a":[ "X", []]}
                        """, """
                        {"a":["X",null]}
                        """, false)
        );
    }
}
