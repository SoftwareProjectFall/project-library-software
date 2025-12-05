package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.strategy.BookFine;
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.JournalFine;

public class BookEdgeCasesTest {

    @Test
    void constructorWithEmptyIsbn_shouldWork() {
        Book book = new Book("", "Title", "Author");
        
        assertEquals("", book.getIsbn());
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
    }

    @Test
    void setFineType_shouldUpdateFineType() {
        Book book = new Book("1", "Test", "Author");
        
        book.setFineType("DVD");
        book.rebuildFineStrategy();
        
        assertEquals("DVD", book.getItemType());
    }

    @Test
    void calculateFine_withZeroDays_shouldReturnZero() {
        Book book = new Book("1", "Test", "Author", new BookFine());
        
        double fine = book.calculateFine(0);
        
        assertEquals(0.0, fine);
    }

    @Test
    void calculateFine_withNegativeDays_shouldStillCalculate() {
        Book book = new Book("1", "Test", "Author", new BookFine());
        
        // Even with negative days, strategy will calculate (though it shouldn't happen in practice)
        double fine = book.calculateFine(-5);
        
        assertEquals(-5.0, fine); // BookFine = 1 * -5
    }

    @Test
    void getItemType_shouldReturnCorrectTypeForJournal() {
        Book book = new Book("1", "Journal", "Editor", new JournalFine());
        
        assertEquals("JOURNAL", book.getItemType());
    }

    @Test
    void getItemType_shouldReturnBookForDefault() {
        Book book = new Book("1", "Book", "Author");
        
        assertEquals("BOOK", book.getItemType());
    }

    @Test
    void setters_shouldUpdateAllFields() {
        Book book = new Book("1", "Old", "OldAuthor");
        LocalDate date = LocalDate.of(2025, 1, 1);
        
        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setIsbn("999");
        book.setBorrowed(true);
        book.setBorrowedByUserId("U1");
        book.setBorrowDate(date);
        book.setDueDate(date.plusDays(7));
        
        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals("999", book.getIsbn());
        assertTrue(book.isBorrowed());
        assertEquals("U1", book.getBorrowedByUserId());
        assertEquals(date, book.getBorrowDate());
        assertEquals(date.plusDays(7), book.getDueDate());
    }

    @Test
    void rebuildFineStrategy_withUnknownType_shouldDefaultToBook() {
        Book book = new Book();
        book.setFineType("UNKNOWN");
        
        book.rebuildFineStrategy();
        
        assertEquals("BOOK", book.getItemType());
    }

    @Test
    void getFineType_shouldReturnBookAfterBasicConstructor() {
        Book book = new Book("1", "Test", "Author");
        
        assertEquals("BOOK", book.getFineType());
    }

    @Test
    void getFineType_shouldReturnBookAfterBookFineConstructor() {
        Book book = new Book("1", "Test", "Author", new BookFine());
        
        assertEquals("BOOK", book.getFineType());
    }

    @Test
    void getFineType_shouldReturnDvdAfterDvdFineConstructor() {
        Book book = new Book("1", "Test", "Author", new DvdFine());
        
        assertEquals("DVD", book.getFineType());
    }

    @Test
    void getFineType_shouldReturnJournalAfterJournalFineConstructor() {
        Book book = new Book("1", "Test", "Author", new JournalFine());
        
        assertEquals("JOURNAL", book.getFineType());
    }

    @Test
    void getFineType_shouldReturnSetValue() {
        Book book = new Book("1", "Test", "Author");
        
        book.setFineType("DVD");
        assertEquals("DVD", book.getFineType());
        
        book.setFineType("JOURNAL");
        assertEquals("JOURNAL", book.getFineType());
        
        book.setFineType("BOOK");
        assertEquals("BOOK", book.getFineType());
    }

    @Test
    void getFineType_shouldReturnNullAfterDefaultConstructor() {
        Book book = new Book();
        
        // fineType is null initially for default constructor
        assertNull(book.getFineType());
    }

    @Test
    void getFineType_shouldReturnValueAfterSetAndGet() {
        Book book = new Book();
        
        book.setFineType("DVD");
        String fineType = book.getFineType();
        
        assertEquals("DVD", fineType);
    }
}

