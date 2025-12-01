package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class BookTest {

    @Test
    void testBasicConstructor() {
        Book book = new Book("123", "Clean Code", "Robert Martin");

        assertEquals("123", book.getIsbn());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("BOOK", book.getItemType());
        assertFalse(book.isBorrowed());
    }

    @Test
    void testConstructorWithStrategy() {
        Book dvd = new Book("1", "Movie", "Dir", new DvdFine());
        Book journal = new Book("2", "Journal", "Ed", new JournalFine());

        assertEquals("DVD", dvd.getItemType());
        assertEquals("JOURNAL", journal.getItemType());
    }

    @Test
    void testCalculateFine_WithStrategy() {
        Book dvd = new Book("1", "Movie", "Dir", new DvdFine());
        assertEquals(40.0, dvd.calculateFine(2));   // 20 * 2
    }

    @Test
    void testCalculateFine_NoStrategy() throws Exception {
        Book book = new Book("1", "X", "Y");

        Field f = Book.class.getDeclaredField("fineStrategy");
        f.setAccessible(true);
        f.set(book, null);

        assertEquals(3.0, book.calculateFine(3)); // fallback = 1 per day
    }

    @Test
    void testRebuildFineStrategy() {
        Book b = new Book();

        b.setFineType("DVD");
        b.rebuildFineStrategy();
        assertEquals("DVD", b.getItemType());

        b.setFineType("JOURNAL");
        b.rebuildFineStrategy();
        assertEquals("JOURNAL", b.getItemType());

        b.setFineType("XYZ");
        b.rebuildFineStrategy();
        assertEquals("BOOK", b.getItemType());
    }

    @Test
    void testToStringAvailableState() {
        Book b = new Book("100", "Test Book", "Author", new BookFine());

        b.setBorrowed(false);

        String s = b.toString();

        assertTrue(s.contains("Available"));
        assertTrue(s.contains("ISBN='100'"));
        assertTrue(s.contains("Title='Test Book'"));
    }
    
    
    
    @Test
    void testToStringBorrowedState() {
        Book b = new Book("123", "Test", "Tester");
        b.setBorrowed(true);
        b.setBorrowedByUserId("U1");
        LocalDate due = LocalDate.of(2025, 1, 10);
        b.setDueDate(due);

        String s = b.toString();
        assertTrue(s.contains("Borrowed by UserID: U1"));
        assertTrue(s.contains("Due: 2025-01-10"));
    }
}
