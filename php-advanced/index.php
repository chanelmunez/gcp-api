<?php

require_once __DIR__ . '/vendor/autoload.php';

use Google\CloudFunctions\FunctionsFramework;
use Psr\Http\Message\ServerRequestInterface;
use GuzzleHttp\Psr7\Response;

use App\Services\UserService;
use App\Utils\Logger;
use App\Config\AppConfig;

FunctionsFramework::http('advancedUserAPI', function (ServerRequestInterface $request) {
    static $userService = null;
    static $logger = null;

    if ($userService === null) {
        $userService = new UserService();
        $logger = new Logger();
    }

    $logger->info("Request received", [
        'method' => $request->getMethod(),
        'path' => $request->getUri()->getPath(),
        'service' => AppConfig::SERVICE_NAME
    ]);

    // Set CORS headers
    $headers = [];
    $corsConfig = AppConfig::getCorsConfig();
    if ($corsConfig['enabled']) {
        $headers = [
            'Access-Control-Allow-Origin' => '*',
            'Access-Control-Allow-Methods' => 'GET, POST, OPTIONS',
            'Access-Control-Allow-Headers' => 'Content-Type',
            'Content-Type' => 'application/json'
        ];
    }

    if ($request->getMethod() === 'OPTIONS') {
        return new Response(204, $headers);
    }

    try {
        $path = $request->getUri()->getPath();
        $method = $request->getMethod();

        if ($method === 'POST' && $path === '/users') {
            $body = json_decode($request->getBody()->getContents(), true);
            $user = $userService->createUser($body['name'], $body['email']);

            $response = [
                'success' => true,
                'data' => $user
            ];

            return new Response(201, $headers, json_encode($response));

        } elseif ($method === 'GET' && $path === '/users') {
            $users = $userService->getAllUsers();

            $response = [
                'success' => true,
                'data' => $users,
                'count' => count($users)
            ];

            return new Response(200, $headers, json_encode($response));

        } elseif ($method === 'GET' && strpos($path, '/users/') === 0) {
            $id = (int) substr($path, 7);
            $user = $userService->getUser($id);

            if ($user) {
                $response = [
                    'success' => true,
                    'data' => $user
                ];
                return new Response(200, $headers, json_encode($response));
            } else {
                $response = [
                    'success' => false,
                    'error' => 'User not found'
                ];
                return new Response(404, $headers, json_encode($response));
            }

        } else {
            $response = [
                'success' => true,
                'message' => 'Advanced PHP Cloud Function with complex folder structure',
                'version' => AppConfig::VERSION,
                'endpoints' => [
                    'POST /users' => 'Create a new user',
                    'GET /users' => 'Get all users',
                    'GET /users/:id' => 'Get user by ID'
                ]
            ];

            return new Response(200, $headers, json_encode($response));
        }

    } catch (\Exception $e) {
        $logger->error("Error processing request", ['error' => $e->getMessage()]);

        $response = [
            'success' => false,
            'error' => $e->getMessage()
        ];

        return new Response(400, $headers, json_encode($response));
    }
});
