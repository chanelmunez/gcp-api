const functions = require('@google-cloud/functions-framework');
const UserService = require('./src/services/userService');
const logger = require('./src/utils/logger');
const config = require('./config/app.config');

const userService = new UserService();

functions.http('advancedUserAPI', (req, res) => {
  logger.info('Request received', {
    method: req.method,
    path: req.path,
    service: config.serviceName
  });

  // Set CORS headers
  if (config.cors.enabled) {
    res.set('Access-Control-Allow-Origin', '*');
    res.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.set('Access-Control-Allow-Headers', 'Content-Type');
  }

  if (req.method === 'OPTIONS') {
    res.status(204).send('');
    return;
  }

  try {
    if (req.method === 'POST' && req.path === '/users') {
      const { name, email } = req.body;
      const user = userService.createUser(name, email);
      res.status(201).json({
        success: true,
        data: user.toJSON()
      });
    } else if (req.method === 'GET' && req.path === '/users') {
      const users = userService.getAllUsers().map(u => u.toJSON());
      res.status(200).json({
        success: true,
        data: users,
        count: users.length
      });
    } else if (req.method === 'GET' && req.path.startsWith('/users/')) {
      const id = parseInt(req.path.split('/')[2]);
      const user = userService.getUser(id);

      if (user) {
        res.status(200).json({
          success: true,
          data: user.toJSON()
        });
      } else {
        res.status(404).json({
          success: false,
          error: 'User not found'
        });
      }
    } else {
      res.status(200).json({
        success: true,
        message: 'Advanced Node.js Cloud Function with complex folder structure',
        version: config.version,
        endpoints: {
          'POST /users': 'Create a new user',
          'GET /users': 'Get all users',
          'GET /users/:id': 'Get user by ID'
        }
      });
    }
  } catch (error) {
    logger.error('Error processing request', { error: error.message });
    res.status(400).json({
      success: false,
      error: error.message
    });
  }
});
