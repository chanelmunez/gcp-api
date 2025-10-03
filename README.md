# GCP Cloud Run Functions - Hello World Examples

This repository contains Hello World examples for all languages supported by GCP Cloud Run Functions.

## Supported Languages

- Node.js
- Python
- Go
- Java
- Ruby
- PHP
- .NET

## Deployment Instructions

### Prerequisites

1. Install the [Google Cloud SDK](https://cloud.google.com/sdk/docs/install)
2. Authenticate: `gcloud auth login`
3. Set your project: `gcloud config set project YOUR_PROJECT_ID`

### Deploy Functions

#### Node.js
```bash
cd nodejs
gcloud functions deploy nodejs-hello \
  --runtime nodejs20 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point helloWorld \
  --region us-central1
```

#### Python
```bash
cd python
gcloud functions deploy python-hello \
  --runtime python312 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point hello_world \
  --region us-central1
```

#### Go
```bash
cd go
gcloud functions deploy go-hello \
  --runtime go122 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point HelloWorld \
  --region us-central1
```

#### Java
```bash
cd java
gcloud functions deploy java-hello \
  --runtime java17 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point functions.HelloWorld \
  --region us-central1
```

#### Ruby
```bash
cd ruby
gcloud functions deploy ruby-hello \
  --runtime ruby33 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point hello_world \
  --region us-central1
```

#### PHP
```bash
cd php
gcloud functions deploy php-hello \
  --runtime php83 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point helloWorld \
  --region us-central1
```

#### .NET
```bash
cd dotnet
gcloud functions deploy dotnet-hello \
  --runtime dotnet8 \
  --trigger-http \
  --allow-unauthenticated \
  --entry-point HelloWorld.Function \
  --region us-central1
```

## Testing

After deployment, each function will return a URL. You can test it with:

```bash
curl <FUNCTION_URL>
```

Or simply visit the URL in your browser.

## Monitoring Deployment

### View Build Logs
```bash
gcloud functions describe FUNCTION_NAME --region us-central1
```

### View Function Logs
```bash
gcloud functions logs read FUNCTION_NAME --region us-central1
```

### List All Functions
```bash
gcloud functions list --region us-central1
```

### GCP Console
Visit the [Cloud Functions Console](https://console.cloud.google.com/functions) to:
- Monitor build status
- View deployment history
- Check function metrics
- View logs in real-time

## Clean Up

To delete a function:
```bash
gcloud functions delete FUNCTION_NAME --region us-central1
```
