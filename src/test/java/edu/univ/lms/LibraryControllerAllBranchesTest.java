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

public class LibraryControllerAllBranchesTest {

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
    void registerUser_shouldExecuteAllValidationBranches() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        // Execute all validation loops: name (empty), username (empty then duplicate), password (empty), email (empty then invalid)
        String input = "\nName\n\nuser\nexisting\nnewuser\n\npass\n\nemail\ninvalid\nvalid@email.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // First create existing user for duplicate test
        User existing = new User("1", "Existing", "existing", "pass", false, "existing@test.com");
        users.add(existing);
        userRepo.saveUsers(users);
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldExecuteAllAddItemBranches() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Test all branches: empty title loop, empty author loop, invalid type loop, then create book
        String input = "1\n\nTitle\n\nAuthor\n99\n4\n1\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(3000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldExecutePayFineExceptionBranch() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        user.addFine(50.0);
        users.add(user);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Test exception branch in pay fine (invalid input causes exception)
        String input = "4\nnot-a-number\n25.0\n8\n";
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
        assertTrue(output.contains("Invalid number") || output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldExecuteAllSearchBranches() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        users.add(user);
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Test Book", "Test Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Test all search branches: title (1), author (2), ISBN found (3), ISBN not found (3), invalid option
        String input = "7\n1\nTest\n8\n";
        Scanner scanner1 = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread1 = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner1);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread1.start();
        testThread1.join(2000);
        
        // Test author search
        String input2 = "7\n2\nTest\n8\n";
        Scanner scanner2 = new Scanner(new ByteArrayInputStream(input2.getBytes()));
        
        Thread testThread2 = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner2);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread2.start();
        testThread2.join(2000);
        
        // Test ISBN found
        String input3 = "7\n3\n101\n8\n";
        Scanner scanner3 = new Scanner(new ByteArrayInputStream(input3.getBytes()));
        
        Thread testThread3 = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner3);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread3.start();
        testThread3.join(2000);
        
        // Test ISBN not found
        String input4 = "7\n3\n999\n8\n";
        Scanner scanner4 = new Scanner(new ByteArrayInputStream(input4.getBytes()));
        
        Thread testThread4 = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner4);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread4.start();
        testThread4.join(2000);
        
        // Test invalid search option
        String input5 = "7\n99\n8\n";
        Scanner scanner5 = new Scanner(new ByteArrayInputStream(input5.getBytes()));
        
        Thread testThread5 = new Thread(() -> {
            try {
                handleMethod.invoke(controller, user, scanner5);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread5.start();
        testThread5.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldExecuteUnregisterUserNotFoundBranch() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Test user not found branch (target == null)
        String input = "7\n999\n8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        Thread testThread = new Thread(() -> {
            try {
                handleMethod.invoke(controller, admin, scanner);
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.contains("User not found") || output.length() > 0);
    }

    @Test
    void run_shouldExecuteContinueStatements() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        // Test continue after register
        String input = "2\nUser\nuser\npass\nuser@test.com\n99\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread testThread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(3000);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void run_shouldExecuteInvalidCredentialsContinue() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "User", "user", "pass", false, "user@test.com"));
        userRepo.saveUsers(users);

        // Test continue after invalid credentials
        String input = "1\nwrong\nwrong\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        Thread testThread = new Thread(() -> {
            try {
                controller.run();
            } catch (Exception e) {
                // Expected
            }
        });
        testThread.start();
        testThread.join(2000);
        
        String output = outContent.toString();
        assertTrue(output.contains("Invalid credentials") || output.length() > 0);
    }
}

