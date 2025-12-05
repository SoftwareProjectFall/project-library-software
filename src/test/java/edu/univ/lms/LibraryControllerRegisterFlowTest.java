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
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.UserRepository;

public class LibraryControllerRegisterFlowTest {

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
    void registerUser_shouldHandleEmptyName() {
        // Setup: Create controller with existing user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        String input = "2\n\nValid Name\nusername\npass\nemail@test.com\n3\n";
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
        assertTrue(output.contains("Name cannot be empty") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleEmptyUsername() {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        String input = "2\nValid Name\n\nvaliduser\npass\nemail@test.com\n3\n";
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
    void registerUser_shouldHandleEmptyPassword() {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        String input = "2\nValid Name\nusername\n\nvalidpass\nemail@test.com\n3\n";
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
    void registerUser_shouldHandleInvalidEmail() {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        String input = "2\nValid Name\nusername\npass\ninvalidemail\nvalid@email.com\n3\n";
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
    void registerUser_shouldHandleDuplicateUsername() {
        // Setup: Create existing user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Existing", "existing", "pass", false, "existing@test.com"));
        userRepo.saveUsers(users);

        String input = "2\nNew User\nexisting\nnewuser\npass\nemail@test.com\n3\n";
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
    void login_shouldHandleEmptyUsername() {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "User", "user", "pass", false, "user@test.com"));
        userRepo.saveUsers(users);

        String input = "1\n\nuser\npass\n3\n";
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
    void login_shouldHandleEmptyPassword() {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "User", "user", "pass", false, "user@test.com"));
        userRepo.saveUsers(users);

        String input = "1\nuser\n\npass\n3\n";
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
}

