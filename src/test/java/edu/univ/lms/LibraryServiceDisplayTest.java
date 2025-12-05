package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.strategy.BookFine;

public class LibraryServiceDisplayTest {

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
    void showAllBooks_shouldDisplayBooksWithOverdueStatus() {
        LibraryService library = new LibraryService();
        
        Book available = new Book("101", "Available", "Author", new BookFine());
        Book borrowed = new Book("102", "Borrowed", "Author", new BookFine());
        borrowed.setBorrowed(true);
        borrowed.setBorrowedByUserId("1");
        borrowed.setDueDate(LocalDate.now().plusDays(5));
        
        Book overdue = new Book("103", "Overdue", "Author", new BookFine());
        overdue.setBorrowed(true);
        overdue.setBorrowedByUserId("2");
        overdue.setDueDate(LocalDate.now().minusDays(3));
        
        library.setItems(new ArrayList<>(Arrays.asList(available, borrowed, overdue)));
        library.showAllBooks();
        
        String output = outContent.toString();
        assertTrue(output.contains("Available"));
        assertTrue(output.contains("Borrowed"));
        assertTrue(output.contains("OVERDUE"));
    }

    @Test
    void showBorrowedBooks_shouldDisplayUserBorrowedBooks() {
        LibraryService library = new LibraryService();
        
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book borrowed1 = new Book("101", "Book1", "Author", new BookFine());
        borrowed1.setBorrowed(true);
        borrowed1.setBorrowedByUserId("1");
        borrowed1.setDueDate(LocalDate.now().plusDays(5));
        
        Book borrowed2 = new Book("102", "Book2", "Author", new BookFine());
        borrowed2.setBorrowed(true);
        borrowed2.setBorrowedByUserId("1");
        borrowed2.setDueDate(LocalDate.now().plusDays(10));
        
        Book otherBorrowed = new Book("103", "Other", "Author", new BookFine());
        otherBorrowed.setBorrowed(true);
        otherBorrowed.setBorrowedByUserId("2");
        otherBorrowed.setDueDate(LocalDate.now().plusDays(5));
        
        library.setItems(new ArrayList<>(Arrays.asList(borrowed1, borrowed2, otherBorrowed)));
        library.showBorrowedBooks(user);
        
        String output = outContent.toString();
        assertTrue(output.contains("Book1"));
        assertTrue(output.contains("Book2"));
        assertFalse(output.contains("Other"));
    }

    @Test
    void showOverdueBooks_shouldDisplayOnlyOverdueBooks() {
        LibraryService library = new LibraryService();
        
        Book overdue1 = new Book("101", "Overdue1", "Author", new BookFine());
        overdue1.setBorrowed(true);
        overdue1.setBorrowedByUserId("1");
        overdue1.setDueDate(LocalDate.now().minusDays(5));
        
        Book overdue2 = new Book("102", "Overdue2", "Author", new BookFine());
        overdue2.setBorrowed(true);
        overdue2.setBorrowedByUserId("2");
        overdue2.setDueDate(LocalDate.now().minusDays(1));
        
        Book notOverdue = new Book("103", "OnTime", "Author", new BookFine());
        notOverdue.setBorrowed(true);
        notOverdue.setBorrowedByUserId("3");
        notOverdue.setDueDate(LocalDate.now().plusDays(5));
        
        library.setItems(new ArrayList<>(Arrays.asList(overdue1, overdue2, notOverdue)));
        library.showOverdueBooks();
        
        String output = outContent.toString();
        assertTrue(output.contains("Overdue1"));
        assertTrue(output.contains("Overdue2"));
        assertFalse(output.contains("OnTime"));
    }
}

