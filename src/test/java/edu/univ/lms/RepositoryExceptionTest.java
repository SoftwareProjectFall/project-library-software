package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final String ITEMS_FILE = "items.json";
    private static final String USERS_FILE = "users.json";

    @AfterEach
    void cleanUp() throws Exception {
        Files.deleteIfExists(Paths.get(ITEMS_FILE));
        Files.deleteIfExists(Paths.get(USERS_FILE));
    }

    @Test
    void bookRepository_saveBooks_shouldHandleIOException() throws Exception {
        // Create a directory with the same name to force IOException
        Files.createDirectories(Paths.get(ITEMS_FILE));
        
        BookRepository repository = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("1", "Test", "Author", new BookFine()));
        
        // Should not throw, should handle exception gracefully
        assertDoesNotThrow(() -> repository.saveBooks(books));
        
        // Clean up
        Files.deleteIfExists(Paths.get(ITEMS_FILE));
    }

    @Test
    void bookRepository_loadBooks_shouldHandleJsonParseException() throws Exception {
        // Create invalid JSON
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write("{invalid json}");
        }
        
        BookRepository repository = new BookRepository();
        
        // Should return empty list, not throw
        List<Book> result = repository.loadBooks();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void bookRepository_loadBooks_shouldHandleNullArray() throws Exception {
        // Create JSON with null array
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write("null");
        }
        
        BookRepository repository = new BookRepository();
        
        List<Book> result = repository.loadBooks();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void userRepository_saveUsers_shouldHandleIOException() throws Exception {
        // Create a directory with the same name to force IOException
        Files.createDirectories(Paths.get(USERS_FILE));
        
        UserRepository repository = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Test", "test", "pass", false, "test@test.com"));
        
        // Should not throw, should handle exception gracefully
        assertDoesNotThrow(() -> repository.saveUsers(users));
        
        // Clean up
        Files.deleteIfExists(Paths.get(USERS_FILE));
    }

    @Test
    void userRepository_loadUsers_shouldHandleJsonParseException() throws Exception {
        // Create invalid JSON
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write("{invalid json}");
        }
        
        UserRepository repository = new UserRepository();
        
        // Should return empty list, not throw
        List<User> result = repository.loadUsers();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void userRepository_loadUsers_shouldHandleNullArray() throws Exception {
        // Create JSON with null array
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write("null");
        }
        
        UserRepository repository = new UserRepository();
        
        List<User> result = repository.loadUsers();
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}

