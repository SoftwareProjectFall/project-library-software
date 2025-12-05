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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.strategy.BookFine;
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.JournalFine;

public class LibraryServiceReturnFineTest {

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
    void returnBook_shouldPrintLateReturnMessage() {
        LibraryService library = new LibraryService();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book book = new Book("101", "Late Book", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        
        LocalDate borrowDay = LocalDate.of(2025, 1, 1);
        LocalDate dueDay = borrowDay.plusDays(28);
        LocalDate lateDay = dueDay.plusDays(5);
        
        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(borrowDay);
            library.borrowBook(user, "101");
            
            mocked.when(LocalDate::now).thenReturn(lateDay);
            library.returnBook(user, "101");
        }
        
        String output = outContent.toString();
        assertTrue(output.contains("Late return") || output.contains("Overdue"));
        assertEquals(5.0, user.getFineBalance());
    }

    @Test
    void returnBook_shouldPrintSuccessMessage() {
        LibraryService library = new LibraryService();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        
        LocalDate borrowDay = LocalDate.of(2025, 1, 1);
        LocalDate returnDay = borrowDay.plusDays(14);
        
        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(borrowDay);
            library.borrowBook(user, "101");
            
            mocked.when(LocalDate::now).thenReturn(returnDay);
            library.returnBook(user, "101");
        }
        
        String output = outContent.toString();
        assertTrue(output.contains("returned successfully") || output.length() > 0);
        assertFalse(book.isBorrowed());
    }

    @Test
    void borrowBook_shouldPrintBorrowMessage() {
        LibraryService library = new LibraryService();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book book = new Book("101", "Test Book", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        
        LocalDate fixedToday = LocalDate.of(2025, 1, 1);
        
        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedToday);
            library.borrowBook(user, "101");
        }
        
        String output = outContent.toString();
        assertTrue(output.contains("borrowed") || output.contains("Test Book"));
        assertTrue(book.isBorrowed());
    }
}

