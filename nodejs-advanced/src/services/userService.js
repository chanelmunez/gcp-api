const User = require('../models/User');
const validator = require('../utils/validator');
const logger = require('../utils/logger');

class UserService {
  constructor() {
    this.users = new Map();
    this.nextId = 1;
  }

  createUser(name, email) {
    if (!validator.isValidName(name)) {
      logger.error('Invalid name provided', { name });
      throw new Error('Invalid name: must be between 2 and 100 characters');
    }

    if (!validator.isValidEmail(email)) {
      logger.error('Invalid email provided', { email });
      throw new Error('Invalid email format');
    }

    const user = new User(this.nextId++, name, email);
    this.users.set(user.id, user);

    logger.info('User created successfully', { userId: user.id });
    return user;
  }

  getUser(id) {
    return this.users.get(id);
  }

  getAllUsers() {
    return Array.from(this.users.values());
  }
}

module.exports = UserService;
