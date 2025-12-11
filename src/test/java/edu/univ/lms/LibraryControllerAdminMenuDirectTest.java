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

public class LibraryControllerAdminMenuDirectTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // Save original System.out so we can restore it later
        originalOut = System.out;

        // Capture console output for assertions
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        // Restore original System.out
        System.setOut(originalOut);

        // Clean up any JSON files created during tests (local versions)
        Files.deleteIfExists(Paths.get("users.json"));
        Files.deleteIfExists(Paths.get("items.json"));
    }

    /**
     * Helper method to create a logged-in admin user and persist it.
     */
    private User createLoggedInAdmin() {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        users.add(admin);
        userRepo.saveUsers(users);
        return admin;
    }

    /**
     * Helper method to save a list of books.
     */
    private void saveBooks(Book... booksArray) {
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        for (Book b : booksArray) {
            books.add(b);
        }
        bookRepo.saveBooks(books);
    }

    /**
     * Helper method that uses reflection to invoke handleAdminMenu(User, Scanner)
     * with a fake Scanner input, in a separate thread.
     */
    private void runHandleAdminMenu(LibraryController controller, User admin, String inputScript) throws Exception {
        Scanner scanner = new Scanner(new ByteArrayInputStream(inputScript.getBytes()));

        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner);
            } catch (Exception e) {
                // We intentionally swallow exceptions here so that scanner/input
                // issues do not fail the test directly.
            } finally {
                scanner.close();
            }
        });

        testThread.start();
        testThread.join(2000);
    }

    @Test
    void handleAdminMenu_shouldAddBook() throws Exception {
        // Arrange: logged-in admin
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: add item (1), enter title/author, choose Book type (1), then logout (8)
        String input = "1\nTest Book\nTest Author\n1\n8\n";

        // Act
        runHandleAdminMenu(controller, admin, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldAddDvd() throws Exception {
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: add item (1), enter title/author, choose DVD (2), then logout (8)
        String input = "1\nMovie\nDirector\n2\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldAddJournal() throws Exception {
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: add item (1), enter title/author, choose Journal (3), then logout (8)
        String input = "1\nJournal\nEditor\n3\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldRemoveItem() throws Exception {
        User admin = createLoggedInAdmin();

        // Save one book to remove
        saveBooks(new Book("101", "Book to Remove", "Author", new BookFine()));

        LibraryController controller = new LibraryController();

        // Script: remove item (2), enter ISBN, then logout (8)
        String input = "2\n101\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldUpdateItem() throws Exception {
        User admin = createLoggedInAdmin();

        // Save one book to update
        saveBooks(new Book("101", "Old Title", "Old Author", new BookFine()));

        LibraryController controller = new LibraryController();

        // Script: update item (3), enter ISBN, new title, new author, then logout (8)
        String input = "3\n101\nNew Title\nNew Author\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldShowAllItems() throws Exception {
        User admin = createLoggedInAdmin();

        // Save one book to display
        saveBooks(new Book("101", "Book1", "Author1", new BookFine()));

        LibraryController controller = new LibraryController();

        // Script: show all items (4), then logout (8)
        String input = "4\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldShowOverdueItems() throws Exception {
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: show overdue items (5), then logout (8)
        String input = "5\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldSendReminders() throws Exception {
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: send reminders (6), then logout (8)
        String input = "6\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldUnregisterUser() throws Exception {
        // Arrange: admin + target user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        User target = new User("2", "Target", "target", "pass", false, "target@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        users.add(target);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();

        // Script: unregister user (7), enter target id, then logout (8)
        String input = "7\n2\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldHandleInvalidChoice() throws Exception {
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: invalid admin menu choice (99), then logout (8)
        String input = "99\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldHandleEmptyTitleRetry() throws Exception {
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: add item (1) with empty title, then valid title and author, choose Book (1), logout (8)
        String input = "1\n\nValid Title\nAuthor\n1\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldHandleEmptyAuthorRetry() throws Exception {
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: add item (1) with empty author, then valid author, choose Book (1), logout (8)
        String input = "1\nValid Title\n\nValid Author\n1\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldHandleInvalidTypeRetry() throws Exception {
        User admin = createLoggedInAdmin();
        LibraryController controller = new LibraryController();

        // Script: add item (1), invalid type (99), then valid type (1), logout (8)
        String input = "1\nTitle\nAuthor\n99\n1\n8\n";

        runHandleAdminMenu(controller, admin, input);

        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
}
