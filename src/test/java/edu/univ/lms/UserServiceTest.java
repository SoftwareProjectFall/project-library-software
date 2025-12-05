package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import edu.univ.lms.model.User;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.service.UserService;

import java.nio.file.Files;
import java.nio.file.Paths;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userService = new UserService(userRepository);
    }

    @AfterEach
    void cleanUp() throws Exception {
        Files.deleteIfExists(Paths.get("users.json"));
    }

    @Test
    void registerUser_shouldAddUserAndSave() {
        List<User> users = new ArrayList<>();
        
        boolean result = userService.registerUser(users, "John Doe", "john", "password123", "john@test.com");
        
        assertTrue(result);
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("john", users.get(0).getUsername());
        assertEquals("john@test.com", users.get(0).getEmail());
        assertFalse(users.get(0).isAdmin());
    }

    @Test
    void registerUser_shouldRejectDuplicateUsername() {
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Existing", "john", "pass", false, "existing@test.com"));
        
        boolean result = userService.registerUser(users, "New User", "john", "password", "new@test.com");
        
        assertFalse(result);
        assertEquals(1, users.size()); // No new user added
    }

    @Test
    void authenticateUser_shouldReturnUserForValidCredentials() {
        List<User> users = new ArrayList<>();
        User user = new User("1", "Test User", "testuser", "password", false, "test@test.com");
        users.add(user);
        
        User result = userService.authenticateUser(users, "testuser", "password");
        
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void authenticateUser_shouldReturnNullForInvalidCredentials() {
        List<User> users = new ArrayList<>();
        User user = new User("1", "Test User", "testuser", "password", false, "test@test.com");
        users.add(user);
        
        User result = userService.authenticateUser(users, "testuser", "wrongpassword");
        
        assertNull(result);
    }

    @Test
    void authenticateUser_shouldReturnNullForNonExistentUser() {
        List<User> users = new ArrayList<>();
        
        User result = userService.authenticateUser(users, "nonexistent", "password");
        
        assertNull(result);
    }

    @Test
    void saveUsers_shouldCallRepository() {
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Test", "test", "pass", false, "test@test.com"));
        
        // Should not throw
        assertDoesNotThrow(() -> userService.saveUsers(users));
    }

    @Test
    void loadUsers_shouldReturnListFromRepository() {
        // First save some users
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Test", "test", "pass", false, "test@test.com"));
        userService.saveUsers(users);
        
        // Then load them
        List<User> loaded = userService.loadUsers();
        
        assertNotNull(loaded);
        assertEquals(1, loaded.size());
        assertEquals("Test", loaded.get(0).getName());
    }

    @Test
    void loadUsers_whenNoFile_shouldReturnEmptyList() {
        // Ensure file doesn't exist
        try {
            Files.deleteIfExists(Paths.get("users.json"));
        } catch (Exception e) {
            // Ignore
        }
        
        List<User> loaded = userService.loadUsers();
        
        assertNotNull(loaded);
        assertEquals(0, loaded.size());
    }
}

