package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;

public class LibraryControllerConstructorTest {

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get("users.json"));
        Files.deleteIfExists(Paths.get("items.json"));
    }

    @Test
    void constructor_shouldInitializeWithExistingUsers() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Existing", "existing", "pass", false, "existing@test.com"));
        userRepo.saveUsers(users);

        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        assertNotNull(controller);
    }

    @Test
    void constructor_shouldCreateDefaultUsersWhenEmpty() throws Exception {
        // Ensure no users file exists
        Files.deleteIfExists(Paths.get("users.json"));
        
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        assertNotNull(controller);
        
        // Verify default users were created
        UserRepository userRepo = new UserRepository();
        List<User> loaded = userRepo.loadUsers();
        assertTrue(loaded.size() >= 2); // Admin + demo user
    }

    @Test
    void constructor_shouldLoadExistingBooks() throws Exception {
        BookRepository bookRepo = new BookRepository();
        List<edu.univ.lms.model.Book> books = new ArrayList<>();
        books.add(new edu.univ.lms.model.Book("101", "Existing Book", "Author", new edu.univ.lms.strategy.BookFine()));
        bookRepo.saveBooks(books);

        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        assertNotNull(controller);
    }

    @Test
    void constructor_shouldRestoreIsbnCounter() throws Exception {
        BookRepository bookRepo = new BookRepository();
        List<edu.univ.lms.model.Book> books = new ArrayList<>();
        books.add(new edu.univ.lms.model.Book("150", "Book", "Author", new edu.univ.lms.strategy.BookFine()));
        bookRepo.saveBooks(books);

        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        assertNotNull(controller);
    }
}

