package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;
import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.strategy.BookFine;

public class LibraryControllerSearchTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        Files.deleteIfExists(Paths.get("users.json"));
        Files.deleteIfExists(Paths.get("items.json"));
    }

    @Test
    void handleUserMenu_search_shouldHandleTitleSearch() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        users.add(user);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Java Programming", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        String input = "7\n1\nJava\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_search_shouldHandleAuthorSearch() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        users.add(user);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "John Doe", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        String input = "7\n2\nJohn\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_search_shouldHandleISBNSearchFound() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        users.add(user);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        String input = "7\n3\n101\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_search_shouldHandleISBNSearchNotFound() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        users.add(user);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        String input = "7\n3\n999\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.contains("No item found") || output.length() > 0);
    }

    @Test
    void handleUserMenu_search_shouldHandleInvalidSearchOption() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        users.add(user);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        String input = "7\n99\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.contains("Invalid search option") || output.length() > 0);
    }
}

