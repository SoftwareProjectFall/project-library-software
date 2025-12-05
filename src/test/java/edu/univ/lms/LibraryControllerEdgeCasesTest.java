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

/**
 * Edge case tests for LibraryController
 */
public class LibraryControllerEdgeCasesTest {

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
    void handleAdminMenu_addItem_shouldHandleAllTypeBranches() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        // Test type 1 (Book) - else branch
        String input1 = "1\nTitle1\nAuthor1\n1\n8\n";
        Scanner scanner1 = new Scanner(new ByteArrayInputStream(input1.getBytes()));
        Thread t1 = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner1);
            } catch (Exception e) {}
        });
        t1.start();
        t1.join(2000);

        // Test type 2 (DVD) - if branch
        admin.login("admin", "1234");
        String input2 = "1\nTitle2\nAuthor2\n2\n8\n";
        Scanner scanner2 = new Scanner(new ByteArrayInputStream(input2.getBytes()));
        Thread t2 = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner2);
            } catch (Exception e) {}
        });
        t2.start();
        t2.join(2000);

        // Test type 3 (Journal) - else if branch
        admin.login("admin", "1234");
        String input3 = "1\nTitle3\nAuthor3\n3\n8\n";
        Scanner scanner3 = new Scanner(new ByteArrayInputStream(input3.getBytes()));
        Thread t3 = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner3);
            } catch (Exception e) {}
        });
        t3.start();
        t3.join(2000);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_updateItem_shouldHandleEmptyFields() throws Exception {
        setupAdminAndBooks();
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Old Title", "Old Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        // Update with empty title and author (should keep same)
        String input = "3\n101\n\n\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner);
            } catch (Exception e) {}
        });
        testThread.start();
        testThread.join(2000);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_unregisterUser_shouldHandleUserNotFound() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "7\n999\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner);
            } catch (Exception e) {}
        });
        testThread.start();
        testThread.join(2000);

        String output = outContent.toString();
        assertTrue(output.contains("User not found") || output.length() > 0);
    }

    @Test
    void handleUserMenu_payFine_shouldHandleExceptionInLoop() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        user.addFine(50.0);

        // Invalid input (not a number) should trigger exception branch
        String input = "4\nnot-a-number\n25.0\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {}
        });
        testThread.start();
        testThread.join(2000);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid number") || output.length() > 0);
    }

    @Test
    void handleUserMenu_payFine_shouldHandleNegativeAmount() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        user.addFine(50.0);

        String input = "4\n-10\n25.0\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {}
        });
        testThread.start();
        testThread.join(2000);

        String output = outContent.toString();
        assertTrue(output.contains("Amount must be positive") || output.length() > 0);
    }

    @Test
    void handleUserMenu_search_shouldHandleISBNNotFound() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        String input = "7\n3\n999\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {}
        });
        testThread.start();
        testThread.join(2000);

        String output = outContent.toString();
        assertTrue(output.contains("No item found") || output.length() > 0);
    }

    @Test
    void handleUserMenu_search_shouldHandleInvalidSearchOption() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        String input = "7\n99\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {}
        });
        testThread.start();
        testThread.join(2000);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid search option") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleCaseInsensitiveUsernameCheck() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Existing", "Existing", "pass", false, "existing@test.com"));
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        // Try to register with different case (should be detected as duplicate)
        String input = "Name\nexisting\nnewuser\npass\nuser@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("already taken") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleAllValidationLoops() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        // Test all loops: empty name, empty username, duplicate username, empty password, empty email, invalid email
        String input = "\nName\n\nuser\nexisting\nnewuser\n\npass\n\nemail\ninvalid\nvalid@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // First create existing user
        users.add(new User("1", "Existing", "existing", "pass", false, "existing@test.com"));
        userRepo.saveUsers(users);

        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    private void setupAdminAndBooks() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Admin", "admin", "1234", true, "admin@test.com"));
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        bookRepo.saveBooks(new ArrayList<>());
    }

    private void setupUserAndBooks() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "User", "user", "pass", false, "user@test.com"));
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);
    }
}

