package mctgserver.service;

import mctgserver.database.UserRepository;

public class UserService {
    private final UserRepository userRepository = new UserRepository();

    public String registerUser(String username, String password) {
        if (userRepository.userExists(username)) {
            return "{\"message\": \"User already exists\"}";
        }
        userRepository.createUser(username, password);
        return "{\"message\": \"Success\"}";
    }
}
