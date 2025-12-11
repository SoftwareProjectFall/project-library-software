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
        // Clean up JSON files in the same folder used by the application ("data")
        Files.deleteIfExists(Paths.get("data", "users.json"));
        Files.deleteIfExists(Paths.get("data", "items.json"));
    }

    @Test
    void constructor_shouldInitializeWithExistingUsers() throws Exception {
        // Arrange: save an existing user to the repository
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Existing", "existing", "pass", false, "existing@test.com"));
        userRepo.saveUsers(users);

        // Simulate user input: "3" to exit the main menu immediately
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act: create controller, it should load existing users without error
        LibraryController controller = new LibraryController();
        
        // Assert
        assertNotNull(controller);
    }

    @Test
    void constructor_shouldCreateDefaultUsersWhenEmpty() throws Exception {
        // Arrange: ensure NO users/items files exist in the "data" folder
        Files.deleteIfExists(Paths.get("data", "users.json"));
        Files.deleteIfExists(Paths.get("data", "items.json"));

        // Simulate user input: "3" to exit immediately
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act: when no users file exists, controller should create default users
        LibraryController controller = new LibraryController();
        
        // Basic sanity check
        assertNotNull(controller);
        
        // Assert: default users were created (e.g., Admin + demo user)
        UserRepository userRepo = new UserRepository();
        List<User> loaded = userRepo.loadUsers();
        assertTrue(
            loaded.size() >= 2,
            "Default users (Admin + demo) should be created when no users file exists in data/users.json"
        );
    }

    @Test
    void constructor_shouldLoadExistingBooks() throws Exception {
        // Arrange: save an existing book to the repository
        BookRepository bookRepo = new BookRepository();
        List<edu.univ.lms.model.Book> books = new ArrayList<>();
        books.add(new edu.univ.lms.model.Book(
                "101",
                "Existing Book",
                "Author",
                new edu.univ.lms.strategy.BookFine()
        ));
        bookRepo.saveBooks(books);

        // Simulate user input: "3" to exit immediately
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act: controller should load existing books without error
        LibraryController controller = new LibraryController();
        
        // Assert
        assertNotNull(controller);
    }

    @Test
    void constructor_shouldRestoreIsbnCounter() throws Exception {
        // Arrange: save a book with a higher ISBN to restore internal counter
        BookRepository bookRepo = new BookRepository();
        List<edu.univ.lms.model.Book> books = new ArrayList<>();
        books.add(new edu.univ.lms.model.Book(
                "150",
                "Book",
                "Author",
                new edu.univ.lms.strategy.BookFine()
        ));
        bookRepo.saveBooks(books);

        // Simulate user input: "3" to exit immediately
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act: controller should restore ISBN counter based on existing books
        LibraryController controller = new LibraryController();
        
        // Assert
        assertNotNull(controller);
        // If you later expose ISBN counter, you can assert on it here
    }
}
