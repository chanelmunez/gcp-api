using System;
using System.Collections.Generic;
using System.Linq;
using AdvancedUserService.Models;
using AdvancedUserService.Utils;

namespace AdvancedUserService.Services
{
    public class UserService
    {
        private readonly Dictionary<int, User> _users = new();
        private int _nextId = 1;
        private readonly Logger _logger = new();
        private readonly Validator _validator = new();

        public User CreateUser(string name, string email)
        {
            if (!_validator.IsValidName(name))
            {
                _logger.Error("Invalid name provided", new Dictionary<string, object> { ["name"] = name });
                throw new Exception("Invalid name: must be between 2 and 100 characters");
            }

            if (!_validator.IsValidEmail(email))
            {
                _logger.Error("Invalid email provided", new Dictionary<string, object> { ["email"] = email });
                throw new Exception("Invalid email format");
            }

            var user = new User(_nextId, name, email);
            _users[_nextId] = user;
            _nextId++;

            _logger.Info("User created successfully", new Dictionary<string, object> { ["user_id"] = user.Id });
            return user;
        }

        public User? GetUser(int id)
        {
            return _users.TryGetValue(id, out var user) ? user : null;
        }

        public List<User> GetAllUsers()
        {
            return _users.Values.ToList();
        }
    }
}
