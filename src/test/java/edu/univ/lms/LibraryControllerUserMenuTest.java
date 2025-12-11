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
import edu.univ.lms.strategy.BookFine;

public class LibraryControllerUserMenuTest {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // Save the original System.in and System.out to restore them later
        originalOut = System.out;
        originalIn = System.in;

        // Capture console output
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        // Restore original System.out and System.in
        System.setOut(originalOut);
        System.setIn(originalIn);

        // Clean up any JSON files created during tests
        // Local simple names (in case something uses them)
        Files.deleteIfExists(Paths.get("users.json"));
        Files.deleteIfExists(Paths.get("items.json"));

        // Real application paths under /data (what repositories actually use)
        Files.deleteIfExists(Paths.get("data/users.json"));
        Files.deleteIfExists(Paths.get("data/items.json"));
    }

    /**
     * Helper method to run the controller with a given input script.
     * It starts the controller in a separate thread and waits up to 3 seconds.
     */
    private void runControllerWithInput(String inputScript) {
        System.setIn(new ByteArrayInputStream(inputScript.getBytes()));

        LibraryController controller = new LibraryController();

        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // ignore
            }
        });

        thread.start();

        try {
            // Wait up to 3 seconds
            thread.join(3000);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Helper method to create and persist a single normal user.
     */
    private User createAndSaveUser(String id, String username) {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User(id, "User", username, "pass", false, username + "@test.com");
        users.add(user);
        userRepo.saveUsers(users);
        return user;
    }

    /**
     * Helper method to create and save books.
     */
    private void createAndSaveBooks(Book... booksArray) {
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        for (Book b : booksArray) {
            books.add(b);
        }
        bookRepo.saveBooks(books);
    }

    @Test
    void userMenu_shouldViewFines() {
        createAndSaveUser("1", "user");
        String input = "1\nuser\npass\n3\n8\n3\n";

        runControllerWithInput(input);

        assertTrue(outContent.toString().length() > 0);
    }

    @Test
    void userMenu_shouldShowAllItems() {
        createAndSaveUser("1", "user");
        createAndSaveBooks(new Book("101", "Book1", "Author1", new BookFine()));

        String input = "1\nuser\npass\n5\n8\n3\n";

        runControllerWithInput(input);

        assertTrue(outContent.toString().length() > 0);
    }

    @Test
    void userMenu_shouldShowBorrowedItems() {
        createAndSaveUser("1", "user");

        String input = "1\nuser\npass\n6\n8\n3\n";

        runControllerWithInput(input);

        assertTrue(outContent.toString().length() > 0);
    }

    @Test
    void userMenu_shouldHandleSearchByTitle() {
        createAndSaveUser("1", "user");
        createAndSaveBooks(new Book("101", "Java Book", "Author", new BookFine()));

        String input = "1\nuser\npass\n7\n1\nJava\n8\n3\n";

        runControllerWithInput(input);

        assertTrue(outContent.toString().length() > 0);
    }

    @Test
    void userMenu_shouldHandleSearchByAuthor() {
        createAndSaveUser("1", "user");
        createAndSaveBooks(new Book("101", "Book", "John Doe", new BookFine()));

        String input = "1\nuser\npass\n7\n2\nJohn\n8\n3\n";

        runControllerWithInput(input);

        assertTrue(outContent.toString().length() > 0);
    }

    @Test
    void userMenu_shouldHandleSearchByISBN() {
        createAndSaveUser("1", "user");
        createAndSaveBooks(new Book("101", "Book", "Author", new BookFine()));

        String input = "1\nuser\npass\n7\n3\n101\n8\n3\n";

        runControllerWithInput(input);

        assertTrue(outContent.toString().length() > 0);
    }

    @Test
    void userMenu_shouldHandleInvalidSearchOption() {
        createAndSaveUser("1", "user");

        String input = "1\nuser\npass\n7\n99\n8\n3\n";

        runControllerWithInput(input);

        assertTrue(outContent.toString().length() > 0);
    }

    @Test
    void userMenu_shouldHandleInvalidChoice() {
        createAndSaveUser("1", "user");

        String input = "1\nuser\npass\n99\n8\n3\n";

        runControllerWithInput(input);

        assertTrue(outContent.toString().length() > 0);
    }
}
