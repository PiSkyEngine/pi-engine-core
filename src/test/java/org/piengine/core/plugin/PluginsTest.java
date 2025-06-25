/*
 * MIT License
 * 
 * Copyright (c) 2025 Sly Technologies Inc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.piengine.core.plugin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.piengine.util.Version;

/**
 * JUnit test cases for the {@link Plugins} class, validating Maven-style plugin
 * identifier pattern matching.
 * <p>
 * These tests cover the {@link Plugins#filterHasPluginId(String...)},
 * {@link Plugins#filterPluginId(String...)}, and {@link Plugins#filter(Function, String...)}
 * methods, ensuring they correctly handle valid and invalid patterns, wildcards
 * ({@code ?} and {@code *}), and edge cases. Tests use mock implementations of
 * {@link HasPluginId} and {@link PluginId} to verify filtering behavior.
 * </p>
 *
 * @author Sly Technologies Inc.
 * @version 2.0
 * @since 1.0
 */
class PluginsTest {

    private List<PluginId> testPluginIds;
    private List<HasPluginId> testPlugins;

    /**
     * Mock implementation of {@link HasPluginId} for testing.
     */
    private static class MockPlugin implements HasPluginId {
        private final PluginId pluginId;

        MockPlugin(String id) {
            this.pluginId = PluginId.parse(id);
        }

        @Override
        public PluginId pluginId() {
            return pluginId;
        }
    }

    /**
     * Mock implementation of {@link Version} for testing.
     */
    private static class MockVersion extends Version {
        public MockVersion(String version) {
            super(version);
        }
    }

    /**
     * Sets up test data before each test.
     */
    @BeforeEach
    void setUp() {
        testPluginIds = Arrays.asList(
            PluginId.parse("com.example:my-plugin:1.0.0"),
            PluginId.parse("com.example:core:1.0.1"),
            PluginId.parse("org.acme:utils:2.0.0"),
            PluginId.parse("com.ex2:plugin:1.5.0")
        );

        testPlugins = Arrays.asList(
            new MockPlugin("com.example:my-plugin:1.0.0"),
            new MockPlugin("com.example:core:1.0.1"),
            new MockPlugin("org.acme:utils:2.0.0"),
            new MockPlugin("com.ex2:plugin:1.5.0")
        );
    }

    /**
     * Tests valid patterns that should be accepted by {@link Plugins#filterHasPluginId}.
     */
    @ParameterizedTest
    @CsvSource(value = {
        "com.example:*",
        "com.example:my-plugin:1.0.*",
        "org.*:utils",
        "com.ex?:plugin:1.?.0",
        "*:my-plugin",
        "*:*",
        "com.example:core:1.0.1"
    })
    void testValidPatterns(String pattern) {
        assertDoesNotThrow(() -> Plugins.filterHasPluginId(pattern),
            "Pattern should be valid: " + pattern);
    }

    /**
     * Tests invalid patterns that should throw {@link InvalidPluginPatternException}.
     */
    @ParameterizedTest
    @CsvSource(value = {
        ":my-plugin",           // Missing group
        "com..example:*",       // Invalid group (double dot)
        "com.example:",         // Missing artifact
        "com.example:plugin:",  // Empty version
        "com.example:1plugin",  // Invalid artifact (starts with number)
        "com.example:*:1..0"    // Invalid version (double dot)
    })
    void testInvalidPatterns(String pattern) {
        assertThrows(Plugins.InvalidPluginPatternException.class,
            () -> Plugins.filterHasPluginId(pattern),
            "Pattern should be invalid: " + pattern);
    }

    /**
     * Tests null inputs for patterns and array.
     */
    @Test
    void testNullInputs() {
        assertThrows(NullPointerException.class,
            () -> Plugins.filterHasPluginId((String[]) null),
            "Null patterns array should throw NullPointerException");

        assertThrows(NullPointerException.class,
            () -> Plugins.filterHasPluginId("com.example:*", null),
            "Null pattern in array should throw NullPointerException");

        Predicate<HasPluginId> filter = Plugins.filterHasPluginId("com.example:*");
        assertThrows(NullPointerException.class,
            () -> filter.test(null),
            "Null HasPluginId should throw NullPointerException");
    }

