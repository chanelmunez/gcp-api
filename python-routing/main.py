import functions_framework
from flask import jsonify
import json

@functions_framework.http
def routing_demo(request):
    path = request.path
    method = request.method

    # Collect all parameters
    params = {
        'path_params': extract_path_params(path),
        'query_params': dict(request.args),
        'body_params': get_body_params(request),
        'headers': dict(request.headers),
        'method': method,
        'full_url': request.url,
        'path': path,
        'form_data': dict(request.form) if request.form else {}
    }

    # Route handling
    if path == '/' or path == '':
        return handle_home(params)
    elif path.startswith('/users/') and method == 'GET':
        return handle_get_user(params)
    elif path == '/users' and method == 'GET':
        return handle_list_users(params)
    elif path == '/users' and method == 'POST':
        return handle_create_user(params)
    elif path.startswith('/products/') and '/reviews' in path:
        return handle_product_review(params)
    elif path.startswith('/search'):
        return handle_search(params)
    elif path.startswith('/api/v1/'):
        return handle_api_v1(params)
    elif path.startswith('/api/v2/'):
        return handle_api_v2(params)
    else:
        return handle_404(params)

def extract_path_params(path):
    parts = [p for p in path.split('/') if p]
    params = {}

    for index, part in enumerate(parts):
        if part.isdigit():
            params[f'segment_{index}'] = int(part)
        else:
            params[f'segment_{index}'] = part

    return params

def get_body_params(request):
    if request.is_json:
        try:
            return request.get_json()
        except:
            return {}
    return {}

def handle_home(params):
    return jsonify({
        'message': 'Welcome to Python Routing Demo',
        'all_parameters': params,
        'available_routes': {
            'GET /': 'This home page',
            'GET /users': 'List all users (supports ?limit=N&offset=N)',
            'GET /users/:id': 'Get user by ID',
            'POST /users': 'Create user (send JSON body)',
            'GET /products/:id/reviews/:reviewId': 'Get product review',
            'GET /search': 'Search (supports ?q=query&category=cat&sort=asc)',
            'GET /api/v1/*': 'API version 1 endpoints',
            'GET /api/v2/*': 'API version 2 endpoints'
        }
    }), 200

def handle_get_user(params):
    user_id = params['path_params'].get('segment_1')
    return jsonify({
        'route': 'GET /users/:id',
        'message': f'Getting user with ID: {user_id}',
        'all_parameters': params,
        'extracted_data': {
            'user_id': user_id,
            'query_filters': params['query_params']
        }
    }), 200

def handle_list_users(params):
    limit = params['query_params'].get('limit', 10)
    offset = params['query_params'].get('offset', 0)

    return jsonify({
        'route': 'GET /users',
        'message': 'Listing users',
        'all_parameters': params,
        'pagination': {
            'limit': int(limit),
            'offset': int(offset)
        }
    }), 200

def handle_create_user(params):
    return jsonify({
        'route': 'POST /users',
        'message': 'Creating new user',
        'all_parameters': params,
        'received_body': params['body_params']
    }), 201

def handle_product_review(params):
    path_parts = [p for p in params['path'].split('/') if p]
    product_id = path_parts[1] if len(path_parts) > 1 else None
    review_id = path_parts[3] if len(path_parts) > 3 else None

    return jsonify({
        'route': 'GET /products/:id/reviews/:reviewId',
        'message': f'Getting review {review_id} for product {product_id}',
        'all_parameters': params,
        'extracted_data': {
            'product_id': product_id,
            'review_id': review_id
        }
    }), 200

def handle_search(params):
    return jsonify({
        'route': 'GET /search',
        'message': 'Performing search',
        'all_parameters': params,
        'search_params': {
            'query': params['query_params'].get('q', ''),
            'category': params['query_params'].get('category', 'all'),
            'sort': params['query_params'].get('sort', 'relevance'),
            'page': params['query_params'].get('page', 1)
        }
    }), 200

def handle_api_v1(params):
    return jsonify({
        'route': 'GET /api/v1/*',
        'message': 'API Version 1 endpoint',
        'all_parameters': params,
        'api_version': 'v1'
    }), 200

def handle_api_v2(params):
    return jsonify({
        'route': 'GET /api/v2/*',
        'message': 'API Version 2 endpoint',
        'all_parameters': params,
        'api_version': 'v2'
    }), 200

def handle_404(params):
    return jsonify({
        'error': 'Route not found',
        'message': f"No handler for {params['method']} {params['path']}",
        'all_parameters': params
    }), 404
