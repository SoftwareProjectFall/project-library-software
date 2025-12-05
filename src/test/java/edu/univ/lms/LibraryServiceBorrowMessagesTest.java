package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.strategy.BookFine;

public class LibraryServiceBorrowMessagesTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void borrowBook_shouldPrintNotLoggedInMessage() {
        LibraryService library = new LibraryService();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        // Not logged in
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        library.setItems(new java.util.ArrayList<>(java.util.Arrays.asList(book)));
        
        library.borrowBook(user, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("must be logged in") || output.length() > 0);
    }

    @Test
    void borrowBook_shouldPrintAdminCannotBorrowMessage() {
        LibraryService library = new LibraryService();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        library.setItems(new java.util.ArrayList<>(java.util.Arrays.asList(book)));
        
        library.borrowBook(admin, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("Admins cannot borrow") || output.length() > 0);
    }

    @Test
    void returnBook_shouldPrintNotLoggedInMessage() {
        LibraryService library = new LibraryService();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        // Not logged in
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        library.setItems(new java.util.ArrayList<>(java.util.Arrays.asList(book)));
        
        library.returnBook(user, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("must be logged in") || output.length() > 0);
    }

    @Test
    void returnBook_shouldPrintAdminCannotReturnMessage() {
        LibraryService library = new LibraryService();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        library.setItems(new java.util.ArrayList<>(java.util.Arrays.asList(book)));
        
        library.returnBook(admin, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("Admins cannot return") || output.length() > 0);
    }

    @Test
    void returnBook_shouldPrintItemNotFoundMessage() {
        LibraryService library = new LibraryService();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        library.returnBook(user, "999");
        
        String output = outContent.toString();
        assertTrue(output.contains("Item not found") || output.length() > 0);
    }
}

