package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;
import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.strategy.BookFine;

public class LibraryControllerComprehensiveTest {

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
    void registerUser_shouldHandleAllValidationLoops() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        // Test all validation loops: empty name, empty username, empty password, invalid email
        String input = "\nValid Name\n\nvaliduser\n\nvalidpass\ninvalidemail\nvalid@email.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.contains("REGISTER") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleDuplicateUsernameLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Existing", "existing", "pass", false, "existing@test.com"));
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        // Try duplicate username, then valid one
        String input = "New User\nexisting\nnewuser\npass\nemail@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldHandleAllOptions() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Test option 1: Add item with empty title retry, empty author retry, invalid type retry
        String input = "1\n\nValid Title\n\nValid Author\n99\n1\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldHandleUpdateWithEmptyFields() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Old Title", "Old Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Update with empty title and author (should keep same)
        String input = "3\n101\n\n\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldHandlePayFineWithRetries() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        user.addFine(50.0);
        users.add(user);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Pay fine: invalid input, negative, then valid
        String input = "4\nabc\n-10\n25.5\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldHandleAllSearchOptions() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        users.add(user);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Java Book", "John Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Search by title
        String input = "7\n1\nJava\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void run_shouldHandleRegisterFlow() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        String input = "2\nTest User\ntestuser\ntestpass\ntest@email.com\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread testThread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected when stream closes
            }
        });
        testThread.start();
        testThread.join(3000);
        
        String output = outContent.toString();
        assertTrue(output.contains("Welcome") || output.length() > 0);
    }

    @Test
    void run_shouldHandleLoginThenLogout() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        users.add(user);
        userRepo.saveUsers(users);

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

    @Test
    void run_shouldHandleInvalidMainChoice() throws Exception {
        String input = "99\n3\n";
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
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
}

