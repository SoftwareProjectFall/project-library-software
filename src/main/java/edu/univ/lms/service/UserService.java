package edu.univ.lms.service;

import edu.univ.lms.model.User;
import edu.univ.lms.repository.UserRepository;
import java.util.List;

/**
 * Service layer for User business logic.
 * Handles user registration, authentication, and user management.
 */
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user.
     * @param users Current list of users
     * @param name User's full name
     * @param username Desired username (must be unique)
     * @param password User's password
     * @param email User's email
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(List<User> users, String name, String username, String password, String email) {
        // Validate username uniqueness
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                System.out.println("This username is already taken. Try another one.");
                return false;
            }
        }

        // Generate userId
        String userId = String.valueOf(users.size() + 1);
        User newUser = new User(userId, name, username, password, false, email);
        users.add(newUser);

        // Save after registration
        userRepository.saveUsers(users);

        System.out.println("User registered successfully. You can now login.");
        return true;
    }

    /**
     * Authenticates a user by username and password.
     * @param users List of all users
     * @param username Username to authenticate
     * @param password Password to authenticate
     * @return Authenticated user, or null if authentication fails
     */
    public User authenticateUser(List<User> users, String username, String password) {
        for (User u : users) {
            if (u.authenticate(username, password)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Saves users to persistent storage.
     */
    public void saveUsers(List<User> users) {
        userRepository.saveUsers(users);
    }

    /**
     * Loads users from persistent storage.
     */
    public List<User> loadUsers() {
        return userRepository.loadUsers();
    }
}

