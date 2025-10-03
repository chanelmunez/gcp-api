package function

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strconv"
	"strings"

	"github.com/GoogleCloudPlatform/functions-framework-go/functions"
)

func init() {
	functions.HTTP("RoutingDemo", routingDemo)
}

type AllParams struct {
	PathParams  map[string]interface{} `json:"path_params"`
	QueryParams map[string][]string    `json:"query_params"`
	BodyParams  interface{}            `json:"body_params"`
	Headers     map[string][]string    `json:"headers"`
	Method      string                 `json:"method"`
	FullURL     string                 `json:"full_url"`
	Path        string                 `json:"path"`
}

func routingDemo(w http.ResponseWriter, r *http.Request) {
	path := r.URL.Path
	method := r.Method

	// Collect all parameters
	params := AllParams{
		PathParams:  extractPathParams(path),
		QueryParams: r.URL.Query(),
		BodyParams:  getBodyParams(r),
		Headers:     r.Header,
		Method:      method,
		FullURL:     r.URL.String(),
		Path:        path,
	}

	w.Header().Set("Content-Type", "application/json")

	// Route handling
	if path == "/" || path == "" {
		handleHome(w, params)
	} else if strings.HasPrefix(path, "/users/") && method == "GET" {
		handleGetUser(w, params)
	} else if path == "/users" && method == "GET" {
		handleListUsers(w, params)
	} else if path == "/users" && method == "POST" {
		handleCreateUser(w, params)
	} else if strings.HasPrefix(path, "/products/") && strings.Contains(path, "/reviews") {
		handleProductReview(w, params)
	} else if strings.HasPrefix(path, "/search") {
		handleSearch(w, params)
	} else if strings.HasPrefix(path, "/api/v1/") {
		handleAPIV1(w, params)
	} else if strings.HasPrefix(path, "/api/v2/") {
		handleAPIV2(w, params)
	} else {
		handle404(w, params)
	}
}

func extractPathParams(path string) map[string]interface{} {
	parts := strings.Split(path, "/")
	params := make(map[string]interface{})

	for i, part := range parts {
		if part == "" {
			continue
		}

		if num, err := strconv.Atoi(part); err == nil {
			params[fmt.Sprintf("segment_%d", i)] = num
		} else {
			params[fmt.Sprintf("segment_%d", i)] = part
		}
	}

	return params
}

func getBodyParams(r *http.Request) interface{} {
	if r.Header.Get("Content-Type") == "application/json" {
		body, err := io.ReadAll(r.Body)
		if err != nil {
			return map[string]string{}
		}

		var result interface{}
		if err := json.Unmarshal(body, &result); err != nil {
			return map[string]string{}
		}
		return result
	}
	return map[string]string{}
}

func handleHome(w http.ResponseWriter, params AllParams) {
	response := map[string]interface{}{
		"message":         "Welcome to Go Routing Demo",
		"all_parameters":  params,
		"available_routes": map[string]string{
			"GET /":                                "This home page",
			"GET /users":                           "List all users (supports ?limit=N&offset=N)",
			"GET /users/:id":                       "Get user by ID",
			"POST /users":                          "Create user (send JSON body)",
			"GET /products/:id/reviews/:reviewId":  "Get product review",
			"GET /search":                          "Search (supports ?q=query&category=cat&sort=asc)",
			"GET /api/v1/*":                        "API version 1 endpoints",
			"GET /api/v2/*":                        "API version 2 endpoints",
		},
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(response)
}

func handleGetUser(w http.ResponseWriter, params AllParams) {
	userId := params.PathParams["segment_2"]

	response := map[string]interface{}{
		"route":          "GET /users/:id",
		"message":        fmt.Sprintf("Getting user with ID: %v", userId),
		"all_parameters": params,
		"extracted_data": map[string]interface{}{
			"user_id":       userId,
			"query_filters": params.QueryParams,
		},
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(response)
}

func handleListUsers(w http.ResponseWriter, params AllParams) {
	limit := 10
	offset := 0

	if l := params.QueryParams["limit"]; len(l) > 0 {
		if parsed, err := strconv.Atoi(l[0]); err == nil {
			limit = parsed
		}
	}

	if o := params.QueryParams["offset"]; len(o) > 0 {
		if parsed, err := strconv.Atoi(o[0]); err == nil {
			offset = parsed
		}
	}

	response := map[string]interface{}{
		"route":          "GET /users",
		"message":        "Listing users",
		"all_parameters": params,
		"pagination": map[string]int{
			"limit":  limit,
			"offset": offset,
		},
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(response)
}

func handleCreateUser(w http.ResponseWriter, params AllParams) {
	response := map[string]interface{}{
		"route":          "POST /users",
		"message":        "Creating new user",
		"all_parameters": params,
		"received_body":  params.BodyParams,
	}

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(response)
}

func handleProductReview(w http.ResponseWriter, params AllParams) {
	parts := strings.Split(params.Path, "/")
	var productId, reviewId string

	if len(parts) > 2 {
		productId = parts[2]
	}
	if len(parts) > 4 {
		reviewId = parts[4]
	}

	response := map[string]interface{}{
		"route":          "GET /products/:id/reviews/:reviewId",
		"message":        fmt.Sprintf("Getting review %s for product %s", reviewId, productId),
		"all_parameters": params,
		"extracted_data": map[string]string{
			"product_id": productId,
			"review_id":  reviewId,
		},
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(response)
}

func handleSearch(w http.ResponseWriter, params AllParams) {
	query := ""
	category := "all"
	sort := "relevance"
	page := "1"

	if q := params.QueryParams["q"]; len(q) > 0 {
		query = q[0]
	}
	if c := params.QueryParams["category"]; len(c) > 0 {
		category = c[0]
	}
	if s := params.QueryParams["sort"]; len(s) > 0 {
		sort = s[0]
	}
	if p := params.QueryParams["page"]; len(p) > 0 {
		page = p[0]
	}

	response := map[string]interface{}{
		"route":          "GET /search",
		"message":        "Performing search",
		"all_parameters": params,
		"search_params": map[string]string{
			"query":    query,
			"category": category,
			"sort":     sort,
			"page":     page,
		},
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(response)
}

func handleAPIV1(w http.ResponseWriter, params AllParams) {
	response := map[string]interface{}{
		"route":          "GET /api/v1/*",
		"message":        "API Version 1 endpoint",
		"all_parameters": params,
		"api_version":    "v1",
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(response)
}

func handleAPIV2(w http.ResponseWriter, params AllParams) {
	response := map[string]interface{}{
		"route":          "GET /api/v2/*",
		"message":        "API Version 2 endpoint",
		"all_parameters": params,
		"api_version":    "v2",
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(response)
}

func handle404(w http.ResponseWriter, params AllParams) {
	response := map[string]interface{}{
		"error":          "Route not found",
		"message":        fmt.Sprintf("No handler for %s %s", params.Method, params.Path),
		"all_parameters": params,
	}

	w.WriteHeader(http.StatusNotFound)
	json.NewEncoder(w).Encode(response)
}
