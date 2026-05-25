package io.github.nizos.tddguard.junit5;

import io.github.nizos.tddguard.junit5.fixtures.FailingFixture;
import io.github.nizos.tddguard.junit5.fixtures.MixedFixture;
import io.github.nizos.tddguard.junit5.fixtures.PassingFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class LauncherIntegrationTest {

    private static final String DATA_PATH = ".claude/tdd-guard/data/test.json";

    @Test
    void runsPassedTestsEndToEnd(@TempDir Path tmp) throws Exception {
        TddGuardListener listener = createListener(tmp);

        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(PassingFixture.class))
                .build();
        var launcher = LauncherFactory.create();
        launcher.execute(request, listener);

        Path testJson = tmp.resolve(DATA_PATH);
        assertTrue(Files.exists(testJson));
        String content = Files.readString(testJson);
        assertTrue(content.contains("\"reason\": \"passed\""));
        assertTrue(content.contains("\"state\": \"passed\""));
    }

    @Test
    void runsFailedTestsEndToEnd(@TempDir Path tmp) throws Exception {
        TddGuardListener listener = createListener(tmp);

        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(FailingFixture.class))
                .build();
        var launcher = LauncherFactory.create();
        launcher.execute(request, listener);

        Path testJson = tmp.resolve(DATA_PATH);
        assertTrue(Files.exists(testJson));
        String content = Files.readString(testJson);
        assertTrue(content.contains("\"reason\": \"failed\""));
        assertTrue(content.contains("\"state\": \"failed\""));
        assertTrue(content.contains("expected: <1> but was: <2>"));
    }

    @Test
    void emitsFailedReasonWhenSomeTestsFailAndSomePass(@TempDir Path tmp) throws Exception {
        TddGuardListener listener = createListener(tmp);

        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(MixedFixture.class))
                .build();
        var launcher = LauncherFactory.create();
        launcher.execute(request, listener);

        Path testJson = tmp.resolve(DATA_PATH);
        assertTrue(Files.exists(testJson));
        String content = Files.readString(testJson);
        assertTrue(content.contains("\"reason\": \"failed\""));
    }

    @Test
    void emitsCorrectModuleIdFromClassName(@TempDir Path tmp) throws Exception {
        TddGuardListener listener = createListener(tmp);

        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(PassingFixture.class))
                .build();
        var launcher = LauncherFactory.create();
        launcher.execute(request, listener);

        Path testJson = tmp.resolve(DATA_PATH);
        assertTrue(Files.exists(testJson));
        String content = Files.readString(testJson);
        String expectedModuleId = PassingFixture.class.getName();
        assertTrue(content.contains("\"moduleId\": \"" + expectedModuleId + "\""));
    }

    private static TddGuardListener createListener(Path projectRoot) {
        ProjectRootResolver resolver = new ProjectRootResolver(
                name -> projectRoot.toString(),
                () -> projectRoot);
        return new TddGuardListener(resolver, new TestJsonWriter(), name -> projectRoot.toString());
    }
}
