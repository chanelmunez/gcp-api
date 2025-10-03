<?php

namespace App\Services;

use App\Models\User;
use App\Utils\Logger;
use App\Utils\Validator;
use Exception;

class UserService
{
    private array $users = [];
    private int $nextId = 1;
    private Logger $logger;
    private Validator $validator;

    public function __construct()
    {
        $this->logger = new Logger();
        $this->validator = new Validator();
    }

    public function createUser(string $name, string $email): User
    {
        if (!$this->validator->isValidName($name)) {
            $this->logger->error("Invalid name provided", ['name' => $name]);
            throw new Exception("Invalid name: must be between 2 and 100 characters");
        }

        if (!$this->validator->isValidEmail($email)) {
            $this->logger->error("Invalid email provided", ['email' => $email]);
            throw new Exception("Invalid email format");
        }

        $user = new User($this->nextId, $name, $email);
        $this->users[$this->nextId] = $user;
        $this->nextId++;

        $this->logger->info("User created successfully", ['user_id' => $user->getId()]);
        return $user;
    }

    public function getUser(int $id): ?User
    {
        return $this->users[$id] ?? null;
    }

    public function getAllUsers(): array
    {
        return array_values($this->users);
    }
}
