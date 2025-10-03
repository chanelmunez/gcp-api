using Google.Cloud.Functions.Framework;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text.Json;
using System.Threading.Tasks;
using AdvancedUserService.Config;
using AdvancedUserService.Models;
using AdvancedUserService.Services;
using AdvancedUserService.Utils;

namespace AdvancedUserService;

public class Function : IHttpFunction
{
    private static readonly UserService _userService = new();
    private static readonly Logger _logger = new();

    public async Task HandleAsync(HttpContext context)
    {
        var request = context.Request;
        var response = context.Response;

        _logger.Info("Request received", new Dictionary<string, object>
        {
            ["method"] = request.Method,
            ["path"] = request.Path.Value ?? "/",
            ["service"] = AppConfig.ServiceName
        });

        // Set CORS headers
        if (AppConfig.Cors.Enabled)
        {
            response.Headers.Add("Access-Control-Allow-Origin", "*");
            response.Headers.Add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.Headers.Add("Access-Control-Allow-Headers", "Content-Type");
        }

        response.ContentType = "application/json";

        if (request.Method == "OPTIONS")
        {
            response.StatusCode = 204;
            return;
        }

        try
        {
            var path = request.Path.Value ?? "/";
            var method = request.Method;

            if (method == "POST" && path == "/users")
            {
                using var reader = new StreamReader(request.Body);
                var body = await reader.ReadToEndAsync();
                var data = JsonSerializer.Deserialize<Dictionary<string, string>>(body);

                var user = _userService.CreateUser(data!["name"], data["email"]);

                var result = new
                {
                    success = true,
                    data = user
                };

                response.StatusCode = 201;
                await response.WriteAsync(JsonSerializer.Serialize(result));
            }
            else if (method == "GET" && path == "/users")
            {
                var users = _userService.GetAllUsers();

                var result = new
                {
                    success = true,
                    data = users,
                    count = users.Count
                };

                await response.WriteAsync(JsonSerializer.Serialize(result));
            }
            else if (method == "GET" && path.StartsWith("/users/"))
            {
                var idStr = path.Substring(7);
                if (int.TryParse(idStr, out var id))
                {
                    var user = _userService.GetUser(id);

                    if (user != null)
                    {
                        var result = new
                        {
                            success = true,
                            data = user
                        };
                        await response.WriteAsync(JsonSerializer.Serialize(result));
                    }
                    else
                    {
                        response.StatusCode = 404;
                        var result = new
                        {
                            success = false,
                            error = "User not found"
                        };
                        await response.WriteAsync(JsonSerializer.Serialize(result));
                    }
                }
            }
            else
            {
                var result = new
                {
                    success = true,
                    message = "Advanced .NET Cloud Function with complex folder structure",
                    version = AppConfig.Version,
                    endpoints = new Dictionary<string, string>
                    {
                        ["POST /users"] = "Create a new user",
                        ["GET /users"] = "Get all users",
                        ["GET /users/:id"] = "Get user by ID"
                    }
                };

                await response.WriteAsync(JsonSerializer.Serialize(result));
            }
        }
        catch (Exception e)
        {
            _logger.Error("Error processing request", new Dictionary<string, object> { ["error"] = e.Message });

            response.StatusCode = 400;
            var result = new
            {
                success = false,
                error = e.Message
            };

            await response.WriteAsync(JsonSerializer.Serialize(result));
        }
    }
}
