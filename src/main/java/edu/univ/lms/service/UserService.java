package edu.univ.lms.service;

import edu.univ.lms.model.User;
import edu.univ.lms.repository.UserRepository;

import java.util.List;

/**
 * Service layer responsible for user-related business logic.
 * <p>
 * This includes user registration, authentication, and persistence
 * through the {@link UserRepository}.
 */
public class UserService {

    /** Repository used for saving and loading user data. */
    private final UserRepository userRepository;

    /**
     * Creates a new UserService with the given repository.
     *
     * @param userRepository repository used for user persistence
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---------------------------------------------------------
    // Registration
    // ---------------------------------------------------------

    /**
     * Registers a new standard (non-admin) user.
     *
     * @param users    current list of all users
     * @param name     full name of the new user
     * @param username desired unique username
     * @param password password for login
     * @param email    user's email address
     * @return {@code true} if registration succeeds; {@code false} otherwise
     */
    public boolean registerUser(List<User> users,
                                String name,
                                String username,
                                String password,
                                String email) {

        // Check for existing username
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                System.out.println("This username is already taken. Try another one.");
                return false;
            }
        }

        // Generate a simple incremental userId
        String userId = String.valueOf(users.size() + 1);

        User newUser = new User(userId, name, username, password, false, email);
        users.add(newUser);

        // Persist updated list
        userRepository.saveUsers(users);

        System.out.println("User registered successfully. You can now login.");
        return true;
    }

    // ---------------------------------------------------------
    // Authentication
    // ---------------------------------------------------------

    /**
     * Authenticates a user based on username and password.
     *
     * @param users    list of all registered users
     * @param username input username
     * @param password input password
     * @return the authenticated {@link User}, or {@code null} if invalid credentials
     */
    public User authenticateUser(List<User> users, String username, String password) {
        for (User u : users) {
            if (u.authenticate(username, password)) {
                return u;
            }
        }
        return null;
    }

    // ---------------------------------------------------------
    // Persistence operations
    // ---------------------------------------------------------

    /**
     * Saves the list of users to persistent storage.
     *
     * @param users users to save
     */
    public void saveUsers(List<User> users) {
        userRepository.saveUsers(users);
    }

    /**
     * Loads all users from persistent storage.
     *
     * @return list of users, or an empty list if none exist
     */
    public List<User> loadUsers() {
        return userRepository.loadUsers();
    }
}
