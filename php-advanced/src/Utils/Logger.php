<?php

namespace App\Utils;

use DateTime;

class Logger
{
    const INFO = 'INFO';
    const ERROR = 'ERROR';
    const WARN = 'WARN';

    private function log(string $level, string $message, array $meta = []): void
    {
        $entry = [
            'timestamp' => (new DateTime())->format(DateTime::ISO8601),
            'level' => $level,
            'message' => $message
        ];

        if (!empty($meta)) {
            $entry = array_merge($entry, $meta);
        }

        echo json_encode($entry) . PHP_EOL;
    }

    public function info(string $message, array $meta = []): void
    {
        $this->log(self::INFO, $message, $meta);
    }

    public function error(string $message, array $meta = []): void
    {
        $this->log(self::ERROR, $message, $meta);
    }

    public function warn(string $message, array $meta = []): void
    {
        $this->log(self::WARN, $message, $meta);
    }
}
