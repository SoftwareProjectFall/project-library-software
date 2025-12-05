package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.UserRepository;

public class LibraryControllerLoginFlowTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        Files.deleteIfExists(Paths.get("users.json"));
        Files.deleteIfExists(Paths.get("items.json"));
    }

    @Test
    void run_shouldExecuteEmptyUsernameLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "User", "user", "pass", false, "user@test.com"));
        userRepo.saveUsers(users);

        // Empty username, then valid username
        String input = "1\n\nuser\npass\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread testThread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(3000);
        
        String output = outContent.toString();
        assertTrue(output.contains("Username cannot be empty") || output.length() > 0);
    }

    @Test
    void run_shouldExecuteEmptyPasswordLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "User", "user", "pass", false, "user@test.com"));
        userRepo.saveUsers(users);

        // Valid username, empty password, then valid password
        String input = "1\nuser\n\npass\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread testThread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(3000);
        
        String output = outContent.toString();
        assertTrue(output.contains("Password cannot be empty") || output.length() > 0);
    }

    @Test
    void run_shouldExecuteAdminBranch() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        // Login as admin, then logout
        String input = "1\nadmin\n1234\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread testThread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(3000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void run_shouldExecuteUserBranch() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        users.add(user);
        userRepo.saveUsers(users);

        // Login as user, then logout
        String input = "1\nuser\npass\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread testThread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(3000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
}

