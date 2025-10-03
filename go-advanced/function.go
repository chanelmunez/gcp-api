package function

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"strings"

	"github.com/GoogleCloudPlatform/functions-framework-go/functions"
	"go-advanced/config"
	"go-advanced/pkg/services"
	"go-advanced/pkg/utils"
)

var userService = services.NewUserService()

func init() {
	functions.HTTP("AdvancedUserAPI", advancedUserAPI)
}

type Response struct {
	Success bool        `json:"success"`
	Data    interface{} `json:"data,omitempty"`
	Error   string      `json:"error,omitempty"`
	Count   int         `json:"count,omitempty"`
	Message string      `json:"message,omitempty"`
	Version string      `json:"version,omitempty"`
}

func advancedUserAPI(w http.ResponseWriter, r *http.Request) {
	utils.Log.Info("Request received", map[string]interface{}{
		"method":  r.Method,
		"path":    r.URL.Path,
		"service": config.AppConfig.ServiceName,
	})

	// Set CORS headers
	if config.AppConfig.CORS.Enabled {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
	}

	w.Header().Set("Content-Type", "application/json")

	if r.Method == "OPTIONS" {
		w.WriteHeader(http.StatusNoContent)
		return
	}

	if r.Method == "POST" && r.URL.Path == "/users" {
		var data map[string]string
		if err := json.NewDecoder(r.Body).Decode(&data); err != nil {
			respondError(w, "Invalid request body", http.StatusBadRequest)
			return
		}

		user, err := userService.CreateUser(data["name"], data["email"])
		if err != nil {
			respondError(w, err.Error(), http.StatusBadRequest)
			return
		}

		respondJSON(w, Response{Success: true, Data: user}, http.StatusCreated)
	} else if r.Method == "GET" && r.URL.Path == "/users" {
		users := userService.GetAllUsers()
		respondJSON(w, Response{Success: true, Data: users, Count: len(users)}, http.StatusOK)
	} else if r.Method == "GET" && strings.HasPrefix(r.URL.Path, "/users/") {
		idStr := strings.TrimPrefix(r.URL.Path, "/users/")
		id, err := strconv.Atoi(idStr)
		if err != nil {
			respondError(w, "Invalid user ID", http.StatusBadRequest)
			return
		}

		user := userService.GetUser(id)
		if user == nil {
			respondError(w, "User not found", http.StatusNotFound)
			return
		}

		respondJSON(w, Response{Success: true, Data: user}, http.StatusOK)
	} else {
		respondJSON(w, Response{
			Success: true,
			Message: "Advanced Go Cloud Function with complex folder structure",
			Version: config.AppConfig.Version,
			Data: map[string]string{
				"POST /users":     "Create a new user",
				"GET /users":      "Get all users",
				"GET /users/:id":  "Get user by ID",
			},
		}, http.StatusOK)
	}
}

func respondJSON(w http.ResponseWriter, data interface{}, status int) {
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(data)
}

func respondError(w http.ResponseWriter, message string, status int) {
	utils.Log.Error("Error processing request", map[string]interface{}{"error": message})
	respondJSON(w, Response{Success: false, Error: message}, status)
}
