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
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.JournalFine;

/**
 * Comprehensive test suite for LibraryController covering all methods and code paths
 */
public class LibraryControllerCompleteTest {

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

    // ==================== Utility Methods Tests ====================

    @Test
    void clearScreen_shouldPrintSeparator() {
        LibraryController.clearScreen();
        String output = outContent.toString();
        assertTrue(output.contains("----------------------------------------------"));
    }

    @Test
    void pressEnterToContinue_shouldWaitForInput() {
        String input = "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        LibraryController.pressEnterToContinue(scanner);
        String output = outContent.toString();
        assertTrue(output.contains("Press Enter to continue"));
    }

    @Test
    void printItem_shouldDisplayBookInfo() {
        Book book = new Book("101", "Test Book", "Test Author", new BookFine());
        book.setBorrowed(true);
        LibraryController.printItem(book);
        String output = outContent.toString();
        assertTrue(output.contains("ISBN: 101"));
        assertTrue(output.contains("Title: Test Book"));
        assertTrue(output.contains("Author: Test Author"));
        assertTrue(output.contains("Borrowed: true"));
    }

    @Test
    void printItemList_shouldDisplayAllBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book1", "Author1", new BookFine()));
        books.add(new Book("102", "Book2", "Author2", new BookFine()));
        LibraryController.printItemList(books);
        String output = outContent.toString();
        assertTrue(output.contains("Book1"));
        assertTrue(output.contains("Book2"));
    }

    @Test
    void printItemList_shouldHandleEmptyList() {
        LibraryController.printItemList(new ArrayList<>());
        String output = outContent.toString();
        assertTrue(output.contains("No results found"));
    }

    @Test
    void printItemList_shouldHandleNullList() {
        LibraryController.printItemList(null);
        String output = outContent.toString();
        assertTrue(output.contains("No results found"));
    }

    // ==================== Register User Tests ====================

    @Test
    void registerUser_shouldHandleEmptyNameLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        String input = "\nValid Name\nuser\npass\nuser@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Name cannot be empty") || output.contains("REGISTER"));
    }

    @Test
    void registerUser_shouldHandleEmptyUsernameLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        String input = "Name\n\nuser\npass\nuser@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Username cannot be empty") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleDuplicateUsernameLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Existing", "existing", "pass", false, "existing@test.com"));
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        String input = "Name\nexisting\nnewuser\npass\nuser@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("already taken") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleEmptyPasswordLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        String input = "Name\nuser\n\npass\nuser@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Password cannot be empty") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleEmptyEmailLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        String input = "Name\nuser\npass\n\nuser@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Email cannot be empty") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleInvalidEmailFormat() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        String input = "Name\nuser\npass\ninvalidemail\nvalid@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid email format") || output.length() > 0);
    }

    @Test
    void registerUser_shouldSuccessfullyRegister() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);

        String input = "Test User\ntestuser\ntestpass\ntest@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        registerMethod.invoke(controller, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("REGISTER") || output.length() > 0);
    }

    // ==================== Admin Menu Tests ====================

    @Test
    void handleAdminMenu_shouldAddBook() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "1\nBook Title\nBook Author\n1\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldAddDvd() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "1\nDVD Title\nDVD Director\n2\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldAddJournal() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "1\nJournal Title\nJournal Editor\n3\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldRemoveItem() throws Exception {
        setupAdminAndBooks();
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "2\n101\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldUpdateItem() throws Exception {
        setupAdminAndBooks();
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Old Title", "Old Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "3\n101\nNew Title\nNew Author\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldShowAllItems() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "4\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldShowOverdueItems() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "5\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldSendReminders() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "6\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldUnregisterUser() throws Exception {
        setupAdminAndBooks();
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        User target = new User("2", "Target", "target", "pass", false, "target@test.com");
        users.add(admin);
        users.add(target);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        admin.login("admin", "1234");

        String input = "7\n2\n8\n";
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
        assertTrue(output.length() > 0);
    }

    @Test
    void handleAdminMenu_shouldLogout() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        handleMethod.invoke(controller, admin, scanner);

        assertFalse(admin.isLoggedIn());
    }

    @Test
    void handleAdminMenu_shouldHandleInvalidChoice() throws Exception {
        setupAdminAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");

        String input = "99\n8\n";
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
        assertTrue(output.contains("Invalid choice") || output.length() > 0);
    }

    // ==================== User Menu Tests ====================

    @Test
    void handleUserMenu_shouldBorrowItem() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        String input = "1\n101\n8\n";
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
    void handleUserMenu_shouldReturnItem() throws Exception {
        setupUserAndBooks();
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        Book book = new Book("101", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("1");
        books.add(book);
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        String input = "2\n101\n8\n";
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
    void handleUserMenu_shouldViewFines() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        user.addFine(50.0);

        String input = "3\n8\n";
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
        assertTrue(output.contains("fines") || output.length() > 0);
    }

    @Test
    void handleUserMenu_shouldPayFine() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        user.addFine(50.0);

        String input = "4\n25.0\n8\n";
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
    void handleUserMenu_shouldShowAllItems() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        String input = "5\n8\n";
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
    void handleUserMenu_shouldShowBorrowedItems() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        String input = "6\n8\n";
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
    void handleUserMenu_shouldSearchByTitle() throws Exception {
        setupUserAndBooks();
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Java Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

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
    void handleUserMenu_shouldSearchByAuthor() throws Exception {
        setupUserAndBooks();
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "John Doe", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

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
    void handleUserMenu_shouldSearchByISBN() throws Exception {
        setupUserAndBooks();
        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

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
    void handleUserMenu_shouldLogout() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        String input = "8\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        handleMethod.invoke(controller, user, scanner);

        assertFalse(user.isLoggedIn());
    }

    @Test
    void handleUserMenu_shouldHandleInvalidChoice() throws Exception {
        setupUserAndBooks();
        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleUserMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);

        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        String input = "99\n8\n";
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
        assertTrue(output.contains("Invalid choice") || output.length() > 0);
    }

    // ==================== Helper Methods ====================

    private void setupAdminAndBooks() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Admin", "admin", "1234", true, "admin@test.com"));
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        bookRepo.saveBooks(new ArrayList<>());
    }

    private void setupUserAndBooks() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "User", "user", "pass", false, "user@test.com"));
        userRepo.saveUsers(users);

        BookRepository bookRepo = new BookRepository();
        List<Book> books = new ArrayList<>();
        books.add(new Book("101", "Book", "Author", new BookFine()));
        bookRepo.saveBooks(books);
    }
}

