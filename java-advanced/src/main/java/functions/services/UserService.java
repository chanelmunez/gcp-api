package functions.services;

import functions.models.User;
import functions.utils.Logger;
import functions.utils.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;
    private Logger logger = new Logger();
    private Validator validator = new Validator();

    public User createUser(String name, String email) throws Exception {
        if (!validator.isValidName(name)) {
            logger.error("Invalid name provided", "{\"name\":\"" + name + "\"}");
            throw new Exception("Invalid name: must be between 2 and 100 characters");
        }

        if (!validator.isValidEmail(email)) {
            logger.error("Invalid email provided", "{\"email\":\"" + email + "\"}");
            throw new Exception("Invalid email format");
        }

        User user = new User(nextId, name, email);
        users.put(nextId, user);
        nextId++;

        logger.info("User created successfully", "{\"user_id\":" + user.getId() + "}");
        return user;
    }

    public User getUser(int id) {
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