    /**
     * Tests empty patterns array behavior.
     */
    @Test
    void testEmptyPatterns() {
        Predicate<HasPluginId> filter = Plugins.filterHasPluginId();
        assertTrue(filter.test(testPlugins.get(0)),
            "Empty patterns should return true for any HasPluginId");

        Predicate<PluginId> idFilter = Plugins.filterPluginId();
        assertTrue(idFilter.test(testPluginIds.get(0)),
            "Empty patterns should return true for any PluginId");
    }

    /**
     * Tests filtering with various patterns and expected matches.
     */
    @ParameterizedTest
    @MethodSource(value = "provideFilterTestCases")
    void testFilterPatterns(String pattern, int expectedMatchCount, String[] expectedIds) {
        Predicate<HasPluginId> filter = Plugins.filterHasPluginId(pattern);
        List<HasPluginId> matched = testPlugins.stream()
                                              .filter(filter)
                                              .toList();

        assertEquals(expectedMatchCount, matched.size(),
            "Unexpected number of matches for pattern: " + pattern);

        List<String> matchedIds = matched.stream()
                                        .map(p -> p.pluginId().toString())
                                        .toList();
        assertTrue(matchedIds.containsAll(Arrays.asList(expectedIds)),
            "Matched IDs should include: " + Arrays.toString(expectedIds));
    }

    /**
     * Provides test cases for filtering patterns.
     */
    static Stream<Arguments> provideFilterTestCases() {
        return Stream.of(
            Arguments.of("com.example:*", 2,
                new String[]{"com.example:my-plugin:1.0.0", "com.example:core:1.0.1"}),
            Arguments.of("*:my-plugin", 1,
                new String[]{"com.example:my-plugin:1.0.0"}),
            Arguments.of("com.ex?:*", 1,
                new String[]{"com.ex2:plugin:1.5.0"}),
            Arguments.of("*:*:1.0.*", 2,
                new String[]{"com.example:my-plugin:1.0.0", "com.example:core:1.0.1"}),
            Arguments.of("org.acme:utils:2.0.0", 1,
                new String[]{"org.acme:utils:2.0.0"}),
            Arguments.of("*:no-match", 0,
                new String[]{})
        );
    }

    /**
     * Tests multiple patterns in a single filter.
     */
    @Test
    void testMultiplePatterns() {
        Predicate<HasPluginId> filter = Plugins.filterHasPluginId(
            "com.example:*",
            "org.acme:utils"
        );
        List<HasPluginId> matched = testPlugins.stream()
                                              .filter(filter)
                                              .toList();

        assertEquals(3, matched.size(),
            "Should match 3 plugins for patterns: com.example:*, org.acme:utils");

        List<String> matchedIds = matched.stream()
                                        .map(p -> p.pluginId().toString())
                                        .toList();
        assertTrue(matchedIds.containsAll(Arrays.asList(
            "com.example:my-plugin:1.0.0",
            "com.example:core:1.0.1",
            "org.acme:utils:2.0.0"
        )), "Matched IDs should include expected plugins");
    }

    /**
     * Tests the generic filter method with a custom wrapper.
     */
    @Test
    void testGenericFilter() {
        record PluginWrapper(PluginId id) implements HasPluginId {
            @Override
            public PluginId pluginId() {
                return id;
            }
        }

        List<PluginWrapper> wrappers = testPluginIds.stream()
                                                   .map(PluginWrapper::new)
                                                   .toList();

        Predicate<PluginWrapper> filter = Plugins.filter(PluginWrapper::pluginId, "com.example:*");
        List<PluginWrapper> matched = wrappers.stream()
                                             .filter(filter)
                                             .toList();

        assertEquals(2, matched.size(),
            "Should match 2 wrappers for pattern: com.example:*");

        List<String> matchedIds = matched.stream()
                                        .map(w -> w.pluginId().toString())
                                        .toList();
        assertTrue(matchedIds.containsAll(Arrays.asList(
            "com.example:my-plugin:1.0.0",
            "com.example:core:1.0.1"
        )), "Matched IDs should include expected plugins");
    }

    @Test
    void testRegexify() {
        Predicate<PluginId> filter = Plugins.filterPluginId("com.ex?:pl*");
        List<PluginId> matched = testPluginIds.stream()
                                             .filter(filter)
                                             .toList();

        assertEquals(1, matched.size(),
            "Should match 1 plugin for pattern: com.ex?:pl*");

        assertEquals("com.ex2:plugin:1.5.0", matched.get(0).toString(),
            "Matched ID should be com.ex2:plugin:1.5.0");
    }
}