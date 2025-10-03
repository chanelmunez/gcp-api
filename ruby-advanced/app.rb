require "functions_framework"
require "json"
require_relative "lib/services/user_service"
require_relative "lib/utils/logger"
require_relative "config/app_config"

user_service = Services::UserService.new
logger = Utils::Logger.new

FunctionsFramework.http "advanced_user_api" do |request|
  logger.info("Request received",
    method: request.request_method,
    path: request.path,
    service: Config::APP_CONFIG[:service_name]
  )

  # Set CORS headers
  headers = {}
  if Config::APP_CONFIG[:cors][:enabled]
    headers = {
      "Access-Control-Allow-Origin" => "*",
      "Access-Control-Allow-Methods" => "GET, POST, OPTIONS",
      "Access-Control-Allow-Headers" => "Content-Type",
      "Content-Type" => "application/json"
    }
  end

  if request.request_method == "OPTIONS"
    [204, headers, [""]]
  else
    begin
      path = request.path
      method = request.request_method

      response = if method == "POST" && path == "/users"
        body = JSON.parse(request.body.read)
        user = user_service.create_user(body["name"], body["email"])

        [201, headers, [JSON.generate({
          success: true,
          data: user.to_h
        })]]

      elsif method == "GET" && path == "/users"
        users = user_service.get_all_users.map(&:to_h)

        [200, headers, [JSON.generate({
          success: true,
          data: users,
          count: users.length
        })]]

      elsif method == "GET" && path.start_with?("/users/")
        id = path.split("/").last.to_i
        user = user_service.get_user(id)

        if user
          [200, headers, [JSON.generate({
            success: true,
            data: user.to_h
          })]]
        else
          [404, headers, [JSON.generate({
            success: false,
            error: "User not found"
          })]]
        end

      else
        [200, headers, [JSON.generate({
          success: true,
          message: "Advanced Ruby Cloud Function with complex folder structure",
          version: Config::APP_CONFIG[:version],
          endpoints: {
            "POST /users" => "Create a new user",
            "GET /users" => "Get all users",
            "GET /users/:id" => "Get user by ID"
          }
        })]]
      end

      response

    rescue => e
      logger.error("Error processing request", error: e.message)
      [400, headers, [JSON.generate({
        success: false,
        error: e.message
      })]]
    end
  end
end
