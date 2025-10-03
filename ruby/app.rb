require "functions_framework"

FunctionsFramework.http "hello_world" do |request|
  "Hello World from Ruby Cloud Run Function!"
end
