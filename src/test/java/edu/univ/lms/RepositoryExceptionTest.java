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

public class RepositoryExceptionTest {

    // NOTE: These files are created in the project root for test purposes only
    private static final String ITEMS_FILE = "items.json";
    private static final String USERS_FILE = "users.json";

    @AfterEach
    void cleanUp() throws Exception {
        // Remove temporary files created by these tests
        Files.deleteIfExists(Paths.get(ITEMS_FILE));
        Files.deleteIfExists(Paths.get(USERS_FILE));
    }

    @Test
    void bookRepository_saveBooks_shouldHandleIOException() throws Exception {
        // Create a directory with the same name to force an IOException
        Files.createDirectories(Paths.get(ITEMS_FILE));

        BookRepository repository = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("1", "Test", "Author", new BookFine()));

        // The save operation should not throw even if an IOException happens internally
        assertDoesNotThrow(() -> repository.saveBooks(books));

        // Clean up the directory so other tests are not affected
        Files.deleteIfExists(Paths.get(ITEMS_FILE));
    }

    @Test
    void bookRepository_loadBooks_shouldHandleJsonParseException() throws Exception {
        // Create invalid JSON to simulate a corrupted items file
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write("{invalid json}");
        }

        BookRepository repository = new BookRepository();

        List<Book> result = repository.loadBooks();

        // We only verify that the method does not return null,
        // even if the underlying JSON file is invalid.
        assertNotNull(result);
    }

    @Test
    void bookRepository_loadBooks_shouldHandleNullArray() throws Exception {
        // Create JSON with null to simulate a null array from file
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write("null");
        }

        BookRepository repository = new BookRepository();

        List<Book> result = repository.loadBooks();

        // Again, we just check that the repository returns a non-null list
        // even if the file content is "null".
        assertNotNull(result);
    }

    @Test
    void userRepository_saveUsers_shouldHandleIOException() throws Exception {
        // Create a directory with the same name to force an IOException
        Files.createDirectories(Paths.get(USERS_FILE));

        UserRepository repository = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Test", "test", "pass", false, "test@test.com"));

        // The save operation should not throw even if an IOException happens internally
        assertDoesNotThrow(() -> repository.saveUsers(users));

        // Clean up the directory so other tests are not affected
        Files.deleteIfExists(Paths.get(USERS_FILE));
    }

    @Test
    void userRepository_loadUsers_shouldHandleJsonParseException() throws Exception {
        // Create invalid JSON to simulate a corrupted users file
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write("{invalid json}");
        }

        UserRepository repository = new UserRepository();

        List<User> result = repository.loadUsers();

        // Implementation may seed one or more default users.
        // Here we only assert that the result is not null.
        assertNotNull(result);
    }

    @Test
    void userRepository_loadUsers_shouldHandleNullArray() throws Exception {
        // Create JSON with null to simulate a null array from file
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write("null");
        }

        UserRepository repository = new UserRepository();

        List<User> result = repository.loadUsers();

        // Again, just make sure we never get a null list back.
        assertNotNull(result);
    }
}
