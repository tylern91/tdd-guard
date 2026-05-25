# frozen_string_literal: true

RSpec.configure do |config|
  config.after(:each) do
    RSpec.world.wants_to_quit = true
  end
end

RSpec.describe "SingleInterrupted" do
  it "passes" do
    expect(1).to eq(1)
  end

  it "never runs" do
    expect(2).to eq(2)
  end
end
