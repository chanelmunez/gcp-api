require "functions_framework"
require "json"

FunctionsFramework.http "routing_demo" do |request|
  path = request.path
  method = request.request_method

  # Collect all parameters
  params = {
    path_params: extract_path_params(path),
    query_params: request.params,
    body_params: get_body_params(request),
    headers: request.env.select { |k, _| k.start_with?("HTTP_") },
    method: method,
    full_url: request.url,
    path: path
  }

  # Route handling
  response = if path == "/" || path.empty?
    handle_home(params)
  elsif path.start_with?("/users/") && method == "GET"
    handle_get_user(params)
  elsif path == "/users" && method == "GET"
    handle_list_users(params)
  elsif path == "/users" && method == "POST"
    handle_create_user(params)
  elsif path.start_with?("/products/") && path.include?("/reviews")
    handle_product_review(params)
  elsif path.start_with?("/search")
    handle_search(params)
  elsif path.start_with?("/api/v1/")
    handle_api_v1(params)
  elsif path.start_with?("/api/v2/")
    handle_api_v2(params)
  else
    handle_404(params)
  end

  response
end

def extract_path_params(path)
  parts = path.split("/").reject(&:empty?)
  params = {}

  parts.each_with_index do |part, index|
    if part =~ /^\d+$/
      params["segment_#{index}"] = part.to_i
    else
      params["segment_#{index}"] = part
    end
  end

  params
end

def get_body_params(request)
  return {} unless request.media_type == "application/json"

  begin
    request.body.rewind
    body = request.body.read
    body.empty? ? {} : JSON.parse(body)
  rescue JSON::ParserError
    {}
  end
end

def handle_home(params)
  [200, { "Content-Type" => "application/json" }, [JSON.generate({
    message: "Welcome to Ruby Routing Demo",
    all_parameters: params,
    available_routes: {
      "GET /" => "This home page",
      "GET /users" => "List all users (supports ?limit=N&offset=N)",
      "GET /users/:id" => "Get user by ID",
      "POST /users" => "Create user (send JSON body)",
      "GET /products/:id/reviews/:reviewId" => "Get product review",
      "GET /search" => "Search (supports ?q=query&category=cat&sort=asc)",
      "GET /api/v1/*" => "API version 1 endpoints",
      "GET /api/v2/*" => "API version 2 endpoints"
    }
  })]]
end

def handle_get_user(params)
  user_id = params[:path_params]["segment_1"]

  [200, { "Content-Type" => "application/json" }, [JSON.generate({
    route: "GET /users/:id",
    message: "Getting user with ID: #{user_id}",
    all_parameters: params,
    extracted_data: {
      user_id: user_id,
      query_filters: params[:query_params]
    }
  })]]
end

def handle_list_users(params)
  limit = params[:query_params]["limit"]&.to_i || 10
  offset = params[:query_params]["offset"]&.to_i || 0

  [200, { "Content-Type" => "application/json" }, [JSON.generate({
    route: "GET /users",
    message: "Listing users",
    all_parameters: params,
    pagination: {
      limit: limit,
      offset: offset
    }
  })]]
end

def handle_create_user(params)
  [201, { "Content-Type" => "application/json" }, [JSON.generate({
    route: "POST /users",
    message: "Creating new user",
    all_parameters: params,
    received_body: params[:body_params]
  })]]
end

def handle_product_review(params)
  path_parts = params[:path].split("/").reject(&:empty?)
  product_id = path_parts[1]
  review_id = path_parts[3]

  [200, { "Content-Type" => "application/json" }, [JSON.generate({
    route: "GET /products/:id/reviews/:reviewId",
    message: "Getting review #{review_id} for product #{product_id}",
    all_parameters: params,
    extracted_data: {
      product_id: product_id,
      review_id: review_id
    }
  })]]
end

def handle_search(params)
  [200, { "Content-Type" => "application/json" }, [JSON.generate({
    route: "GET /search",
    message: "Performing search",
    all_parameters: params,
    search_params: {
      query: params[:query_params]["q"] || "",
      category: params[:query_params]["category"] || "all",
      sort: params[:query_params]["sort"] || "relevance",
      page: params[:query_params]["page"] || 1
    }
  })]]
end

def handle_api_v1(params)
  [200, { "Content-Type" => "application/json" }, [JSON.generate({
    route: "GET /api/v1/*",
    message: "API Version 1 endpoint",
    all_parameters: params,
    api_version: "v1"
  })]]
end

def handle_api_v2(params)
  [200, { "Content-Type" => "application/json" }, [JSON.generate({
    route: "GET /api/v2/*",
    message: "API Version 2 endpoint",
    all_parameters: params,
    api_version: "v2"
  })]]
end

def handle_404(params)
  [404, { "Content-Type" => "application/json" }, [JSON.generate({
    error: "Route not found",
    message: "No handler for #{params[:method]} #{params[:path]}",
    all_parameters: params
  })]]
end
