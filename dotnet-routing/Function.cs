using Google.Cloud.Functions.Framework;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;

namespace RoutingDemo;

public class Function : IHttpFunction
{
    public async Task HandleAsync(HttpContext context)
    {
        var request = context.Request;
        var response = context.Response;
        var path = request.Path.Value ?? "/";
        var method = request.Method;

        // Collect all parameters
        var allParams = new Dictionary<string, object>
        {
            ["path_params"] = ExtractPathParams(path),
            ["query_params"] = request.Query.ToDictionary(q => q.Key, q => q.Value.ToArray()),
            ["body_params"] = await GetBodyParams(request),
            ["headers"] = request.Headers.ToDictionary(h => h.Key, h => h.Value.ToArray()),
            ["method"] = method,
            ["full_url"] = $"{request.Scheme}://{request.Host}{request.Path}{request.QueryString}",
            ["path"] = path
        };

        response.ContentType = "application/json";

        // Route handling
        if (path == "/" || string.IsNullOrEmpty(path))
        {
            await HandleHome(response, allParams);
        }
        else if (path.StartsWith("/users/") && method == "GET")
        {
            await HandleGetUser(response, allParams);
        }
        else if (path == "/users" && method == "GET")
        {
            await HandleListUsers(response, allParams);
        }
        else if (path == "/users" && method == "POST")
        {
            await HandleCreateUser(response, allParams);
        }
        else if (path.StartsWith("/products/") && path.Contains("/reviews"))
        {
            await HandleProductReview(response, allParams);
        }
        else if (path.StartsWith("/search"))
        {
            await HandleSearch(response, allParams);
        }
        else if (path.StartsWith("/api/v1/"))
        {
            await HandleApiV1(response, allParams);
        }
        else if (path.StartsWith("/api/v2/"))
        {
            await HandleApiV2(response, allParams);
        }
        else
        {
            await Handle404(response, allParams);
        }
    }

    private Dictionary<string, object> ExtractPathParams(string path)
    {
        var parts = path.Split('/', StringSplitOptions.RemoveEmptyEntries);
        var pathParams = new Dictionary<string, object>();

        for (int i = 0; i < parts.Length; i++)
        {
            if (int.TryParse(parts[i], out int numValue))
            {
                pathParams[$"segment_{i}"] = numValue;
            }
            else
            {
                pathParams[$"segment_{i}"] = parts[i];
            }
        }

        return pathParams;
    }

    private async Task<object> GetBodyParams(HttpRequest request)
    {
        if (request.ContentType?.Contains("application/json") == true)
        {
            try
            {
                using var reader = new StreamReader(request.Body);
                var body = await reader.ReadToEndAsync();
                if (!string.IsNullOrEmpty(body))
                {
                    return JsonSerializer.Deserialize<Dictionary<string, object>>(body) ?? new Dictionary<string, object>();
                }
            }
            catch
            {
                return new Dictionary<string, object>();
            }
        }
        return new Dictionary<string, object>();
    }

    private async Task HandleHome(HttpResponse response, Dictionary<string, object> allParams)
    {
        var result = new
        {
            message = "Welcome to .NET Routing Demo",
            all_parameters = allParams,
            available_routes = new Dictionary<string, string>
            {
                ["GET /"] = "This home page",
                ["GET /users"] = "List all users (supports ?limit=N&offset=N)",
                ["GET /users/:id"] = "Get user by ID",
                ["POST /users"] = "Create user (send JSON body)",
                ["GET /products/:id/reviews/:reviewId"] = "Get product review",
                ["GET /search"] = "Search (supports ?q=query&category=cat&sort=asc)",
                ["GET /api/v1/*"] = "API version 1 endpoints",
                ["GET /api/v2/*"] = "API version 2 endpoints"
            }
        };

        response.StatusCode = 200;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }

    private async Task HandleGetUser(HttpResponse response, Dictionary<string, object> allParams)
    {
        var pathParams = (Dictionary<string, object>)allParams["path_params"];
        var userId = pathParams.ContainsKey("segment_1") ? pathParams["segment_1"] : null;

        var result = new
        {
            route = "GET /users/:id",
            message = $"Getting user with ID: {userId}",
            all_parameters = allParams,
            extracted_data = new
            {
                user_id = userId,
                query_filters = allParams["query_params"]
            }
        };

        response.StatusCode = 200;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }

    private async Task HandleListUsers(HttpResponse response, Dictionary<string, object> allParams)
    {
        var queryParams = (Dictionary<string, string[]>)allParams["query_params"];
        int limit = 10;
        int offset = 0;

        if (queryParams.ContainsKey("limit") && int.TryParse(queryParams["limit"][0], out int parsedLimit))
        {
            limit = parsedLimit;
        }

        if (queryParams.ContainsKey("offset") && int.TryParse(queryParams["offset"][0], out int parsedOffset))
        {
            offset = parsedOffset;
        }

        var result = new
        {
            route = "GET /users",
            message = "Listing users",
            all_parameters = allParams,
            pagination = new { limit, offset }
        };

        response.StatusCode = 200;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }

    private async Task HandleCreateUser(HttpResponse response, Dictionary<string, object> allParams)
    {
        var result = new
        {
            route = "POST /users",
            message = "Creating new user",
            all_parameters = allParams,
            received_body = allParams["body_params"]
        };

        response.StatusCode = 201;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }

    private async Task HandleProductReview(HttpResponse response, Dictionary<string, object> allParams)
    {
        var path = (string)allParams["path"];
        var parts = path.Split('/', StringSplitOptions.RemoveEmptyEntries);

        string? productId = parts.Length > 1 ? parts[1] : null;
        string? reviewId = parts.Length > 3 ? parts[3] : null;

        var result = new
        {
            route = "GET /products/:id/reviews/:reviewId",
            message = $"Getting review {reviewId} for product {productId}",
            all_parameters = allParams,
            extracted_data = new { product_id = productId, review_id = reviewId }
        };

        response.StatusCode = 200;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }

    private async Task HandleSearch(HttpResponse response, Dictionary<string, object> allParams)
    {
        var queryParams = (Dictionary<string, string[]>)allParams["query_params"];

        var result = new
        {
            route = "GET /search",
            message = "Performing search",
            all_parameters = allParams,
            search_params = new
            {
                query = queryParams.ContainsKey("q") ? queryParams["q"][0] : "",
                category = queryParams.ContainsKey("category") ? queryParams["category"][0] : "all",
                sort = queryParams.ContainsKey("sort") ? queryParams["sort"][0] : "relevance",
                page = queryParams.ContainsKey("page") ? queryParams["page"][0] : "1"
            }
        };

        response.StatusCode = 200;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }

    private async Task HandleApiV1(HttpResponse response, Dictionary<string, object> allParams)
    {
        var result = new
        {
            route = "GET /api/v1/*",
            message = "API Version 1 endpoint",
            all_parameters = allParams,
            api_version = "v1"
        };

        response.StatusCode = 200;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }

    private async Task HandleApiV2(HttpResponse response, Dictionary<string, object> allParams)
    {
        var result = new
        {
            route = "GET /api/v2/*",
            message = "API Version 2 endpoint",
            all_parameters = allParams,
            api_version = "v2"
        };

        response.StatusCode = 200;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }

    private async Task Handle404(HttpResponse response, Dictionary<string, object> allParams)
    {
        var result = new
        {
            error = "Route not found",
            message = $"No handler for {allParams["method"]} {allParams["path"]}",
            all_parameters = allParams
        };

        response.StatusCode = 404;
        await response.WriteAsync(JsonSerializer.Serialize(result));
    }
}
