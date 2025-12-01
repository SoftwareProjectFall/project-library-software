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

public class SaveLoadManagerTest {

    private static final String ITEMS_FILE = "items.json";
    private static final String USERS_FILE = "users.json";

    @AfterEach
    void cleanUp() throws Exception {
        // Delete test files/directories after each test
        Path itemsPath = Paths.get(ITEMS_FILE);
        if (Files.exists(itemsPath)) Files.delete(itemsPath);

        Path usersPath = Paths.get(USERS_FILE);
        if (Files.exists(usersPath)) Files.delete(usersPath);
    }

    //                     loadItems Tests

    @Test
    void loadItems_fileNotFound_returnsEmptyList() throws Exception {
        // Ensure file doesn't exist
        Files.deleteIfExists(Paths.get(ITEMS_FILE));

        List<Book> list = SaveLoadManager.loadItems();

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    void loadItems_validJson_loadsBooksAndRebuildsStrategy() throws Exception {
        // Valid JSON input
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

        List<Book> list = SaveLoadManager.loadItems();

        assertEquals(1, list.size());
        Book b = list.get(0);

        assertEquals("123", b.getIsbn());
        assertEquals("Test Book", b.getTitle());
        assertEquals("Author", b.getAuthor());
        assertEquals("BOOK", b.getItemType());
    }

    @Test
    void loadItems_corruptedJson_returnsEmptyList() throws Exception {
        // Broken/invalid JSON
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.write("this-is-not-json");
        }

        List<Book> list = SaveLoadManager.loadItems();

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    //                     loadUsers Tests

    @Test
    void loadUsers_fileNotFound_returnsEmptyList() throws Exception {
        // Ensure file doesn't exist
        Files.deleteIfExists(Paths.get(USERS_FILE));

        List<User> list = SaveLoadManager.loadUsers();

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    void loadUsers_validJson_loadsUsersCorrectly() throws Exception {
        // Valid JSON input
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

        List<User> list = SaveLoadManager.loadUsers();

        assertEquals(1, list.size());
        User u = list.get(0);

        assertEquals("1", u.getUserId());
        assertEquals("Mahmoud", u.getName());
        assertEquals("mahmoud", u.getUsername());
        assertEquals("m@test.com", u.getEmail());
        assertFalse(u.isAdmin());
        assertEquals(0.0, u.getFineBalance());
    }

    @Test
    void loadUsers_corruptedJson_returnsEmptyList() throws Exception {
        // Broken/invalid JSON
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.write("not-json-at-all");
        }

        List<User> list = SaveLoadManager.loadUsers();

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    //                     saveItems Tests

    @Test
    void saveItems_shouldCreateJsonFileWithContent() throws Exception {
        // Save valid book list
        Book b = new Book("1", "Saved Book", "Author", new BookFine());
        List<Book> books = List.of(b);

        SaveLoadManager.saveItems(books);

        assertTrue(Files.exists(Paths.get(ITEMS_FILE)));
        String content = Files.readString(Paths.get(ITEMS_FILE));
        assertTrue(content.contains("Saved Book"));
        assertTrue(content.contains("\"isbn\":\"1\""));
    }

    @Test
    void saveItems_whenIOException_shouldTriggerCatchBlock() throws Exception {
        // Create DIRECTORY with same name to force FileWriter failure
        Files.createDirectory(Paths.get(ITEMS_FILE));

        Book b = new Book("1", "Error Book", "Author", new BookFine());
        List<Book> books = List.of(b);

        // Must not throw, catch block should handle it
        assertDoesNotThrow(() -> SaveLoadManager.saveItems(books));
    }

    //                     saveUsers Tests

    @Test
    void saveUsers_shouldCreateJsonFileWithContent() throws Exception {
        // Save valid user list
        User u = new User("1", "Mahmoud", "mahmoud", "1234", false, "m@test.com");
        List<User> users = List.of(u);

        SaveLoadManager.saveUsers(users);

        assertTrue(Files.exists(Paths.get(USERS_FILE)));
        String content = Files.readString(Paths.get(USERS_FILE));
        assertTrue(content.contains("Mahmoud"));
        assertTrue(content.contains("\"userId\":\"1\""));
    }

    @Test
    void saveUsers_whenIOException_shouldTriggerCatchBlock() throws Exception {
        // Force FileWriter to fail by creating a directory instead of a file
        Files.createDirectory(Paths.get(USERS_FILE));

        User u = new User("1", "Error User", "user", "1234", false, "u@test.com");
        List<User> users = List.of(u);

        // Should not throw because catch handles it
        assertDoesNotThrow(() -> SaveLoadManager.saveUsers(users));
    }
}
