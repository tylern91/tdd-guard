# frozen_string_literal: true

require "tdd_guard_minitest/reporter"

# Directly exercise the reporter to simulate an interrupted run:
# expected_count = 2 (two methods defined), but only one result is recorded.

reporter = TddGuardMinitest::Reporter.new(StringIO.new)

# Simulate start: set expected count to 2 manually
reporter.instance_variable_set(:@expected_count, 2)

# Simulate one completed test result (the second test never ran)
passing_result = Struct.new(:name, :klass, :skipped?, :passed?, :failures, :source_location).new(
  "test_first",
  "SingleInterruptedTest",
  false,
  true,
  [],
  [__FILE__, __LINE__]
)
reporter.record(passing_result)

reporter.report
