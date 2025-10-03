<?php

namespace App\Config;

class AppConfig
{
    public const SERVICE_NAME = 'advanced-user-service-php';
    public const VERSION = '1.0.0';

    public static function getEnvironment(): string
    {
        return getenv('ENV') ?: 'development';
    }

    public static function getCorsConfig(): array
    {
        return [
            'enabled' => true,
            'origins' => ['*']
        ];
    }
}
