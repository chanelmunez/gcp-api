require_relative '../models/user'
require_relative '../utils/logger'
require_relative '../utils/validator'

module Services
  class UserService
    def initialize
      @users = {}
      @next_id = 1
      @logger = Utils::Logger.new
    end

    def create_user(name, email)
      unless Utils::Validator.valid_name?(name)
        @logger.error("Invalid name provided", name: name)
        raise StandardError, "Invalid name: must be between 2 and 100 characters"
      end

      unless Utils::Validator.valid_email?(email)
        @logger.error("Invalid email provided", email: email)
        raise StandardError, "Invalid email format"
      end

      user = Models::User.new(@next_id, name, email)
      @users[@next_id] = user
      @next_id += 1

      @logger.info("User created successfully", user_id: user.id)
      user
    end

    def get_user(id)
      @users[id]
    end

    def get_all_users
      @users.values
    end
  end
end
