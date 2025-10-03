import functions_framework
from flask import jsonify
from src.services import UserService
from src.utils import logger
from config import config

user_service = UserService()

@functions_framework.http
def advanced_user_api(request):
    logger.info("Request received", method=request.method, path=request.path, service=config["service_name"])

    # Set CORS headers
    headers = {}
    if config["cors"]["enabled"]:
        headers = {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
            'Access-Control-Allow-Headers': 'Content-Type'
        }

    if request.method == 'OPTIONS':
        return ('', 204, headers)

    try:
        if request.method == 'POST' and request.path == '/users':
            data = request.get_json()
            user = user_service.create_user(data['name'], data['email'])
            return (jsonify({
                'success': True,
                'data': user.to_dict()
            }), 201, headers)

        elif request.method == 'GET' and request.path == '/users':
            users = [u.to_dict() for u in user_service.get_all_users()]
            return (jsonify({
                'success': True,
                'data': users,
                'count': len(users)
            }), 200, headers)

        elif request.method == 'GET' and request.path.startswith('/users/'):
            user_id = int(request.path.split('/')[-1])
            user = user_service.get_user(user_id)

            if user:
                return (jsonify({
                    'success': True,
                    'data': user.to_dict()
                }), 200, headers)
            else:
                return (jsonify({
                    'success': False,
                    'error': 'User not found'
                }), 404, headers)

        else:
            return (jsonify({
                'success': True,
                'message': 'Advanced Python Cloud Function with complex folder structure',
                'version': config['version'],
                'endpoints': {
                    'POST /users': 'Create a new user',
                    'GET /users': 'Get all users',
                    'GET /users/:id': 'Get user by ID'
                }
            }), 200, headers)

    except Exception as e:
        logger.error("Error processing request", error=str(e))
        return (jsonify({
            'success': False,
            'error': str(e)
        }), 400, headers)
