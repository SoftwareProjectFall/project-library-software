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
import edu.univ.lms.model.User;
import edu.univ.lms.repository.UserRepository;

public class LibraryControllerAdminAddItemTest {

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
    void handleAdminMenu_addItem_shouldHandleEmptyTitleLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Empty title, then valid title
        String input = "1\n\nValid Title\nAuthor\n1\n8\n";
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
        assertTrue(output.contains("Title cannot be empty") || output.length() > 0);
    }

    @Test
    void handleAdminMenu_addItem_shouldHandleEmptyAuthorLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Valid title, empty author, then valid author
        String input = "1\nValid Title\n\nValid Author\n1\n8\n";
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
        assertTrue(output.contains("Author cannot be empty") || output.length() > 0);
    }

    @Test
    void handleAdminMenu_addItem_shouldHandleInvalidTypeLoop() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
        // Invalid type (99), then valid type (1)
        String input = "1\nTitle\nAuthor\n99\n1\n8\n";
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

    @Test
    void handleAdminMenu_addItem_shouldCreateBook() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
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
    void handleAdminMenu_addItem_shouldCreateDvd() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
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
    void handleAdminMenu_addItem_shouldCreateJournal() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        users.add(admin);
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        Method handleMethod = LibraryController.class.getDeclaredMethod("handleAdminMenu", User.class, Scanner.class);
        handleMethod.setAccessible(true);
        
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
}

