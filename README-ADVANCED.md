# GCP Cloud Run Functions - Advanced Examples

Advanced Cloud Run Functions demonstrating complex folder structures and multiple module imports across all supported languages.

## Features

Each advanced example includes:
- **RESTful API** with multiple endpoints (POST/GET)
- **Complex folder structure** with separated concerns
- **Models** for data structures
- **Services** for business logic
- **Utils** for shared utilities (logger, validator)
- **Config** for application configuration
- **Structured logging** with JSON output
- **Input validation** with custom validators
- **Error handling** with proper HTTP status codes

## API Endpoints

All advanced functions expose the same REST API:

- `GET /` - Service information and available endpoints
- `POST /users` - Create a new user
- `GET /users` - Get all users
- `GET /users/:id` - Get user by ID

### Example Requests

```bash
# Get service info
curl https://YOUR-FUNCTION-URL/

# Create a user
curl -X POST https://YOUR-FUNCTION-URL/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'

# Get all users
curl https://YOUR-FUNCTION-URL/users

# Get user by ID
curl https://YOUR-FUNCTION-URL/users/1
```

## Folder Structures

### Node.js (`nodejs-advanced/`)
```
nodejs-advanced/
├── config/
│   └── app.config.js          # App configuration
├── src/
│   ├── models/
│   │   └── User.js            # User model
│   ├── services/
│   │   └── userService.js     # User business logic
│   └── utils/
│       ├── logger.js          # Logging utility
│       └── validator.js       # Validation utility
├── index.js                   # Function entry point
└── package.json
```

### Python (`python-advanced/`)
```
python-advanced/
├── config/
│   ├── __init__.py
│   └── app_config.py          # App configuration
├── src/
│   ├── __init__.py
│   ├── models/
│   │   ├── __init__.py
│   │   └── user.py            # User model
│   ├── services/
│   │   ├── __init__.py
│   │   └── user_service.py    # User business logic
│   └── utils/
│       ├── __init__.py
│       ├── logger.py          # Logging utility
│       └── validator.py       # Validation utility
├── main.py                    # Function entry point
└── requirements.txt
```

### Go (`go-advanced/`)
```
go-advanced/
├── config/
│   └── config.go              # App configuration
├── pkg/
│   ├── models/
│   │   └── user.go            # User model
│   ├── services/
│   │   └── user_service.go    # User business logic
│   └── utils/
│       ├── logger.go          # Logging utility
│       └── validator.go       # Validation utility
├── function.go                # Function entry point
└── go.mod
```

### Java (`java-advanced/`)
```
java-advanced/
├── src/main/java/functions/
│   ├── config/
│   │   └── AppConfig.java     # App configuration
│   ├── models/
│   │   └── User.java          # User model
│   ├── services/
│   │   └── UserService.java   # User business logic
│   ├── utils/
│   │   ├── Logger.java        # Logging utility
│   │   └── Validator.java     # Validation utility
│   └── AdvancedUserAPI.java   # Function entry point
└── pom.xml
```

### Ruby (`ruby-advanced/`)
```
ruby-advanced/
├── config/
│   └── app_config.rb          # App configuration
├── lib/
│   ├── models/
│   │   └── user.rb            # User model
│   ├── services/
│   │   └── user_service.rb    # User business logic
│   └── utils/
│       ├── logger.rb          # Logging utility
│       └── validator.rb       # Validation utility
├── app.rb                     # Function entry point
└── Gemfile
```

### PHP (`php-advanced/`)
```
php-advanced/
├── config/
│   └── AppConfig.php          # App configuration
├── src/
│   ├── Models/
│   │   └── User.php           # User model
│   ├── Services/
│   │   └── UserService.php    # User business logic
│   └── Utils/
│       ├── Logger.php         # Logging utility
│       └── Validator.php      # Validation utility
├── index.php                  # Function entry point
└── composer.json
```

### .NET (`dotnet-advanced/`)
```
dotnet-advanced/
├── Config/
│   └── AppConfig.cs           # App configuration
├── Models/
│   └── User.cs                # User model
├── Services/
│   └── UserService.cs         # User business logic
├── Utils/
│   ├── Logger.cs              # Logging utility
│   └── Validator.cs           # Validation utility
├── Function.cs                # Function entry point
└── AdvancedUserService.csproj
```

## Deployment Instructions

### Node.js
```bash
cd nodejs-advanced
gcloud functions deploy nodejs-advanced \
  --gen2 \
  --runtime nodejs20 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point advancedUserAPI \
  --region us-central1
```

### Python
```bash
cd python-advanced
gcloud functions deploy python-advanced \
  --gen2 \
  --runtime python312 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point advanced_user_api \
  --region us-central1
```

### Go
```bash
cd go-advanced
gcloud functions deploy go-advanced \
  --gen2 \
  --runtime go122 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point AdvancedUserAPI \
  --region us-central1
```

### Java
```bash
cd java-advanced
gcloud functions deploy java-advanced \
  --gen2 \
  --runtime java17 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point functions.AdvancedUserAPI \
  --region us-central1
```

### Ruby
```bash
cd ruby-advanced
gcloud functions deploy ruby-advanced \
  --gen2 \
  --runtime ruby33 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point advanced_user_api \
  --region us-central1
```

### PHP
```bash
cd php-advanced
gcloud functions deploy php-advanced \
  --gen2 \
  --runtime php83 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point advancedUserAPI \
  --region us-central1
```

### .NET
```bash
cd dotnet-advanced
gcloud functions deploy dotnet-advanced \
  --gen2 \
  --runtime dotnet8 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point AdvancedUserService.Function \
  --region us-central1
```

## Key Patterns Demonstrated

### 1. Separation of Concerns
- Models handle data structure
- Services contain business logic
- Utils provide shared functionality
- Config manages application settings

### 2. Dependency Injection
- Services receive dependencies through constructors
- Utilities are imported and reused across modules

### 3. Error Handling
- Validation errors return 400 Bad Request
- Not found errors return 404 Not Found
- Successful creates return 201 Created
- All errors include descriptive messages

### 4. Structured Logging
- JSON-formatted log entries
- Consistent timestamp and level fields
- Contextual metadata included

### 5. CORS Support
- Configurable CORS headers
- OPTIONS request handling
- Cross-origin access enabled

## Monitoring

View logs for any deployed function:

```bash
gcloud functions logs read FUNCTION_NAME --gen2 --region us-central1 --limit 50
```

View structured logs in Cloud Console:
```
https://console.cloud.google.com/logs/query
```

## Testing Locally

Each function can be tested locally using the Functions Framework:

**Node.js:**
```bash
cd nodejs-advanced && npm install && npm start
```

**Python:**
```bash
cd python-advanced && pip install -r requirements.txt && functions-framework --target=advanced_user_api
```

**Go:**
```bash
cd go-advanced && go run function.go
```

## Clean Up

Delete all advanced functions:
```bash
gcloud functions delete nodejs-advanced --gen2 --region us-central1
gcloud functions delete python-advanced --gen2 --region us-central1
gcloud functions delete go-advanced --gen2 --region us-central1
gcloud functions delete java-advanced --gen2 --region us-central1
gcloud functions delete ruby-advanced --gen2 --region us-central1
gcloud functions delete php-advanced --gen2 --region us-central1
gcloud functions delete dotnet-advanced --gen2 --region us-central1
```
