package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.strategy.BookFine;

/**
 * Tests that repository classes behave safely when IO or JSON errors occur.
 * The goal is to make sure they never crash and always return a safe list.
 */
public class RepositoryExceptionTest {

    // Test files created in the project root (they are deleted after each test)
    private static final String ITEMS_FILE = "items.json";
    private static final String USERS_FILE = "users.json";

    @AfterEach
    void cleanUp() throws Exception {
        // Remove temporary test files created by these tests
        Files.deleteIfExists(Paths.get(ITEMS_FILE));
        Files.deleteIfExists(Paths.get(USERS_FILE));
    }

    @Test
    void bookRepository_saveBooks_shouldHandleIOException() throws Exception {
        // Arrange: create a directory with the same name to force an IOException
        Files.createDirectories(Paths.get(ITEMS_FILE));

        BookRepository repository = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("1", "Test", "Author", new BookFine()));

        // Act & Assert: repository should handle the exception internally
        assertDoesNotThrow(() -> repository.saveBooks(books));

        // Clean up the directory so next tests are not affected
        Files.deleteIfExists(Paths.get(ITEMS_FILE));
    }

    @Test
    void bookRepository_loadBooks_shouldHandleJsonParseException() throws Exception {
        // Arrange: write invalid JSON to simulate a corrupted items file
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write("{invalid json}");
        }

        BookRepository repository = new BookRepository();

        // Act: loading books should not throw and should fall back to a safe list
        List<Book> result = repository.loadBooks();

        // Assert: result is non-null and contains at least one book (default/seeded)
        assertNotNull(result, "Repository should never return null when JSON is invalid");
        assertFalse(result.isEmpty(),
                "Repository should return at least one default book when JSON parsing fails");
    }

    @Test
    void bookRepository_loadBooks_shouldHandleNullArray() throws Exception {
        // Arrange: write "null" to simulate a null array in the JSON file
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write("null");
        }

        BookRepository repository = new BookRepository();

        // Act
        List<Book> result = repository.loadBooks();

        // Assert: still get a non-null, non-empty safe list
        assertNotNull(result, "Repository should never return null when file content is null");
        assertFalse(result.isEmpty(),
                "Repository should return at least one default book when file content is null");
    }

    @Test
    void userRepository_saveUsers_shouldHandleIOException() throws Exception {
        // Arrange: create a directory with the same name to force an IOException
        Files.createDirectories(Paths.get(USERS_FILE));

        UserRepository repository = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Test", "test", "pass", false, "test@test.com"));

        // Act & Assert: repository should handle the exception internally
        assertDoesNotThrow(() -> repository.saveUsers(users));

        // Clean up the directory so next tests are not affected
        Files.deleteIfExists(Paths.get(USERS_FILE));
    }

    @Test
    void userRepository_loadUsers_shouldHandleJsonParseException() throws Exception {
        // Arrange: write invalid JSON to simulate a corrupted users file
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write("{invalid json}");
        }

        UserRepository repository = new UserRepository();

        // Act: loading users should not throw and should fall back to a safe list
        List<User> result = repository.loadUsers();

        // Assert: result is non-null and contains at least one user (default/seeded)
        assertNotNull(result, "Repository should never return null when JSON is invalid");
        assertFalse(result.isEmpty(),
                "Repository should return at least one default user when JSON parsing fails");
    }

    @Test
    void userRepository_loadUsers_shouldHandleNullArray() throws Exception {
        // Arrange: write "null" to simulate a null array in the JSON file
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write("null");
        }

        UserRepository repository = new UserRepository();

        // Act
        List<User> result = repository.loadUsers();

        // Assert: still get a non-null, non-empty safe list
        assertNotNull(result, "Repository should never return null when file content is null");
        assertFalse(result.isEmpty(),
                "Repository should return at least one default user when file content is null");
    }
}
