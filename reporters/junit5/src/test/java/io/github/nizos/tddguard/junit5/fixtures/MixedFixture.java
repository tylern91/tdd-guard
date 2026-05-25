package io.github.nizos.tddguard.junit5.fixtures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Fixture executed by {@code LauncherIntegrationTest} via the JUnit Platform Launcher API.
 * Excluded from the regular test suite via {@code tasks.test.exclude} in {@code build.gradle.kts}.
 */
public class MixedFixture {
    @Test
    void passes() {
    }

    @Test
    void fails() {
        assertEquals(1, 2);
    }
}
