<?php

use Google\CloudFunctions\FunctionsFramework;
use Psr\Http\Message\ServerRequestInterface;

// Register the function with Functions Framework.
FunctionsFramework::http('helloWorld', 'helloWorld');

function helloWorld(ServerRequestInterface $request): string
{
    return 'Hello World from PHP Cloud Run Function!';
}
