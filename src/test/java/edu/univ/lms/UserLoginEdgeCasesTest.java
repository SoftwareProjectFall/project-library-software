package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.User;

public class UserLoginEdgeCasesTest {

    @Test
    void login_shouldHandleAdminUser() {
        User admin = new User("1", "Admin", "admin", "pass", true, "admin@test.com");
        
        boolean result = admin.login("admin", "pass");
        
        assertTrue(result);
        assertTrue(admin.isLoggedIn());
    }

    @Test
    void login_shouldHandleNormalUser() {
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        
        boolean result = user.login("user", "pass");
        
        assertTrue(result);
        assertTrue(user.isLoggedIn());
    }

    @Test
    void authenticate_shouldBeCaseSensitive() {
        User user = new User("1", "Test", "TestUser", "Password", false, "test@test.com");
        
        assertTrue(user.authenticate("TestUser", "Password"));
        assertFalse(user.authenticate("testuser", "Password")); // Case sensitive
        assertFalse(user.authenticate("TestUser", "password")); // Case sensitive
    }

    @Test
    void logout_whenNotLoggedIn_shouldNotCrash() {
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        
        // Should not throw
        assertDoesNotThrow(() -> user.logout());
        assertFalse(user.isLoggedIn());
    }

    @Test
    void logout_afterLogin_shouldSetLoggedInFalse() {
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        user.login("test", "pass");
        assertTrue(user.isLoggedIn());
        
        user.logout();
        
        assertFalse(user.isLoggedIn());
    }
}

