<?php

use Google\CloudFunctions\FunctionsFramework;
use Psr\Http\Message\ServerRequestInterface;
use GuzzleHttp\Psr7\Response;

require_once __DIR__ . '/vendor/autoload.php';

FunctionsFramework::http('routingDemo', function (ServerRequestInterface $request) {
    $path = $request->getUri()->getPath();
    $method = $request->getMethod();

    // Collect all parameters
    $params = [
        'path_params' => extractPathParams($path),
        'query_params' => $request->getQueryParams(),
        'body_params' => getBodyParams($request),
        'headers' => $request->getHeaders(),
        'method' => $method,
        'full_url' => (string) $request->getUri(),
        'path' => $path
    ];

    // Route handling
    if ($path === '/' || $path === '') {
        return handleHome($params);
    } elseif (str_starts_with($path, '/users/') && $method === 'GET') {
        return handleGetUser($params);
    } elseif ($path === '/users' && $method === 'GET') {
        return handleListUsers($params);
    } elseif ($path === '/users' && $method === 'POST') {
        return handleCreateUser($params);
    } elseif (str_starts_with($path, '/products/') && str_contains($path, '/reviews')) {
        return handleProductReview($params);
    } elseif (str_starts_with($path, '/search')) {
        return handleSearch($params);
    } elseif (str_starts_with($path, '/api/v1/')) {
        return handleApiV1($params);
    } elseif (str_starts_with($path, '/api/v2/')) {
        return handleApiV2($params);
    } else {
        return handle404($params);
    }
});

function extractPathParams(string $path): array
{
    $parts = array_filter(explode('/', $path));
    $params = [];

    foreach (array_values($parts) as $index => $part) {
        if (is_numeric($part)) {
            $params["segment_$index"] = (int) $part;
        } else {
            $params["segment_$index"] = $part;
        }
    }

    return $params;
}

function getBodyParams(ServerRequestInterface $request): array
{
    $contentType = $request->getHeaderLine('Content-Type');

    if (str_contains($contentType, 'application/json')) {
        $body = (string) $request->getBody();
        if (!empty($body)) {
            return json_decode($body, true) ?? [];
        }
    }

    return [];
}

function handleHome(array $params): Response
{
    $response = [
        'message' => 'Welcome to PHP Routing Demo',
        'all_parameters' => $params,
        'available_routes' => [
            'GET /' => 'This home page',
            'GET /users' => 'List all users (supports ?limit=N&offset=N)',
            'GET /users/:id' => 'Get user by ID',
            'POST /users' => 'Create user (send JSON body)',
            'GET /products/:id/reviews/:reviewId' => 'Get product review',
            'GET /search' => 'Search (supports ?q=query&category=cat&sort=asc)',
            'GET /api/v1/*' => 'API version 1 endpoints',
            'GET /api/v2/*' => 'API version 2 endpoints'
        ]
    ];

    return new Response(
        200,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}

function handleGetUser(array $params): Response
{
    $userId = $params['path_params']['segment_1'] ?? null;

    $response = [
        'route' => 'GET /users/:id',
        'message' => "Getting user with ID: $userId",
        'all_parameters' => $params,
        'extracted_data' => [
            'user_id' => $userId,
            'query_filters' => $params['query_params']
        ]
    ];

    return new Response(
        200,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}

function handleListUsers(array $params): Response
{
    $limit = (int) ($params['query_params']['limit'] ?? 10);
    $offset = (int) ($params['query_params']['offset'] ?? 0);

    $response = [
        'route' => 'GET /users',
        'message' => 'Listing users',
        'all_parameters' => $params,
        'pagination' => [
            'limit' => $limit,
            'offset' => $offset
        ]
    ];

    return new Response(
        200,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}

function handleCreateUser(array $params): Response
{
    $response = [
        'route' => 'POST /users',
        'message' => 'Creating new user',
        'all_parameters' => $params,
        'received_body' => $params['body_params']
    ];

    return new Response(
        201,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}

function handleProductReview(array $params): Response
{
    $pathParts = array_filter(explode('/', $params['path']));
    $pathParts = array_values($pathParts);

    $productId = $pathParts[1] ?? null;
    $reviewId = $pathParts[3] ?? null;

    $response = [
        'route' => 'GET /products/:id/reviews/:reviewId',
        'message' => "Getting review $reviewId for product $productId",
        'all_parameters' => $params,
        'extracted_data' => [
            'product_id' => $productId,
            'review_id' => $reviewId
        ]
    ];

    return new Response(
        200,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}

function handleSearch(array $params): Response
{
    $response = [
        'route' => 'GET /search',
        'message' => 'Performing search',
        'all_parameters' => $params,
        'search_params' => [
            'query' => $params['query_params']['q'] ?? '',
            'category' => $params['query_params']['category'] ?? 'all',
            'sort' => $params['query_params']['sort'] ?? 'relevance',
            'page' => $params['query_params']['page'] ?? 1
        ]
    ];

    return new Response(
        200,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}

function handleApiV1(array $params): Response
{
    $response = [
        'route' => 'GET /api/v1/*',
        'message' => 'API Version 1 endpoint',
        'all_parameters' => $params,
        'api_version' => 'v1'
    ];

    return new Response(
        200,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}

function handleApiV2(array $params): Response
{
    $response = [
        'route' => 'GET /api/v2/*',
        'message' => 'API Version 2 endpoint',
        'all_parameters' => $params,
        'api_version' => 'v2'
    ];

    return new Response(
        200,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}

function handle404(array $params): Response
{
    $response = [
        'error' => 'Route not found',
        'message' => "No handler for {$params['method']} {$params['path']}",
        'all_parameters' => $params
    ];

    return new Response(
        404,
        ['Content-Type' => 'application/json'],
        json_encode($response)
    );
}
