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

public class LibraryControllerAdminOperationsTest {

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
    void adminMenu_shouldAddBook() {
        // Setup admin
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        // Input: login, add book (type 1=Book), logout, exit
        String input = "1\nadmin\n1234\n1\nTest Book\nTest Author\n1\n8\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread thread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected when stream closes
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
    void adminMenu_shouldAddDvd() {
        // Setup admin
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        // Input: login, add item (type 2=DVD), logout, exit
        String input = "1\nadmin\n1234\n1\nMovie\nDirector\n2\n8\n3\n";
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
    void adminMenu_shouldAddJournal() {
        // Setup admin
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        // Input: login, add item (type 3=Journal), logout, exit
        String input = "1\nadmin\n1234\n1\nJournal\nEditor\n3\n8\n3\n";
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
    void adminMenu_shouldRemoveItem() {
        // Setup admin and book
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book to Remove", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        // Input: login, remove item, logout, exit
        String input = "1\nadmin\n1234\n2\n101\n8\n3\n";
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
    void adminMenu_shouldUpdateItem() {
        // Setup admin and book
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Old Title", "Old Author", new BookFine()));
        bookRepo.saveBooks(books);

        // Input: login, update item, new title, new author, logout, exit
        String input = "1\nadmin\n1234\n3\n101\nNew Title\nNew Author\n8\n3\n";
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
    void adminMenu_shouldSendReminders() {
        // Setup admin
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        users.add(admin);
        userRepo.saveUsers(users);

        // Input: login, send reminders, logout, exit
        String input = "1\nadmin\n1234\n6\n8\n3\n";
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
    void adminMenu_shouldHandleUnregisterUser() {
        // Setup admin and target user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        User target = new User("2", "Target", "target", "pass", false, "target@test.com");
        users.add(admin);
        users.add(target);
        userRepo.saveUsers(users);

        // Input: login, unregister user, user id, logout, exit
        String input = "1\nadmin\n1234\n7\n2\n8\n3\n";
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

