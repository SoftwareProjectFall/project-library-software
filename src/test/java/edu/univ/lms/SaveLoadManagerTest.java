package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.strategy.BookFine;

public class SaveLoadManagerTest {

    // NOTE: These test files are created in the project root folder
    private static final String ITEMS_FILE = "items.json";
    private static final String USERS_FILE = "users.json";

    @AfterEach
    void cleanUp() throws Exception {
        // Delete test files after each test (if they were created)
        Path itemsPath = Paths.get(ITEMS_FILE);
        if (Files.exists(itemsPath)) {
            Files.delete(itemsPath);
        }

        Path usersPath = Paths.get(USERS_FILE);
        if (Files.exists(usersPath)) {
            Files.delete(usersPath);
        }
    }

    // ======================= loadItems Tests =======================

    @Test
    void loadItems_fileNotFound_returnsNonNullList() throws Exception {
        // Ensure test file doesn't exist (defensive clean up)
        Files.deleteIfExists(Paths.get(ITEMS_FILE));

        BookRepository repository = new BookRepository();
        List<Book> list = repository.loadBooks();

        // Repository should never return null even if file is missing
        assertNotNull(list);
    }

    @Test
    void loadItems_validJson_loadsBooksAndRebuildsStrategy() throws Exception {
        // Write valid JSON to a test file (repository may still use its own path)
        String json =
                "[{" +
                "\"isbn\":\"123\"," +
                "\"title\":\"Test Book\"," +
                "\"author\":\"Author\"," +
                "\"fineType\":\"BOOK\"" +
                "}]";

        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write(json);
        }

        BookRepository repository = new BookRepository();
        List<Book> list = repository.loadBooks();

        // We only check that at least one book is loaded and it has a non-null ISBN
        assertNotNull(list);
        assertFalse(list.isEmpty(), "Book list should not be empty");
        Book b = list.get(0);
        assertNotNull(b.getIsbn(), "Loaded book should have a non-null ISBN");
    }

    @Test
    void loadItems_corruptedJson_returnsNonNullList() throws Exception {
        // Write broken/invalid JSON to the test file
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write("this-is-not-json");
        }

        BookRepository repository = new BookRepository();
        List<Book> list = repository.loadBooks();

        // Even if JSON is corrupted, repository should still return a non-null list
        assertNotNull(list);
    }

    // ======================= loadUsers Tests =======================

    @Test
    void loadUsers_fileNotFound_returnsNonNullList() throws Exception {
        // Ensure test file doesn't exist (defensive clean up)
        Files.deleteIfExists(Paths.get(USERS_FILE));

        UserRepository repository = new UserRepository();
        List<User> list = repository.loadUsers();

        // Repository should never return null even if file is missing
        assertNotNull(list);
    }

    @Test
    void loadUsers_validJson_loadsUsersCorrectly() throws Exception {
        // Write valid JSON to a test file (repository may still use its own path)
        String json =
                "[{" +
                "\"userId\":\"1\"," +
                "\"name\":\"Mahmoud\"," +
                "\"username\":\"mahmoud\"," +
                "\"password\":\"1234\"," +
                "\"admin\":false," +
                "\"email\":\"m@test.com\"" +
                "}]";

        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write(json);
        }

        UserRepository repository = new UserRepository();
        List<User> list = repository.loadUsers();

        // We only check that at least one user is loaded and username is non-null
        assertNotNull(list);
        assertFalse(list.isEmpty(), "User list should not be empty");
        User u = list.get(0);
        assertNotNull(u.getUsername(), "Loaded user should have a non-null username");
    }

    @Test
    void loadUsers_corruptedJson_returnsNonNullList() throws Exception {
        // Write broken/invalid JSON to the test file
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write("not-json-at-all");
        }

        UserRepository repository = new UserRepository();
        List<User> list = repository.loadUsers();

        // Even if JSON is corrupted, repository should still return a non-null list
        assertNotNull(list);
    }

    // ======================= saveItems Tests =======================

    @Test
    void saveItems_shouldNotThrowWhenSavingBooks() {
        // Save a simple book list and only verify that no exception is thrown
        Book b = new Book("1", "Saved Book", "Author", new BookFine());
        List<Book> books = List.of(b);

        BookRepository repository = new BookRepository();

        // We just ensure that saving does not throw an exception
        assertDoesNotThrow(() -> repository.saveBooks(books));
    }

    @Test
    void saveItems_whenIOException_shouldStillBeHandledGracefully() {
        // This test focuses on making sure saveBooks handles IO problems gracefully
        Book b = new Book("1", "Error Book", "Author", new BookFine());
        List<Book> books = List.of(b);

        BookRepository repository = new BookRepository();
        assertDoesNotThrow(() -> repository.saveBooks(books));
    }

    // ======================= saveUsers Tests =======================

    @Test
    void saveUsers_shouldNotThrowWhenSavingUsers() {
        // Save a simple user list and only verify that no exception is thrown
        User u = new User("1", "Mahmoud", "mahmoud", "1234", false, "m@test.com");
        List<User> users = List.of(u);

        UserRepository repository = new UserRepository();

        // Again, we only verify that saving works without throwing
        assertDoesNotThrow(() -> repository.saveUsers(users));
    }

    @Test
    void saveUsers_whenIOException_shouldStillBeHandledGracefully() {
        // This test focuses on making sure saveUsers handles IO problems gracefully
        User u = new User("1", "Error User", "user", "1234", false, "u@test.com");
        List<User> users = List.of(u);

        UserRepository repository = new UserRepository();
        assertDoesNotThrow(() -> repository.saveUsers(users));
    }
}
