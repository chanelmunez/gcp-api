# GCP Cloud Run Functions - Routing & Parameter Examples

Comprehensive routing examples demonstrating path parameters, query strings, request bodies, and header extraction across all supported languages.

## Features

Each routing function demonstrates:
- **Multiple route handlers** with different URL patterns
- **Path parameter extraction** (e.g., `/users/123`)
- **Query string parsing** (e.g., `?limit=10&offset=20`)
- **Request body parsing** (JSON)
- **Header inspection**
- **HTTP method handling** (GET, POST, etc.)
- **Nested routes** (e.g., `/products/:id/reviews/:reviewId`)
- **Wildcard routes** (e.g., `/api/v1/*`)
- **404 handling** for undefined routes
- **All parameters displayed** in every response

## Available Routes

All implementations support these routes:

| Route | Method | Description | Parameters |
|-------|--------|-------------|------------|
| `/` | GET | Home page with route list | - |
| `/users` | GET | List users | Query: `limit`, `offset` |
| `/users/:id` | GET | Get user by ID | Path: `id` |
| `/users` | POST | Create user | Body: JSON |
| `/products/:id/reviews/:reviewId` | GET | Get product review | Path: `productId`, `reviewId` |
| `/search` | GET | Search | Query: `q`, `category`, `sort`, `page` |
| `/api/v1/*` | GET | API v1 endpoints | Path: any |
| `/api/v2/*` | GET | API v2 endpoints | Path: any |

## Example Requests

### Get Home Page
```bash
curl https://YOUR-FUNCTION-URL/
```

### Get User by ID
```bash
curl https://YOUR-FUNCTION-URL/users/42
```

### List Users with Pagination
```bash
curl "https://YOUR-FUNCTION-URL/users?limit=20&offset=40"
```

### Create User (POST with JSON)
```bash
curl -X POST https://YOUR-FUNCTION-URL/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane@example.com","age":30}'
```

### Nested Route - Product Reviews
```bash
curl https://YOUR-FUNCTION-URL/products/123/reviews/456
```

### Search with Multiple Query Params
```bash
curl "https://YOUR-FUNCTION-URL/search?q=laptop&category=electronics&sort=price&page=2"
```

### API Versioning
```bash
curl https://YOUR-FUNCTION-URL/api/v1/anything/here
curl https://YOUR-FUNCTION-URL/api/v2/something/else
```

## Response Format

All routes return JSON with:
- `route`: The matched route pattern
- `message`: Description of the action
- `all_parameters`: Complete parameter breakdown including:
  - `path_params`: Extracted path segments
  - `query_params`: Query string parameters
  - `body_params`: Request body (if JSON)
  - `headers`: All request headers
  - `method`: HTTP method
  - `full_url`: Complete request URL
  - `path`: Request path
- `extracted_data`: Route-specific extracted values

### Example Response
```json
{
  "route": "GET /users/:id",
  "message": "Getting user with ID: 42",
  "all_parameters": {
    "path_params": {
      "segment_0": "users",
      "segment_1": 42
    },
    "query_params": {
      "include": ["profile"]
    },
    "body_params": {},
    "headers": {
      "user-agent": ["curl/7.68.0"],
      "accept": ["*/*"]
    },
    "method": "GET",
    "full_url": "https://example.com/users/42?include=profile",
    "path": "/users/42"
  },
  "extracted_data": {
    "user_id": 42,
    "query_filters": {
      "include": ["profile"]
    }
  }
}
```

## Deployment

### Node.js
```bash
cd nodejs-routing
gcloud functions deploy nodejs-routing \
  --gen2 \
  --runtime nodejs20 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point routingDemo \
  --region us-central1
```

### Python
```bash
cd python-routing
gcloud functions deploy python-routing \
  --gen2 \
  --runtime python312 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point routing_demo \
  --region us-central1
```

### Go
```bash
cd go-routing
gcloud functions deploy go-routing \
  --gen2 \
  --runtime go122 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point RoutingDemo \
  --region us-central1
```

### Java
```bash
cd java-routing
gcloud functions deploy java-routing \
  --gen2 \
  --runtime java17 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point functions.RoutingDemo \
  --region us-central1
```

### Ruby
```bash
cd ruby-routing
gcloud functions deploy ruby-routing \
  --gen2 \
  --runtime ruby33 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point routing_demo \
  --region us-central1
```

### PHP
```bash
cd php-routing
gcloud functions deploy php-routing \
  --gen2 \
  --runtime php83 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point routingDemo \
  --region us-central1
```

### .NET
```bash
cd dotnet-routing
gcloud functions deploy dotnet-routing \
  --gen2 \
  --runtime dotnet8 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point RoutingDemo.Function \
  --region us-central1
```

## Key Patterns Demonstrated

### 1. Path Parameter Extraction
All implementations extract path segments and attempt to parse numeric values:
- `/users/123` → `path_params.segment_1 = 123` (integer)
- `/users/john` → `path_params.segment_1 = "john"` (string)

### 2. Query String Parsing
Query parameters are extracted from the URL:
- `?limit=10&offset=20` → `query_params: {limit: "10", offset: "20"}`
- Multiple values supported: `?tag=js&tag=go` → `query_params: {tag: ["js", "go"]}`

### 3. Request Body Handling
JSON bodies are automatically parsed when `Content-Type: application/json`:
```json
{
  "name": "John",
  "email": "john@example.com"
}
```

### 4. Header Access
All request headers are captured and available in `all_parameters.headers`

### 5. Route Matching Priority
Routes are matched in order:
1. Exact matches (`/users`)
2. Prefix matches with specific patterns (`/users/:id`)
3. Wildcard/catch-all routes (`/api/v1/*`)
4. 404 handler (no match)

### 6. HTTP Method Routing
Same path, different methods:
- `GET /users` → List users
- `POST /users` → Create user

## Testing Locally

### Node.js
```bash
cd nodejs-routing
npm install
npm start
# Test: curl http://localhost:8080/users/123
```

### Python
```bash
cd python-routing
pip install -r requirements.txt
functions-framework --target=routing_demo
# Test: curl http://localhost:8080/users/123
```

### Go
```bash
cd go-routing
go run function.go
# Test: curl http://localhost:8080/users/123
```

## Use Cases

These routing patterns are useful for:
- **RESTful APIs** with resource-based URLs
- **Microservices** with versioned endpoints
- **Webhooks** that need to parse different event types
- **API Gateways** routing to different handlers
- **Documentation** auto-generating from available routes
- **Debugging** seeing exactly what parameters were received

## Clean Up

Delete all routing functions:
```bash
gcloud functions delete nodejs-routing --gen2 --region us-central1
gcloud functions delete python-routing --gen2 --region us-central1
gcloud functions delete go-routing --gen2 --region us-central1
gcloud functions delete java-routing --gen2 --region us-central1
gcloud functions delete ruby-routing --gen2 --region us-central1
gcloud functions delete php-routing --gen2 --region us-central1
gcloud functions delete dotnet-routing --gen2 --region us-central1
```

## Notes

- All parameters are logged in every response for debugging
- Path segments are automatically indexed (segment_0, segment_1, etc.)
- Numeric path segments are parsed as integers when possible
- Query parameters support multiple values (arrays)
- 404 responses still include all received parameters
- CORS is not enabled by default (add headers if needed)
