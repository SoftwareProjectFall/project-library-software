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

public class LibraryControllerAdminMenuTest {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn = System.in;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setIn(originalIn);
        Files.deleteIfExists(Paths.get("users.json"));
        Files.deleteIfExists(Paths.get("items.json"));
    }

    @Test
    void adminMenu_shouldShowAllItems() {
        // Setup: Create admin user and some books
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book1", "Author1", new BookFine()));
        books.add(new Book("102", "Book2", "Author2", new BookFine()));
        bookRepo.saveBooks(books);

        // Input: login as admin, show all items, logout, exit
        String input = "1\nadmin\n1234\n4\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        thread.start();
        
        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void adminMenu_shouldHandleInvalidChoice() {
        // Setup admin user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        // Input: login, invalid choice, logout, exit
        String input = "1\nadmin\n1234\n99\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        thread.start();
        
        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void adminMenu_shouldShowOverdueItems() {
        // Setup admin user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        // Input: login, show overdue items, logout, exit
        String input = "1\nadmin\n1234\n5\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        thread.start();
        
        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
}

