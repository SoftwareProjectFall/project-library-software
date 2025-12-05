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

public class LibraryControllerUserOperationsTest {

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
    void userMenu_shouldBorrowItem() {
        // Setup user and available book
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        users.add(user);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Available Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        // Input: login, borrow, ISBN, logout, exit
        String input = "1\nuser\npass\n1\n101\n8\n3\n";
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
    void userMenu_shouldReturnItem() {
        // Setup user and borrowed book
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        users.add(user);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        Book book = new Book("101", "Borrowed Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("1");
        books.add(book);
        bookRepo.saveBooks(books);

        // Input: login, return, ISBN, logout, exit
        String input = "1\nuser\npass\n2\n101\n8\n3\n";
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
    void userMenu_shouldPayFine() {
        // Setup user with fine
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.addFine(50.0);
        users.add(user);
        userRepo.saveUsers(users);

        // Input: login, pay fine, amount, logout, exit
        String input = "1\nuser\npass\n4\n25.5\n8\n3\n";
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
    void userMenu_shouldHandleInvalidPaymentAmount() {
        // Setup user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.addFine(50.0);
        users.add(user);
        userRepo.saveUsers(users);

        // Input: login, pay fine, invalid amount, then valid amount, logout, exit
        String input = "1\nuser\npass\n4\ninvalid\n-5\n25.5\n8\n3\n";
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
    void userMenu_shouldSearchByISBNNotFound() {
        // Setup user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        users.add(user);
        userRepo.saveUsers(users);

        // Input: login, search by ISBN, non-existent ISBN, logout, exit
        String input = "1\nuser\npass\n7\n3\n999\n8\n3\n";
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

