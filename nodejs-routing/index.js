const functions = require('@google-cloud/functions-framework');

functions.http('routingDemo', (req, res) => {
  const path = req.path;
  const method = req.method;

  // Collect all parameters
  const params = {
    path_params: extractPathParams(path),
    query_params: req.query,
    body_params: req.body,
    headers: req.headers,
    method: method,
    full_url: req.url,
    path: path
  };

  // Route handling
  if (path === '/' || path === '') {
    handleHome(res, params);
  } else if (path.startsWith('/users/') && method === 'GET') {
    handleGetUser(res, params);
  } else if (path === '/users' && method === 'GET') {
    handleListUsers(res, params);
  } else if (path === '/users' && method === 'POST') {
    handleCreateUser(res, params);
  } else if (path.startsWith('/products/') && path.includes('/reviews')) {
    handleProductReview(res, params);
  } else if (path.startsWith('/search')) {
    handleSearch(res, params);
  } else if (path.startsWith('/api/v1/')) {
    handleApiV1(res, params);
  } else if (path.startsWith('/api/v2/')) {
    handleApiV2(res, params);
  } else {
    handle404(res, params);
  }
});

function extractPathParams(path) {
  const parts = path.split('/').filter(p => p);
  const params = {};

  // Extract numeric IDs or path segments
  parts.forEach((part, index) => {
    if (!isNaN(part)) {
      params[`segment_${index}`] = parseInt(part);
    } else {
      params[`segment_${index}`] = part;
    }
  });

  return params;
}

function handleHome(res, params) {
  res.status(200).json({
    message: 'Welcome to Node.js Routing Demo',
    all_parameters: params,
    available_routes: {
      'GET /': 'This home page',
      'GET /users': 'List all users (supports ?limit=N&offset=N)',
      'GET /users/:id': 'Get user by ID',
      'POST /users': 'Create user (send JSON body)',
      'GET /products/:id/reviews/:reviewId': 'Get product review',
      'GET /search': 'Search (supports ?q=query&category=cat&sort=asc)',
      'GET /api/v1/*': 'API version 1 endpoints',
      'GET /api/v2/*': 'API version 2 endpoints'
    }
  });
}

function handleGetUser(res, params) {
  const userId = params.path_params.segment_1;
  res.status(200).json({
    route: 'GET /users/:id',
    message: `Getting user with ID: ${userId}`,
    all_parameters: params,
    extracted_data: {
      user_id: userId,
      query_filters: params.query_params
    }
  });
}

function handleListUsers(res, params) {
  const limit = params.query_params.limit || 10;
  const offset = params.query_params.offset || 0;

  res.status(200).json({
    route: 'GET /users',
    message: 'Listing users',
    all_parameters: params,
    pagination: {
      limit: parseInt(limit),
      offset: parseInt(offset)
    }
  });
}

function handleCreateUser(res, params) {
  res.status(201).json({
    route: 'POST /users',
    message: 'Creating new user',
    all_parameters: params,
    received_body: params.body_params
  });
}

function handleProductReview(res, params) {
  const pathParts = params.path.split('/').filter(p => p);
  const productId = pathParts[1];
  const reviewId = pathParts[3];

  res.status(200).json({
    route: 'GET /products/:id/reviews/:reviewId',
    message: `Getting review ${reviewId} for product ${productId}`,
    all_parameters: params,
    extracted_data: {
      product_id: productId,
      review_id: reviewId
    }
  });
}

function handleSearch(res, params) {
  res.status(200).json({
    route: 'GET /search',
    message: 'Performing search',
    all_parameters: params,
    search_params: {
      query: params.query_params.q || '',
      category: params.query_params.category || 'all',
      sort: params.query_params.sort || 'relevance',
      page: params.query_params.page || 1
    }
  });
}

function handleApiV1(res, params) {
  res.status(200).json({
    route: 'GET /api/v1/*',
    message: 'API Version 1 endpoint',
    all_parameters: params,
    api_version: 'v1'
  });
}

function handleApiV2(res, params) {
  res.status(200).json({
    route: 'GET /api/v2/*',
    message: 'API Version 2 endpoint',
    all_parameters: params,
    api_version: 'v2'
  });
}

function handle404(res, params) {
  res.status(404).json({
    error: 'Route not found',
    message: `No handler for ${params.method} ${params.path}`,
    all_parameters: params
  });
}
