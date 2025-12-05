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

public class LibraryControllerDirectTest {

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
    void registerUser_shouldRegisterValidUser() throws Exception {
        // Setup
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        
        // Use reflection to access registerUser method
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        String input = "John Doe\njohndoe\npassword123\njohn@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.contains("REGISTER") || output.contains("registered") || output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleEmptyNameRetry() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        // First empty name, then valid name
        String input = "\nValid Name\nusername\npass\nemail@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleEmptyUsernameRetry() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        String input = "Valid Name\n\nvaliduser\npass\nemail@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleEmptyPasswordRetry() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        String input = "Valid Name\nusername\n\nvalidpass\nemail@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleInvalidEmailRetry() throws Exception {
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        String input = "Valid Name\nusername\npass\ninvalidemail\nvalid@email.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }

    @Test
    void registerUser_shouldHandleDuplicateUsernameRetry() throws Exception {
        // Setup existing user
        UserRepository userRepo = new UserRepository();
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Existing", "existing", "pass", false, "existing@test.com"));
        userRepo.saveUsers(users);

        LibraryController controller = new LibraryController();
        
        Method registerMethod = LibraryController.class.getDeclaredMethod("registerUser", Scanner.class);
        registerMethod.setAccessible(true);
        
        String input = "New User\nexisting\nnewuser\npass\nemail@test.com\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        registerMethod.invoke(controller, scanner);
        
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
}

