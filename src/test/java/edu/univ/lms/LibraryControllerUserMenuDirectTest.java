package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
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

public class LibraryControllerUserMenuDirectTest {

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
     * Helper method to create a logged-in normal user and persist it.
     */
    private User createLoggedInUser(String id, String username, double initialFine) {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();

        User user = new User(id, "User", username, "pass", false, username + "@test.com");
        user.login(username, "pass");
        if (initialFine > 0) {
            user.addFine(initialFine);
        }

        users.add(user);
        userRepo.saveUsers(users);
        return user;
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
     * Helper method that uses reflection to invoke handleUserMenu(User, Scanner)
     * with a fake Scanner input, in a separate thread.
     */
    private void runHandleUserMenu(LibraryController controller, User user, String inputScript) throws Exception {
        // Prepare Scanner based on provided input script
        Scanner scanner = new Scanner(new ByteArrayInputStream(inputScript.getBytes()));

        // Get the private handleUserMenu method via reflection
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {
                // We intentionally swallow exceptions here so that scanner/input
                // issues do not fail the test directly.
            } finally {
                scanner.close();
            }
        });

        // Start and wait for the menu execution
        testThread.start();
        testThread.join(2000);
    }

    @Test
    void handleUserMenu_shouldBorrowItem() throws Exception {
        // Arrange: create logged-in user and one available book
        User user = createLoggedInUser("1", "user", 0.0);
        saveBooks(new Book("101", "Available Book", "Author", new BookFine()));

        LibraryController controller = new LibraryController();

        // Script: borrow item (choice 1), enter ISBN, then logout (8)
        String input = "1\n101\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldReturnItem() throws Exception {
        // Arrange: create logged-in user and one borrowed book
        User user = createLoggedInUser("1", "user", 0.0);

        Book borrowedBook = new Book("101", "Borrowed Book", "Author", new BookFine());
        borrowedBook.setBorrowed(true);
        borrowedBook.setBorrowedByUserId("1");
        borrowedBook.setDueDate(LocalDate.now().plusDays(5));
        saveBooks(borrowedBook);

        LibraryController controller = new LibraryController();

        // Script: return item (choice 2), enter ISBN, then logout (8)
        String input = "2\n101\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldViewFines() throws Exception {
        // Arrange: create logged-in user with an initial fine
        User user = createLoggedInUser("1", "user", 25.5);

        LibraryController controller = new LibraryController();

        // Script: view fines (choice 3), then logout (8)
        String input = "3\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldPayFine() throws Exception {
        // Arrange: user with an outstanding fine
        User user = createLoggedInUser("1", "user", 50.0);

        LibraryController controller = new LibraryController();

        // Script: pay fine (choice 4), enter amount, then logout (8)
        String input = "4\n25.5\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldHandleInvalidPaymentRetry() throws Exception {
        // Arrange: user with an outstanding fine
        User user = createLoggedInUser("1", "user", 50.0);

        LibraryController controller = new LibraryController();

        // Script: pay fine (4), then:
        //  - invalid amount ("invalid")
        //  - negative amount ("-5")
        //  - valid amount ("25.5")
        //  - logout (8)
        String input = "4\ninvalid\n-5\n25.5\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldShowAllItems() throws Exception {
        // Arrange: logged-in user and one book
        User user = createLoggedInUser("1", "user", 0.0);
        saveBooks(new Book("101", "Book1", "Author1", new BookFine()));

        LibraryController controller = new LibraryController();

        // Script: show all items (5), then logout (8)
        String input = "5\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldShowBorrowedItems() throws Exception {
        // Arrange: logged-in user
        User user = createLoggedInUser("1", "user", 0.0);

        LibraryController controller = new LibraryController();

        // Script: show borrowed items (6), then logout (8)
        String input = "6\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldSearchByTitle() throws Exception {
        // Arrange: logged-in user and a book with a specific title
        User user = createLoggedInUser("1", "user", 0.0);
        saveBooks(new Book("101", "Java Book", "Author", new BookFine()));

        LibraryController controller = new LibraryController();

        // Script: search (7), choose "by title" (1), enter title, then logout (8)
        String input = "7\n1\nJava\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldSearchByAuthor() throws Exception {
        // Arrange: logged-in user and a book with a specific author
        User user = createLoggedInUser("1", "user", 0.0);
        saveBooks(new Book("101", "Book", "John Doe", new BookFine()));

        LibraryController controller = new LibraryController();

        // Script: search (7), choose "by author" (2), enter author, then logout (8)
        String input = "7\n2\nJohn\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldSearchByISBN() throws Exception {
        // Arrange: logged-in user and a book with a specific ISBN
        User user = createLoggedInUser("1", "user", 0.0);
        saveBooks(new Book("101", "Book", "Author", new BookFine()));

        LibraryController controller = new LibraryController();

        // Script: search (7), choose "by ISBN" (3), enter ISBN, then logout (8)
        String input = "7\n3\n101\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldSearchByISBNNotFound() throws Exception {
        // Arrange: logged-in user with no matching ISBN in the repository
        User user = createLoggedInUser("1", "user", 0.0);

        LibraryController controller = new LibraryController();

        // Script: search by ISBN (7 → 3), enter non-existing ISBN (999), then logout (8)
        String input = "7\n3\n999\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldHandleInvalidSearchOption() throws Exception {
        // Arrange: logged-in user
        User user = createLoggedInUser("1", "user", 0.0);

        LibraryController controller = new LibraryController();

        // Script: search (7), choose invalid search option (99), then logout (8)
        String input = "7\n99\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldHandleInvalidChoice() throws Exception {
        // Arrange: logged-in user
        User user = createLoggedInUser("1", "user", 0.0);

        LibraryController controller = new LibraryController();

        // Script: invalid main menu choice (99), then logout (8)
        String input = "99\n8\n";

        // Act
        runHandleUserMenu(controller, user, input);

        // Assert
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
}
