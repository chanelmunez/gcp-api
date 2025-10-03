<?php

namespace App\Utils;

class Validator
{
    public function isValidEmail(string $email): bool
    {
        return (bool) filter_var($email, FILTER_VALIDATE_EMAIL);
    }

    public function isValidName(string $name): bool
    {
        $length = strlen($name);
        return $length >= 2 && $length <= 100;
    }
}
