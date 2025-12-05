package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.User;

public class UserEdgeCasesTest {

    @Test
    void defaultConstructor_shouldCreateEmptyUser() {
        User user = new User();
        
        assertNotNull(user);
        assertEquals(0.0, user.getFineBalance());
        assertFalse(user.isLoggedIn());
    }

    @Test
    void setters_shouldUpdateAllFields() {
        User user = new User();
        
        user.setUserId("99");
        user.setName("New Name");
        user.setUsername("newuser");
        user.setPassword("newpass");
        user.setEmail("new@email.com");
        user.setAdmin(true);
        
        assertEquals("99", user.getUserId());
        assertEquals("New Name", user.getName());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals("new@email.com", user.getEmail());
        assertTrue(user.isAdmin());
    }

    @Test
    void addFine_withZero_shouldNotAdd() {
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        
        user.addFine(0.0);
        
        assertEquals(0.0, user.getFineBalance());
    }

    @Test
    void addFine_withNegative_shouldNotAdd() {
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        
        user.addFine(-10.0);
        
        assertEquals(0.0, user.getFineBalance());
    }

    @Test
    void login_withNullUsername_shouldFail() {
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        
        boolean result = user.login(null, "pass");
        
        assertFalse(result);
        assertFalse(user.isLoggedIn());
    }

    @Test
    void login_withNullPassword_shouldFail() {
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        
        boolean result = user.login("test", null);
        
        assertFalse(result);
        assertFalse(user.isLoggedIn());
    }

    @Test
    void authenticate_withNullCredentials_shouldReturnFalse() {
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        
        assertFalse(user.authenticate(null, "pass"));
        assertFalse(user.authenticate("test", null));
    }
}

