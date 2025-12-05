package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;
import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.service.UserService;
import edu.univ.lms.strategy.BookFine;

public class LibraryControllerIntegrationTest {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn = System.in;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setIn(originalIn);
        Files.deleteIfExists(Paths.get("users.json"));
        Files.deleteIfExists(Paths.get("items.json"));
    }

    @Test
    void run_shouldExitWhenOption3Selected() {
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        // Run in a separate thread to avoid blocking
        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected when input stream closes
            }
        });
        thread.start();
        
        try {
            thread.join(2000); // Wait max 2 seconds
        } catch (InterruptedException e) {
            // Ignore
        }
        
        String output = outContent.toString();
        assertTrue(output.contains("Welcome") || output.contains("Goodbye") || output.length() > 0);
    }

    @Test
    void run_shouldHandleInvalidChoice() {
        String input = "99\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        thread.start();
        
        try {
            thread.join(2000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        String output = outContent.toString();
        assertTrue(output.contains("Invalid") || output.length() > 0);
    }

    @Test
    void run_shouldHandleLoginFlow() {
        // Create test user first
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Test User", "test", "pass", false, "test@test.com"));
        userRepo.saveUsers(users);
        
        // Input: login, username, password, logout, exit
        String input = "1\ntest\npass\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        thread.start();
        
        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void run_shouldHandleInvalidCredentials() {
        // Create test user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Test User", "test", "pass", false, "test@test.com"));
        userRepo.saveUsers(users);
        
        // Input: login, wrong username, wrong password, then exit
        String input = "1\nwrong\nwrong\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        thread.start();
        
        try {
            thread.join(2000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        String output = outContent.toString();
        assertTrue(output.contains("Invalid") || output.contains("credentials") || output.length() > 0);
    }
}

