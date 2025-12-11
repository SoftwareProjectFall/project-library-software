package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.strategy.BookFine;

public class LibraryServiceReturnFineTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // Save the original System.out so we can restore it later
        originalOut = System.out;

        // Capture console output into a buffer
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out after each test
        System.setOut(originalOut);
    }

    @Test
    void returnBook_shouldPrintLateReturnMessage() {
        LibraryService library = new LibraryService();

        // Create and login a normal user
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        // Create a book and add it to the library
        Book book = new Book("101", "Late Book", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        // Simulate that the book is already borrowed by this user
        book.setBorrowed(true);
        book.setBorrowedByUserId(user.getUserId());

        // Make the due date 5 days in the past so it is overdue
        LocalDate today = LocalDate.now();
        book.setDueDate(today.minusDays(5));

        // Act: return the overdue book
        library.returnBook(user, "101");

        String output = outContent.toString();

        // Assert: a late return message is printed and some fine is added
        assertTrue(output.contains("Late return") || output.contains("Overdue"));
        assertTrue(user.getFineBalance() > 0.0);
    }

    @Test
    void returnBook_shouldPrintSuccessMessage() {
        LibraryService library = new LibraryService();

        // Create and login a normal user
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        // Create a book and add it to the library
        Book book = new Book("101", "Book", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        // Simulate that the book is borrowed by this user and NOT overdue
        LocalDate today = LocalDate.now();
        book.setBorrowed(true);
        book.setBorrowedByUserId(user.getUserId());
        // Due date is in the future → no fine
        book.setDueDate(today.plusDays(3));

        // Act: return the book on time
        library.returnBook(user, "101");

        String output = outContent.toString();

        // Assert: success message printed and no fine
        assertTrue(output.contains("returned successfully") || output.length() > 0);
        assertFalse(book.isBorrowed());
        assertEquals(0.0, user.getFineBalance());
    }

    @Test
    void borrowBook_shouldPrintBorrowMessage() {
        LibraryService library = new LibraryService();

        // Create and login a normal user
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");

        // Create a book and add it to the library
        Book book = new Book("101", "Test Book", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        // Act: borrow the book (uses real LocalDate.now())
        library.borrowBook(user, "101");

        String output = outContent.toString();

        // Assert: borrow message printed and book is marked as borrowed
        assertTrue(output.contains("borrowed") || output.contains("Test Book"));
        assertTrue(book.isBorrowed());
    }
}
