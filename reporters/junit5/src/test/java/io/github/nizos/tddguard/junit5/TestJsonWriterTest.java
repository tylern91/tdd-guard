package io.github.nizos.tddguard.junit5;

import io.github.nizos.tddguard.junit5.model.TestCase;
import io.github.nizos.tddguard.junit5.model.TestModule;
import io.github.nizos.tddguard.junit5.model.TestResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static io.github.nizos.tddguard.junit5.factories.TestData.moduleWith;
import static io.github.nizos.tddguard.junit5.factories.TestData.passedTest;
import static io.github.nizos.tddguard.junit5.factories.TestData.failedTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestJsonWriterTest {

    private final TestJsonWriter writer = new TestJsonWriter();

    @Test
    void serializesEmptyResult() {
        TestResult result = new TestResult(Collections.emptyList());

        String json = writer.serialize(result);

        assertEquals("{\n  \"testModules\": []\n}\n", json);
    }

    @Test
    void serializesPassedTest() {
        TestResult result = new TestResult(List.of(
                new TestModule("com.example.MyTest", List.of(
                        TestCase.passed("shouldWork", "com.example.MyTest::shouldWork")
                ))
        ));

        String json = writer.serialize(result);

        assertTrue(json.contains("\"moduleId\": \"com.example.MyTest\""));
        assertTrue(json.contains("\"name\": \"shouldWork\""));
        assertTrue(json.contains("\"fullName\": \"com.example.MyTest::shouldWork\""));
        assertTrue(json.contains("\"state\": \"passed\""));
    }

    @Test
    void serializesFailedTestWithError() {
        TestResult result = new TestResult(List.of(
                moduleWith("com.example.MyTest",
                        failedTest("shouldFail", "expected true but was false"))
        ));

        String json = writer.serialize(result);

        assertTrue(json.contains("\"state\": \"failed\""));
        assertTrue(json.contains("\"errors\": ["));
        assertTrue(json.contains("\"message\": \"expected true but was false\""));
    }

    @Test
    void serializesSkippedTest() {
        TestResult result = new TestResult(List.of(
                moduleWith("com.example.MyTest",
                        TestCase.skipped("shouldSkip", "com.example.MyTest::shouldSkip"))
        ));

        String json = writer.serialize(result);

        assertTrue(json.contains("\"state\": \"skipped\""));
    }

    @Test
    void omitsErrorsFieldForPassedAndSkippedTests() {
        TestResult result = new TestResult(List.of(
                moduleWith("com.example.MyTest",
                        passedTest("a"),
                        TestCase.skipped("b", "com.example.MyTest::b"))
        ));

        String json = writer.serialize(result);

        assertTrue(!json.contains("\"errors\""));
    }

    @Test
    void groupsMultipleTestsByModule() {
        TestResult result = new TestResult(List.of(
                moduleWith("com.example.FirstTest", passedTest("a"), passedTest("b")),
                moduleWith("com.example.SecondTest", passedTest("c"))
        ));

        String json = writer.serialize(result);

        assertTrue(json.contains("\"moduleId\": \"com.example.FirstTest\""));
        assertTrue(json.contains("\"moduleId\": \"com.example.SecondTest\""));
    }

    @Test
    void escapesQuotesInStrings() {
        TestResult result = new TestResult(List.of(
                moduleWith("com.example.MyTest",
                        failedTest("test", "expected \"foo\" but got \"bar\""))
        ));

        String json = writer.serialize(result);

        assertTrue(json.contains("\\\"foo\\\""));
        assertTrue(json.contains("\\\"bar\\\""));
    }

    @Test
    void escapesNewlinesAndTabs() {
        TestResult result = new TestResult(List.of(
                moduleWith("com.example.MyTest",
                        failedTest("test", "line1\nline2\ttabbed"))
        ));

        String json = writer.serialize(result);

        assertTrue(json.contains("line1\\nline2\\ttabbed"));
    }

    @Test
    void escapesBackslashes() {
        TestResult result = new TestResult(List.of(
                moduleWith("com.example.MyTest",
                        failedTest("test", "path\\to\\file"))
        ));

        String json = writer.serialize(result);

        assertTrue(json.contains("path\\\\to\\\\file"));
    }

    @Test
    void writesFileAtomically(@TempDir Path tmp) throws IOException {
        TestResult result = new TestResult(List.of(
                moduleWith("com.example.MyTest", passedTest("a"))
        ));

        writer.write(tmp, result);

        Path target = tmp.resolve("test.json");
        assertTrue(Files.exists(target));
        String content = Files.readString(target);
        assertTrue(content.contains("\"testModules\""));
    }

    @Test
    void createsParentDirectoriesWhenWriting(@TempDir Path tmp) throws IOException {
        Path nested = tmp.resolve("a").resolve("b").resolve("c");
        TestResult result = new TestResult(Collections.emptyList());

        writer.write(nested, result);

        assertTrue(Files.exists(nested.resolve("test.json")));
    }
    @Test
    void serializesReasonWhenPresent() {
        TestResult result = new TestResult(Collections.emptyList(), "passed");

        String json = writer.serialize(result);

        assertTrue(json.contains("\"reason\": \"passed\""));
    }

    @Test
    void omitsReasonWhenNull() {
        TestResult result = new TestResult(Collections.emptyList());

        String json = writer.serialize(result);

        assertTrue(!json.contains("\"reason\""));
    }
}
