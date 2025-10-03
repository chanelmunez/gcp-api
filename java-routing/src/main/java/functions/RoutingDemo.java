package functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RoutingDemo implements HttpFunction {
    private static final Gson gson = new Gson();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        String method = request.getMethod();

        // Collect all parameters
        Map<String, Object> params = new HashMap<>();
        params.put("path_params", extractPathParams(path));
        params.put("query_params", request.getQueryParameters());
        params.put("body_params", getBodyParams(request));
        params.put("headers", getHeaders(request));
        params.put("method", method);
        params.put("full_url", request.getUri());
        params.put("path", path);

        response.appendHeader("Content-Type", "application/json");
        BufferedWriter writer = response.getWriter();

        // Route handling
        if (path.equals("/") || path.isEmpty()) {
            handleHome(writer, params);
        } else if (path.startsWith("/users/") && method.equals("GET")) {
            handleGetUser(writer, params);
        } else if (path.equals("/users") && method.equals("GET")) {
            handleListUsers(writer, params);
        } else if (path.equals("/users") && method.equals("POST")) {
            handleCreateUser(writer, params, response);
        } else if (path.startsWith("/products/") && path.contains("/reviews")) {
            handleProductReview(writer, params);
        } else if (path.startsWith("/search")) {
            handleSearch(writer, params);
        } else if (path.startsWith("/api/v1/")) {
            handleApiV1(writer, params);
        } else if (path.startsWith("/api/v2/")) {
            handleApiV2(writer, params);
        } else {
            handle404(writer, params, response);
        }
    }

    private Map<String, Object> extractPathParams(String path) {
        Map<String, Object> params = new HashMap<>();
        String[] parts = path.split("/");

        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                try {
                    params.put("segment_" + i, Integer.parseInt(parts[i]));
                } catch (NumberFormatException e) {
                    params.put("segment_" + i, parts[i]);
                }
            }
        }

        return params;
    }

    private Object getBodyParams(HttpRequest request) {
        try {
            String contentType = request.getContentType().orElse("");
            if (contentType.contains("application/json")) {
                BufferedReader reader = request.getReader();
                String body = reader.lines().collect(Collectors.joining());
                if (!body.isEmpty()) {
                    return gson.fromJson(body, Object.class);
                }
            }
        } catch (Exception e) {
            // Return empty map on error
        }
        return new HashMap<String, String>();
    }

    private Map<String, List<String>> getHeaders(HttpRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        request.getHeaders().forEach((key, values) -> {
            headers.put(key, values);
        });
        return headers;
    }

    private void handleHome(BufferedWriter writer, Map<String, Object> params) throws IOException {
        Map<String, Object> routes = new LinkedHashMap<>();
        routes.put("GET /", "This home page");
        routes.put("GET /users", "List all users (supports ?limit=N&offset=N)");
        routes.put("GET /users/:id", "Get user by ID");
        routes.put("POST /users", "Create user (send JSON body)");
        routes.put("GET /products/:id/reviews/:reviewId", "Get product review");
        routes.put("GET /search", "Search (supports ?q=query&category=cat&sort=asc)");
        routes.put("GET /api/v1/*", "API version 1 endpoints");
        routes.put("GET /api/v2/*", "API version 2 endpoints");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Java Routing Demo");
        response.put("all_parameters", params);
        response.put("available_routes", routes);

        writer.write(gson.toJson(response));
    }

    private void handleGetUser(BufferedWriter writer, Map<String, Object> params) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> pathParams = (Map<String, Object>) params.get("path_params");
        Object userId = pathParams.get("segment_2");

        Map<String, Object> extractedData = new HashMap<>();
        extractedData.put("user_id", userId);
        extractedData.put("query_filters", params.get("query_params"));

        Map<String, Object> response = new HashMap<>();
        response.put("route", "GET /users/:id");
        response.put("message", "Getting user with ID: " + userId);
        response.put("all_parameters", params);
        response.put("extracted_data", extractedData);

        writer.write(gson.toJson(response));
    }

    private void handleListUsers(BufferedWriter writer, Map<String, Object> params) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> queryParams = (Map<String, List<String>>) params.get("query_params");

        int limit = 10;
        int offset = 0;

        if (queryParams.containsKey("limit")) {
            try {
                limit = Integer.parseInt(queryParams.get("limit").get(0));
            } catch (Exception e) {}
        }

        if (queryParams.containsKey("offset")) {
            try {
                offset = Integer.parseInt(queryParams.get("offset").get(0));
            } catch (Exception e) {}
        }

        Map<String, Integer> pagination = new HashMap<>();
        pagination.put("limit", limit);
        pagination.put("offset", offset);

        Map<String, Object> response = new HashMap<>();
        response.put("route", "GET /users");
        response.put("message", "Listing users");
        response.put("all_parameters", params);
        response.put("pagination", pagination);

        writer.write(gson.toJson(response));
    }

    private void handleCreateUser(BufferedWriter writer, Map<String, Object> params, HttpResponse httpResponse) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("route", "POST /users");
        response.put("message", "Creating new user");
        response.put("all_parameters", params);
        response.put("received_body", params.get("body_params"));

        httpResponse.setStatusCode(201);
        writer.write(gson.toJson(response));
    }

    private void handleProductReview(BufferedWriter writer, Map<String, Object> params) throws IOException {
        String path = (String) params.get("path");
        String[] parts = path.split("/");

        String productId = parts.length > 2 ? parts[2] : null;
        String reviewId = parts.length > 4 ? parts[4] : null;

        Map<String, String> extractedData = new HashMap<>();
        extractedData.put("product_id", productId);
        extractedData.put("review_id", reviewId);

        Map<String, Object> response = new HashMap<>();
        response.put("route", "GET /products/:id/reviews/:reviewId");
        response.put("message", String.format("Getting review %s for product %s", reviewId, productId));
        response.put("all_parameters", params);
        response.put("extracted_data", extractedData);

        writer.write(gson.toJson(response));
    }

    private void handleSearch(BufferedWriter writer, Map<String, Object> params) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> queryParams = (Map<String, List<String>>) params.get("query_params");

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("query", queryParams.getOrDefault("q", List.of("")).get(0));
        searchParams.put("category", queryParams.getOrDefault("category", List.of("all")).get(0));
        searchParams.put("sort", queryParams.getOrDefault("sort", List.of("relevance")).get(0));
        searchParams.put("page", queryParams.getOrDefault("page", List.of("1")).get(0));

        Map<String, Object> response = new HashMap<>();
        response.put("route", "GET /search");
        response.put("message", "Performing search");
        response.put("all_parameters", params);
        response.put("search_params", searchParams);

        writer.write(gson.toJson(response));
    }

    private void handleApiV1(BufferedWriter writer, Map<String, Object> params) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("route", "GET /api/v1/*");
        response.put("message", "API Version 1 endpoint");
        response.put("all_parameters", params);
        response.put("api_version", "v1");

        writer.write(gson.toJson(response));
    }

    private void handleApiV2(BufferedWriter writer, Map<String, Object> params) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("route", "GET /api/v2/*");
        response.put("message", "API Version 2 endpoint");
        response.put("all_parameters", params);
        response.put("api_version", "v2");

        writer.write(gson.toJson(response));
    }

    private void handle404(BufferedWriter writer, Map<String, Object> params, HttpResponse httpResponse) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Route not found");
        response.put("message", String.format("No handler for %s %s", params.get("method"), params.get("path")));
        response.put("all_parameters", params);

        httpResponse.setStatusCode(404);
        writer.write(gson.toJson(response));
    }
}
