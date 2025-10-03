package functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import functions.config.AppConfig;
import functions.models.User;
import functions.services.UserService;
import functions.utils.Logger;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdvancedUserAPI implements HttpFunction {
    private static final UserService userService = new UserService();
    private static final Logger logger = new Logger();
    private static final Gson gson = new Gson();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        logger.info("Request received", String.format(
            "{\"method\":\"%s\",\"path\":\"%s\",\"service\":\"%s\"}",
            request.getMethod(), request.getPath(), AppConfig.SERVICE_NAME
        ));

        // Set CORS headers
        if (AppConfig.CORS.ENABLED) {
            response.appendHeader("Access-Control-Allow-Origin", "*");
            response.appendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
        }

        response.appendHeader("Content-Type", "application/json");
        BufferedWriter writer = response.getWriter();

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatusCode(204);
            return;
        }

        try {
            String path = request.getPath();
            String method = request.getMethod();

            if ("POST".equals(method) && "/users".equals(path)) {
                JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
                String name = body.get("name").getAsString();
                String email = body.get("email").getAsString();

                User user = userService.createUser(name, email);

                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("data", parseJson(user.toJson()));

                response.setStatusCode(201);
                writer.write(gson.toJson(resp));

            } else if ("GET".equals(method) && "/users".equals(path)) {
                List<User> users = userService.getAllUsers();
                List<JsonObject> userJsons = users.stream()
                    .map(u -> parseJson(u.toJson()))
                    .collect(Collectors.toList());

                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("data", userJsons);
                resp.put("count", users.size());

                writer.write(gson.toJson(resp));

            } else if ("GET".equals(method) && path.startsWith("/users/")) {
                int id = Integer.parseInt(path.substring(7));
                User user = userService.getUser(id);

                Map<String, Object> resp = new HashMap<>();
                if (user != null) {
                    resp.put("success", true);
                    resp.put("data", parseJson(user.toJson()));
                    writer.write(gson.toJson(resp));
                } else {
                    response.setStatusCode(404);
                    resp.put("success", false);
                    resp.put("error", "User not found");
                    writer.write(gson.toJson(resp));
                }

            } else {
                Map<String, Object> endpoints = new HashMap<>();
                endpoints.put("POST /users", "Create a new user");
                endpoints.put("GET /users", "Get all users");
                endpoints.put("GET /users/:id", "Get user by ID");

                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("message", "Advanced Java Cloud Function with complex folder structure");
                resp.put("version", AppConfig.VERSION);
                resp.put("endpoints", endpoints);

                writer.write(gson.toJson(resp));
            }

        } catch (Exception e) {
            logger.error("Error processing request", "{\"error\":\"" + e.getMessage() + "\"}");
            response.setStatusCode(400);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("error", e.getMessage());
            writer.write(gson.toJson(resp));
        }
    }

    private JsonObject parseJson(String json) {
        return gson.fromJson(json, JsonObject.class);
    }
}
