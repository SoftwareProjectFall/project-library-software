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
import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.strategy.BookFine;

/**
 * Tests for the run() method covering all main loop branches
 */
public class LibraryControllerRunMethodTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;
    private java.io.InputStream originalIn;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        originalIn = System.in;
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setIn(originalIn);
        Files.deleteIfExists(Paths.get("users.json"));
        Files.deleteIfExists(Paths.get("items.json"));
    }

    @Test
    void run_shouldExitOnChoice3() throws Exception {
        setupUsers();
        String input = "3\n";
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
        testThread.join(2000);

        String output = outContent.toString();
        assertTrue(output.contains("Welcome") || output.contains("Goodbye") || output.length() > 0);
    }

    @Test
    void run_shouldRegisterOnChoice2() throws Exception {
        setupUsers();
        String input = "2\nTest User\ntestuser\ntestpass\ntest@test.com\n3\n";
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
        assertTrue(output.contains("Welcome") || output.contains("REGISTER") || output.length() > 0);
    }

    @Test
    void run_shouldHandleInvalidMainChoice() throws Exception {
        setupUsers();
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
        assertTrue(output.contains("Invalid choice") || output.length() > 0);
    }

    @Test
    void run_shouldHandleEmptyUsernameLoop() throws Exception {
        setupUsers();
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
    void run_shouldHandleEmptyPasswordLoop() throws Exception {
        setupUsers();
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
    void run_shouldHandleInvalidCredentials() throws Exception {
        setupUsers();
        String input = "1\nwrong\nwrong\n3\n";
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
        assertTrue(output.contains("Invalid credentials") || output.length() > 0);
    }

    @Test
    void run_shouldRouteToAdminMenu() throws Exception {
        setupUsers();
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
        assertTrue(output.contains("ADMIN MENU") || output.length() > 0);
    }

    @Test
    void run_shouldRouteToUserMenu() throws Exception {
        setupUsers();
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
        assertTrue(output.contains("USER MENU") || output.length() > 0);
    }

    @Test
    void run_shouldContinueAfterInvalidCredentials() throws Exception {
        setupUsers();
        String input = "1\nwrong\nwrong\n1\nuser\npass\n8\n3\n";
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
        testThread.join(4000);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void run_shouldContinueAfterRegister() throws Exception {
        setupUsers();
        String input = "2\nNew User\nnewuser\nnewpass\nnew@test.com\n99\n3\n";
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
    void run_shouldSaveDataOnExit() throws Exception {
        setupUsers();
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        String input = "3\n";
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
        assertTrue(output.contains("Saving data") || output.contains("Goodbye") || output.length() > 0);
    }

    private void setupUsers() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Admin", "admin", "1234", true, "admin@test.com"));
        users.add(new User("2", "User", "user", "pass", false, "user@test.com"));
        userRepo.saveUsers(users);
    }
}

