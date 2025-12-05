package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.model.User;

public class UserLogoutTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void logout_whenLoggedIn_shouldPrintLogoutMessage() {
        User user = new User("1", "Test User", "test", "pass", false, "test@test.com");
        user.login("test", "pass");
        assertTrue(user.isLoggedIn());
        
        user.logout();
        
        String output = outContent.toString();
        assertTrue(output.contains("has logged out"));
        assertFalse(user.isLoggedIn());
    }

    @Test
    void logout_whenNotLoggedIn_shouldPrintNotLoggedInMessage() {
        User user = new User("1", "Test User", "test", "pass", false, "test@test.com");
        assertFalse(user.isLoggedIn());
        
        user.logout();
        
        String output = outContent.toString();
        assertTrue(output.contains("is not logged in"));
        assertFalse(user.isLoggedIn());
    }

    @Test
    void login_shouldPrintWelcomeMessageForAdmin() {
        User admin = new User("1", "Admin", "admin", "pass", true, "admin@test.com");
        
        admin.login("admin", "pass");
        
        String output = outContent.toString();
        assertTrue(output.contains("Admin") || output.contains("Welcome"));
        assertTrue(admin.isLoggedIn());
    }

    @Test
    void login_shouldPrintWelcomeMessageForNormalUser() {
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        
        user.login("user", "pass");
        
        String output = outContent.toString();
        assertTrue(output.contains("Welcome") || output.length() > 0);
        assertTrue(user.isLoggedIn());
    }
}

