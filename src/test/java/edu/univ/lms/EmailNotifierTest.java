package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

/**
 * Simple test for EmailNotifier.
 * We only check that it prints a message without throwing exceptions.
 */
public class EmailNotifierTest {

    @Test
    void notify_shouldPrintMessageToConsole() {
        EmailNotifier notifier = new EmailNotifier();

        User user = new User("1", "Mahmoud", "mahmoud", "1234", false, "m@test.com");

        // Capture System.out to verify output (optional, but nice)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        try {
            notifier.notify(user, "Test message");
        } finally {
            // restore original System.out
            System.setOut(original);
        }

        String output = out.toString();

        assertTrue(output.contains("To: m@test.com"));
        assertTrue(output.contains("Test message"));
    }
}
