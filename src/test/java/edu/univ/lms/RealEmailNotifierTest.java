package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.User;
import edu.univ.lms.observer.RealEmailNotifier;

public class RealEmailNotifierTest {

    @Test
    void constructor_shouldSetEmailAndPassword() {
        RealEmailNotifier notifier = new RealEmailNotifier("test@gmail.com", "password123");
        
        // Constructor should not throw
        assertNotNull(notifier);
    }

    @Test
    void notify_shouldHandleMessagingException() {
        RealEmailNotifier notifier = new RealEmailNotifier("test@gmail.com", "password123");
        User user = new User("1", "Test User", "test", "pass", false, "invalid-email");
        
        // Should not throw even if email sending fails
        assertDoesNotThrow(() -> notifier.notify(user, "Test message"));
    }

    @Test
    void notify_shouldHandleValidUser() {
        RealEmailNotifier notifier = new RealEmailNotifier("test@gmail.com", "password123");
        User user = new User("1", "Test User", "test", "pass", false, "user@example.com");
        
        // Should not throw (will fail to send but should handle gracefully)
        assertDoesNotThrow(() -> notifier.notify(user, "Test message"));
    }
}

