package io.github.nizos.tddguard.junit5.factories;

import io.github.nizos.tddguard.junit5.model.TestCase;
import io.github.nizos.tddguard.junit5.model.TestError;
import io.github.nizos.tddguard.junit5.model.TestModule;
import io.github.nizos.tddguard.junit5.model.TestResult;

import java.util.Arrays;
import java.util.List;

public final class TestData {
    private TestData() {}

    public static TestCase passedTest(String name) {
        return TestCase.passed(name, "test::" + name);
    }

    public static TestCase failedTest(String name, String message) {
        return TestCase.failed(name, "test::" + name, List.of(new TestError(message)));
    }

    public static TestModule moduleWith(String moduleId, TestCase... tests) {
        return new TestModule(moduleId, Arrays.asList(tests));
    }

    public static TestResult resultsWith(String reason, TestModule... modules) {
        return new TestResult(Arrays.asList(modules), reason);
    }
}
