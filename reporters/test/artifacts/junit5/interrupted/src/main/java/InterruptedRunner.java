import io.github.nizos.tddguard.junit5.TddGuardListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class InterruptedRunner {
    public static void main(String[] args) {
        // Discover 2 tests (sets expectedCount = 2 via testPlanExecutionStarted)
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(TwoTestFixture.class))
                .build();
        var launcher = LauncherFactory.create();
        var testPlan = launcher.discover(request);

        // Use TddGuardListener via public no-arg constructor (reads TDD_GUARD_PROJECT_ROOT from env)
        var listener = new TddGuardListener();
        listener.testPlanExecutionStarted(testPlan);

        // Record only 1 of 2 tests (simulates interrupted run)
        var testId = buildTestIdentifier(TwoTestFixture.class.getName(), "testFirst");
        listener.executionFinished(testId, TestExecutionResult.successful());

        listener.testPlanExecutionFinished(testPlan);
    }

    private static TestIdentifier buildTestIdentifier(String className, String methodName) {
        UniqueId id = UniqueId.root("test", className + "#" + methodName);
        TestDescriptor descriptor = new AbstractTestDescriptor(
                id, methodName, MethodSource.from(className, methodName)) {
            @Override
            public TestDescriptor.Type getType() {
                return TestDescriptor.Type.TEST;
            }
        };
        return TestIdentifier.from(descriptor);
    }
}
